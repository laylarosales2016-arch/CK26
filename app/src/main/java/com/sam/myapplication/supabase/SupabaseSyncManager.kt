package com.sam.myapplication.supabase

import com.sam.myapplication.data.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.decodeRecord
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.serialization.json.*

class SupabaseSyncManager(
    private val repository: AttendanceRepository
) {
    private val client = SupabaseModule.client
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
        coerceInputValues = true 
        explicitNulls = false
    }

    private var chatChannel: io.github.jan.supabase.realtime.RealtimeChannel? = null

    fun subscribeToChatRealtime(scope: CoroutineScope, currentUserId: String?) {
        try {
            // Close existing channel if any
            chatChannel?.let { 
                scope.launch { try { it.unsubscribe() } catch(e: Exception) {} }
            }

            Log.d("SupabaseSync", "Setting up chat realtime subscription for $currentUserId")
            val channel = client.realtime.channel("chat_messages_v2_${currentUserId ?: "anon"}")
            chatChannel = channel
            
            val flow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "chat_messages"
            }
            
            flow.onEach { action ->
                try {
                    when (action) {
                        is PostgresAction.Insert -> {
                            val record = action.record
                            Log.d("SupabaseSync", "Realtime message received (Insert): $record")
                            val newMessage = json.decodeFromJsonElement<ChatMessage>(record)
                            if (newMessage.receiverId == "group" || newMessage.receiverId == currentUserId || newMessage.senderId == currentUserId) {
                                repository.insertMessage(newMessage)
                            }
                        }
                        is PostgresAction.Update -> {
                            val record = action.record
                            Log.d("SupabaseSync", "Realtime message received (Update): $record")
                            val updatedMessage = json.decodeFromJsonElement<ChatMessage>(record)
                            if (updatedMessage.receiverId == "group" || updatedMessage.receiverId == currentUserId || updatedMessage.senderId == currentUserId) {
                                repository.insertMessage(updatedMessage) // onConflict = REPLACE handles update
                            }
                        }
                        is PostgresAction.Delete -> {
                            val oldRecord = action.oldRecord
                            Log.d("SupabaseSync", "Realtime message received (Delete): $oldRecord")
                            val messageId = oldRecord["id"]?.jsonPrimitive?.longOrNull
                            if (messageId != null) {
                                repository.deleteMessageById(messageId)
                            }
                        }
                        else -> {}
                    }
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Error handling realtime message: ${e.message}", e)
                }
            }.launchIn(scope)

            scope.launch {
                try {
                    channel.subscribe()
                    Log.d("SupabaseSync", "Chat channel subscribed successfully")
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Subscription error: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Failed to setup chat realtime: ${e.message}")
        }
    }

    suspend fun sendChatMessage(message: ChatMessage) {
        try {
            // Remove 'id' if it's a dummy value (0) to let Supabase generate it
            val data = json.encodeToJsonElement(message).jsonObject.toMutableMap().apply {
                if (message.id == 0L) remove("id")
            }.let { JsonObject(it) }
            
            val response = client.postgrest["chat_messages"].insert(data) {
                select()
            }
            
            val savedMessage = response.decodeSingle<ChatMessage>()
            repository.insertMessage(savedMessage)
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Failed to send chat message: ${e.message}")
            throw e
        }
    }

    suspend fun updateChatMessage(messageId: Long, newText: String) {
        try {
            val updateData = buildJsonObject { put("message", newText) }
            client.postgrest["chat_messages"].update(updateData) {
                filter { eq("id", messageId) }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Failed to update chat message: ${e.message}")
            throw e
        }
    }

    suspend fun deleteChatMessage(messageId: Long) {
        try {
            client.postgrest["chat_messages"].delete {
                filter { eq("id", messageId) }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Failed to delete chat message: ${e.message}")
            throw e
        }
    }

    suspend fun uploadEmployeeData(employeeId: String) {
        try {
            val emp = repository.allEmployees.first().find { it.id == employeeId } ?: return
            val empNo = emp.employeeNo ?: employeeId
            
            Log.d("SupabaseSync", "Uploading data for $empNo (ID: $employeeId)")

            // 1. Save Employee
            val empData = json.encodeToJsonElement(emp).jsonObject.toMutableMap().apply {
                put("employee_no", JsonPrimitive(empNo))
                // Ensure critical fields are not null for Supabase constraints
                put("payroll_access_code", JsonPrimitive(emp.payrollAccessCode ?: "FISC"))
                put("payroll_username", JsonPrimitive(emp.payrollUsername ?: empNo))
                put("payroll_password", JsonPrimitive(emp.payrollPassword ?: ""))
                
                // EXPLICIT FIELD HANDLING
                put("is_certified", JsonPrimitive(emp.isCertified ?: false))
                put("is_admin", JsonPrimitive(emp.isAdmin ?: false))
                put("is_resigned", JsonPrimitive(emp.isResigned ?: false))
                put("is_hidden_from_scheduler", JsonPrimitive(emp.isHiddenFromScheduler))
                put("row_color", JsonPrimitive(emp.rowColor))
                put("font_color", JsonPrimitive(emp.fontColor))
                put("scheduler_position", JsonPrimitive(emp.schedulerPosition))
                put("scheduler_row_color", JsonPrimitive(emp.schedulerRowColor))
                put("scheduler_font_color", JsonPrimitive(emp.schedulerFontColor))
                
                // Handle list serialization explicitly
                put("custom_offences", json.encodeToJsonElement(emp.customOffences ?: emptyList<CustomOffence>()))
                put("certified_positions", json.encodeToJsonElement(emp.certifiedPositions ?: emptyList<String>()))

                // We keep the employee_no as the primary link. 
                // Removing 'id' allows Supabase to use its own PK if configured,
                // but we conflict on 'employee_no'.
                remove("id") 
            }.let { JsonObject(it) }

            try {
                Log.d("SupabaseSync", "Upserting employee: $empNo")
                client.postgrest["employees"].upsert(empData) { onConflict = "employee_no" }
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Employee upsert failed for $empNo: ${e.message}")
                throw e // Critical failure
            }

            // 2. Save Attendance
            val att = repository.getAttendanceForEmployee(employeeId).first()
            if (att.isNotEmpty()) {
                val attData = att.map { record ->
                    json.encodeToJsonElement(record).jsonObject.toMutableMap().apply {
                        put("employee_id", JsonPrimitive(empNo))
                        remove("id") // Let Supabase generate ID
                    }.let { JsonObject(it) }
                }
                try {
                    Log.d("SupabaseSync", "Upserting ${attData.size} attendance records for $empNo")
                    client.postgrest["attendance_records"].upsert(attData) { 
                        onConflict = "employee_id,date,status" 
                    }
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Attendance upsert failed for $empNo: ${e.message}")
                    // Non-critical, continue
                }
            }

            // 3. Save DTR
            val dtr = repository.getDTRForEmployee(employeeId).first()
            if (dtr.isNotEmpty()) {
                val dtrData = dtr.map { record ->
                    json.encodeToJsonElement(record).jsonObject.toMutableMap().apply {
                        put("employee_id", JsonPrimitive(empNo))
                        remove("id")
                    }.let { JsonObject(it) }
                }
                try {
                    Log.d("SupabaseSync", "Upserting ${dtrData.size} DTR records for $empNo")
                    client.postgrest["daily_time_records"].upsert(dtrData) { 
                        onConflict = "employee_id,date" 
                    }
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "DTR upsert failed for $empNo: ${e.message}")
                }
            }

            // 4. Save Requests
            val req = repository.getRequestsForEmployee(employeeId).first()
            if (req.isNotEmpty()) {
                val reqData = req
                    .distinctBy { "${it.employeeId}_${it.date}_${it.type}_${it.createdAt}" }
                    .map { record ->
                        json.encodeToJsonElement(record).jsonObject.toMutableMap().apply {
                            put("employee_id", JsonPrimitive(empNo))
                            remove("id")
                        }.let { JsonObject(it) }
                    }
                try {
                    Log.d("SupabaseSync", "Upserting ${reqData.size} requests for $empNo")
                    client.postgrest["requests"].upsert(reqData) { 
                        onConflict = "employee_id,date,type,created_at" 
                    }
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Requests upsert failed for $empNo: ${e.message}")
                }
            }
            Log.d("SupabaseSync", "Upload successful for $empNo")
        } catch (e: Exception) {
            Log.e("SupabaseSync", "UPLOAD FAILED for $employeeId: ${e.message}")
            throw e
        }
    }

    suspend fun deleteEmployeeRemote(employeeId: String, employeeNo: String?) {
        try {
            val empNo = employeeNo ?: employeeId
            
            Log.d("SupabaseSync", "Permanently deleting $empNo from server...")
            
            client.postgrest["employees"].delete { filter { eq("employee_no", empNo) } }
            client.postgrest["attendance_records"].delete { filter { eq("employee_id", empNo) } }
            client.postgrest["daily_time_records"].delete { filter { eq("employee_id", empNo) } }
            client.postgrest["requests"].delete { filter { eq("employee_id", empNo) } }
            client.postgrest["chat_messages"].delete { 
                filter { 
                    or {
                        eq("sender_id", empNo)
                        eq("receiver_id", empNo)
                    }
                }
            }
            Log.d("SupabaseSync", "Successfully deleted $empNo from server")
        } catch (e: Exception) {
            Log.e("SupabaseSync", "DELETE REMOTE FAILED: ${e.message}")
        }
    }

    suspend fun deleteDTRRemote(employeeNo: String, date: String) {
        try {
            client.postgrest["daily_time_records"].delete {
                filter {
                    eq("employee_id", employeeNo)
                    eq("date", date)
                }
            }
            Log.d("SupabaseSync", "Deleted DTR for $employeeNo on $date from server")
        } catch (e: Exception) {
            Log.e("SupabaseSync", "DELETE DTR REMOTE FAILED: ${e.message}")
        }
    }

    suspend fun deleteAttendanceRemote(employeeNo: String, date: String) {
        try {
            client.postgrest["attendance_records"].delete {
                filter {
                    eq("employee_id", employeeNo)
                    eq("date", date)
                }
            }
            Log.d("SupabaseSync", "Deleted Attendance for $employeeNo on $date from server")
        } catch (e: Exception) {
            Log.e("SupabaseSync", "DELETE ATTENDANCE REMOTE FAILED: ${e.message}")
        }
    }

    suspend fun syncEmployeeDataSmart(employeeId: String) {
        try {
            // 1. First Upload local data (pushes any offline changes to server)
            uploadEmployeeData(employeeId)
            
            // 2. Then Download remote data
            val empLocal = repository.getEmployeeById(employeeId) ?: return
            val employeeNo = empLocal.employeeNo ?: employeeId
            
            Log.d("SupabaseSync", "Smart syncing data for $employeeNo")
            val remoteEmp = client.postgrest["employees"].select { filter { eq("employee_no", employeeNo) } }.decodeSingle<Employee>()
            repository.insertEmployee(remoteEmp.copy(id = employeeId))

            // Download records using "Insert with Replace" which Room handles via @Insert(onConflict = OnConflictStrategy.REPLACE)
            // Since we uploaded first, the server should have our latest. 
            // If the server has even NEWER data from elsewhere, it will overwrite ours locally, which is correct for a "sync".
            
            // Attendance
            try {
                val records = client.postgrest["attendance_records"].select { filter { eq("employee_id", employeeNo) } }.decodeList<AttendanceRecord>()
                records.forEach { repository.insertAttendance(it.copy(id = 0, employeeId = employeeId)) }
            } catch (e: Exception) { Log.e("SupabaseSync", "Smart Attendance failed: ${e.message}") }

            // DTR - Safe Merge
            try {
                val remoteDTR = client.postgrest["daily_time_records"].select { filter { eq("employee_id", employeeNo) } }.decodeList<DailyTimeRecord>()
                remoteDTR.forEach { remoteRec ->
                    val localRec = repository.getDTRForEmployeeByDate(employeeId, remoteRec.date).first()
                    if (localRec == null) {
                        repository.insertDTR(remoteRec.copy(id = 0, employeeId = employeeId))
                    } else {
                        // MERGE: If remote has more data (e.g. timeOut), update local
                        if (remoteRec.timeOut != null && localRec.timeOut == null) {
                            repository.insertDTR(remoteRec.copy(id = localRec.id, employeeId = employeeId))
                        }
                    }
                }
            } catch (e: Exception) { Log.e("SupabaseSync", "Smart DTR failed: ${e.message}") }

            // Requests
            try {
                val records = client.postgrest["requests"].select { filter { eq("employee_id", employeeNo) } }.decodeList<AttendanceRequest>()
                records.forEach { repository.insertRequest(it.copy(id = 0, employeeId = employeeId)) }
            } catch (e: Exception) { Log.e("SupabaseSync", "Smart Requests failed: ${e.message}") }

            syncAnnouncements()
            repository.repairEmployeeLinks()
            Log.d("SupabaseSync", "Smart sync complete for $employeeNo")
        } catch (e: Exception) {
            Log.e("SupabaseSync", "SMART SYNC FAILED for $employeeId: ${e.message}")
        }
    }

    suspend fun retrieveEmployeeData(employeeNo: String) {
        try {
            Log.d("SupabaseSync", "Retrieving data for $employeeNo")
            val emp = client.postgrest["employees"].select { filter { eq("employee_no", employeeNo) } }.decodeSingleOrNull<Employee>()
            
            if (emp == null) {
                Log.w("SupabaseSync", "Employee $employeeNo not found on server. Aborting destructive local clear.")
                return
            }

            val existing = repository.getEmployeeByNo(employeeNo)
            val targetId = existing?.id ?: employeeNo
            repository.insertEmployee(emp.copy(id = targetId))
            repository.deleteAllAttendanceForEmployee(targetId)
            repository.deleteAllDTRForEmployee(targetId)
            repository.deleteAllRequestsForEmployee(targetId)
            try {
                val records = client.postgrest["attendance_records"].select { filter { eq("employee_id", employeeNo) } }.decodeList<AttendanceRecord>()
                records.forEach { repository.insertAttendance(it.copy(id = 0, employeeId = targetId)) }
            } catch (e: Exception) {}
            try {
                val records = client.postgrest["daily_time_records"].select { filter { eq("employee_id", employeeNo) } }.decodeList<DailyTimeRecord>()
                records.forEach { repository.insertDTR(it.copy(id = 0, employeeId = targetId)) }
            } catch (e: Exception) {}
            try {
                val records = client.postgrest["requests"].select { filter { eq("employee_id", employeeNo) } }.decodeList<AttendanceRequest>()
                records.forEach { repository.insertRequest(it.copy(id = 0, employeeId = targetId)) }
            } catch (e: Exception) {}
            syncAnnouncements()
        } catch (e: Exception) {
            Log.e("SupabaseSync", "RETRIEVE FAILED for $employeeNo: ${e.message}")
        }
    }

    suspend fun retrieveAllData() {
        try {
            Log.d("SupabaseSync", "Starting safety-first bulk retrieval...")
            
            // NEW SAFETY STEP: Upload everything local first
            try {
                Log.d("SupabaseSync", "Pre-sync upload started...")
                majorUpload()
                Log.d("SupabaseSync", "Pre-sync upload completed successfully")
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Pre-sync upload failed, but proceeding with caution: ${e.message}")
            }

            // 1. Fetch remote data
            val remoteEmps = client.postgrest["employees"].select().decodeList<Employee>()
            val allAttendance = client.postgrest["attendance_records"].select().decodeList<AttendanceRecord>()
            val allDTR = client.postgrest["daily_time_records"].select().decodeList<DailyTimeRecord>()
            val allRequests = client.postgrest["requests"].select().decodeList<AttendanceRequest>()

            // If we reached here without exception, clearing local is safe as we have fresh data
            repository.clearAllAttendance()
            repository.clearAllDTR()
            repository.clearAllRequests()
            repository.clearAllEmployees()
            
            remoteEmps.forEach { emp ->
                val empNo = emp.employeeNo ?: return@forEach
                repository.insertEmployee(emp.copy(id = empNo))
            }

            allAttendance.forEach { record ->
                repository.insertAttendance(record.copy(id = 0)) 
            }

            allDTR.forEach { record ->
                repository.insertDTR(record.copy(id = 0))
            }

            allRequests.forEach { record ->
                repository.insertRequest(record.copy(id = 0))
            }

            // 6. Announcements
            try {
                syncAnnouncements()
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Announcements sync failed during retrieval: ${e.message}")
            }

            // 7. Daily Summary Notes
            try {
                syncDailyNotes()
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Daily notes sync failed during retrieval: ${e.message}")
            }

            // 9. Final Link Repair
            repository.repairEmployeeLinks()

            // 8. Work Permits
            try {
                val remotePermits = retrieveWorkPermits()
                if (remotePermits.isNotEmpty()) {
                    repository.insertPermits(remotePermits)
                    Log.d("SupabaseSync", "Retrieved ${remotePermits.size} work permits")
                }
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Work permits sync failed during retrieval: ${e.message}")
            }

            // 9. Attrition Records
            try {
                syncAttrition()
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Attrition sync failed during retrieval: ${e.message}")
            }

            // 10. DA Records
            try {
                syncDA()
            } catch (e: Exception) {
                Log.e("SupabaseSync", "DA sync failed during retrieval: ${e.message}")
            }

            // 11. Shift Templates
            try {
                val remoteTemplates = client.postgrest["shift_templates"].select().decodeList<ShiftTemplate>()
                remoteTemplates.forEach { repository.insertShiftTemplate(it) }
                Log.d("SupabaseSync", "Retrieved ${remoteTemplates.size} shift templates")
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Shift templates sync failed during retrieval: ${e.message}")
            }

            // 12. Employee Schedules
            try {
                val remoteSchedules = client.postgrest["employee_schedules"].select().decodeList<EmployeeSchedule>()
                remoteSchedules.forEach { repository.insertSchedule(it) }
                Log.d("SupabaseSync", "Retrieved ${remoteSchedules.size} schedules")
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Schedules sync failed during retrieval: ${e.message}")
            }

            // 13. Chat Messages
            try {
                syncChatMessages()
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Chat sync failed during retrieval: ${e.message}")
            }

            Log.d("SupabaseSync", "Bulk retrieval complete.")
        } catch (e: Exception) {
            Log.e("SupabaseSync", "CRITICAL SYNC FAILURE: ${e.message}")
            throw e
        }
    }

    suspend fun retrieveAccounts() {
        try {
            client.postgrest["employees"].select().decodeList<Employee>().forEach { 
                repository.insertEmployee(it.copy(id = it.employeeNo!!)) 
            }
        } catch (e: Exception) {}
    }
    
    suspend fun majorUpload() {
        repository.allEmployees.first().forEach { try { uploadEmployeeData(it.id) } catch (e: Exception) {} }
        // Also sync other shared data
        try { syncAttrition() } catch (e: Exception) {}
        try { syncDA() } catch (e: Exception) {}
        try { syncShiftTemplates() } catch (e: Exception) {}
        try { syncSchedules() } catch (e: Exception) {}
        try { syncChatMessages() } catch (e: Exception) {}
    }

    suspend fun retrieveAllRequests() {
        try {
            val remoteRequests = client.postgrest["requests"].select().decodeList<AttendanceRequest>()
            repository.clearAllRequests()
            remoteRequests.forEach { req ->
                repository.insertRequest(req.copy(id = 0)) 
            }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "RETRIEVE ALL REQUESTS FAILED: ${e.message}")
            throw e
        }
    }

    suspend fun retrieveAllAttendance() {
        try {
            val remoteAttendance = client.postgrest["attendance_records"].select().decodeList<AttendanceRecord>()
            repository.clearAllAttendance()
            remoteAttendance.forEach { record ->
                repository.insertAttendance(record.copy(id = 0))
            }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "RETRIEVE ALL ATTENDANCE FAILED: ${e.message}")
            throw e
        }
    }

    suspend fun syncAnnouncements() {
        try {
            // 1. Upload local announcements
            val localAnnouncements = repository.allAnnouncements.first()
            if (localAnnouncements.isNotEmpty()) {
                val data = localAnnouncements
                    .map { ann ->
                        // Clean data: use snake_case keys manually to be 100% sure
                        buildJsonObject {
                            put("title", ann.title)
                            put("content", ann.content)
                            put("target_employee_id", ann.targetEmployeeId)
                            put("created_at", ann.createdAt)
                            put("is_read", ann.isRead)
                        }
                    }
                    .distinctBy { it["title"].toString() + it["created_at"].toString() }
                
                try {
                    client.postgrest["announcements"].upsert(data) { 
                        onConflict = "title,created_at" 
                    }
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Announcements upload failed: ${e.message}")
                    // Continue to download even if upload fails
                }
            }

            // 2. Download remote announcements
            val remoteAnnouncements = client.postgrest["announcements"].select().decodeList<Announcement>()
            
            remoteAnnouncements.forEach { ann ->
                repository.insertAnnouncement(ann.copy(id = 0)) 
            }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "SYNC ANNOUNCEMENTS FAILED: ${e.message}")
        }
    }

    suspend fun syncDailyNotes() {
        try {
            // 1. Upload local notes
            val localNotes = repository.allDailyNotes.first()
            if (localNotes.isNotEmpty()) {
                client.postgrest["daily_summary_notes"].upsert(localNotes) {
                    onConflict = "date"
                }
            }

            // 2. Download remote notes
            val remoteNotes = client.postgrest["daily_summary_notes"].select().decodeList<DailySummaryNote>()
            remoteNotes.forEach { repository.insertDailyNote(it) }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "SYNC DAILY NOTES FAILED: ${e.message}")
        }
    }

    suspend fun syncAttendanceForMonth(yearMonth: String) {
        try {
            Log.d("SupabaseSync", "Syncing attendance for month: $yearMonth")
            // Fetch records where date starts with YYYY-MM
            val remoteRecords = client.postgrest["attendance_records"]
                .select { filter { like("date", "$yearMonth%") } }
                .decodeList<AttendanceRecord>()
            
            if (remoteRecords.isNotEmpty()) {
                val employees = repository.allEmployees.first()
                val empNoToIdMap = employees.associateBy({ it.employeeNo }, { it.id })
                
                remoteRecords.forEach { record ->
                    val targetId = empNoToIdMap[record.employeeId] ?: record.employeeId
                    repository.insertAttendance(record.copy(id = 0, employeeId = targetId))
                }
                Log.d("SupabaseSync", "Synced ${remoteRecords.size} attendance records for $yearMonth")
            }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "SYNC ATTENDANCE FOR MONTH FAILED for $yearMonth: ${e.message}")
        }
    }

    suspend fun syncAttrition() {
        try {
            // 1. Upload local attrition
            val localAttrition = repository.allAttrition.first()
            if (localAttrition.isNotEmpty()) {
                client.postgrest["attrition_records"].upsert(localAttrition) {
                    onConflict = "id"
                }
            }

            // 2. Download remote attrition
            val remoteAttrition = client.postgrest["attrition_records"].select().decodeList<AttritionRecord>()
            remoteAttrition.forEach { repository.insertAttrition(it) }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "SYNC ATTRITION FAILED: ${e.message}")
        }
    }

    suspend fun syncDA() {
        try {
            // 1. Upload local DA
            val localDA = repository.allDA.first()
            if (localDA.isNotEmpty()) {
                client.postgrest["da_records"].upsert(localDA) {
                    onConflict = "id"
                }
            }

            // 2. Download remote DA
            val remoteDA = client.postgrest["da_records"].select().decodeList<DARecord>()
            remoteDA.forEach { repository.insertDA(it) }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "SYNC DA FAILED: ${e.message}")
        }
    }

    suspend fun uploadActivityLog(log: ActivityLog) {
        try {
            client.postgrest["activity_logs"].insert(log)
            Log.d("SupabaseSync", "Activity log uploaded: ${log.action}")
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Activity Log upload failed: ${e.message}")
        }
    }

    suspend fun retrieveAllActivityLogs() {
        try {
            val logs = client.postgrest["activity_logs"].select {
                order("timestamp", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                limit(200)
            }.decodeList<ActivityLog>()
            repository.insertActivityLogs(logs)
            Log.d("SupabaseSync", "Retrieved ${logs.size} activity logs")
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Activity Logs retrieval failed: ${e.message}")
        }
    }

    suspend fun deleteAttritionRemote(id: Int) {
        try {
            client.postgrest["attrition_records"].delete { filter { eq("id", id) } }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Failed to delete attrition remotely: ${e.message}")
        }
    }

    suspend fun deleteDARemote(id: Int) {
        try {
            client.postgrest["da_records"].delete { filter { eq("id", id) } }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Failed to delete DA remotely: ${e.message}")
        }
    }

    suspend fun saveTargetHeadcount(count: Int) {
        saveSetting("target_headcount", count.toString())
    }

    suspend fun saveExcludedIds(ids: Set<String>) {
        saveSetting("excluded_ids", ids.joinToString(","))
    }

    suspend fun syncWorkPermits(permits: List<WorkPermit>) {
        try {
            // Upsert one by one to avoid payload size limits
            // IMPORTANT: We EXCLUDE cached_html from Supabase sync to save storage space and bandwidth
            permits.forEach { permit ->
                try {
                    val permitData = json.encodeToJsonElement(permit).jsonObject.toMutableMap().apply {
                        remove("cached_html")
                    }.let { JsonObject(it) }
                    
                    client.postgrest["work_permits"].upsert(permitData) {
                        onConflict = "reference_number" 
                    }
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Single permit upsert failed for ${permit.referenceNumber}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Work permit sync failed: ${e.message}")
        }
    }

    suspend fun retrieveWorkPermits(): List<WorkPermit> {
        return try {
            client.postgrest["work_permits"].select().decodeList<WorkPermit>()
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Work permit retrieval failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun retrieveTargetHeadcount(): Int? {
        return try {
            val result = client.postgrest["settings"].select { 
                filter { eq("key", "target_headcount") } 
            }.decodeSingleOrNull<Map<String, String>>()
            result?.get("value")?.toIntOrNull()
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Failed to retrieve headcount: ${e.message}")
            null
        }
    }

    suspend fun retrieveExcludedIds(): Set<String>? {
        return try {
            val result = client.postgrest["settings"].select { 
                filter { eq("key", "excluded_ids") } 
            }.decodeSingleOrNull<Map<String, String>>()
            result?.get("value")?.split(",")?.filter { it.isNotBlank() }?.toSet()
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Failed to retrieve excluded IDs: ${e.message}")
            null
        }
    }

    suspend fun saveMedalCountdownStart(date: String) {
        saveSetting("medal_countdown_start", date)
    }

    suspend fun saveSetting(key: String, value: String) {
        try {
            val data = mapOf("key" to key, "value" to value)
            client.postgrest["settings"].upsert(data) { onConflict = "key" }
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Failed to save setting $key: ${e.message}")
        }
    }

    suspend fun getSetting(key: String): String? {
        return try {
            val result = client.postgrest["settings"].select { 
                filter { eq("key", key) } 
            }.decodeSingleOrNull<Map<String, String>>()
            result?.get("value")
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Failed to retrieve setting $key: ${e.message}")
            null
        }
    }

    suspend fun getAllSettings(): List<Map<String, String>> {
        return try {
            client.postgrest["settings"].select().decodeList<Map<String, String>>()
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Failed to retrieve all settings: ${e.message}")
            emptyList()
        }
    }

    suspend fun syncShiftTemplates() {
        try {
            // Upload local templates
            val localTemplates = repository.allShiftTemplatesFirst()
            if (localTemplates.isNotEmpty()) {
                client.postgrest["shift_templates"].upsert(localTemplates) {
                    onConflict = "time_range"
                }
            }
            
            // Download remote templates
            val remoteTemplates = client.postgrest["shift_templates"].select().decodeList<ShiftTemplate>()
            remoteTemplates.forEach { repository.insertShiftTemplate(it) }
        } catch (e: Exception) {
            Log.e("SupabaseSyncManager", "Failed to sync shift templates: ${e.message}")
        }
    }

    suspend fun downloadSchedules() {
        try {
            val employees = repository.allEmployees.first()
            val remoteSchedules = client.postgrest["employee_schedules"].select().decodeList<EmployeeSchedule>()
            remoteSchedules.forEach { remoteSched ->
                val emp = employees.find { it.id == remoteSched.employeeId }
                    ?: employees.find { it.employeeNo == remoteSched.employeeId }
                val localId = emp?.id ?: remoteSched.employeeId
                repository.insertSchedule(remoteSched.copy(employeeId = localId))
            }
        } catch (e: Exception) {
            Log.e("SupabaseSyncManager", "Failed to download schedules: ${e.message}")
        }
    }

    suspend fun uploadSchedules() {
        try {
            val employees = repository.allEmployees.first()
            val localSchedules = repository.allSchedulesFirst()
            if (localSchedules.isNotEmpty()) {
                val mappedSchedules = localSchedules.map { sched ->
                    val emp = employees.find { it.id == sched.employeeId }
                    val remoteId = emp?.id ?: emp?.employeeNo ?: sched.employeeId
                    sched.copy(employeeId = remoteId)
                }
                client.postgrest["employee_schedules"].upsert(mappedSchedules) {
                    onConflict = "employee_id,date"
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSyncManager", "Failed to upload schedules: ${e.message}")
        }
    }

    suspend fun syncSchedules() {
        downloadSchedules()
        uploadSchedules()
    }

    suspend fun syncChatMessages() {
        try {
            // 1. Download only LATEST 100 remote messages
            val remoteMessages = client.postgrest["chat_messages"]
                .select {
                    order("timestamp", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                    limit(100)
                }.decodeList<ChatMessage>()
            
            val remoteIds = remoteMessages.map { it.id }.toSet()
            
            // 2. Identify local messages that SHOULD be on the server but AREN'T
            // ONLY check messages within the range of IDs we just fetched
            val minRemoteId = remoteMessages.minOfOrNull { it.id } ?: 0L
            val maxRemoteId = remoteMessages.maxOfOrNull { it.id } ?: Long.MAX_VALUE
            
            val localMessages = repository.allMessages.first()
            val toDeleteLocally = localMessages.filter { 
                it.id in minRemoteId..maxRemoteId && !remoteIds.contains(it.id) 
            }
            
            // 3. Prune only confirmed deleted messages
            toDeleteLocally.forEach { 
                Log.d("SupabaseSync", "Pruning deleted message: ${it.id}")
                repository.deleteMessageById(it.id) 
            }

            // 4. Save/Update remote messages
            remoteMessages.forEach { repository.insertMessage(it) }
            
        } catch (e: Exception) {
            Log.e("SupabaseSyncManager", "Failed to sync chat messages: ${e.message}")
        }
    }
}
