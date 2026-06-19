package com.sam.myapplication.ui

import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.sam.myapplication.auth.GoogleAuthService
import com.sam.myapplication.data.*
import com.sam.myapplication.supabase.*
import com.sam.myapplication.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class AttendanceViewModel(
    private val repository: AttendanceRepository,
    private val authService: GoogleAuthService,
    private val supabaseSync: SupabaseSyncManager,
    private val supabaseStorage: SupabaseStorageSyncManager,
    private val context: Context
) : ViewModel() {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val prefs = context.getSharedPreferences("attendance_prefs", Context.MODE_PRIVATE)

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isOnline: StateFlow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(true) }
            override fun onLost(network: Network) { trySend(false) }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        trySend(connectivityManager.activeNetwork != null)
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), connectivityManager.activeNetwork != null)

    init {
        // Automatically sync settings on startup
        viewModelScope.launch {
            syncSettings()
            supabaseSync.syncShiftTemplates()
            supabaseSync.syncSchedules()
        }

        // Subscribe to chat when logged in
        viewModelScope.launch {
            loggedInEmployee.collect { emp ->
                if (emp != null) {
                    supabaseSync.subscribeToChatRealtime(viewModelScope, emp.employeeNo ?: emp.id)
                }
            }
        }

        // Reconnect realtime if online status changes from offline to online
        viewModelScope.launch {
            isOnline.collect { online ->
                if (online) {
                    val emp = _loggedInEmployee.value
                    if (emp != null) {
                        Log.d("AttendanceViewModel", "Internet restored, refreshing realtime subscription")
                        supabaseSync.subscribeToChatRealtime(viewModelScope, emp.employeeNo ?: emp.id)
                    }
                }
            }
        }

        // Periodic fallback poll every 30 seconds to ensure we didn't miss anything (Realtime is primary)
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(30_000)
                if (isOnline.value && _loggedInEmployee.value != null) {
                    supabaseSync.syncChatMessages()
                }
            }
        }
    }

    // --- CHAT FUNCTIONALITY ---

    val groupChatMessages: Flow<List<ChatMessage>> = repository.allMessages.map { list ->
        list.filter { it.receiverId == "group" }.sortedBy { it.timestamp }
    }.flowOn(Dispatchers.IO)

    fun getPrivateMessages(otherId: String): Flow<List<ChatMessage>> {
        val myId = loggedInEmployee.value?.employeeNo ?: loggedInEmployee.value?.id ?: ""
        return repository.getMessagesWith(myId, otherId).map { it.sortedBy { it.timestamp } }
    }

    fun sendChatMessage(receiverId: String, text: String) {
        val senderId = loggedInEmployee.value?.employeeNo ?: loggedInEmployee.value?.id ?: return
        viewModelScope.launch {
            try {
                val message = ChatMessage(
                    id = 0L, // Dummy ID, will be replaced by Supabase
                    senderId = senderId,
                    receiverId = receiverId,
                    message = text,
                    timestamp = System.currentTimeMillis()
                )
                // Send to Supabase (it will return the actual ID and we save it then)
                supabaseSync.sendChatMessage(message)
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Failed to send message: ${e.message}")
            }
        }
    }

    fun editChatMessage(message: ChatMessage, newText: String) {
        viewModelScope.launch {
            try {
                val updated = message.copy(message = newText)
                repository.updateMessage(updated)
                supabaseSync.updateChatMessage(updated.id, newText)
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Failed to edit message: ${e.message}")
            }
        }
    }

    fun deleteChatMessage(message: ChatMessage) {
        viewModelScope.launch {
            try {
                repository.deleteMessage(message)
                supabaseSync.deleteChatMessage(message.id)
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Failed to delete message: ${e.message}")
            }
        }
    }

    suspend fun uploadImageToImgBB(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val bytes = inputStream.readBytes()
            val base64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)
            
            val client = OkHttpClient()
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key", "25b74965a773e51763313202c1db57de")
                .addFormDataPart("image", base64Image)
                .build()
            
            val request = Request.Builder()
                .url("https://api.imgbb.com/1/upload")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            val jsonResponse = response.body?.string() ?: return@withContext null
            val jsonObject = Json.parseToJsonElement(jsonResponse).jsonObject
            
            if (jsonObject["success"]?.jsonPrimitive?.boolean == true) {
                jsonObject["data"]?.jsonObject?.get("url")?.jsonPrimitive?.content
            } else null
        } catch (e: Exception) {
            Log.e("AttendanceViewModel", "imgBB upload failed", e)
            null
        }
    }

    fun markMessagesAsRead(senderId: String) {
        val myId = loggedInEmployee.value?.employeeNo ?: loggedInEmployee.value?.id ?: return
        viewModelScope.launch {
            repository.markAsRead(myId, senderId)
        }
    }

    private val _targetHeadcount = MutableStateFlow(prefs.getInt("target_headcount", 0))
    val targetHeadcount: StateFlow<Int> = _targetHeadcount.asStateFlow()

    private val _medalCountdownStart = MutableStateFlow(prefs.getString("medal_countdown_start", LocalDate.now().toString()) ?: LocalDate.now().toString())
    val medalCountdownStart: StateFlow<String> = _medalCountdownStart.asStateFlow()

    private val _excludedFromHeadcountIds = MutableStateFlow(prefs?.getStringSet("excluded_ids", emptySet()) ?: emptySet())
    val excludedFromHeadcountIds: StateFlow<Set<String>> = _excludedFromHeadcountIds.asStateFlow()

    private val _syncProgress = MutableStateFlow<Float?>(null)
    val syncProgress: StateFlow<Float?> = _syncProgress.asStateFlow()

    private val _syncStatus = MutableStateFlow<String?>(null)
    val syncStatus: StateFlow<String?> = _syncStatus.asStateFlow()

    private val _settingsUpdated = MutableStateFlow(0)
    val settingsUpdated: StateFlow<Int> = _settingsUpdated.asStateFlow()

    private val _isScrapingMallId = MutableStateFlow(false)
    val isScrapingMallId: StateFlow<Boolean> = _isScrapingMallId.asStateFlow()

    private val _selectedIdsToScrape = MutableStateFlow<List<String>>(emptyList())
    val selectedIdsToScrape: StateFlow<List<String>> = _selectedIdsToScrape.asStateFlow()

    private val _scrapingProgress = MutableStateFlow(0f)
    val scrapingProgress: StateFlow<Float> = _scrapingProgress.asStateFlow()

    fun startScrapingMallId(ids: List<String>) {
        _selectedIdsToScrape.value = ids
        _isScrapingMallId.value = true
        _scrapingProgress.value = 0f
    }

    fun stopScrapingMallId() {
        _isScrapingMallId.value = false
        _scrapingProgress.value = 0f
    }

    fun setScrapingProgress(progress: Float) {
        _scrapingProgress.value = progress
    }

    fun setTargetHeadcount(count: Int) {
        viewModelScope.launch {
            _targetHeadcount.value = count
            prefs?.edit()?.putInt("target_headcount", count)?.apply()
            // Save to server
            supabaseSync.saveTargetHeadcount(count)
        }
    }

    fun setMedalCountdownStart(date: String) {
        viewModelScope.launch {
            _medalCountdownStart.value = date
            prefs.edit().putString("medal_countdown_start", date).apply()
            supabaseSync.saveMedalCountdownStart(date)
        }
    }

    fun clearSyncStatus() {
        _syncProgress.value = null
        _syncStatus.value = null
    }

    fun toggleHeadcountExclusion(employeeId: String) {
        val current = _excludedFromHeadcountIds.value.toMutableSet()
        if (current.contains(employeeId)) current.remove(employeeId)
        else current.add(employeeId)
        _excludedFromHeadcountIds.value = current
        prefs.edit().putStringSet("excluded_ids", current).apply()
        
        // Save to server
        viewModelScope.launch {
            supabaseSync.saveExcludedIds(current)
        }
    }

    private val _lastShownAnnouncementId = MutableStateFlow(prefs.getInt("last_announcement_id", -1))
    val lastShownAnnouncementId: StateFlow<Int> = _lastShownAnnouncementId

    fun setLastShownAnnouncementId(id: Int) {
        _lastShownAnnouncementId.value = id
        prefs.edit().putInt("last_announcement_id", id).apply()
    }

    private val _selectedTheme = MutableStateFlow(AppTheme.DARK)
    val selectedTheme: StateFlow<AppTheme> = _selectedTheme.asStateFlow()

    fun setTheme(theme: AppTheme) {
        _selectedTheme.value = theme
    }

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    val currentUser = authService.currentUser

    val allEmployees: StateFlow<List<Employee>> = repository.allEmployees
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allRequests: StateFlow<List<AttendanceRequest>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allActivityLogs: StateFlow<List<ActivityLog>> = repository.allActivityLogs
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allSchedules: StateFlow<List<EmployeeSchedule>> = repository.allSchedules
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allShiftTemplates: StateFlow<List<ShiftTemplate>> = repository.allShiftTemplates
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getSchedulesInRange(startDate: String, endDate: String) = repository.getSchedulesInRange(startDate, endDate)
    fun getSchedulesInRange(employeeId: String, startDate: String, endDate: String) = repository.getSchedulesInRange(employeeId, startDate, endDate)

    // Removed messaging functions

    fun getUnreadMessageCount(userId: String): Flow<Int> {
        return repository.getUnreadMessages(userId).map { it.size }
    }

    fun getUnreadMessages(userId: String): Flow<List<ChatMessage>> {
        return repository.getUnreadMessages(userId)
    }

    private val _loggedInEmployee = MutableStateFlow<Employee?>(null)
    val loggedInEmployee: StateFlow<Employee?> = _loggedInEmployee.asStateFlow()

    private val _loginStatus = MutableStateFlow<String?>(null)
    val loginStatus: StateFlow<String?> = _loginStatus.asStateFlow()

    fun login(employeeNo: String, passwordHash: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            // Secure Admin Login from Supabase Settings
            val adminUser = supabaseSync.getSetting("admin_username") ?: "fusion"
            val adminPass = supabaseSync.getSetting("admin_portal_pass") ?: "chowking2026"
            val chowkingUser = supabaseSync.getSetting("chowking_username") ?: "chowking"
            val chowkingPass = supabaseSync.getSetting("chowking_portal_pass") ?: "chowking2026"

            if ((employeeNo == adminUser || employeeNo == chowkingUser) && 
                (passwordHash == adminPass || passwordHash == chowkingPass)) {
                
                val existing = repository.getEmployeeByNo(employeeNo)
                val admin = if (existing != null) {
                    if (existing.isAdmin != true || existing.passwordHash != passwordHash) {
                        val updated = existing.copy(isAdmin = true, passwordHash = passwordHash)
                        repository.updateEmployee(updated)
                        updated
                    } else existing
                } else {
                    Employee(
                        id = employeeNo,
                        employeeNo = employeeNo,
                        passwordHash = passwordHash,
                        firstName = if (employeeNo == adminUser) "Admin" else "Chowking",
                        lastName = if (employeeNo == adminUser) "Fusion" else "Admin",
                        email = "$employeeNo@admin.com",
                        phoneNumber = "0000",
                        department = "Management",
                        position = "Administrator",
                        isAdmin = true
                    ).also { repository.insertEmployee(it) }
                }
                
                _loggedInEmployee.value = admin
                _loginStatus.value = "Login Successful"
                onResult(true, null)
                return@launch
            }

            val employee = repository.getEmployeeByNo(employeeNo)
            if (employee != null && employee.passwordHash == passwordHash) {
                _loggedInEmployee.value = employee
                _loginStatus.value = "Login Successful"
                onResult(true, null)
            } else {
                onResult(false, "Invalid Employee No or Password")
            }
        }
    }

    fun signOut() {
        _loggedInEmployee.value = null
        _loginStatus.value = null
        
        // Clear WebView cookies and session data to prevent account leakage on shared devices
        try {
            val cookieManager = android.webkit.CookieManager.getInstance()
            cookieManager.removeAllCookies { success ->
                Log.d("AttendanceViewModel", "Cookies cleared on sign out: ${success}")
            }
            cookieManager.flush()
            
            // Delete cached payslips from cache directory
            val cacheDir = context.cacheDir
            cacheDir.listFiles()?.forEach { file ->
                if (file.name.endsWith(".pdf")) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            Log.e("AttendanceViewModel", "Failed to clear cookies or cache", e)
        }
    }

    fun changePassword(newPassword: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val current = _loggedInEmployee.value ?: return@launch
            val updated = current.copy(passwordHash = newPassword)
            repository.updateEmployee(updated)
            _loggedInEmployee.value = updated
            uploadEmployeeData(updated.id) { onComplete(it) }
        }
    }

    fun adminChangePassword(employeeId: String, newPassword: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val employee = repository.allEmployees.first().find { it.id == employeeId } ?: return@launch
            val updated = employee.copy(passwordHash = newPassword)
            repository.updateEmployee(updated)
            uploadEmployeeData(employeeId) { onComplete(it) }
        }
    }

    // --- GRANULAR SUPABASE DATABASE SYNC (Postgrest) ---

    suspend fun uploadUpdateConfig(config: com.sam.myapplication.UpdateInfo): Boolean {
        val jsonString = json.encodeToString(config)
        return supabaseStorage.uploadUpdateConfig(jsonString)
    }

    suspend fun getUpdateConfig(): com.sam.myapplication.UpdateInfo? {
        val jsonString = supabaseStorage.getUpdateConfig() ?: return null
        return try {
            json.decodeFromString<com.sam.myapplication.UpdateInfo>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    fun syncWithCloudStorage(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Syncing with cloud..."
                supabaseStorage.syncData()
                _syncStatus.value = "Cloud sync successful"
                onComplete(true)
            } catch (e: Exception) {
                _syncStatus.value = "Cloud sync failed"
                onComplete(false)
            }
        }
    }

    fun forceSyncWithCloudStorage(onComplete: (Boolean) -> Unit) {
        syncWithCloudStorage(onComplete)
    }

    fun syncAccountsOnly(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Retrieving accounts..."
                supabaseSync.retrieveAccounts()
                _syncStatus.value = "Retrieval complete"
                onComplete(true)
            } catch (e: Exception) {
                _syncStatus.value = "Account sync failed"
                _authError.value = "Account sync failed"
                onComplete(false)
            }
        }
    }

    private suspend fun syncSettings() {
        try {
            val allSettings = supabaseSync.getAllSettings()
            val editor = prefs.edit()
            
            allSettings.forEach { setting ->
                val key = setting["key"] ?: return@forEach
                val value = setting["value"] ?: return@forEach
                
                when {
                    key == "target_headcount" -> {
                        val count = value.toIntOrNull() ?: 0
                        _targetHeadcount.value = count
                        editor.putInt(key, count)
                    }
                    key == "excluded_ids" -> {
                        val ids = value.split(",").filter { it.isNotBlank() }.toSet()
                        _excludedFromHeadcountIds.value = ids
                        editor.putStringSet(key, ids)
                    }
                    key == "medal_countdown_start" -> {
                        _medalCountdownStart.value = value
                        editor.putString(key, value)
                    }
                    key == "portal_username" -> {
                        editor.putString(key, value)
                    }
                    key == "portal_password" -> {
                        editor.putString(key, value)
                    }
                    key.startsWith("pos_color_") || key.startsWith("pos_font_color_") -> {
                        editor.putInt(key, value.toIntOrNull() ?: 0)
                    }
                }
            }
            editor.apply()
            _settingsUpdated.value++
        } catch (e: Exception) {
            Log.e("AttendanceViewModel", "Failed to sync settings: ${e.message}")
        }
    }

    fun retrieveEmployeeData(employeeNo: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Retrieving employee info..."
                supabaseSync.retrieveEmployeeData(employeeNo)
                
                // Also sync global settings
                syncSettings()
                
                val emp = repository.getEmployeeByNo(employeeNo)
                if (emp != null) {
                    _syncStatus.value = "Downloading photo..."
                    val photoUri = supabaseStorage.downloadEmployeePhoto(employeeNo, emp.id)
                    if (photoUri != null) {
                        repository.updateEmployee(emp.copy(profileImageUri = photoUri))
                    }
                }
                _syncStatus.value = "Retrieval successful"
                onComplete(true)
            } catch (e: Exception) {
                _syncStatus.value = "Retrieval failed"
                onComplete(false)
            }
        }
    }

    fun uploadEmployeeData(employeeId: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                supabaseSync.uploadEmployeeData(employeeId)
                
                // Still enqueue background work for reliability and retries if needed, 
                // but we performed one immediately for UI feedback.
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val syncRequest = OneTimeWorkRequestBuilder<com.sam.myapplication.sync.SyncWorker>()
                    .setConstraints(constraints)
                    .setInputData(workDataOf("employee_id" to employeeId))
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, java.util.concurrent.TimeUnit.MINUTES)
                    .build()

                WorkManager.getInstance(context).enqueueUniqueWork(
                    "sync_$employeeId",
                    ExistingWorkPolicy.REPLACE,
                    syncRequest
                )
                
                onComplete(true)
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Manual upload failed for $employeeId: ${e.message}")
                onComplete(false)
            }
        }
    }

    fun retrieveAllData(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _syncProgress.value = 0.1f
                _syncStatus.value = "Downloading database..."
                // 1. Sync all records from Database
                supabaseSync.retrieveAllData()
                
                // 1.1 Sync target headcount & excluded IDs
                syncSettings()
                
                _syncProgress.value = 0.5f
                _syncStatus.value = "Downloading profile photos..."
                // 2. Download all profile photos
                val employees = repository.allEmployees.first()
                var count = 0
                employees.forEach { emp ->
                    emp.employeeNo?.let { no ->
                        if (no.isNotBlank()) {
                            val localUri = supabaseStorage.downloadEmployeePhoto(no, emp.id)
                            if (localUri != null) {
                                repository.updateEmployee(emp.copy(profileImageUri = localUri))
                            }
                        }
                    }
                    count++
                    _syncProgress.value = 0.5f + (0.5f * (count.toFloat() / employees.size))
                }
                
                _syncProgress.value = null
                _syncStatus.value = "Retrieval complete"
                onComplete(true)
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Full retrieval failed: ${e.message}")
                _syncProgress.value = null
                _syncStatus.value = "Retrieval failed"
                onComplete(false)
            }
        }
    }

    fun performBackgroundSync() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Sync global stuff
                supabaseSync.syncAnnouncements()
                supabaseSync.syncDailyNotes()
                // supabaseSync.syncSchedules() // Removed to allow manual Upload/Download control
                supabaseSync.syncShiftTemplates()
                syncSettings()
                
                // If admin, sync current month's attendance
                if (loggedInEmployee.value?.isAdmin == true) {
                    supabaseSync.syncAttendanceForMonth(java.time.YearMonth.now().toString())
                }
                
                // Automatic Database Repair
                repository.repairEmployeeLinks()
                
                // If employee, sync their own data
                loggedInEmployee.value?.id?.let { id ->
                    if (loggedInEmployee.value?.isAdmin != true) {
                        supabaseSync.syncEmployeeDataSmart(id)
                    }
                }
                
                Log.d("AttendanceViewModel", "Background sync completed")
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Background sync failed", e)
            }
        }
    }

    fun majorUpload(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Starting major upload..."
                _syncProgress.value = 0f
                val employees = repository.allEmployees.first()
                var count = 0
                var failedCount = 0
                
                employees.forEach { emp ->
                    _syncStatus.value = "Uploading ${emp.firstName}..."
                    try {
                        supabaseSync.uploadEmployeeData(emp.id)
                    } catch (e: Exception) {
                        Log.e("AttendanceViewModel", "Major upload failed for ${emp.firstName}: ${e.message}")
                        failedCount++
                    }
                    count++
                    _syncProgress.value = count.toFloat() / employees.size
                }
                
                _syncProgress.value = null
                if (failedCount > 0) {
                    _syncStatus.value = "Major upload finished with $failedCount errors"
                    onComplete(false)
                } else {
                    _syncStatus.value = "Major upload successful"
                    onComplete(true)
                }
            } catch (e: Exception) {
                _syncProgress.value = null
                _syncStatus.value = "Major upload interrupted"
                onComplete(false)
            }
        }
    }

    fun retrieveAllRequests(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Downloading requests..."
                supabaseSync.retrieveAllRequests()
                _syncStatus.value = "Requests downloaded"
                onComplete(true)
            } catch (e: Exception) {
                _syncStatus.value = "Requests retrieval failed"
                onComplete(false)
            }
        }
    }

    fun retrieveAllAttendance(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Downloading attendance..."
                supabaseSync.retrieveAllAttendance()
                
                // Also sync global settings
                syncSettings()

                _syncStatus.value = "Attendance downloaded"
                onComplete(true)
            } catch (e: Exception) {
                _syncStatus.value = "Attendance retrieval failed"
                onComplete(false)
            }
        }
    }

    fun retrieveAllPhotos(onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Downloading photos..."
                val employees = repository.allEmployees.first()
                Log.d("AttendanceViewModel", "Starting bulk photo retrieval for ${employees.size} employees")
                var successCount = 0
                employees.forEach { emp ->
                    val no = emp.employeeNo
                    if (!no.isNullOrBlank()) {
                        val localUri = supabaseStorage.downloadEmployeePhoto(no, emp.id)
                        if (localUri != null) {
                            repository.updateEmployee(emp.copy(profileImageUri = localUri))
                            successCount++
                        }
                    }
                    _syncProgress.value = successCount.toFloat() / employees.size
                }
                Log.d("AttendanceViewModel", "Bulk photo retrieval finished. Success: $successCount/${employees.size}")
                _syncStatus.value = "Photos retrieval complete"
                _syncProgress.value = null
                onComplete(successCount)
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Photo retrieval failed: ${e.message}")
                _syncStatus.value = "Photo retrieval failed"
                _syncProgress.value = null
                onComplete(0)
            }
        }
    }

    // --- OTHER METHODS ---

    fun uploadProfileImage(employeeId: String, contentResolver: ContentResolver, uri: Uri, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                // 1. Upload to Storage
                supabaseStorage.uploadEmployeePhoto(employeeId, uri)
                
                // 2. Sync updated employee record (with localUri/remote path) to Database
                uploadEmployeeData(employeeId)
                
                onComplete?.invoke(true)
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Photo upload failed: ${e.message}")
                onComplete?.invoke(false)
            }
        }
    }

    fun addEmployee(context: Context, employee: Employee) {
        viewModelScope.launch {
            _syncStatus.value = "Saving employee..."
            repository.insertEmployee(employee)
            logActivity("Add Employee", "Name: ${employee.firstName} ${employee.lastName}, ID: ${employee.employeeNo}")
            
            // Explicitly upload immediately after insert
            uploadEmployeeData(employee.id) { success ->
                _syncStatus.value = if (success) "Employee saved & synced" else "Saved locally (Sync pending)"
            }
        }
    }

    fun updateMallIdStatus(employeeId: String, status: String) {
        viewModelScope.launch {
            val emp = repository.getEmployeeById(employeeId)
            if (emp != null && emp.mallIdStatus != status) {
                repository.updateEmployee(emp.copy(mallIdStatus = status))
                uploadEmployeeData(employeeId)
            }
        }
    }

    fun updateEmployee(context: Context, employee: Employee) {
        viewModelScope.launch {
            _syncStatus.value = "Updating employee..."
            repository.updateEmployee(employee)
            logActivity("Update Employee", "Name: ${employee.firstName} ${employee.lastName}, ID: ${employee.employeeNo}")
            uploadEmployeeData(employee.id) {
                _syncStatus.value = if (it) "Update synced to cloud" else "Updated locally (Sync pending)"
            }
        }
    }

    fun toggleEmployeeSchedulerVisibility(context: Context, employee: Employee) {
        updateEmployee(context, employee.copy(isHiddenFromScheduler = !employee.isHiddenFromScheduler))
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            _syncStatus.value = "Deleting employee..."
            // capture details before local delete
            val id = employee.id
            val empNo = employee.employeeNo
            
            logActivity("Delete Employee", "Name: ${employee.firstName} ${employee.lastName}, ID: $empNo")
            
            // Delete from server and storage first
            supabaseSync.deleteEmployeeRemote(id, empNo)
            supabaseStorage.deleteEmployeeStorage(empNo ?: id, id)
            
            // Then delete locally
            repository.deleteEmployee(employee)
            _syncStatus.value = "Employee deleted"
        }
    }

    fun markAttendance(employeeId: String, date: String, statusNotes: Map<AttendanceStatus, String?>) {
        viewModelScope.launch {
            _syncStatus.value = "Updating attendance..."
            repository.deleteAttendance(employeeId, date)
            statusNotes.forEach { (status, note) ->
                repository.insertAttendance(AttendanceRecord(employeeId = employeeId, date = date, status = status, note = note))
            }
            logActivity("Mark Attendance", "Emp: $employeeId, Date: $date, Status: ${statusNotes.keys.joinToString()}")

            // Auto-DA for ABSENT
            if (statusNotes.containsKey(AttendanceStatus.ABSENT)) {
                val absentNote = statusNotes[AttendanceStatus.ABSENT]
                val currentDA = repository.allDA.first()
                
                val emp = allEmployees.value.find { it.id == employeeId }
                if (emp != null) {
                    val targetDate = try { LocalDate.parse(date) } catch(e: Exception) { null }
                    
                    val existing = if (targetDate != null) {
                        currentDA.find { rec ->
                            rec.employeeId == employeeId && rec.dateAwol?.split(",")?.any { dStr ->
                                try {
                                    val d = LocalDate.parse(dStr.trim())
                                    d == targetDate || d.plusDays(1) == targetDate || d.minusDays(1) == targetDate
                                } catch (e: Exception) {
                                    false
                                }
                            } == true
                        }
                    } else {
                        currentDA.find { it.employeeId == employeeId && it.dateAwol == date }
                    }
                    
                    val record = if (existing != null) {
                        val currentDates = existing.dateAwol?.split(",")?.map { it.trim() }?.toMutableSet() ?: mutableSetOf()
                        currentDates.add(date)
                        val newAwolDates = currentDates.filter { it.isNotBlank() }.sorted().joinToString(", ")
                        existing.copy(
                            dateAwol = newAwolDates, 
                            verdict = if (existing.verdict.isNullOrBlank()) absentNote else existing.verdict,
                            employeeName = "${emp.firstName} ${emp.lastName}"
                        )
                    } else {
                        DARecord(
                            employeeId = employeeId,
                            employeeName = "${emp.firstName} ${emp.lastName}",
                            dateAwol = date,
                            daType = "First offence Awol",
                            status = "Not Yet Reporting",
                            verdict = absentNote
                        )
                    }
                    saveDARecord(record)
                }
            }

            uploadEmployeeData(employeeId) {
                _syncStatus.value = if (it) "Attendance synced" else "Attendance saved locally"
            }
        }
    }

    fun updateAttendanceNote(employeeId: String, date: String, note: String?) {
        viewModelScope.launch {
            _syncStatus.value = "Updating note..."
            repository.updateAttendanceNote(employeeId, date, note)
            uploadEmployeeData(employeeId) {
                _syncStatus.value = if (it) "Note synced" else "Note saved locally"
            }
        }
    }

    fun deleteAttendance(employeeId: String, date: String) {
        viewModelScope.launch {
            _syncStatus.value = "Deleting attendance..."
            val emp = repository.getEmployeeById(employeeId)
            val empNo = emp?.employeeNo ?: employeeId
            
            // Delete from server first
            supabaseSync.deleteAttendanceRemote(empNo, date)
            
            // Then delete locally
            repository.deleteAttendance(employeeId, date)
            
            uploadEmployeeData(employeeId) {
                _syncStatus.value = if (it) "Deletion synced" else "Deleted locally"
            }
        }
    }

    fun deleteDTRRecord(employeeId: String, date: String) {
        viewModelScope.launch {
            _syncStatus.value = "Deleting DTR..."
            val emp = repository.getEmployeeById(employeeId)
            val empNo = emp?.employeeNo ?: employeeId
            
            // Delete from server first
            supabaseSync.deleteDTRRemote(empNo, date)
            
            // Then delete locally
            repository.deleteDTR(employeeId, date)

            uploadEmployeeData(employeeId) {
                _syncStatus.value = if (it) "DTR deletion synced" else "Deleted locally"
            }
        }
    }

    fun saveDTRRecord(employeeId: String, dates: List<String>, timeIn: String?, timeOut: String?, note: String?, hasOT: Boolean, otApproved: String?) {
        viewModelScope.launch {
            _syncStatus.value = "Saving DTR..."
            dates.forEach { date ->
                val record = DailyTimeRecord(employeeId = employeeId, date = date, timeIn = timeIn, timeOut = timeOut, note = note, hasOvertime = hasOT, overtimeApprovedBy = otApproved)
                repository.insertDTR(record)
            }
            logActivity("Save DTR", "Emp: $employeeId, Dates: ${dates.joinToString()}, Time: $timeIn-$timeOut")
            uploadEmployeeData(employeeId) {
                _syncStatus.value = if (it) "DTR synced" else "DTR saved locally"
            }
        }
    }

    fun syncScheduleToDTR(employee: Employee, date: String, scheduleText: String?, tag: String?) {
        batchSyncSchedulesToDTR(employee, listOf(EmployeeSchedule(employee.id, date, scheduleText, tag)))
    }

    fun batchSyncSchedulesToDTR(employee: Employee, schedules: List<EmployeeSchedule>) {
        viewModelScope.launch {
            _syncStatus.value = "Syncing with Terminal..."
            var count = 0
            
            schedules.forEach { sched ->
                val isRD = sched.tag == "RD" || sched.tag == "RRD" || sched.scheduleText == "RD" || sched.scheduleText == "RRD"
                var ti: String? = null
                var to: String? = null
                var note = ""

                fun fix(t: String?): String? {
                    if (t == null) return null
                    val str = t.trim().uppercase()
                    val regex = Regex("""(\d{1,2})(?:[:.](\d{2}))?\s*(AM|PM)?""")
                    val militaryRegex = Regex("""^(\d{2})(\d{2})$""")
                    val match = regex.find(str) ?: militaryRegex.find(str)

                    if (match != null) {
                        var h: Int
                        var m: String
                        var ampm: String? = null

                        if (match.groupValues.size == 3 && str.length == 4 && !str.contains(":") && !str.contains(".")) {
                            h = match.groupValues[1].toInt()
                            m = match.groupValues[2]
                        } else {
                            h = match.groupValues[1].toInt()
                            m = match.groupValues.getOrNull(2)?.takeIf { it.isNotBlank() } ?: "00"
                            ampm = match.groupValues.getOrNull(3)?.takeIf { it.isNotBlank() }
                        }

                        if (ampm == "PM" && h < 12) h += 12
                        if (ampm == "AM" && h == 12) h = 0
                        
                        // No graveyard: assume 1-5 is PM
                        if (ampm == null && h >= 1 && h <= 5) h += 12

                        return "%02d:%s".format(h, m)
                    }
                    return null
                }

                if (isRD) {
                    note = "[RD] Posted from Schedule"
                } else if (!sched.scheduleText.isNullOrBlank()) {
                    val parts = sched.scheduleText.split("-", "–", "—", " TO ", " to ")
                    if (parts.size >= 2) {
                        ti = fix(parts[0])
                        to = fix(parts[1])

                        if (ti != null && to != null) {
                            val hIn = ti.split(":")[0].toInt()
                            val hOutRaw = to.split(":")[0].toInt()
                            val mOut = to.split(":")[1]
                            var finalHOut = hOutRaw
                            if (finalHOut <= hIn && finalHOut < 12) finalHOut += 12
                            to = "%02d:%s".format(finalHOut, mOut)
                        }
                    }
                }

                if (ti != null || to != null || isRD) {
                    val record = DailyTimeRecord(
                        employeeId = employee.id,
                        date = sched.date,
                        timeIn = ti,
                        timeOut = to,
                        note = note.ifBlank { "Posted from Schedule" }
                    )
                    repository.insertDTR(record)
                    count++
                }
            }
            
            if (count > 0) {
                uploadEmployeeData(employee.id) {
                    _syncStatus.value = if (it) "Successfully synced $count entries" else "Saved $count locally (Sync pending)"
                }
            } else {
                _syncStatus.value = "No valid shifts found to sync"
            }
        }
    }

    fun submitRequest(request: AttendanceRequest) {
        viewModelScope.launch {
            _syncStatus.value = "Submitting request..."
            repository.insertRequest(request)
            uploadEmployeeData(request.employeeId) {
                _syncStatus.value = if (it) "Request synced" else "Request pending"
            }
        }
    }

    fun approveRequest(request: AttendanceRequest, adminName: String?) {
        viewModelScope.launch {
            _syncStatus.value = "Approving request..."
            if (request.type == RequestType.PROFILE_UPDATE && !request.note.isNullOrBlank()) {
                try {
                    val employee = repository.getEmployeeById(request.employeeId)
                    if (employee != null) {
                        val regex = "Update (.+) to: (.+)".toRegex()
                        val matchResult = regex.find(request.note)
                        if (matchResult != null) {
                            val field = matchResult.groupValues[1]
                            val value = matchResult.groupValues[2]
                            
                            val updatedEmployee = when (field) {
                                "First Name" -> employee.copy(firstName = value)
                                "Middle Name" -> employee.copy(middleName = value)
                                "Last Name" -> employee.copy(lastName = value)
                                "Birthday" -> employee.copy(birthday = value)
                                "Location" -> employee.copy(location = value)
                                "Date Hired" -> employee.copy(dateHired = value)
                                "Mall ID#" -> employee.copy(mallIdNo = value)
                                "ID Expiration" -> employee.copy(mallIdExpirationDate = value)
                                "Email" -> employee.copy(email = value)
                                "Contact No." -> employee.copy(cpNumber = value, phoneNumber = value)
                                "TIN#" -> employee.copy(tinNumber = value)
                                "SSS#" -> employee.copy(sss = value)
                                "PhilHealth#" -> employee.copy(philHealth = value)
                                "Pag-IBIG#" -> employee.copy(pagibig = value)
                                "Bank" -> employee.copy(bank = value)
                                "Account No." -> employee.copy(bankAccountNumber = value)
                                "Cap" -> employee.copy(uniformCap = value.toIntOrNull() ?: employee.uniformCap)
                                "Apron" -> employee.copy(uniformApron = value.toIntOrNull() ?: employee.uniformApron)
                                "Shirt" -> employee.copy(uniformShirt = value.toIntOrNull() ?: employee.uniformShirt)
                                "Pants" -> employee.copy(uniformPants = value.toIntOrNull() ?: employee.uniformPants)
                                "Resign Date" -> employee.copy(resignationDate = value, isResigned = true)
                                "Payroll Access" -> employee.copy(payrollAccessCode = value)
                                "Payroll User" -> employee.copy(payrollUsername = value)
                                "Payroll Pass" -> employee.copy(payrollPassword = value)
                                else -> employee
                            }
                            
                            if (updatedEmployee != employee) {
                                repository.updateEmployee(updatedEmployee)
                                Log.d("AttendanceViewModel", "Applied profile update for ${employee.employeeNo}: $field = $value")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AttendanceViewModel", "Failed to apply profile update: ${e.message}")
                }
            }

            val updated = request.copy(status = RequestStatus.GRANTED, adminNote = "Approved by $adminName")
            repository.updateRequest(updated)
            logActivity("Approve Request", "Emp: ${request.employeeId}, Date: ${request.date}, Type: ${request.type}")
            uploadEmployeeData(request.employeeId) {
                _syncStatus.value = if (it) "Approval synced" else "Approved locally"
            }
        }
    }

    fun rejectRequest(request: AttendanceRequest, adminName: String?) {
        viewModelScope.launch {
            _syncStatus.value = "Rejecting request..."
            val updated = request.copy(status = RequestStatus.REJECTED, adminNote = "Rejected by $adminName")
            repository.updateRequest(updated)
            logActivity("Reject Request", "Emp: ${request.employeeId}, Date: ${request.date}, Type: ${request.type}")
            uploadEmployeeData(request.employeeId) {
                _syncStatus.value = if (it) "Rejection synced" else "Rejected locally"
            }
        }
    }

    fun getRequestsForEmployee(employeeId: String) = repository.getRequestsForEmployee(employeeId)
    fun getAttendanceForEmployee(employeeId: String) = repository.getAttendanceForEmployee(employeeId)
    fun getDTRForEmployee(employeeId: String) = repository.getDTRForEmployee(employeeId)
    fun getAnnouncementsForEmployee(employeeId: String) = repository.getAnnouncementsForEmployee(employeeId)

    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>> {
        return repository.getAttendanceByDate(date)
    }

    val allAttendanceRecords: Flow<List<AttendanceRecord>> = repository.allAttendanceRecords
    val allDailyNotes: Flow<List<DailySummaryNote>> = repository.allDailyNotes

    fun getDailyNote(date: String): Flow<DailySummaryNote?> {
        return repository.getDailyNote(date)
    }

    fun syncAttendanceForMonth(yearMonth: String) {
        viewModelScope.launch(Dispatchers.IO) {
            supabaseSync.syncAttendanceForMonth(yearMonth)
            supabaseSync.syncDailyNotes()
        }
    }

    fun saveDailyNote(date: String, note: String?) {
        viewModelScope.launch {
            _syncStatus.value = "Saving summary note..."
            repository.insertDailyNote(DailySummaryNote(date = date, note = note))
            launch(Dispatchers.IO) {
                try {
                    supabaseSync.syncDailyNotes()
                    _syncStatus.value = "Note saved & synced"
                } catch (e: Exception) {
                    _syncStatus.value = "Note saved locally"
                }
            }
        }
    }
    
    fun sendAnnouncement(title: String, content: String, targetId: String?) {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Sending announcement..."
                // 1. Save locally first
                repository.insertAnnouncement(Announcement(title = title, content = content, targetEmployeeId = targetId))
                
                // 2. Sync to cloud in background
                launch(Dispatchers.IO) {
                    try {
                        supabaseSync.syncAnnouncements()
                    } catch (e: Exception) {
                        Log.e("AttendanceViewModel", "Sync failed: ${e.message}")
                    }
                }
                
                _syncStatus.value = "Announcement sent"
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Failed to send: ${e.message}")
                _syncStatus.value = "Failed to send"
            }
        }
    }

    fun deleteAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            repository.deleteAnnouncement(announcement)
        }
    }

    fun markAnnouncementAsRead(id: Int) {
        viewModelScope.launch {
            repository.markAnnouncementAsRead(id)
        }
    }

    // Schedule methods
    fun saveSchedule(schedule: EmployeeSchedule) {
        viewModelScope.launch { 
            repository.insertSchedule(schedule)
        }
    }

    fun deleteSchedule(schedule: EmployeeSchedule) {
        viewModelScope.launch { 
            repository.deleteSchedule(schedule)
        }
    }

    fun saveShiftTemplate(template: ShiftTemplate) {
        viewModelScope.launch { 
            repository.insertShiftTemplate(template)
            launch(Dispatchers.IO) {
                try {
                    supabaseSync.syncShiftTemplates()
                } catch (e: Exception) {}
            }
        }
    }

    fun deleteShiftTemplate(template: ShiftTemplate) {
        viewModelScope.launch { 
            repository.deleteShiftTemplate(template)
            launch(Dispatchers.IO) {
                try {
                    supabaseSync.syncShiftTemplates()
                } catch (e: Exception) {}
            }
        }
    }

    fun uploadSchedulerData() {
        viewModelScope.launch {
            _syncStatus.value = "Uploading scheduler data..."
            try {
                withContext(Dispatchers.IO) {
                    supabaseSync.uploadSchedules()
                    supabaseSync.syncShiftTemplates()
                    syncSettings()
                }
                _syncStatus.value = "Upload complete"
            } catch (e: Exception) {
                _syncStatus.value = "Upload failed: ${e.message}"
            }
        }
    }

    fun downloadSchedulerData() {
        viewModelScope.launch {
            _syncStatus.value = "Downloading scheduler data..."
            try {
                withContext(Dispatchers.IO) {
                    supabaseSync.downloadSchedules()
                    supabaseSync.syncShiftTemplates()
                    syncSettings()
                }
                _syncStatus.value = "Download complete"
            } catch (e: Exception) {
                _syncStatus.value = "Download failed: ${e.message}"
            }
        }
    }

    fun syncSchedulerData() {
        viewModelScope.launch {
            _syncStatus.value = "Syncing scheduler data..."
            Log.d("AttendanceViewModel", "Manual scheduler sync started")
            try {
                withContext(Dispatchers.IO) {
                    supabaseSync.syncSchedules()
                    supabaseSync.syncShiftTemplates()
                    syncSettings() // This now also syncs position colors
                }
                _syncStatus.value = "Scheduler sync complete"
                Log.d("AttendanceViewModel", "Manual scheduler sync complete")
            } catch (e: Exception) {
                _syncStatus.value = "Scheduler sync failed: ${e.message}"
                Log.e("AttendanceViewModel", "Manual scheduler sync failed", e)
            }
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun logActivity(action: String, details: String? = null) {
        viewModelScope.launch {
            val log = ActivityLog(
                employeeId = _loggedInEmployee.value?.employeeNo ?: "admin",
                action = action,
                details = details
            )
            repository.insertActivityLog(log)
            supabaseSync.uploadActivityLog(log)
        }
    }

    fun retrieveActivityLogs() {
        if (_loggedInEmployee.value?.isAdmin == true) {
            viewModelScope.launch {
                _syncStatus.value = "Retrieving logs..."
                supabaseSync.retrieveAllActivityLogs()
                _syncStatus.value = "Logs retrieved"
            }
        }
    }

    // Work Permits
    val allWorkPermits: Flow<List<WorkPermit>> = repository.allPermits

    fun updateWorkPermit(permit: WorkPermit) {
        viewModelScope.launch {
            repository.insertPermit(permit)
            // Sync to Supabase
            supabaseSync.syncWorkPermits(listOf(permit))
        }
    }

    fun syncWorkPermits(scrapedPermits: List<WorkPermit>) {
        viewModelScope.launch {
            // Merge scraped data with local AND remote data to prevent wiping labels/notes
            val localExisting = repository.allPermits.first()
            val remoteExisting = try { supabaseSync.retrieveWorkPermits() } catch(e: Exception) { emptyList() }
            
            val merged = scrapedPermits.map { scraped ->
                val local = localExisting.find { it.referenceNumber == scraped.referenceNumber }
                val remote = remoteExisting.find { it.referenceNumber == scraped.referenceNumber }
                
                scraped.copy(
                    customLabel = local?.customLabel ?: remote?.customLabel,
                    cachedHtml = local?.cachedHtml ?: remote?.cachedHtml,
                    isExpired = local?.isExpired ?: remote?.isExpired ?: false
                )
            }
            repository.insertPermits(merged)
            supabaseSync.syncWorkPermits(merged)
        }
    }

    fun updatePermitLabel(referenceNumber: String, label: String) {
        viewModelScope.launch {
            repository.updatePermitLabel(referenceNumber, label)
            val updated = repository.getPermitByRef(referenceNumber)
            if (updated != null) {
                supabaseSync.syncWorkPermits(listOf(updated))
            }
        }
    }

    fun markPermitExpired(referenceNumber: String, expired: Boolean = true) {
        viewModelScope.launch {
            repository.updatePermitExpiredStatus(referenceNumber, expired)
            val updated = repository.getPermitByRef(referenceNumber)
            if (updated != null) {
                supabaseSync.syncWorkPermits(listOf(updated))
            }
        }
    }

    fun updatePermitCache(referenceNumber: String, html: String) {
        viewModelScope.launch {
            repository.updatePermitCache(referenceNumber, html)
            val updated = repository.getPermitByRef(referenceNumber)
            if (updated != null) {
                supabaseSync.syncWorkPermits(listOf(updated))
            }
        }
    }

    fun appendPermitCapture(referenceNumber: String, tabName: String, tabHtml: String, shellHtml: String) {
        viewModelScope.launch {
            val existingPermit = repository.getPermitByRef(referenceNumber)
            val currentCache = existingPermit?.cachedHtml
            
            val newBundle = if (!currentCache.isNullOrBlank() && currentCache.startsWith("{\"_is_permit_bundle\":true")) {
                try {
                    val bundleObj = json.parseToJsonElement(currentCache).jsonObject.toMutableMap()
                    val tabsArray = bundleObj["tabs"]?.jsonArray?.toMutableList() ?: mutableListOf()
                    
                    val existingTabIndex = tabsArray.indexOfFirst { it.jsonObject["name"]?.jsonPrimitive?.content == tabName }
                    val newTab = buildJsonObject {
                        put("name", tabName)
                        put("html", tabHtml)
                    }
                    
                    if (existingTabIndex != -1) {
                        tabsArray[existingTabIndex] = newTab
                    } else {
                        tabsArray.add(newTab)
                    }
                    
                    bundleObj["tabs"] = JsonArray(tabsArray)
                    bundleObj["shell"] = JsonPrimitive(shellHtml)
                    
                    json.encodeToString(JsonObject(bundleObj))
                } catch (e: Exception) {
                    createInitialBundle(tabName, tabHtml, shellHtml)
                }
            } else {
                createInitialBundle(tabName, tabHtml, shellHtml)
            }
            
            repository.updatePermitCache(referenceNumber, newBundle)
        }
    }

    private fun createInitialBundle(name: String, html: String, shell: String): String {
        val bundle = buildJsonObject {
            put("_is_permit_bundle", true)
            put("shell", shell)
            put("tabs", buildJsonArray {
                add(buildJsonObject {
                    put("name", name)
                    put("html", html)
                })
            })
        }
        return json.encodeToString(bundle)
    }

    fun clearPermitCache() {
        viewModelScope.launch {
            repository.clearPermitCache()
        }
    }

    fun fetchRemotePermits() {
        viewModelScope.launch {
            val remote = supabaseSync.retrieveWorkPermits()
            if (remote.isNotEmpty()) {
                val existing = repository.allPermits.first()
                val merged = remote.map { r ->
                    val local = existing.find { it.referenceNumber == r.referenceNumber }
                    if (local != null) {
                        r.copy(
                            cachedHtml = local.cachedHtml // Preserve offline content
                        )
                    } else {
                        r
                    }
                }
                repository.insertPermits(merged)
            }
        }
    }

    // Attrition
    val allAttrition: StateFlow<List<AttritionRecord>> = repository.allAttrition
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveAttritionRecord(record: AttritionRecord) {
        viewModelScope.launch {
            _syncStatus.value = "Saving attrition..."
            repository.insertAttrition(record)
            launch(Dispatchers.IO) {
                try {
                    supabaseSync.syncAttrition()
                    _syncStatus.value = "Attrition saved & synced"
                } catch (e: Exception) {
                    _syncStatus.value = "Attrition saved locally"
                }
            }
        }
    }

    fun deleteAttritionRecord(record: AttritionRecord) {
        viewModelScope.launch {
            _syncStatus.value = "Deleting attrition..."
            repository.deleteAttrition(record)
            launch(Dispatchers.IO) {
                try {
                    supabaseSync.deleteAttritionRemote(record.id)
                    _syncStatus.value = "Attrition deleted & synced"
                } catch (e: Exception) {
                    _syncStatus.value = "Attrition deleted locally"
                }
            }
        }
    }

    // DA
    val allDA: StateFlow<List<DARecord>> = repository.allDA
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveDARecord(record: DARecord) {
        viewModelScope.launch {
            _syncStatus.value = "Saving DA..."
            repository.insertDA(record)
            logActivity("Save DA Record", "For: ${record.employeeName}, Type: ${record.daType}")
            launch(Dispatchers.IO) {
                try {
                    supabaseSync.syncDA()
                    _syncStatus.value = "DA saved & synced"
                } catch (e: Exception) {
                    _syncStatus.value = "DA saved locally"
                }
            }
        }
    }

    fun deleteDARecord(record: DARecord) {
        viewModelScope.launch {
            _syncStatus.value = "Deleting DA..."
            repository.deleteDA(record)
            logActivity("Delete DA Record", "For: ${record.employeeName}, Type: ${record.daType}")
            launch(Dispatchers.IO) {
                try {
                    supabaseSync.deleteDARemote(record.id)
                    _syncStatus.value = "DA deleted & synced"
                } catch (e: Exception) {
                    _syncStatus.value = "DA deleted locally"
                }
            }
        }
    }

    fun exportSchedulerToExcel(
        outputStream: OutputStream,
        employees: List<Employee>,
        allSchedules: List<EmployeeSchedule>,
        weekDates: List<java.time.LocalDate>,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val html = StringBuilder()
                // XML-based HTML for better Excel compatibility
                html.append("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns=\"http://www.w3.org/TR/REC-html40\">")
                html.append("<head><meta charset=\"UTF-8\">")
                html.append("<!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>Schedule</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]-->")
                html.append("<style>")
                html.append("  .header { background-color: #000080; color: #FFFFFF; font-weight: bold; text-align: center; }")
                html.append("  .sub-header { background-color: #CCCCCC; font-weight: bold; text-align: center; }")
                // Use explicit mso-number-format: "\@" to force treat as Text (stops decimal conversion)
                html.append("  .data-cell { mso-number-format:\"\\@\"; text-align: center; background-color: #FFFFE0; }")
                html.append("</style></head><body>")
                html.append("<table border=\"1\" style=\"border-collapse: collapse; font-family: Arial, sans-serif;\">")
                
                // Header 1: Days
                html.append("<tr class=\"header\">")
                html.append("<th rowspan=\"2\">Name</th>")
                val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
                for (i in 0..6) {
                    html.append("<th colspan=\"3\">${days[i]}<br/>${weekDates[i]}</th>")
                }
                html.append("</tr>")
                
                // Header 2: TI, TO, Brk
                html.append("<tr class=\"sub-header\">")
                for (i in 0..6) {
                    html.append("<td>TI</td><td>TO</td><td>Brk</td>")
                }
                html.append("</tr>")
                
                // Rows: Employees
                for (emp in employees) {
                    html.append("<tr>")
                    html.append("<td style=\"padding: 4px; font-weight: bold; mso-number-format:'\\@';\">${emp.firstName} ${emp.lastName ?: ""}</td>")
                    
                    for (date in weekDates) {
                        val sched = allSchedules.find { it.employeeId == emp.id && it.date == date.toString() }
                        if (sched?.tag == "RD" || sched?.tag == "RRD") {
                            html.append("<td class=\"data-cell\">RD</td>")
                            html.append("<td class=\"data-cell\">RD</td>")
                            html.append("<td class=\"data-cell\">RD</td>")
                        } else if (!sched?.scheduleText.isNullOrBlank()) {
                            val times = parseScheduleTimes(sched.scheduleText)
                            html.append("<td class=\"data-cell\">${times.first}</td>")
                            html.append("<td class=\"data-cell\">${times.second}</td>")
                            html.append("<td class=\"data-cell\">1</td>")
                        } else {
                            html.append("<td class=\"data-cell\"></td><td class=\"data-cell\"></td><td class=\"data-cell\"></td>")
                        }
                    }
                    html.append("</tr>")
                }
                
                html.append("</table></body></html>")
                
                withContext(Dispatchers.IO) {
                    outputStream.use { it.write(html.toString().toByteArray()) }
                }
                onComplete(true)
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Excel export failed", e)
                onComplete(false)
            }
        }
    }

    private fun parseScheduleTimes(text: String?): Pair<String, String> {
        if (text.isNullOrBlank()) return Pair("", "")
        val clean = text.lowercase().replace(" ", "")
        val parts = clean.split("-", "–")
        if (parts.size != 2) return Pair(text, "")

        fun to24h(timeStr: String, isEnd: Boolean = false, otherHour: Int? = null): String {
            val hasPm = timeStr.contains("pm")
            val hasAm = timeStr.contains("am")
            val digitsOnly = timeStr.replace("[^0-9:]".toRegex(), "")
            
            if (digitsOnly.isEmpty()) return timeStr
            
            var hour: Int
            var minute = 0
            
            if (digitsOnly.contains(":")) {
                val timeParts = digitsOnly.split(":")
                hour = timeParts[0].toIntOrNull() ?: 0
                minute = if (timeParts.size > 1) timeParts[1].toIntOrNull() ?: 0 else 0
            } else {
                hour = digitsOnly.toIntOrNull() ?: 0
            }
            
            if (hasPm && hour < 12) hour += 12
            if (hasAm && hour == 12) hour = 0
            
            // Heuristic if no AM/PM specified
            if (!hasAm && !hasPm && hour > 0 && hour < 12) {
                if (isEnd) {
                    // End times 1-11 are almost always PM in this context
                    hour += 12
                } else if (hour < 7) {
                    // Start times 1-6 are usually PM (1pm-6pm)
                    hour += 12
                }
            }
            
            return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
        }

        val startTime = to24h(parts[0], false)
        val startH = startTime.split(":")[0].toIntOrNull()
        val endTime = to24h(parts[1], true, startH)
        
        return Pair(startTime, endTime)
    }

    // Export/Import Implementations
    fun exportEmployees(contentResolver: ContentResolver, outputStream: OutputStream, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Preparing backup..."
                val employees = repository.allEmployees.first()
                val attendance = repository.allAttendanceRecords.first()
                val dtr = repository.allDTRRecords.first()
                val requests = repository.allRequests.first()
                val announcements = repository.allAnnouncements.first()
                val attrition = repository.allAttrition.first()
                val da = repository.allDA.first()
                val dailyNotes = repository.allDailyNotes.first()
                val workPermits = repository.allPermits.first()
                val messages = repository.allMessages.first()

                val employeeBackups = employees.mapIndexed { index, emp ->
                    _syncProgress.value = (index.toFloat() / employees.size) * 0.5f
                    var base64: String? = null
                    if (!emp.profileImageUri.isNullOrEmpty()) {
                        try {
                            val uri = Uri.parse(emp.profileImageUri)
                            context.contentResolver.openInputStream(uri)?.use { input ->
                                base64 = Base64.encodeToString(input.readBytes(), Base64.NO_WRAP)
                            }
                        } catch (e: Exception) {
                            Log.e("AttendanceViewModel", "Image encoding failed for ${emp.employeeNo}", e)
                        }
                    }
                    EmployeeBackup(emp, base64)
                }

                val backupData = BackupData(
                    employees = employeeBackups,
                    attendanceRecords = attendance,
                    dailyTimeRecords = dtr,
                    requests = requests,
                    announcements = announcements,
                    attritionRecords = attrition,
                    daRecords = da,
                    dailyNotes = dailyNotes,
                    workPermits = workPermits,
                    messages = messages
                )

                val jsonString = json.encodeToString(backupData)
                withContext(Dispatchers.IO) {
                    outputStream.use { it.write(jsonString.toByteArray()) }
                }
                _syncStatus.value = "Backup successful"
                _syncProgress.value = null
                onComplete(true)
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Full Export failed", e)
                _syncStatus.value = "Backup failed"
                _syncProgress.value = null
                onComplete(false)
            }
        }
    }

    fun importEmployees(ctx: Context, inputStream: InputStream, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _syncStatus.value = "Reading backup file..."
                val jsonString = withContext(Dispatchers.IO) {
                    inputStream.bufferedReader().use { it.readText() }
                }
                val backupData = json.decodeFromString<BackupData>(jsonString)

                _syncStatus.value = "Clearing local database..."
                // Clear all tables before importing to ensure a exact match of the backup
                repository.clearAllAttendance()
                repository.clearAllDTR()
                repository.clearAllRequests()
                repository.clearAllDailyNotes()
                repository.clearAllAnnouncements()
                repository.clearAllMessages()
                repository.clearAllPermits()
                repository.clearAllDA()
                repository.clearAllAttrition()
                repository.clearAllEmployees()

                _syncStatus.value = "Importing employees..."
                // Import Employees and Photos
                backupData.employees.forEachIndexed { index, backup ->
                    _syncProgress.value = (index.toFloat() / backupData.employees.size) * 0.2f
                    var updatedEmp = backup.employee
                    // Restore profile image from base64 if available
                    if (!backup.profileImageBase64.isNullOrEmpty()) {
                        try {
                            val bytes = Base64.decode(backup.profileImageBase64, Base64.NO_WRAP)
                            val imagesDir = File(context.filesDir, "profile_images")
                            if (!imagesDir.exists()) imagesDir.mkdirs()
                            val fileName = "photo_${backup.employee.employeeNo?.replace("/", "_")}.jpg"
                            val file = File(imagesDir, fileName)
                            file.writeBytes(bytes)
                            updatedEmp = updatedEmp.copy(profileImageUri = Uri.fromFile(file).toString())
                        } catch (e: Exception) {
                            Log.e("AttendanceViewModel", "Image decoding failed during import", e)
                        }
                    }
                    repository.insertEmployee(updatedEmp)
                }

                _syncStatus.value = "Importing attendance..."
                // Import Attendance
                backupData.attendanceRecords.forEachIndexed { index, it -> 
                    _syncProgress.value = 0.2f + ((index.toFloat() / backupData.attendanceRecords.size) * 0.2f)
                    repository.insertAttendance(it) 
                }

                _syncStatus.value = "Importing DTR..."
                // Import DTR
                backupData.dailyTimeRecords.forEachIndexed { index, it -> 
                    _syncProgress.value = 0.4f + ((index.toFloat() / backupData.dailyTimeRecords.size) * 0.2f)
                    repository.insertDTR(it) 
                }

                _syncStatus.value = "Importing requests..."
                // Import Requests
                backupData.requests.forEachIndexed { index, it -> 
                    _syncProgress.value = 0.6f + ((index.toFloat() / backupData.requests.size) * 0.2f)
                    repository.insertRequest(it) 
                }

                _syncStatus.value = "Importing announcements..."
                // Import Announcements
                backupData.announcements.forEachIndexed { index, it -> 
                    _syncProgress.value = 0.8f + ((index.toFloat() / backupData.announcements.size) * 0.05f)
                    repository.insertAnnouncement(it) 
                }

                _syncStatus.value = "Importing attrition..."
                // Import Attrition
                backupData.attritionRecords.forEachIndexed { index, it ->
                    _syncProgress.value = 0.85f + ((index.toFloat() / backupData.attritionRecords.size) * 0.05f)
                    repository.insertAttrition(it)
                }

                _syncStatus.value = "Importing DA..."
                // Import DA
                backupData.daRecords.forEachIndexed { index, it ->
                    _syncProgress.value = 0.9f + ((index.toFloat() / backupData.daRecords.size) * 0.04f)
                    repository.insertDA(it)
                }

                _syncStatus.value = "Importing Daily Notes..."
                backupData.dailyNotes.forEachIndexed { index, it ->
                    _syncProgress.value = 0.94f + ((index.toFloat() / backupData.dailyNotes.size) * 0.03f)
                    repository.insertDailyNote(it)
                }

                _syncStatus.value = "Importing Permits..."
                backupData.workPermits.forEachIndexed { index, it ->
                    _syncProgress.value = 0.97f + ((index.toFloat() / backupData.workPermits.size) * 0.01f)
                    repository.insertPermit(it)
                }

                _syncStatus.value = "Importing Messages..."
                backupData.messages.forEachIndexed { index, it ->
                    _syncProgress.value = 0.98f + ((index.toFloat() / backupData.messages.size) * 0.02f)
                    repository.insertMessage(it)
                }

                _syncStatus.value = "Import complete"
                _syncProgress.value = null
                onComplete(true)
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "JSON Import failed", e)
                _syncStatus.value = "Import failed"
                _syncProgress.value = null
                onComplete(false)
            }
        }
    }

    // Stubs removed - implemented above

    fun resetAndRefreshCloud(onComplete: (Boolean) -> Unit) { onComplete(true) }
    fun saveDTR(empId: String, month: String, data: String, onComplete: (Boolean) -> Unit) {
        uploadEmployeeData(empId, onComplete)
    }
    fun stopSyncServer() {}
    fun discoverSyncPeers() {}
    fun startSyncServer() {}
    fun syncWithPeer(device: android.net.nsd.NsdServiceInfo, onComplete: (Boolean) -> Unit) {}
    val discoveredDevices = MutableStateFlow<List<android.net.nsd.NsdServiceInfo>>(emptyList()).asStateFlow()

    fun repairEmployeeLinks() {
        viewModelScope.launch(Dispatchers.IO) {
            _syncStatus.value = "Repairing record links..."
            repository.repairEmployeeLinks()
            _syncStatus.value = "Repair complete"
        }
    }

    fun emergencyLogDump() {
        viewModelScope.launch {
            try {
                supabaseSync.retrieveAllActivityLogs()
                Log.d("EMERGENCY_RECOVERY", "Dumping activity logs for recovery...")
                val logs = repository.allActivityLogs.first()
                logs.forEach { log ->
                    Log.d("EMERGENCY_RECOVERY", "LOG: ${log.timestamp} - ${log.action} - ${log.details}")
                }
            } catch (e: Exception) {
                Log.e("EMERGENCY_RECOVERY", "Log dump failed: ${e.message}")
            }
        }
    }

    fun emergencyRecoverFromLogs(onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            var count = 0
            try {
                supabaseSync.retrieveAllActivityLogs()
                val logs = repository.allActivityLogs.first()
                
                val recoveredMap = mutableMapOf<String, String>() // ID to Name
                
                // Parse logs backwards to find deleted employees
                logs.filter { it.action == "Delete Employee" || it.action == "Update Employee" || it.action == "Add Employee" }
                    .forEach { log ->
                        val details = log.details ?: ""
                        val name = details.substringAfter("Name: ").substringBefore(", ID:")
                        val id = details.substringAfter("ID: ").trim()
                        
                        if (id.isNotEmpty() && !recoveredMap.containsKey(id)) {
                            recoveredMap[id] = name
                        }
                    }
                
                // Also check attendance logs for IDs that might not have a name yet
                logs.filter { it.action == "Mark Attendance" }.forEach { log ->
                    val id = log.details?.substringAfter("Emp: ")?.substringBefore(", Date:")?.trim() ?: ""
                    if (id.isNotEmpty() && !recoveredMap.containsKey(id)) {
                        recoveredMap[id] = "Recovered ($id)"
                    }
                }

                val currentEmployees = repository.allEmployees.first().map { it.id }.toSet()
                
                recoveredMap.forEach { (id, name) ->
                    if (!currentEmployees.contains(id)) {
                        val names = name.split(" ")
                        val firstName = names.getOrNull(0) ?: "Recovered"
                        val lastName = if (names.size > 1) names.last() else ""
                        
                        val emp = Employee(
                            id = id,
                            employeeNo = id,
                            firstName = firstName,
                            lastName = lastName
                        )
                        repository.insertEmployee(emp)
                        supabaseSync.uploadEmployeeData(emp.id)
                        count++
                    }
                }
                _syncStatus.value = "Recovered $count employees from logs"
                onComplete(count)
            } catch (e: Exception) {
                Log.e("EMERGENCY_RECOVERY", "Recovery failed: ${e.message}")
                onComplete(-1)
            }
        }
    }

    fun getPositionColor(position: String): Int {
        return prefs.getInt("pos_color_$position", 0)
    }

    fun setPositionColor(position: String, color: Int) {
        prefs.edit().putInt("pos_color_$position", color).apply()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                supabaseSync.saveSetting("pos_color_$position", color.toString())
            } catch (e: Exception) {}
        }
    }

    fun getPositionFontColor(position: String): Int {
        return prefs.getInt("pos_font_color_$position", 0)
    }

    fun setPositionFontColor(position: String, color: Int) {
        prefs.edit().putInt("pos_font_color_$position", color).apply()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                supabaseSync.saveSetting("pos_font_color_$position", color.toString())
            } catch (e: Exception) {}
        }
    }

    fun getPortalUsername(): String = prefs.getString("portal_username", "smtp_smckmall_058184") ?: "smtp_smckmall_058184"
    fun getPortalPassword(): String = prefs.getString("portal_password", "cJEjZn") ?: "cJEjZn"

    class Factory(
        private val repository: AttendanceRepository,
        private val authService: GoogleAuthService,
        private val supabaseSync: SupabaseSyncManager,
        private val supabaseStorage: SupabaseStorageSyncManager,
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AttendanceViewModel(repository, authService, supabaseSync, supabaseStorage, context) as T
        }
    }
}
