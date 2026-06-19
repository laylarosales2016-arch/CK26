package com.sam.myapplication.ui

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import android.webkit.JavascriptInterface
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.animation.*
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebResourceRequest
import android.webkit.WebResourceError
import android.webkit.SslErrorHandler
import android.net.http.SslError
import android.webkit.WebResourceResponse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isUnspecified
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.sam.myapplication.R
import com.sam.myapplication.data.AttendanceStatus
import com.sam.myapplication.data.Employee
import com.sam.myapplication.data.AttendanceRecord
import com.sam.myapplication.data.AttendanceRequest
import com.sam.myapplication.data.DailyTimeRecord
import com.sam.myapplication.data.WorkPermit
import com.sam.myapplication.data.AttritionRecord
import com.sam.myapplication.data.DARecord
import com.sam.myapplication.data.Announcement
import com.sam.myapplication.data.DailySummaryNote
import com.sam.myapplication.data.RequestType
import android.net.nsd.NsdServiceInfo
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

val LocalScale = compositionLocalOf { 1f }

@Composable
fun Int.s(): Dp = (this * LocalScale.current).dp

@Composable
fun Int.spS(): TextUnit = (this * LocalScale.current).sp

fun getPositionRank(pos: String?): Int {
    return when (pos) {
        "Coordinator" -> 1
        "Team Leader" -> 2
        "Cashier" -> 3
        "Dispatch" -> 4
        "SS" -> 5
        "CIC" -> 6
        "Assembler" -> 7
        "Noodle", "Noodles" -> 8
        "Backup" -> 9
        "Fyer", "Fryer" -> 10
        "DJ" -> 11
        "Manager" -> 12
        "Assistant Manager" -> 13
        "Senior Crew" -> 14
        "Administrator" -> 15
        "Excrew" -> 1000
        else -> 999
    }
}

fun formatAwolDates(awolString: String?): String {
    if (awolString.isNullOrBlank()) return "---"
    val dates = awolString.split(",").mapNotNull { 
        try { LocalDate.parse(it.trim()) } catch(e: Exception) { null }
    }.sorted()
    
    if (dates.isEmpty()) return awolString
    
    val result = mutableListOf<String>()
    if (dates.isNotEmpty()) {
        var start = dates[0]
        var end = dates[0]
        
        for (i in 1 until dates.size) {
            if (dates[i] == end.plusDays(1)) {
                end = dates[i]
            } else {
                result.add(formatRange(start, end))
                start = dates[i]
                end = dates[i]
            }
        }
        result.add(formatRange(start, end))
    }
    return result.joinToString(", ")
}

fun formatDate(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "---"
    return try {
        val date = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
        date.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}

private fun formatRange(start: LocalDate, end: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)
    val dayFormatter = DateTimeFormatter.ofPattern("d", Locale.ENGLISH)
    val yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH)
    return if (start == end) {
        "${start.format(formatter)}, ${start.format(yearFormatter)}"
    } else {
        if (start.month == end.month) {
            "${start.format(formatter)}-${end.dayOfMonth}, ${start.format(yearFormatter)}"
        } else {
            "${start.format(formatter)} - ${end.format(formatter)}, ${start.format(yearFormatter)}"
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object EmployeeList : Screen("employee_list")
    object AddEmployee : Screen("add_employee")
    object Permit : Screen("permit")
    object Scheduler : Screen("scheduler")
    data class ExternalWeb(val url: String, val title: String) : Screen("external_web?url=${android.net.Uri.encode(url)}&title=$title") {
        companion object {
            const val routePattern = "external_web?url={url}&title={title}"
        }
    }
    data class Payslip(val id: String) : Screen("payslip/$id") {
        companion object {
            const val routePattern = "payslip/{employeeId}"
        }
    }
    data class EditEmployee(val id: String) : Screen("edit_employee/$id") {
        companion object {
            const val routePattern = "edit_employee/{employeeId}"
        }
    }
    data class EmployeeDetail(val id: String) : Screen("employee_detail/$id") {
        companion object {
            const val routePattern = "employee_detail/{employeeId}"
        }
    }
    object Chat : Screen("chat")
    data class ChatDetail(val otherId: String, val name: String) : Screen("chat_detail/$otherId?name=$name") {
        companion object {
            const val routePattern = "chat_detail/{otherId}?name={name}"
        }
    }
}

@Composable
fun rememberProfileImageModel(employee: Employee, context: Context, refreshKey: Long = 0L): Any? {
    return remember(employee.id, employee.profileImageUri, employee.employeeNo, refreshKey) {
        val empNo = employee.employeeNo
        if (empNo.isNullOrBlank()) return@remember employee.profileImageUri
        
        val safeEmpNo = empNo.trim().replace("/", "_").replace("#", "_").replace(" ", "_")
        val localFile = java.io.File(context.filesDir, "profile_images/photo_$safeEmpNo.jpg")
        
        // Priority 1: Valid local file
        if (localFile.exists() && localFile.length() > 50) {
            localFile
        } else {
            // Priority 2: Remote URL (if any)
            if (!employee.profileImageUri.isNullOrBlank()) {
                employee.profileImageUri
            } else {
                null
            }
        }
    }
}

@Composable
fun BackgroundAutoLogin(
    isAdmin: Boolean, 
    isOnline: Boolean, 
    portalUser: String?, 
    portalPass: String?, 
    onLoginSuccess: () -> Unit
) {
    if (!isAdmin || !isOnline || portalUser.isNullOrBlank() || portalPass.isNullOrBlank()) return

    val loginUrl = "https://oma.smphi.com/TP_WorkPermit/"
    var hasAttempted by rememberSaveable { mutableStateOf(false) }
    
    // Reset attempt if admin logs out
    LaunchedEffect(isAdmin) {
        if (!isAdmin) hasAttempted = false
    }
    
    if (hasAttempted) return

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(0, 0)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        view?.evaluateJavascript("""
                            (function() {
                                var user = '$portalUser';
                                var pass = '$portalPass';
                                
                                function simulateHumanInput(el, value) {
                                    if (!el) return;
                                    el.focus();
                                    el.value = "";
                                    try {
                                        document.execCommand('selectAll', false, null);
                                        document.execCommand('delete', false, null);
                                        document.execCommand('insertText', false, value);
                                    } catch (e) {}
                                    if (el.value !== value) el.value = value;
                                    ['input', 'change', 'blur', 'keyup', 'keydown', 'keypress'].forEach(function(type) {
                                        var evt = document.createEvent('HTMLEvents');
                                        evt.initEvent(type, true, true);
                                        el.dispatchEvent(evt);
                                    });
                                }
                                
                                function fill() {
                                    var u = document.getElementById('Input_UsernameVal') || document.querySelector('input[id*="User"]');
                                    var p = document.getElementById('Input_PasswordVal') || document.querySelector('input[id*="Pass"]');
                                    var b = document.querySelector('#b5-Button button') || document.querySelector('button.btn-primary');
                                    if (u && p && b) {
                                        simulateHumanInput(u, user);
                                        setTimeout(function() {
                                            simulateHumanInput(p, pass);
                                            setTimeout(function() { b.click(); }, 800);
                                        }, 800);
                                        return true;
                                    }
                                    return false;
                                }
                                var attempts = 0;
                                function auto() { if (attempts++ < 15 && !fill()) setTimeout(auto, 1500); }
                                auto();
                            })();
                        """.trimIndent(), null)

                        if (url?.contains("Dashboard", ignoreCase = true) == true || 
                            url?.contains("SMTP_Mobile_v2", ignoreCase = true) == true) {
                            onLoginSuccess()
                        }
                    }
                }
                loadUrl(loginUrl)
            }
        },
        update = {}
    )
}

@Composable
fun BackgroundMallIDScraper(
    viewModel: AttendanceViewModel,
    isOnline: Boolean
) {
    val context = LocalContext.current
    val isScraping by viewModel.isScrapingMallId.collectAsState()
    if (!isScraping || !isOnline) return

    val employees by viewModel.allEmployees.collectAsState()
    val selectedIds by viewModel.selectedIdsToScrape.collectAsState()
    val employeesToScrape = remember(employees, selectedIds) {
        employees.filter { selectedIds.contains(it.id) }
    }

    if (employeesToScrape.isEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "No employees with Mall ID found to scrape.", Toast.LENGTH_SHORT).show()
            viewModel.stopScrapingMallId()
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var retryTrigger by remember { mutableIntStateOf(0) }
    var currentRetryCount by remember { mutableIntStateOf(0) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isScraping) {
        if (isScraping) {
            Toast.makeText(context, "Auto-Scraping Started: checking ${employeesToScrape.size} IDs", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(currentIndex, isScraping, retryTrigger) {
        if (isScraping) {
            if (currentIndex < employeesToScrape.size) {
                viewModel.setScrapingProgress(currentIndex.toFloat() / employeesToScrape.size)
                val emp = employeesToScrape[currentIndex]
                Log.d("Scraper", "Checking employee ${currentIndex + 1}/${employeesToScrape.size}: ${emp.firstName} (MallID: ${emp.mallIdNo}) - Retry: $currentRetryCount")
                val url = "https://oma.smphi.com/SMTP_Mobile_v2/MallIDDetails?Id=${emp.mallIdNo}"
                webViewInstance?.loadUrl(url)
            } else {
                Toast.makeText(context, "Auto-Scraping Finished! Checked ${employeesToScrape.size} employees.", Toast.LENGTH_LONG).show()
                viewModel.stopScrapingMallId()
                currentIndex = 0
                retryTrigger = 0
                currentRetryCount = 0
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(0, 0)
                webViewInstance = this
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        if (url?.contains("MallIDDetails") == true) {
                            // EXTRA WAIT: Increased to 10 seconds to ensure website fully opens/renders
                            scope.launch {
                                delay(10000) 
                                view?.evaluateJavascript("""
                                    (function() {
                                        var status = "";
                                        
                                        // 1. Precise Match: Look for specific class names or data-expression
                                        var exprs = document.querySelectorAll('[data-expression]');
                                        for (var j = 0; j < exprs.length; j++) {
                                            var txt = exprs[j].innerText.trim();
                                            if (["Active", "For Renewal", "Printing", "Pick up", "Orientation", "For Re-Orientation", "For Printing", "Draft", "For Pick Up", "Expired", "Cancelled", "Rejected", "For Approval"].some(s => txt.toLowerCase() === s.toLowerCase())) {
                                                status = txt;
                                                break;
                                            }
                                        }
                                        // 2. Fallback: Search for labels and siblings (OutSystems specific)
                                        if (!status) {
                                            var labels = document.querySelectorAll('label');
                                            for(var i=0; i<labels.length; i++) {
                                                var labelText = labels[i].innerText.toLowerCase();
                                                if(labelText.indexOf('id status') !== -1 || labelText.indexOf('request status') !== -1) {
                                                    var container = labels[i].closest('[data-container]') || labels[i].parentElement.parentElement;
                                                    var val = container.innerText.replace('ID Status', '').replace('Request Status', '').trim();
                                                    if(val) { status = val.split('\n')[0]; break; }
                                                }
                                            }
                                        }
                                        return status;
                                    })();
                                """.trimIndent()) { result ->
                                    val status = result.trim('"')
                                    val emp = employeesToScrape[currentIndex]
                                    
                                    scope.launch {
                                        if (status.isNotBlank() && status != "null") {
                                            viewModel.updateMallIdStatus(emp.id, status)
                                            Toast.makeText(context, "Scraped: ${emp.firstName} -> $status", Toast.LENGTH_SHORT).show()
                                            delay(5000)
                                            currentRetryCount = 0
                                            currentIndex++ // SUCCESS: Move to next
                                        } else {
                                            // Status missing or page not fully ready yet
                                            if (currentRetryCount < 2) {
                                                Toast.makeText(context, "Retrying ${emp.firstName} (Attempt ${currentRetryCount + 2})...", Toast.LENGTH_SHORT).show()
                                                currentRetryCount++
                                                retryTrigger++ // Triggers reload in LaunchedEffect
                                            } else {
                                                Toast.makeText(context, "No status found for ${emp.firstName}. Skipping.", Toast.LENGTH_SHORT).show()
                                                currentRetryCount = 0
                                                currentIndex++
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (url?.contains("Login") == true) {
                            Toast.makeText(context, "Scraper paused: Web session expired. Please login manually in 'Mall ID Details (Manual)'", Toast.LENGTH_LONG).show()
                            viewModel.stopScrapingMallId()
                        }
                    }
                }
            }
        },
        update = {}
    )
}


@Composable
fun AttendanceApp(viewModel: AttendanceViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val scale = (screenWidth / 360f).coerceIn(0.85f, 1.15f)

    CompositionLocalProvider(LocalScale provides scale) {
        val context = LocalContext.current
        val navController = rememberNavController()
        val currentUser by viewModel.currentUser.collectAsState()
        val employees by viewModel.allEmployees.collectAsState()
        val authError by viewModel.authError.collectAsState()
        val isOnline by viewModel.isOnline.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        var birthdayAlertShown by remember { mutableStateOf(hasBirthdayAlertBeenShownToday(context)) }
        val birthdaysToday = remember(employees) {
            employees.filter { emp ->
                emp.position != "Excrew" && calculateDaysToBirthday(emp.birthday) == 0L
            }
        }

        if (birthdaysToday.isNotEmpty() && !birthdayAlertShown) {
            val message = remember(birthdaysToday) {
                getBirthdayGreetingMessage("")
            }
            AlertDialog(
                onDismissRequest = { 
                    setBirthdayAlertShownToday(context)
                    birthdayAlertShown = true 
                },
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("🎉 Happy Birthday! 🎂", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF4081))
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        birthdaysToday.forEach { emp ->
                            Text(
                                "${emp.firstName} ${emp.lastName}", 
                                style = MaterialTheme.typography.headlineSmall, 
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            message,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            setBirthdayAlertShownToday(context)
                            birthdayAlertShown = true 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081))
                    ) { Text("Hooray! 🎊") }
                }
            )
        }


        LaunchedEffect(authError) {
            authError?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.clearAuthError()
            }
        }

        val loggedInEmployee by viewModel.loggedInEmployee.collectAsState()
        var isLoginReady by remember { mutableStateOf(false) }
        
        // Find Portal Credentials from Supabase Settings
        val portalCreds = remember(employees) {
            Pair(viewModel.getPortalUsername(), viewModel.getPortalPassword())
        }

        // Background Auto-Login for Web Portals
        BackgroundAutoLogin(
            isAdmin = loggedInEmployee?.isAdmin == true,
            isOnline = isOnline,
            portalUser = portalCreds.first,
            portalPass = portalCreds.second,
            onLoginSuccess = { isLoginReady = true }
        )

        // Automated Scraper
        BackgroundMallIDScraper(viewModel, isOnline)
        
        LaunchedEffect(currentUser, loggedInEmployee) {
            if (currentUser == null && loggedInEmployee == null) {
                if (navController.currentDestination?.route != Screen.Login.route) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            } else if (currentUser != null || loggedInEmployee != null) {
                // Check if admin or restricted employee
                if (loggedInEmployee?.isAdmin == true) {
                    if (navController.currentDestination?.route == Screen.Login.route) {
                        navController.navigate(Screen.EmployeeList.route) {
                            popUpTo(0)
                        }
                    }
                } else if (loggedInEmployee != null) {
                    // Restricted employee - navigate to their own detail
                    if (navController.currentDestination?.route == Screen.Login.route) {
                        navController.navigate(Screen.EmployeeDetail(loggedInEmployee!!.id).route) {
                            popUpTo(0)
                        }
                    }
                }
            }
        }


        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        onSync = {
                            viewModel.syncAccountsOnly { success ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (success) "Account retrieval successful" else "Account retrieval failed"
                                    )
                                }
                            }
                        },
                        onEmployeeLogin = { no, pw ->
                            viewModel.login(no, pw) { success, err ->
                                if (!success) {
                                    scope.launch { snackbarHostState.showSnackbar(err ?: "Login failed") }
                                }
                            }
                        }
                    )
                }
                composable(Screen.EmployeeList.route) {
                    if (loggedInEmployee?.isAdmin == true) {
                        EmployeeListScreen(
                            viewModel = viewModel,
                            onAddEmployee = { navController.navigate(Screen.AddEmployee.route) },
                            onEmployeeClick = { employee -> navController.navigate(Screen.EmployeeDetail(employee.id).route) },
                            onPermitClick = { navController.navigate(Screen.Permit.route) },
                            onSchedulerClick = { navController.navigate(Screen.Scheduler.route) },
                            onChatClick = { navController.navigate(Screen.Chat.route) },
                        onSendMessageClick = { id, name -> navController.navigate(Screen.ChatDetail(id, name).route) },
                        onExternalWebClick = { url, title -> navController.navigate(Screen.ExternalWeb(url, title).route) }
                        )
                    } else {
                        // Security fallback: If a non-admin somehow reaches the list, send them home
                        LaunchedEffect(loggedInEmployee) {
                            if (loggedInEmployee != null) {
                                navController.navigate(Screen.EmployeeDetail(loggedInEmployee!!.id).route) {
                                    popUpTo(Screen.EmployeeList.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0)
                                }
                            }
                        }
                    }
                }
                composable(Screen.AddEmployee.route) {
                    AddEmployeeScreen(
                        onEmployeeAdded = { employee ->
                            viewModel.addEmployee(context, employee)
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Permit.route) {
                    PermitWebViewScreen(
                        viewModel = viewModel, 
                        portalUser = portalCreds.first,
                        portalPass = portalCreds.second,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Scheduler.route) {
                    SchedulerScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.ExternalWeb.routePattern) { backStackEntry ->
                    val url = backStackEntry.arguments?.getString("url") ?: ""
                    val title = backStackEntry.arguments?.getString("title") ?: "Web Portal"
                    ExternalWebViewScreen(
                        url = url, 
                        title = title, 
                        portalUser = portalCreds.first,
                        portalPass = portalCreds.second,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.EditEmployee.routePattern) { backStackEntry ->
                    val employeeId = backStackEntry.arguments?.getString("employeeId")
                    if (employeeId != null) {
                        EditEmployeeScreen(
                            employeeId = employeeId,
                            viewModel = viewModel,
                            onEmployeeUpdated = { employee ->
                                viewModel.updateEmployee(context, employee)
                                navController.popBackStack()
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable(Screen.EmployeeDetail.routePattern) { backStackEntry ->
                    val employeeId = backStackEntry.arguments?.getString("employeeId")
                    if (employeeId != null) {
                        EmployeeDetailScreen(
                            employeeId = employeeId,
                            viewModel = viewModel,
                            onEditEmployee = { id -> navController.navigate(Screen.EditEmployee(id).route) },
                            onPayslip = { id -> navController.navigate(Screen.Payslip(id).route) },
                            onChatClick = { id, name -> navController.navigate(Screen.ChatDetail(id, name).route) },
                            onOpenChatList = { navController.navigate(Screen.Chat.route) },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable(Screen.Payslip.routePattern) { backStackEntry ->
                    val employeeId = backStackEntry.arguments?.getString("employeeId")
                    val employee = employees.find { it.id == employeeId }
                    if (employee != null) {
                        PayslipWebViewScreen(
                            employee = employee,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable(Screen.Chat.route) {
                    ChatScreen(
                        viewModel = viewModel,
                        onChatSelected = { id: String, name: String -> navController.navigate(Screen.ChatDetail(id, name).route) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.ChatDetail.routePattern) { backStackEntry ->
                    val otherId = backStackEntry.arguments?.getString("otherId") ?: ""
                    val name = backStackEntry.arguments?.getString("name") ?: "Chat"
                    ChatDetailScreen(
                        viewModel = viewModel,
                        otherId = otherId,
                        chatName = name,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onSync: () -> Unit,
    onEmployeeLogin: (String, String) -> Unit
) {
    var employeeNo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.s())
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(110.s()),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                AsyncImage(
                    model = R.drawable.app_icon,
                    contentDescription = "Chowking Logo",
                    modifier = Modifier
                        .padding(16.s())
                        .clip(CircleShape)
                )
            }
            
            Spacer(modifier = Modifier.height(24.s()))
            
            Text(
                text = "Chowking Employee",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.spS()),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Attendance & Records System",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.spS()),
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.s()))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.s()),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.s()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = employeeNo,
                        onValueChange = { employeeNo = it },
                        label = { Text("Employee Number", fontSize = 12.spS()) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        leadingIcon = { Icon(Icons.Default.Person, null, modifier = Modifier.size(20.s())) }
                    )
                    Spacer(modifier = Modifier.height(12.s()))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", fontSize = 12.spS()) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        leadingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    modifier = Modifier.size(20.s())
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.s()))
                    
                    Button(
                        onClick = { onEmployeeLogin(employeeNo, password) },
                        modifier = Modifier.fillMaxWidth().height(52.s()),
                        shape = MaterialTheme.shapes.large,
                        enabled = employeeNo.isNotBlank() && password.isNotBlank()
                    ) {
                        Text("LOGIN", style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.spS()), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.s()))
            
            Text(
                "Initial password is your Employee Number",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.spS()),
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = onSync,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(Icons.Default.Sync, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Manual Cloud Sync (Retrieve Accounts)")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(
    viewModel: AttendanceViewModel,
    onAddEmployee: () -> Unit,
    onEmployeeClick: (Employee) -> Unit,
    onPermitClick: () -> Unit,
    onSchedulerClick: () -> Unit,
    onChatClick: () -> Unit,
    onSendMessageClick: (String, String) -> Unit,
    onExternalWebClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val loggedInEmployee by viewModel.loggedInEmployee.collectAsState()
    val employees by viewModel.allEmployees.collectAsState()
    val targetHeadcount by viewModel.targetHeadcount.collectAsState()
    val excludedIds by viewModel.excludedFromHeadcountIds.collectAsState()
    
    val excludedPositions = listOf("Coordinator", "Excrew", "Assistant Manager", "Senior Crew", "Manager")
    
    val headcount = remember(employees, excludedIds) {
        employees.count { 
            !it.id.contains("#") && 
            it.isAdmin != true && 
            !excludedPositions.contains(it.position) &&
            !excludedIds.contains(it.id)
        }
    }

    var showExcrew by rememberSaveable { mutableStateOf(false) }

    val sortedEmployees = remember(employees, showExcrew) {
        employees.filter { if (!showExcrew) it.position != "Excrew" else true }
            .sortedWith(compareBy({ getPositionRank(it.position) }, { it.firstName ?: "" }))
    }
    val groupedEmployees = remember(sortedEmployees) {
        sortedEmployees.groupBy { it.position ?: "Unassigned" }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var birthdayAlertShown by remember { mutableStateOf(hasBirthdayAlertBeenShownToday(context)) }
    val birthdaysToday = remember(employees) {
        employees.filter { emp ->
            emp.position != "Excrew" && calculateDaysToBirthday(emp.birthday) == 0L
        }
    }

    if (birthdaysToday.isNotEmpty() && !birthdayAlertShown) {
        val message = remember(birthdaysToday) {
            getBirthdayGreetingMessage("")
        }
        AlertDialog(
            onDismissRequest = { 
                setBirthdayAlertShownToday(context)
                birthdayAlertShown = true 
            },
            title = { 
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("🎉 Happy Birthday! 🎂", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF4081))
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    birthdaysToday.forEach { emp ->
                        Text(
                            "${emp.firstName} ${emp.lastName}", 
                            style = MaterialTheme.typography.headlineSmall, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        message,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        setBirthdayAlertShownToday(context)
                        birthdayAlertShown = true 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081))
                ) { Text("Hooray! 🎊") }
            }
        )
    }

    var showMenu by rememberSaveable { mutableStateOf(false) }
    var menuCategory by rememberSaveable { mutableStateOf<String?>(null) } // null = main list, "backup", "id", "rtw_attrition", "announcement"
    var showRequestManager by rememberSaveable { mutableStateOf(false) }
    var showAnnouncementDialog by rememberSaveable { mutableStateOf(false) }
    var showViewAnnouncements by rememberSaveable { mutableStateOf(false) }
    var showDownloadWarning by rememberSaveable { mutableStateOf(false) }
    var downloadAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showRetrieveAllWarning by rememberSaveable { mutableStateOf(false) }
    var showHeadcountDialog by rememberSaveable { mutableStateOf(false) }
    var showIDRenewalManager by rememberSaveable { mutableStateOf(false) }
    var showMallIDList by rememberSaveable { mutableStateOf(false) }
    var showHealthIDRenewalManager by rememberSaveable { mutableStateOf(false) }
    var showUpdateManager by rememberSaveable { mutableStateOf(false) }
    var showDailySummaryManager by rememberSaveable { mutableStateOf(false) }
    var showMultiScraperDialog by rememberSaveable { mutableStateOf(false) }
    var showBirthdayCelebrantDialog by rememberSaveable { mutableStateOf(false) }
    var showAttritionManager by rememberSaveable { mutableStateOf(false) }
    var showDAManager by rememberSaveable { mutableStateOf(false) }
    var showMedalCountdownDialog by rememberSaveable { mutableStateOf(false) }
    var imageRefreshKey by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Pre-calculate medals for the list
    val medalCountdownStart by viewModel.medalCountdownStart.collectAsState()
    val allAttendance by viewModel.allAttendanceRecords.collectAsState(initial = emptyList())
    val medalMap = remember(allAttendance, medalCountdownStart) {
        val startDate = try { LocalDate.parse(medalCountdownStart) } catch (e: Exception) { LocalDate.now().minusDays(30) }
        val endDate = startDate.plusDays(30) // 30-day window
        
        allAttendance.filter { 
            val recordDate = try { LocalDate.parse(it.date) } catch (e: Exception) { null }
            recordDate != null && (recordDate == startDate || recordDate.isAfter(startDate)) && recordDate.isBefore(endDate) &&
            (it.status == AttendanceStatus.LATE || it.status == AttendanceStatus.OVERBREAK) 
        }
            .groupBy { it.employeeId }
            .mapValues { (_, records) ->
                var gold = 0
                var silver = 0
                records.forEach { rec ->
                    val note = rec.note ?: ""
                    if (note.startsWith("(") && note.contains(" mins)")) {
                        val mins = note.substringAfter("(").substringBefore(" mins)").toIntOrNull() ?: 0
                        if (mins >= 5) gold++ else silver++
                    } else {
                        // Default if no minutes recorded? Let's assume gold if status exists but no mins (old data)
                        // Or maybe the user only wants it for new data. 
                        // Let's go with: if status is LATE/OVERBREAK but no mins specified, count as gold to be safe
                        gold++
                    }
                }
                silver to gold
            }
    }
    
    val expiringIDs = remember(sortedEmployees) {
        sortedEmployees.filter {
            if (it.position == "Excrew") return@filter false
            val days = calculateDaysToExpiry(it.mallIdExpirationDate)
            days != null && days <= 30
        }.sortedBy { calculateDaysToExpiry(it.mallIdExpirationDate) ?: 9999 }
    }

    val expiringHealthIDs = remember(sortedEmployees) {
        sortedEmployees.filter {
            if (it.position == "Excrew") return@filter false
            val days = calculateDaysToExpiry(it.healthIdExpirationDate)
            days != null && days <= 30
        }.sortedBy { calculateDaysToExpiry(it.healthIdExpirationDate) ?: 9999 }
    }
    
    val unreadTotal by viewModel.getUnreadMessageCount(loggedInEmployee?.id ?: "").collectAsState(initial = 0)
    val allRequests by viewModel.allRequests.collectAsState(initial = emptyList())
    
    val resigningSoon = remember(sortedEmployees) {
        sortedEmployees.filter {
            it.isResigned == true && it.resignationDate != null &&
            (calculateDaysToExpiry(it.resignationDate) ?: 999L) <= 14
        }
    }
    
    val totalAlerts = remember(expiringIDs, expiringHealthIDs, birthdaysToday, allRequests, resigningSoon, unreadTotal) {
        expiringIDs.size + expiringHealthIDs.size + birthdaysToday.size + 
        allRequests.count { it.status == com.sam.myapplication.data.RequestStatus.PENDING } + 
        resigningSoon.size + unreadTotal
    }

    if (showDownloadWarning) {
        AlertDialog(
            onDismissRequest = { showDownloadWarning = false },
            title = { Text("Warning: Server Sync") },
            text = { Text("This will replace your current local data with the server backup. Any unsaved local changes may be lost. Continue?") },
            confirmButton = {
                Button(onClick = { 
                    downloadAction?.invoke()
                    showDownloadWarning = false
                }) { Text("Confirm Download") }
            },
            dismissButton = { TextButton(onClick = { showDownloadWarning = false }) { Text("Cancel") } }
        )
    }

    if (showRetrieveAllWarning) {
        AlertDialog(
            onDismissRequest = { showRetrieveAllWarning = false },
            title = { Text("Warning: Full Database Retrieval") },
            text = { Text("This will replace all local data (Employees, Attendance, DTR) with the server backup. Any unsaved local changes will be permanently lost. Proceed?") },
            confirmButton = {
                Button(onClick = { 
                    showRetrieveAllWarning = false
                    viewModel.retrieveAllData { success ->
                        if (success) {
                            imageRefreshKey = System.currentTimeMillis()
                            // Also trigger a photo sync automatically after DB sync
                            viewModel.retrieveAllPhotos { _ -> 
                                imageRefreshKey = System.currentTimeMillis()
                            }
                        }
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (success) "Full retrieval successful" else "Full retrieval failed"
                            )
                        }
                    }
                }) { Text("Confirm Retrieval") }
            },
            dismissButton = { TextButton(onClick = { showRetrieveAllWarning = false }) { Text("Cancel") } }
        )
    }

    if (showHeadcountDialog) {
        var targetText by remember { mutableStateOf(targetHeadcount.toString()) }
        AlertDialog(
            onDismissRequest = { showHeadcountDialog = false },
            title = { Text("Headcount Settings") },
            text = {
                Column {
                    OutlinedTextField(
                        value = targetText,
                        onValueChange = { targetText = it },
                        label = { Text("Target Headcount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Select Employees to Exclude from count:", style = MaterialTheme.typography.labelMedium)
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(sortedEmployees.filter { 
                            !it.id.contains("#") && 
                            it.id != "fusion" && it.id != "chowking" &&
                            !excludedPositions.contains(it.position)
                        }) { emp ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.toggleHeadcountExclusion(emp.id) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = excludedIds.contains(emp.id), onCheckedChange = { viewModel.toggleHeadcountExclusion(emp.id) })
                                Text("${emp.firstName} ${emp.lastName}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.setTargetHeadcount(targetText.toIntOrNull() ?: 0)
                    showHeadcountDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showHeadcountDialog = false }) { Text("Close") } }
        )
    }

    
    if (showAnnouncementDialog) {
        AnnouncementDialog(
            employees = sortedEmployees,
            onDismiss = { showAnnouncementDialog = false },
            onSend = { title, content, targetId ->
                viewModel.sendAnnouncement(title, content, targetId)
                showAnnouncementDialog = false
                scope.launch { snackbarHostState.showSnackbar("Announcement sent") }
            }
        )
    }

    if (showViewAnnouncements) {
        val announcements by viewModel.allAnnouncements.collectAsState()
        ViewAnnouncementsDialog(
            announcements = announcements,
            onDismiss = { showViewAnnouncements = false },
            onDelete = { viewModel.deleteAnnouncement(it) }
        )
    }

    if (showUpdateManager) {
        AppUpdateManagerDialog(
            viewModel = viewModel,
            onDismiss = { showUpdateManager = false }
        )
    }

    val syncProgress by viewModel.syncProgress.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    if (showDAManager) {
        DAManagerDialog(
            viewModel = viewModel,
            employees = sortedEmployees,
            onDismiss = { showDAManager = false }
        )
    }

    if (showAttritionManager) {
        AttritionManagerDialog(
            viewModel = viewModel,
            employees = sortedEmployees,
            onDismiss = { showAttritionManager = false }
        )
    }

    if (showBirthdayCelebrantDialog) {
        AlertDialog(
            onDismissRequest = { showBirthdayCelebrantDialog = false },
            title = { 
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Cake, null, tint = Color(0xFFFF4081))
                    Spacer(Modifier.width(8.dp))
                    Text("Birthday Celebrants")
                }
            },
            text = {
                val bdayList = sortedEmployees.filter { it.position != "Excrew" && !it.birthday.isNullOrBlank() }
                    .sortedBy { calculateDaysToBirthday(it.birthday) ?: 9999 }
                
                if (bdayList.isEmpty()) {
                    Text("No birthday records found.")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 450.dp)) {
                        items(bdayList) { emp ->
                            val days = calculateDaysToBirthday(emp.birthday)
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (days == 0L) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                ListItem(
                                    headlineContent = { 
                                        Text("${emp.firstName} ${emp.lastName}", fontWeight = if (days == 0L) FontWeight.Bold else FontWeight.Normal) 
                                    },
                                    supportingContent = { Text("Birthday: ${emp.birthday}") },
                                    trailingContent = {
                                        Surface(
                                            color = if (days == 0L) Color.Red else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = if (days == 0L) "TODAY 🎂" else "$days d",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (days == 0L) Color.White else MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showBirthdayCelebrantDialog = false }) { Text("Close") } }
        )
    }

    if (showDailySummaryManager) {
        DailySummaryDialog(
            viewModel = viewModel,
            onDismiss = { showDailySummaryManager = false }
        )
    }

    if (showMultiScraperDialog) {
        var selectedIds by remember { mutableStateOf(sortedEmployees.filter { !it.mallIdNo.isNullOrBlank() && it.position != "Excrew" }.map { it.id }.toSet()) }
        AlertDialog(
            onDismissRequest = { showMultiScraperDialog = false },
            title = { Text("Select IDs to Scrape") },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { 
                            selectedIds = sortedEmployees.filter { !it.mallIdNo.isNullOrBlank() && it.position != "Excrew" }.map { it.id }.toSet() 
                        }) { Text("Select All") }
                        TextButton(onClick = { selectedIds = emptySet() }) { Text("Deselect All") }
                    }
                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                        items(sortedEmployees.filter { !it.mallIdNo.isNullOrBlank() && it.position != "Excrew" }) { emp ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { 
                                    selectedIds = if (selectedIds.contains(emp.id)) selectedIds - emp.id else selectedIds + emp.id
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = selectedIds.contains(emp.id), onCheckedChange = { 
                                    selectedIds = if (it) selectedIds + emp.id else selectedIds - emp.id
                                })
                                Text("${emp.firstName} ${emp.lastName}", modifier = Modifier.weight(1f))
                                Text(emp.mallIdStatus ?: "", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.startScrapingMallId(selectedIds.toList())
                        showMultiScraperDialog = false
                    },
                    enabled = selectedIds.isNotEmpty()
                ) { Text("Start Scrape (${selectedIds.size})") }
            },
            dismissButton = { TextButton(onClick = { showMultiScraperDialog = false }) { Text("Cancel") } }
        )
    }

    if (showMedalCountdownDialog) {
        var dateText by remember { mutableStateOf(medalCountdownStart) }
        AlertDialog(
            onDismissRequest = { showMedalCountdownDialog = false },
            title = { Text("Medal Countdown Start Date") },
            text = {
                Column {
                    Text("Enter the date when the 30-day medal countdown should start (YYYY-MM-DD):", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        label = { Text("Start Date") },
                        placeholder = { Text("2024-05-01") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.setMedalCountdownStart(dateText)
                    showMedalCountdownDialog = false
                    scope.launch { snackbarHostState.showSnackbar("Medal countdown start date updated to $dateText") }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showMedalCountdownDialog = false }) { Text("Cancel") } }
        )
    }



    if (showIDRenewalManager) {
        AlertDialog(
            onDismissRequest = { showIDRenewalManager = false },
            title = { 
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Badge, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text("ID & Status Renewal Warning")
                }
            },
            text = {
                if (expiringIDs.isEmpty() && resigningSoon.isEmpty()) {
                    Text("No IDs expiring or pending resignations within the alert period.")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                        if (resigningSoon.isNotEmpty()) {
                            item { Text("Pending Resignations:", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp)) }
                            items(resigningSoon) { emp ->
                                val days = calculateDaysToExpiry(emp.resignationDate)
                                Card(
                                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth().clickable { onEmployeeClick(emp); showIDRenewalManager = false },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                                ) {
                                    ListItem(
                                        headlineContent = { Text("${emp.firstName} ${emp.lastName}", fontWeight = FontWeight.Bold) },
                                        supportingContent = { Text("Last Day: ${emp.resignationDate} (${days ?: 0} days left)") },
                                        leadingContent = { Icon(Icons.Default.PersonOff, null) },
                                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                    )
                                }
                            }
                        }
                        
                        if (expiringIDs.isNotEmpty()) {
                            item { Text("Expiring Mall IDs:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp)) }
                            items(expiringIDs) { emp ->
                                val days = calculateDaysToExpiry(emp.mallIdExpirationDate)
                                Card(
                                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth().clickable { 
                                        val url = "https://oma.smphi.com/SMTP_Mobile_v2/MallIDDetails?Id=${emp.mallIdNo}"
                                        onExternalWebClick(url, "Mall ID Details")
                                    },
                                    colors = CardDefaults.cardColors(containerColor = if (days != null && days <= 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    ListItem(
                                        headlineContent = { Text("${emp.firstName} ${emp.lastName}", fontWeight = FontWeight.Bold) },
                                        supportingContent = { Text("Mall ID: ${emp.mallIdNo}\nExpires: ${emp.mallIdExpirationDate} (${days ?: 0} days left)") },
                                        trailingContent = { Icon(Icons.Default.Visibility, null) },
                                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showIDRenewalManager = false }) { Text("Close") } }
        )
    }

    if (showMallIDList) {
        AlertDialog(
            onDismissRequest = { showMallIDList = false },
            title = { 
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Badge, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Select Employee for Mall ID")
                }
            },
            text = {
                val listWithId = sortedEmployees.filter { !it.mallIdNo.isNullOrBlank() && it.position != "Excrew" }
                if (listWithId.isEmpty()) {
                    Text("No employees with Mall ID found.")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                        items(listWithId) { emp ->
                            Card(
                                modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth().clickable { 
                                    showMallIDList = false
                                    val url = "https://oma.smphi.com/SMTP_Mobile_v2/MallIDDetails?Id=${emp.mallIdNo}"
                                    onExternalWebClick(url, "Mall ID Details")
                                },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                ListItem(
                                    headlineContent = { Text("${emp.firstName} ${emp.lastName}", fontWeight = FontWeight.Bold) },
                                    supportingContent = { Text("Mall ID: ${emp.mallIdNo}") },
                                    trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showMallIDList = false }) { Text("Close") } }
        )
    }

    if (showHealthIDRenewalManager) {
        AlertDialog(
            onDismissRequest = { showHealthIDRenewalManager = false },
            title = { 
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HealthAndSafety, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text("Health ID Renewal Warning")
                }
            },
            text = {
                if (expiringHealthIDs.isEmpty()) {
                    Text("No Health IDs expiring within 30 days.")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                        items(expiringHealthIDs) { emp ->
                            val days = calculateDaysToExpiry(emp.healthIdExpirationDate)
                            Card(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (days != null && days <= 0) 
                                        MaterialTheme.colorScheme.errorContainer 
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                ListItem(
                                    headlineContent = { Text("${emp.firstName} ${emp.lastName}", fontWeight = FontWeight.Bold) },
                                    supportingContent = { Text("Expires: ${emp.healthIdExpirationDate} (${days ?: 0} days left)") },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showHealthIDRenewalManager = false }) { Text("Close") } }
        )
    }

    if (showRequestManager) {
        val allRequests by viewModel.allRequests.collectAsState()
        val employees by viewModel.allEmployees.collectAsState()
        
        AlertDialog(
            onDismissRequest = { showRequestManager = false },
            title = { Text("Pending Requests") },
            text = {
                val pending = allRequests.filter { it.status == com.sam.myapplication.data.RequestStatus.PENDING }
                if (pending.isEmpty()) {
                    Text("No pending requests")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                        items(pending) { req ->
                            val emp = employees.find { it.id == req.employeeId }
                            Card(modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("${emp?.firstName ?: ""} ${emp?.lastName ?: ""}", fontWeight = FontWeight.Bold)
                                    Text("${req.type.name} for ${req.date}")
                                    Text(req.note ?: "", style = MaterialTheme.typography.bodySmall)
                                    Row {
                                        TextButton(onClick = { viewModel.approveRequest(req, loggedInEmployee?.firstName ?: "Admin") }) {
                                            Text("Approve", color = Color.Green)
                                        }
                                        TextButton(onClick = { viewModel.rejectRequest(req, loggedInEmployee?.firstName ?: "Admin") }) {
                                            Text("Reject", color = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showRequestManager = false }) { Text("Close") } }
        )
    }


    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.let { outputStream ->
                viewModel.exportEmployees(context.contentResolver, outputStream) { success ->
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (success) "Complete backup saved successfully" else "Backup failed"
                        )
                    }
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.let { inputStream ->
                viewModel.importEmployees(context, inputStream) { success ->
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (success) "Restore successful" else "Restore failed"
                        )
                    }
                }
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Chowking Employee", fontSize = 18.spS())
                            ConnectivityStatus(viewModel)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showHeadcountDialog = true }
                        ) {
                            Text("$headcount / $targetHeadcount Headcount", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.spS()), color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onChatClick) {
                        val unreadCount by viewModel.getUnreadMessageCount(loggedInEmployee?.employeeNo ?: loggedInEmployee?.id ?: "").collectAsState(0)
                        BadgedBox(badge = { if (unreadCount > 0) Badge { Text(unreadCount.toString()) } }) {
                            Icon(Icons.Default.Chat, contentDescription = "Terminal Chat", modifier = Modifier.size(20.s()))
                        }
                    }

                    IconButton(onClick = { showDailySummaryManager = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Daily Summary", modifier = Modifier.size(20.s()))
                    }

                    IconButton(onClick = { 
                        showMenu = !showMenu 
                        menuCategory = null // Reset to main menu when opening
                    }) {
                        BadgedBox(badge = { if (totalAlerts > 0) Badge { Text(totalAlerts.toString()) } }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(20.s()))
                        }
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.width(260.s())
                    ) {
                        AnimatedContent(
                            targetState = menuCategory,
                            transitionSpec = {
                                if (targetState != null) {
                                    // Slide in from right when going into category
                                    slideInHorizontally { it } + fadeIn() togetherWith
                                            slideOutHorizontally { -it } + fadeOut()
                                } else {
                                    // Slide in from left when going back to main
                                    slideInHorizontally { -it } + fadeIn() togetherWith
                                            slideOutHorizontally { it } + fadeOut()
                                }
                            },
                            label = "MenuAnimation"
                        ) { targetCategory ->
                            Column {
                                when (targetCategory) {
                                    null -> {
                                        // MAIN MENU
                                        DropdownMenuItem(
                                            text = { Text("Backup and restore", fontWeight = FontWeight.Bold) },
                                            onClick = { menuCategory = "backup" },
                                            leadingIcon = { Icon(Icons.Default.Backup, null) },
                                            trailingIcon = { Icon(Icons.Default.ChevronRight, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Employee ID", fontWeight = FontWeight.Bold) },
                                            onClick = { menuCategory = "id" },
                                            leadingIcon = { Icon(Icons.Default.Badge, null) },
                                            trailingIcon = { Icon(Icons.Default.ChevronRight, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Employee RTW & Attrition", fontWeight = FontWeight.Bold) },
                                            onClick = { menuCategory = "rtw_attrition" },
                                            leadingIcon = { Icon(Icons.Default.PersonOff, null) },
                                            trailingIcon = { Icon(Icons.Default.ChevronRight, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Announcement", fontWeight = FontWeight.Bold) },
                                            onClick = { menuCategory = "announcement" },
                                            leadingIcon = { Icon(Icons.Default.Announcement, null) },
                                            trailingIcon = { Icon(Icons.Default.ChevronRight, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Target Headcount", fontWeight = FontWeight.Bold) },
                                            onClick = {
                                                showMenu = false
                                                showHeadcountDialog = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.Groups, null) }
                                        )
                                        HorizontalDivider()
                                        DropdownMenuItem(
                                            text = { Text("Birthday Celebrant") },
                                            onClick = {
                                                showMenu = false
                                                showBirthdayCelebrantDialog = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.Cake, null) }
                                        )
                                        if (loggedInEmployee?.isAdmin == true) {
                                            DropdownMenuItem(
                                                text = { Text("Permit Dashboard") },
                                                onClick = {
                                                    showMenu = false
                                                    onPermitClick()
                                                },
                                                leadingIcon = { Icon(Icons.Default.Description, null) }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Scheduler") },
                                                onClick = {
                                                    showMenu = false
                                                    onSchedulerClick()
                                                },
                                                leadingIcon = { Icon(Icons.Default.Schedule, null) }
                                            )
                                        }
                                        val allRequests by viewModel.allRequests.collectAsState()
                                        DropdownMenuItem(
                                            text = { 
                                                BadgedBox(badge = { if (allRequests.any { it.status == com.sam.myapplication.data.RequestStatus.PENDING }) Badge() }) {
                                                    Text("Pending Requests", fontWeight = FontWeight.Bold) 
                                                }
                                            },
                                            onClick = {
                                                showMenu = false
                                                showRequestManager = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.Notifications, null) }
                                        )
                                        if (loggedInEmployee?.isAdmin == true) {
                                            DropdownMenuItem(
                                                text = { Text("Update Management") },
                                                onClick = {
                                                    showMenu = false
                                                    showUpdateManager = true
                                                },
                                                leadingIcon = { Icon(Icons.Default.Settings, null) }
                                            )
                                        }
                                        DropdownMenuItem(
                                            text = { Text(if (showExcrew) "Hide Excrew" else "View Excrew") },
                                            onClick = {
                                                showExcrew = !showExcrew
                                                showMenu = false
                                            },
                                            leadingIcon = { Icon(if (showExcrew) Icons.Default.VisibilityOff else Icons.Default.Visibility, null) }
                                        )
                                        HorizontalDivider()
                                        DropdownMenuItem(
                                            text = { Text("Sign Out") },
                                            onClick = {
                                                showMenu = false
                                                viewModel.signOut()
                                            },
                                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                                        )
                                    }
                                    "backup" -> {
                                        // BACKUP/RESTORE SUB-MENU
                                        DropdownMenuItem(
                                            text = { Text("Back to Main Menu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) },
                                            onClick = { menuCategory = null },
                                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(16.dp)) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Retrieve All Data (Full Sync)") },
                                            onClick = {
                                                showMenu = false
                                                showRetrieveAllWarning = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.CloudDownload, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Major Upload") },
                                            onClick = {
                                                showMenu = false
                                                viewModel.majorUpload { success ->
                                                    scope.launch { snackbarHostState.showSnackbar(if (success) "Major upload initiated" else "Upload failed") }
                                                }
                                            },
                                            leadingIcon = { Icon(Icons.Default.FileUpload, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Complete Backup (Local/Offline)") },
                                            onClick = {
                                                showMenu = false
                                                exportLauncher.launch("chowking_complete_backup.json")
                                            },
                                            leadingIcon = { Icon(Icons.Default.FileDownload, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Restore Backup (Local/Offline)") },
                                            onClick = {
                                                showMenu = false
                                                importLauncher.launch(arrayOf("application/json"))
                                            },
                                            leadingIcon = { Icon(Icons.Default.Restore, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Retrieve Attendance") },
                                            onClick = {
                                                showMenu = false
                                                downloadAction = {
                                                    viewModel.retrieveAllAttendance { success ->
                                                        scope.launch { snackbarHostState.showSnackbar(if (success) "Attendance retrieved" else "Retrieval failed") }
                                                    }
                                                }
                                                showDownloadWarning = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.CheckCircle, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Photo Retrieval") },
                                            onClick = {
                                                showMenu = false
                                                downloadAction = {
                                                    viewModel.retrieveAllPhotos { count ->
                                                        if (count > 0) imageRefreshKey = System.currentTimeMillis()
                                                        scope.launch { snackbarHostState.showSnackbar(if (count > 0) "Downloaded $count photos" else "No photos found on server") }
                                                    }
                                                }
                                                showDownloadWarning = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.Visibility, null) }
                                        )
                                        if (loggedInEmployee?.isAdmin == true) {
                                            DropdownMenuItem(
                                                text = { Text("Repair Database Links") },
                                                onClick = {
                                                    showMenu = false
                                                    viewModel.repairEmployeeLinks()
                                                },
                                                leadingIcon = { Icon(Icons.Default.Build, null) }
                                            )
                                        }
                                    }
                                    "id" -> {
                                        // EMPLOYEE ID SUB-MENU
                                        DropdownMenuItem(
                                            text = { Text("Back to Main Menu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) },
                                            onClick = { menuCategory = null },
                                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(16.dp)) }
                                        )
                                        DropdownMenuItem(
                                            text = { 
                                                BadgedBox(badge = { 
                                                    if (expiringIDs.isNotEmpty() || expiringHealthIDs.isNotEmpty() || resigningSoon.isNotEmpty()) {
                                                        Badge { Text((expiringIDs.size + expiringHealthIDs.size + resigningSoon.size).toString()) }
                                                    }
                                                }) {
                                                    Text("ID & Status Renew")
                                                }
                                            },
                                            onClick = {
                                                showMenu = false
                                                showIDRenewalManager = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.Badge, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { 
                                                BadgedBox(badge = { 
                                                    if (expiringHealthIDs.isNotEmpty()) {
                                                        Badge { Text(expiringHealthIDs.size.toString()) }
                                                    }
                                                }) {
                                                    Text("Health ID Renew") 
                                                }
                                            },
                                            onClick = {
                                                showMenu = false
                                                showHealthIDRenewalManager = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.HealthAndSafety, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Mall ID Tempo Pass") },
                                            onClick = {
                                                showMenu = false
                                                val url = "https://oma.smphi.com/SMTP_Mobile_v2/CreateTemporaryPass"
                                                onExternalWebClick(url, "Mall ID Tempo Pass")
                                            },
                                            leadingIcon = { Icon(Icons.Default.ConfirmationNumber, null) }
                                        )
                                        if (loggedInEmployee?.isAdmin == true) {
                                            DropdownMenuItem(
                                                text = { Text("Mall ID Automated Scraper") },
                                                onClick = {
                                                    showMenu = false
                                                    showMultiScraperDialog = true
                                                },
                                                leadingIcon = { Icon(Icons.Default.Sync, null) }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Mall ID Details (Manual)") },
                                                onClick = {
                                                    showMenu = false
                                                    showMallIDList = true
                                                },
                                                leadingIcon = { Icon(Icons.Default.Visibility, null) }
                                            )
                                        }
                                    }
                                    "rtw_attrition" -> {
                                        // EMPLOYEE RTW & ATTRITION SUB-MENU
                                        DropdownMenuItem(
                                            text = { Text("Back to Main Menu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) },
                                            onClick = { menuCategory = null },
                                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(16.dp)) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Disciplinary Action (DA)") },
                                            onClick = {
                                                showMenu = false
                                                showDAManager = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.History, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Attrition") },
                                            onClick = {
                                                showMenu = false
                                                showAttritionManager = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.PersonOff, null) }
                                        )
                                        if (loggedInEmployee?.isAdmin == true) {
                                            DropdownMenuItem(
                                                text = { Text("Medal Countdown") },
                                                onClick = {
                                                    showMenu = false
                                                    showMedalCountdownDialog = true
                                                },
                                                leadingIcon = { Icon(Icons.Default.MilitaryTech, null) }
                                            )
                                        }
                                    }
                                    "announcement" -> {
                                        // ANNOUNCEMENT SUB-MENU
                                        DropdownMenuItem(
                                            text = { Text("Back to Main Menu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) },
                                            onClick = { menuCategory = null },
                                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(16.dp)) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Send Announcement") },
                                            onClick = {
                                                showMenu = false
                                                showAnnouncementDialog = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.Announcement, null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("View Announcement") },
                                            onClick = {
                                                showMenu = false
                                                showViewAnnouncements = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.List, null) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEmployee) {
                Icon(Icons.Default.Add, contentDescription = "Add Employee")
            }
        }
    ) { padding ->
        val scrapingActive by viewModel.isScrapingMallId.collectAsState()
        val scrapingProgress by viewModel.scrapingProgress.collectAsState()

        Column(modifier = Modifier.padding(padding)) {
            if (scrapingActive) {
                LinearProgressIndicator(
                    progress = { scrapingProgress },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
            LazyColumn {
                groupedEmployees.forEach { (position, employeesInGroup) ->
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = position,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    items(employeesInGroup) { employee ->
                        val daysToBirthday = calculateDaysToBirthday(employee.birthday)
                        val daysToExpiry = calculateDaysToExpiry(employee.mallIdExpirationDate)
                        val daysToHealthExpiry = calculateDaysToExpiry(employee.healthIdExpirationDate)
                        val daysToResign = if (employee.isResigned == true && employee.resignationDate != null) calculateDaysToExpiry(employee.resignationDate) else null
                        val timeHired = calculateTimeHired(employee.dateHired)
                        
                        val totalOffences = (employee.lateOffenceLevel ?: 0) + 
                                (employee.absentOffenceLevel ?: 0) + 
                                (employee.hamperingOffenceLevel ?: 0) + 
                                (employee.customOffences?.sumOf { it.level } ?: 0)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .clickable { onEmployeeClick(employee) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = MaterialTheme.shapes.large
                        ) {
                            ListItem(
                                headlineContent = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "${employee.firstName ?: ""} ${employee.lastName ?: ""}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (totalOffences > 0) {
                                            Spacer(Modifier.width(8.dp))
                                            Surface(
                                                color = Color(0xFFFF4081), // Pink color
                                                shape = CircleShape,
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        "DA",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color.White,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        }

                                        // Medals moved to supportingContent

                                        if (!employee.mallIdStatus.isNullOrBlank()) {
                                            Spacer(Modifier.width(8.dp))
                                            StatusBadgeCompact(employee.mallIdStatus)
                                        }
                                        
                                    }
                                },
                                supportingContent = { 
                                    Column {
                                        Text(
                                            (employee.position ?: "") + if (employee.isResigned == true) " (Resigned)" else "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            if (employee.isCertified == true) {
                                                if (!employee.certifiedPositions.isNullOrEmpty()) "Certified (${employee.certifiedPositions.joinToString(", ")})" else "Certified"
                                            } else "Not Certified",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (employee.isCertified == true) Color(0xFF4CAF50) else Color(0xFFF44336)
                                        )
                                        
                                        // Medals in the list under Certified
                                        val medalCounts = medalMap[employee.id] ?: (0 to 0)
                                        if (medalCounts.first > 0 || medalCounts.second > 0) {
                                            MedalDisplay(silverCount = medalCounts.first, goldCount = medalCounts.second, modifier = Modifier.padding(top = 4.dp))
                                        }
                                    }
                                },
                                leadingContent = {
                                    Surface(
                                        modifier = Modifier.size(44.s()),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shadowElevation = 2.dp
                                    ) {
                                        val model = rememberProfileImageModel(employee, context, imageRefreshKey)
                                        if (model != null) {
                                            AsyncImage(
                                                model = model,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop,
                                                placeholder = rememberVectorPainter(Icons.Default.Sync),
                                                error = rememberVectorPainter(Icons.Default.Person),
                                                onError = { state ->
                                                    android.util.Log.e("CoilError", "Failed to load list image: $model", state.result.throwable)
                                                }
                                            )
                                        } else {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                modifier = Modifier.padding(10.s())
                                            )
                                        }
                                    }
                                },
                                trailingContent = {
                                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            shape = MaterialTheme.shapes.extraSmall,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        ) {
                                            Text(
                                                "#${employee.employeeNo ?: ""}",
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        if (timeHired != null) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                                shape = MaterialTheme.shapes.extraSmall,
                                                modifier = Modifier.padding(bottom = 2.dp)
                                            ) {
                                                Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Work, null, modifier = Modifier.size(10.dp))
                                                    Spacer(Modifier.width(2.dp))
                                                    Text(timeHired, style = MaterialTheme.typography.labelSmall)
                                                }
                                            }
                                        }
                                        if (daysToBirthday != null) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = MaterialTheme.shapes.extraSmall,
                                                modifier = Modifier.padding(bottom = 2.dp)
                                            ) {
                                                Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Cake, null, modifier = Modifier.size(10.dp))
                                                    Spacer(Modifier.width(2.dp))
                                                    Text("$daysToBirthday d", style = MaterialTheme.typography.labelSmall)
                                                }
                                            }
                                        }
                                        if (daysToResign != null) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.errorContainer,
                                                shape = MaterialTheme.shapes.extraSmall,
                                                modifier = Modifier.padding(bottom = 2.dp)
                                            ) {
                                                Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.ExitToApp, null, modifier = Modifier.size(10.dp))
                                                    Spacer(Modifier.width(2.dp))
                                                    Text("$daysToResign d", style = MaterialTheme.typography.labelSmall)
                                                }
                                            }
                                        }
                                        if (daysToExpiry != null) {
                                            val color = if (daysToExpiry < 30) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
                                            Surface(
                                                color = color,
                                                shape = MaterialTheme.shapes.extraSmall,
                                                modifier = Modifier.padding(bottom = 2.dp)
                                            ) {
                                                Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Badge, null, modifier = Modifier.size(10.dp))
                                                    Spacer(Modifier.width(2.dp))
                                                    Text("$daysToExpiry d", style = MaterialTheme.typography.labelSmall)
                                                }
                                            }
                                        }
                                        if (daysToHealthExpiry != null) {
                                            val color = if (daysToHealthExpiry < 30) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer
                                            Surface(
                                                color = color,
                                                shape = MaterialTheme.shapes.extraSmall
                                            ) {
                                                Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.HealthAndSafety, null, modifier = Modifier.size(10.dp))
                                                    Spacer(Modifier.width(2.dp))
                                                    Text("$daysToHealthExpiry d", style = MaterialTheme.typography.labelSmall)
                                                }
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (loggedInEmployee?.isAdmin == true && !employee.mallIdNo.isNullOrBlank()) {
                                                IconButton(
                                                    onClick = { viewModel.startScrapingMallId(listOf(employee.id)) },
                                                    modifier = Modifier.size(24.dp).padding(top = 4.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.ManageSearch,
                                                        contentDescription = "Scrape Mall ID",
                                                        tint = MaterialTheme.colorScheme.secondary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }

                                            // Message Button
                                            if (loggedInEmployee?.id != employee.id && employee.isAdmin != true) {
                                                IconButton(
                                                    onClick = { onSendMessageClick(employee.id, "${employee.firstName} ${employee.lastName}") },
                                                    modifier = Modifier.size(24.dp).padding(top = 4.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Message,
                                                        contentDescription = "Send Message",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppUpdateManagerDialog(
    viewModel: AttendanceViewModel,
    onDismiss: () -> Unit
) {
    var versionCode by remember { mutableStateOf("") }
    var versionName by remember { mutableStateOf("") }
    var downloadUrl by remember { mutableStateOf("") }
    var changes by remember { mutableStateOf("") }
    var isForceUpdate by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val currentAppVersionName = remember {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "Unknown"
        } catch (e: Exception) { "Unknown" }
    }
    val currentAppVersionCode = remember {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (e: Exception) { 0 }
    }

    LaunchedEffect(Unit) {
        val config = viewModel.getUpdateConfig()
        if (config != null) {
            versionCode = config.versionCode.toString()
            versionName = config.versionName
            downloadUrl = config.downloadUrl
            changes = config.changes ?: ""
            isForceUpdate = config.isForceUpdate
        }
        isLoading = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("App Update Management") },
        text = {
            if (isLoading) {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                "Current App Version:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "v$currentAppVersionName (Code: $currentAppVersionCode)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    OutlinedTextField(
                        value = versionCode,
                        onValueChange = { versionCode = it },
                        label = { Text("Version Code (Integer)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = versionName,
                        onValueChange = { versionName = it },
                        label = { Text("Version Name (e.g. 1.1.0)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = downloadUrl,
                        onValueChange = { downloadUrl = it },
                        label = { Text("Download URL (Mediafire)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = changes,
                        onValueChange = { changes = it },
                        label = { Text("What's New / Changes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isForceUpdate, onCheckedChange = { isForceUpdate = it })
                        Text("Force Update?")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val vCode = versionCode.toIntOrNull() ?: 0
                    if (vCode > 0 && versionName.isNotBlank() && downloadUrl.isNotBlank()) {
                        scope.launch {
                            val success = viewModel.uploadUpdateConfig(
                                com.sam.myapplication.UpdateInfo(
                                    versionCode = vCode,
                                    versionName = versionName,
                                    downloadUrl = downloadUrl,
                                    isForceUpdate = isForceUpdate,
                                    changes = if (changes.isNotBlank()) changes else null
                                )
                            )
                            if (success) onDismiss()
                        }
                    }
                },
                enabled = !isLoading
            ) { Text("Save to Supabase") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DADialog(
    employee: Employee,
    onDismiss: () -> Unit,
    onConfirm: (Employee) -> Unit
) {
    var lateLevel by remember { mutableIntStateOf(employee.lateOffenceLevel ?: 0) }
    var absentLevel by remember { mutableIntStateOf(employee.absentOffenceLevel ?: 0) }
    var hamperingLevel by remember { mutableIntStateOf(employee.hamperingOffenceLevel ?: 0) }
    var customOffences by remember { mutableStateOf(employee.customOffences ?: emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newOffenceName by remember { mutableStateOf("") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Offence Type") },
            text = {
                OutlinedTextField(
                    value = newOffenceName,
                    onValueChange = { newOffenceName = it },
                    label = { Text("Offence Name (e.g. Uniform)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newOffenceName.isNotBlank()) {
                        customOffences = customOffences + com.sam.myapplication.data.CustomOffence(newOffenceName)
                        newOffenceName = ""
                        showAddDialog = false
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Disciplinary Action (DA)") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 450.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OffenceSection("Lates Offence", lateLevel) { lateLevel = it }
                OffenceSection("Absent Offence", absentLevel) { absentLevel = it }
                OffenceSection("Hampering Offence", hamperingLevel) { hamperingLevel = it }
                
                customOffences.forEachIndexed { index, offence ->
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(offence.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                customOffences = customOffences.toMutableList().apply { removeAt(index) }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        OffenceSection("", offence.level) { newLevel ->
                            customOffences = customOffences.toMutableList().apply {
                                this[index] = offence.copy(level = newLevel)
                            }
                        }
                    }
                }

                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add More Offence")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    employee.copy(
                        lateOffenceLevel = lateLevel,
                        absentOffenceLevel = absentLevel,
                        hamperingOffenceLevel = hamperingLevel,
                        customOffences = customOffences
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DAManagerDialog(
    viewModel: AttendanceViewModel,
    employees: List<Employee>,
    onDismiss: () -> Unit
) {
    val daRecords by viewModel.allDA.collectAsState()
    val recordToEditState = remember { mutableStateOf<DARecord?>(null) }
    var showRecordDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    
    var filterEmployeeId by remember { mutableStateOf("") }
    var showPendingOnly by remember { mutableStateOf(false) }
    var showExcrewOnly by remember { mutableStateOf(false) }

    val filteredRecords = remember(daRecords, filterEmployeeId, showPendingOnly, showExcrewOnly, employees) {
        daRecords.filter { rec ->
            val emp = employees.find { it.employeeNo == rec.employeeId }
            val isExcrew = emp?.position == "Excrew"
            
            val idMatch = filterEmployeeId.isEmpty() || rec.employeeId.contains(filterEmployeeId, ignoreCase = true)
            val statusMatch = !showPendingOnly || (rec.status.trim().equals("Not Yet Reporting", ignoreCase = true) || rec.dateReport.isNullOrBlank())
            val excrewMatch = if (showExcrewOnly) isExcrew else !isExcrew
            
            idMatch && statusMatch && excrewMatch
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        title = { Text("Disciplinary Action (DA) Management") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f).padding(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { 
                            recordToEditState.value = null
                            showRecordDialog = true 
                        }, 
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add DA Record")
                    }
                    Button(
                        onClick = { isEditMode = !isEditMode },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEditMode) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (isEditMode) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(if (isEditMode) Icons.Default.Close else Icons.Default.Edit, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isEditMode) "Done Editing" else "Edit DA Records")
                    }
                }
                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    var expandedFilter by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedFilter,
                        onExpandedChange = { expandedFilter = !expandedFilter },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = if (filterEmployeeId.isEmpty()) "All Employee IDs" else filterEmployeeId,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select ID to Filter") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFilter) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedFilter,
                            onDismissRequest = { expandedFilter = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Employee IDs") },
                                onClick = {
                                    filterEmployeeId = ""
                                    expandedFilter = false
                                }
                            )
                            // Get unique IDs from daRecords and sort them
                            daRecords.map { it.employeeId }.distinct().sorted().forEach { id ->
                                val empName = daRecords.find { it.employeeId == id }?.employeeName?.split(" ")?.firstOrNull() ?: ""
                                DropdownMenuItem(
                                    text = { Text("$id ($empName)") },
                                    onClick = {
                                        filterEmployeeId = id
                                        expandedFilter = false
                                    }
                                )
                            }
                        }
                    }
                    
                    FilterChip(
                        selected = showPendingOnly,
                        onClick = { showPendingOnly = !showPendingOnly },
                        label = { Text("Pending") },
                        leadingIcon = if (showPendingOnly) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                    
                    FilterChip(
                        selected = showExcrewOnly,
                        onClick = { showExcrewOnly = !showExcrewOnly },
                        label = { Text("Excrew") },
                        leadingIcon = if (showExcrewOnly) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                }
                Spacer(Modifier.height(8.dp))

                Box(modifier = Modifier.weight(1f).fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    Column {
                        // Table Header
                        Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).padding(vertical = 4.dp)) {
                            val headers = if (isEditMode) {
                                listOf("Actions", "Name", "Date Report", "DA", "Date of AWOL", "Status", "Verdict/Medcert")
                            } else {
                                listOf("Name", "Date Report", "DA", "Date of AWOL", "Status", "Verdict/Medcert")
                            }
                            headers.forEach {
                                val width = if (it == "Actions") 80.dp else 150.dp
                                Text(it, modifier = Modifier.width(width).padding(horizontal = 4.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(filteredRecords) { rec ->
                                Row(modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.LightGray).padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (isEditMode) {
                                        Row(modifier = Modifier.width(80.dp).padding(horizontal = 4.dp)) {
                                            IconButton(onClick = { 
                                                recordToEditState.value = rec
                                                showRecordDialog = true
                                            }, modifier = Modifier.size(24.dp)) {
                                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                            }
                                            IconButton(onClick = { viewModel.deleteDARecord(rec) }, modifier = Modifier.size(24.dp)) {
                                                Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                    Text(rec.employeeName, modifier = Modifier.width(150.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(formatDate(rec.dateReport), modifier = Modifier.width(150.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(rec.daType, modifier = Modifier.width(150.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(formatAwolDates(rec.dateAwol), modifier = Modifier.width(150.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(rec.status, modifier = Modifier.width(150.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(rec.verdict ?: "---", modifier = Modifier.width(150.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )

    if (showRecordDialog) {
        val editing = recordToEditState.value
        var selectedEmp by remember { mutableStateOf(employees.find { it.id == editing?.employeeId }) }
        var dateReport by remember { mutableStateOf(editing?.dateReport?.let { try { LocalDate.parse(it) } catch(e:Exception) { null } }) }
        
        // Multi-date selection for AWOL
        var selectedAwolDates by remember { 
            mutableStateOf(
                editing?.dateAwol?.split(",")?.mapNotNull { 
                    try { LocalDate.parse(it.trim()) } catch(e:Exception) { null } 
                }?.toSet() ?: emptySet()
            )
        }
        
        var daType by remember { mutableStateOf(editing?.daType ?: "") }
        var customDa by remember { mutableStateOf(if (editing != null && !listOf("First offence Awol", "Second offence Awol", "Third Offence Awol", "Fourth offence Awol", "First offence Late", "Second offence Late", "Third Offence Late", "Fourth offence Late", "No DA (Valid)", "No DA (Emergency)", "No DA (With Medcert)").contains(editing.daType)) editing.daType else "") }
        var verdict by remember { mutableStateOf(editing?.verdict ?: "") }
        
        var showReportPicker by remember { mutableStateOf(false) }
        var showAwolPicker by remember { mutableStateOf(false) }

        val daOptions = listOf(
            "First offence Awol", "Second offence Awol", "Third Offence Awol", "Fourth offence Awol",
            "First offence Late", "Second offence Late", "Third Offence Late", "Fourth offence Late",
            "No DA (Valid)", "No DA (Emergency)", "No DA (With Medcert)",
            "Custom DA"
        )

        AlertDialog(
            onDismissRequest = { showRecordDialog = false },
            title = { Text(if (editing == null) "Add DA Record" else "Edit DA Record") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Employee Selection
                    var expandedEmp by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expandedEmp, onExpandedChange = { expandedEmp = !expandedEmp }) {
                        OutlinedTextField(
                            value = selectedEmp?.let { "${it.firstName} ${it.lastName}" } ?: "Select Employee",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Name") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEmp) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandedEmp, onDismissRequest = { expandedEmp = false }) {
                            employees.forEach { emp ->
                                DropdownMenuItem(
                                    text = { Text("${emp.employeeNo} - ${emp.firstName} ${emp.lastName}") },
                                    onClick = {
                                        selectedEmp = emp
                                        expandedEmp = false
                                    }
                                )
                            }
                        }
                    }

                    // Date Report
                    OutlinedButton(onClick = { showReportPicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(dateReport?.let { "Date Report: $it" } ?: "Select Date Report")
                    }

                    // DA Type
                    var expandedDa by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expandedDa, onExpandedChange = { expandedDa = !expandedDa }) {
                        val displayType = if (daType == "Custom DA" || (daType.isNotBlank() && !daOptions.contains(daType))) "Custom DA" else if (daType.isEmpty()) "Select DA" else daType
                        OutlinedTextField(
                            value = displayType,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("DA") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDa) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandedDa, onDismissRequest = { expandedDa = false }) {
                            daOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        daType = opt
                                        expandedDa = false
                                    }
                                )
                            }
                        }
                    }

                    if (daType == "Custom DA" || (daType.isNotBlank() && !daOptions.contains(daType))) {
                        OutlinedTextField(
                            value = customDa,
                            onValueChange = { 
                                customDa = it
                            },
                            label = { Text("Input Custom DA") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Date of AWOL (Multiple Selection)
                    Column {
                        Text("Dates of AWOL", style = MaterialTheme.typography.labelMedium)
                        OutlinedButton(onClick = { showAwolPicker = true }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Add Date")
                        }
                        
                        @OptIn(ExperimentalLayoutApi::class)
                        if (selectedAwolDates.isNotEmpty()) {
                            FlowRow(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                selectedAwolDates.sorted().forEach { date ->
                                    InputChip(
                                        selected = true,
                                        onClick = { selectedAwolDates = selectedAwolDates.filter { it != date }.toSet() },
                                        label = { Text(date.toString(), fontSize = 10.sp) },
                                        trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(12.dp)) }
                                    )
                                }
                            }
                        }
                    }

                    // Verdict
                    OutlinedTextField(
                        value = verdict,
                        onValueChange = { verdict = it },
                        label = { Text("Verdict/Medcert/No Medcert") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Pickers
                    if (showReportPicker) {
                        val state = rememberDatePickerState()
                        DatePickerDialog(onDismissRequest = { showReportPicker = false }, confirmButton = {
                            TextButton(onClick = {
                                dateReport = state.selectedDateMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                                showReportPicker = false
                            }) { Text("OK") }
                        }) { DatePicker(state = state) }
                    }
                    if (showAwolPicker) {
                        val state = rememberDatePickerState()
                        DatePickerDialog(onDismissRequest = { showAwolPicker = false }, confirmButton = {
                            TextButton(onClick = {
                                state.selectedDateMillis?.let { 
                                    val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                                    selectedAwolDates = selectedAwolDates + date
                                }
                                showAwolPicker = false
                            }) { Text("Add") }
                        }) { DatePicker(state = state) }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedEmp?.let { emp ->
                            val finalDa = if (daType == "Custom DA") customDa else daType
                            val statusText = if (dateReport != null) "Done Report" else "Not Yet Reporting"
                            val awolDatesString = selectedAwolDates.sorted().joinToString(", ")
                            
                            viewModel.saveDARecord(DARecord(
                                id = editing?.id ?: 0,
                                employeeId = emp.id,
                                employeeName = "${emp.firstName} ${emp.lastName}",
                                dateReport = dateReport?.toString(),
                                daType = finalDa,
                                dateAwol = awolDatesString,
                                status = statusText,
                                verdict = verdict
                            ))
                            showRecordDialog = false
                        }
                    },
                    enabled = selectedEmp != null && (daType.isNotBlank() && daType != "Custom DA" || (daType == "Custom DA" && customDa.isNotBlank()))
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showRecordDialog = false }) { Text("Cancel") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttritionManagerDialog(
    viewModel: AttendanceViewModel,
    employees: List<Employee>,
    onDismiss: () -> Unit
) {
    val attritionRecords by viewModel.allAttrition.collectAsState()
    val recordToEditState = remember { mutableStateOf<AttritionRecord?>(null) }
    var showRecordDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        title = { Text("Attrition Management") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f).padding(8.dp)) {
                Button(onClick = { 
                    recordToEditState.value = null
                    showRecordDialog = true 
                }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add Attrition Record")
                }
                Spacer(Modifier.height(16.dp))

                Box(modifier = Modifier.weight(1f).fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    Column {
                        // Table Header
                        Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).padding(vertical = 4.dp)) {
                            listOf("Actions", "ID", "Last Name", "First Name", "Middle Name", "Location", "Date Hired", "Last Day", "Reason", "Position").forEach {
                                val width = if (it == "Actions") 80.dp else 120.dp
                                Text(it, modifier = Modifier.width(width).padding(horizontal = 4.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(attritionRecords) { rec ->
                                Row(modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.LightGray).padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Row(modifier = Modifier.width(80.dp).padding(horizontal = 4.dp)) {
                                        IconButton(onClick = { 
                                            recordToEditState.value = rec
                                            showRecordDialog = true
                                        }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = { viewModel.deleteAttritionRecord(rec) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                    Text(rec.employeeNo, modifier = Modifier.width(120.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(rec.lastName, modifier = Modifier.width(120.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(rec.firstName, modifier = Modifier.width(120.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(rec.middleName, modifier = Modifier.width(120.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(rec.location, modifier = Modifier.width(120.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(rec.dateHired, modifier = Modifier.width(120.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(rec.lastDayOfDuty, modifier = Modifier.width(120.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(rec.reasonForSeparation, modifier = Modifier.width(120.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                    Text(rec.position, modifier = Modifier.width(120.dp).padding(horizontal = 4.dp), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )

    if (showRecordDialog) {
        val editing = recordToEditState.value
        var selectedEmp by remember { mutableStateOf(employees.find { it.employeeNo == editing?.employeeNo }) }
        var lastDay by remember { mutableStateOf(editing?.lastDayOfDuty?.let { try { LocalDate.parse(it) } catch(e:Exception) { LocalDate.now() } } ?: LocalDate.now()) }
        var reason by remember { mutableStateOf(editing?.reasonForSeparation ?: "") }
        var position by remember { mutableStateOf(editing?.position ?: "") }
        var showDatePicker by remember { mutableStateOf(false) }

        val positionOptions = listOf(
            "Coordinator", "Team Leader", "Cashier", "Dispatch", "SS", "CIC", 
            "Assembler", "Noodle", "Backup", "Fyer", "DJ", "Manager", 
            "Assistant Manager", "Senior Crew", "Administrator", "Excrew"
        )

        AlertDialog(
            onDismissRequest = { showRecordDialog = false },
            title = { Text(if (editing == null) "Add Attrition Record" else "Edit Attrition Record") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Employee Selection
                    var expandedEmp by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expandedEmp, onExpandedChange = { expandedEmp = !expandedEmp }) {
                        OutlinedTextField(
                            value = selectedEmp?.let { "${it.employeeNo} - ${it.lastName}" } ?: "Select Employee",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Employee ID") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEmp) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandedEmp, onDismissRequest = { expandedEmp = false }) {
                            employees.forEach { emp ->
                                DropdownMenuItem(
                                    text = { Text("${emp.employeeNo} - ${emp.firstName} ${emp.lastName}") },
                                    onClick = {
                                        selectedEmp = emp
                                        if (editing == null) position = emp.position ?: ""
                                        expandedEmp = false
                                    }
                                )
                            }
                        }
                    }

                    selectedEmp?.let {
                        Text("Name: ${it.firstName} ${it.middleName} ${it.lastName}", style = MaterialTheme.typography.bodySmall)
                        Text("Date Hired: ${it.dateHired ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                    }

                    // Position Selection
                    var expandedPos by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expandedPos, onExpandedChange = { expandedPos = !expandedPos }) {
                        OutlinedTextField(
                            value = position,
                            onValueChange = { position = it },
                            label = { Text("Position") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPos) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandedPos, onDismissRequest = { expandedPos = false }) {
                            positionOptions.forEach { pos ->
                                DropdownMenuItem(
                                    text = { Text(pos) },
                                    onClick = {
                                        position = pos
                                        expandedPos = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Last Day: $lastDay")
                    }

                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Reason for separation") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = lastDay.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        lastDay = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                                    }
                                    showDatePicker = false
                                }) { Text("OK") }
                            }
                        ) { DatePicker(state = datePickerState) }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedEmp?.let { emp ->
                            viewModel.saveAttritionRecord(AttritionRecord(
                                id = editing?.id ?: 0,
                                employeeNo = emp.employeeNo ?: "",
                                lastName = emp.lastName ?: "",
                                firstName = emp.firstName ?: "",
                                middleName = emp.middleName ?: "",
                                location = "CK SM CLARK L1 PMAG",
                                lastDayOfDuty = lastDay.toString(),
                                reasonForSeparation = reason,
                                position = position,
                                dateHired = emp.dateHired ?: ""
                            ))
                            showRecordDialog = false
                        }
                    },
                    enabled = selectedEmp != null && reason.isNotBlank() && position.isNotBlank()
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showRecordDialog = false }) { Text("Cancel") } }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementDialog(
    employees: List<Employee>,
    onDismiss: () -> Unit,
    onSend: (String, String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedTarget by remember { mutableStateOf<String?>(null) } // null means ALL
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Announcement") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Text("Send to:", style = MaterialTheme.typography.labelMedium)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedTarget == null) "All Employees" else employees.find { it.id == selectedTarget }?.let { "${it.firstName ?: ""} ${it.lastName ?: ""}" } ?: "Unknown",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Employees") },
                            onClick = {
                                selectedTarget = null
                                expanded = false
                            }
                        )
                        employees.forEach { emp ->
                            DropdownMenuItem(
                                text = { Text("${emp.firstName ?: ""} ${emp.lastName ?: ""}") },
                                onClick = {
                                    selectedTarget = emp.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank() && content.isNotBlank()) onSend(title, content, selectedTarget) },
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AnnouncementContent(content: String) {
    val imageUrl = remember(content) {
        val urlRegex = "(https?://\\S+\\.(?:jpg|jpeg|png|gif|webp|bmp)(\\?\\S*)?)".toRegex(RegexOption.IGNORE_CASE)
        urlRegex.find(content)?.value
    }

    Column {
        Text(content, style = MaterialTheme.typography.bodyMedium)
        if (imageUrl != null) {
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = imageUrl,
                contentDescription = "Announcement Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Fit,
                placeholder = rememberVectorPainter(Icons.Default.Sync),
                error = rememberVectorPainter(Icons.Default.Info)
            )
        }
    }
}

@Composable
fun ViewAnnouncementsDialog(
    announcements: List<com.sam.myapplication.data.Announcement>,
    onDismiss: () -> Unit,
    isEmployeeView: Boolean = false,
    onMarkAsRead: (Int) -> Unit = {},
    onDelete: (com.sam.myapplication.data.Announcement) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEmployeeView) "Announcements" else "All Sent Announcements") },
        text = {
            if (announcements.isEmpty()) {
                Text("No announcements found.")
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(announcements) { announcement ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = if (!announcement.isRead && isEmployeeView) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(announcement.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    if (!isEmployeeView) {
                                        IconButton(onClick = { onDelete(announcement) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                        }
                                    } else if (!announcement.isRead) {
                                        TextButton(onClick = { onMarkAsRead(announcement.id) }) {
                                            Text("Mark Read")
                                        }
                                    }
                                }
                                AnnouncementContent(announcement.content)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(announcement.createdAt)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                if (!isEmployeeView) {
                                    Text(
                                        text = if (announcement.targetEmployeeId == null) "Sent to: All" else "Sent to: Specific Employee",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun DTRCalendar(
    month: YearMonth,
    dtrRecords: List<DailyTimeRecord>,
    attendanceRecords: List<AttendanceRecord>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(month.minusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(20.dp))
            }
            Text(
                text = "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onMonthChange(month.plusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(20.dp))
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        val firstDayOfMonth = month.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val daysInMonth = month.lengthOfMonth()
        val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7

        Column {
            for (i in 0 until totalCells step 7) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (j in 0 until 7) {
                        val dayIndex = i + j
                        val dayNumber = dayIndex - firstDayOfWeek + 1
                        if (dayNumber in 1..daysInMonth) {
                            val date = month.atDay(dayNumber)
                            val hasDTR = dtrRecords.any { it.date == date.toString() }
                            val isRD = attendanceRecords.any { it.date == date.toString() && it.status == AttendanceStatus.RD }
                            val isSelected = selectedDate == date

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            isRD -> Color.Blue.copy(alpha = 0.2f)
                                            hasDTR -> Color.Green.copy(alpha = 0.2f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 0.dp else 1.dp,
                                        color = when {
                                            isRD -> Color.Blue.copy(alpha = 0.4f)
                                            hasDTR -> Color.Green.copy(alpha = 0.4f)
                                            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                        },
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .clickable { onDateSelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    if (isRD) {
                                        Text("RD", style = MaterialTheme.typography.labelSmall, color = if (isSelected) Color.White else Color.Blue, fontWeight = FontWeight.Bold, modifier = Modifier.scale(0.8f))
                                    }
                                    Text(
                                        text = dayNumber.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else if (isRD) Color.Blue else if (hasDTR) Color(0xFF2E7D32) else Color.Unspecified
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequestCalendar(
    month: YearMonth,
    requests: List<com.sam.myapplication.data.AttendanceRequest>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(month.minusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(20.dp))
            }
            Text(
                text = "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onMonthChange(month.plusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(20.dp))
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        val firstDayOfMonth = month.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val daysInMonth = month.lengthOfMonth()
        val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7

        Column {
            for (i in 0 until totalCells step 7) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (j in 0 until 7) {
                        val dayIndex = i + j
                        val dayNumber = dayIndex - firstDayOfWeek + 1
                        if (dayNumber in 1..daysInMonth) {
                            val date = month.atDay(dayNumber)
                            val dayRequests = requests.filter { it.date == date.toString() }
                            val isSelected = date == selectedDate
                            
                            val hasGranted = dayRequests.any { it.status == com.sam.myapplication.data.RequestStatus.GRANTED }
                            val hasPendingOrRejected = dayRequests.any { it.status == com.sam.myapplication.data.RequestStatus.PENDING || it.status == com.sam.myapplication.data.RequestStatus.REJECTED }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(
                                        when {
                                            hasGranted -> Color.Green.copy(alpha = 0.2f)
                                            hasPendingOrRejected -> Color.Red.copy(alpha = 0.2f)
                                            isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            hasGranted -> Color.Green.copy(alpha = 0.4f)
                                            hasPendingOrRejected -> Color.Red.copy(alpha = 0.4f)
                                            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                        },
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .clickable { onDateSelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when {
                                        hasGranted -> Color(0xFF2E7D32)
                                        hasPendingOrRejected -> Color(0xFFC62828)
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        else -> Color.Unspecified
                                    },
                                    fontWeight = if (isSelected || hasGranted || hasPendingOrRejected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DTRDialog(
    employee: Employee,
    initialMonth: YearMonth,
    attendanceRecords: List<AttendanceRecord>,
    dtrRecords: List<DailyTimeRecord>,
    onDismiss: () -> Unit,
    onSaveDTR: (String, String?, String?, String?, Boolean, String?) -> Unit,
    onDeleteDTR: (String) -> Unit,
    onSetRD: (String, Boolean) -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var month by remember { mutableStateOf(initialMonth) }
    var timeIn by remember { mutableStateOf("") }
    var timeOut by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var hasOvertime by remember { mutableStateOf(false) }
    var overtimeApprovedBy by remember { mutableStateOf("") }
    var showPrintOutput by remember { mutableStateOf(false) }

    // Update fields when date is selected
    LaunchedEffect(selectedDate) {
        val date = selectedDate.toString()
        val existing = dtrRecords.find { it.date == date }
        timeIn = existing?.timeIn ?: ""
        timeOut = existing?.timeOut ?: ""
        note = existing?.note ?: ""
        hasOvertime = existing?.hasOvertime ?: false
        overtimeApprovedBy = existing?.overtimeApprovedBy ?: ""
    }

    if (showPrintOutput) {
        PrintOutputDialog(
            employee = employee,
            dtrRecords = dtrRecords,
            attendanceRecords = attendanceRecords,
            onDismiss = { showPrintOutput = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Time Record Management") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                DTRCalendar(
                    month = month,
                    dtrRecords = dtrRecords,
                    attendanceRecords = attendanceRecords,
                    selectedDate = selectedDate,
                    onDateSelected = { date -> selectedDate = date },
                    onMonthChange = { month = it }
                )

                Spacer(Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isRD = attendanceRecords.any { it.date == selectedDate.toString() && it.status == AttendanceStatus.RD }
                    Checkbox(checked = isRD, onCheckedChange = { checked -> onSetRD(selectedDate.toString(), checked) })
                    Text("Tag as Rest Day (RD)")
                }

                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = timeIn,
                        onValueChange = { timeIn = it },
                        label = { Text("Time In (HH:mm)") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("08:00") }
                    )
                    OutlinedTextField(
                        value = timeOut,
                        onValueChange = { timeOut = it },
                        label = { Text("Time Out (HH:mm)") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("17:00") }
                    )
                }
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hasOvertime, onCheckedChange = { hasOvertime = it })
                    Text("With Overtime?")
                }

                if (hasOvertime) {
                    OutlinedTextField(
                        value = overtimeApprovedBy,
                        onValueChange = { overtimeApprovedBy = it },
                        label = { Text("Approved By") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Manager Name") }
                    )
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { 
                            if (timeIn.isNotBlank() || timeOut.isNotBlank() || note.isNotBlank()) {
                                onSaveDTR(selectedDate.toString(), timeIn, timeOut, note, hasOvertime, overtimeApprovedBy)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = timeIn.isNotBlank() || timeOut.isNotBlank() || note.isNotBlank()
                    ) { Text("Save Record") }
                    
                    OutlinedButton(
                        onClick = { onDeleteDTR(selectedDate.toString()) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("Delete") }
                }

                Button(
                    onClick = { showPrintOutput = true },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(Icons.Default.Print, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Print Out Output Screen")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text("DTR Logs for ${month.month} ${month.year}", style = MaterialTheme.typography.titleSmall)
                
                Column(modifier = Modifier.heightIn(max = 200.dp)) {
                    val currentMonthRecords = dtrRecords.filter { 
                        val d = LocalDate.parse(it.date)
                        d.year == month.year && d.month == month.month
                    }.sortedByDescending { it.date }
                    
                    if (currentMonthRecords.isEmpty()) {
                        Text("No records for this month", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(currentMonthRecords) { log ->
                                val atts = attendanceRecords.filter { it.date == log.date && it.status != AttendanceStatus.LATE }
                                Column(
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .fillMaxWidth()
                                        .clickable { selectedDate = LocalDate.parse(log.date) }
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(log.date, fontWeight = FontWeight.Bold)
                                        if (atts.isNotEmpty()) {
                                            Text(atts.joinToString(", ") { if (it.status == AttendanceStatus.RD) "Rest Day" else if (it.status == AttendanceStatus.RTW) "RTW" else it.status.name }, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                    Text("${convertTo12Hour(log.timeIn)} to ${convertTo12Hour(log.timeOut)}", style = MaterialTheme.typography.bodySmall)
                                    if (log.hasOvertime) {
                                        Text("OT Approved by: ${log.overtimeApprovedBy ?: "N/A"}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF4CAF50))
                                    }
                                    if (log.note != null) Text(log.note, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintOutputDialog(
    employee: Employee,
    dtrRecords: List<DailyTimeRecord>,
    attendanceRecords: List<AttendanceRecord>,
    onDismiss: () -> Unit
) {
    var startDate by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    if (showStartPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(onDismissRequest = { showStartPicker = false }, confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { startDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                showStartPicker = false
            }) { Text("OK") }
        }) { DatePicker(state = state) }
    }
    if (showEndPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(onDismissRequest = { showEndPicker = false }, confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { endDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                showEndPicker = false
            }) { Text("OK") }
        }) { DatePicker(state = state) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Print Preview / Summary") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { showStartPicker = true }, modifier = Modifier.weight(1f)) {
                        Text("Start: $startDate")
                    }
                    OutlinedButton(onClick = { showEndPicker = true }, modifier = Modifier.weight(1f)) {
                        Text("End: $endDate")
                    }
                }
                Spacer(Modifier.height(16.dp))
                
                val filteredRecords = dtrRecords.filter {
                    val d = LocalDate.parse(it.date)
                    (d.isEqual(startDate) || d.isAfter(startDate)) && (d.isEqual(endDate) || d.isBefore(endDate))
                }.sortedBy { it.date }

                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    item {
                        Text("${employee.firstName} ${employee.lastName} (#${employee.employeeNo})", fontWeight = FontWeight.Bold)
                        Text("Period: $startDate to $endDate", style = MaterialTheme.typography.labelSmall)
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    }
                    items(filteredRecords) { rec ->
                        val isRD = attendanceRecords.any { it.date == rec.date && it.status == AttendanceStatus.RD }
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(rec.date, fontWeight = FontWeight.SemiBold)
                                if (isRD) Text("REST DAY", color = Color.Blue, style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${rec.timeIn ?: "--"} - ${rec.timeOut ?: "--"}")
                                if (rec.hasOvertime) {
                                    Text("OT Approved", color = Color(0xFF4CAF50), style = MaterialTheme.typography.labelSmall)
                                    if (!rec.overtimeApprovedBy.isNullOrBlank()) {
                                        Text("By: ${rec.overtimeApprovedBy}", color = Color(0xFF4CAF50), style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun OffenceSection(label: String, currentLevel: Int, onLevelChange: (Int) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..4).forEach { level ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = currentLevel >= level,
                        onCheckedChange = { checked ->
                            if (checked) onLevelChange(level)
                            else onLevelChange(level - 1)
                        }
                    )
                    Text(
                        text = "${level}${getOrdinal(level)}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

fun getOrdinal(n: Int): String {
    return when (n) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    employeeId: String,
    viewModel: AttendanceViewModel,
    onEditEmployee: (String) -> Unit,
    onPayslip: (String) -> Unit,
    onChatClick: (String, String) -> Unit,
    onOpenChatList: () -> Unit,
    onBack: () -> Unit
) {
    val employees by viewModel.allEmployees.collectAsState()
    val employee = employees.find { it.id == employeeId }
    val attendanceRecords by viewModel.getAttendanceForEmployee(employeeId).collectAsState(initial = emptyList())
    val dtrRecords by viewModel.getDTRForEmployee(employeeId).collectAsState(initial = emptyList())
    val requests by viewModel.getRequestsForEmployee(employeeId).collectAsState(initial = emptyList())
    val loggedInEmployee by viewModel.loggedInEmployee.collectAsState()
    
    val today = LocalDate.now().toString()
    val todayDTR = dtrRecords.find { it.date == today }
    
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showRequestDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showDADialog by remember { mutableStateOf(false) }
    var showDTRDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showAnnouncements by remember { mutableStateOf(false) }
    var showAdminChangePasswordDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var showDownloadWarning by remember { mutableStateOf(false) }
    var downloadAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var imageRefreshKey by remember { mutableLongStateOf(0L) }
    
    if (showDownloadWarning) {
        AlertDialog(
            onDismissRequest = { showDownloadWarning = false },
            title = { Text("Warning: Server Sync") },
            text = { Text("Downloading will replace your current local data with the server backup. Any unsaved local changes may be lost. Continue?") },
            confirmButton = {
                Button(onClick = { 
                    downloadAction?.invoke()
                    showDownloadWarning = false
                }) { Text("Confirm Download") }
            },
            dismissButton = { TextButton(onClick = { showDownloadWarning = false }) { Text("Cancel") } }
        )
    }


    var editingField by remember { mutableStateOf<String?>(null) }
    var editingValue by remember { mutableStateOf("") }
    
                // NEW ANNOUNCEMENT POPUP LOGIC
    val announcements by viewModel.getAnnouncementsForEmployee(employeeId).collectAsState(initial = emptyList())
    val lastShownId by viewModel.lastShownAnnouncementId.collectAsState()
    
    val newestUnread = announcements.filter { !it.isRead }.maxByOrNull { it.createdAt }
    
    if (newestUnread != null && newestUnread.id != lastShownId) {
        AlertDialog(
            onDismissRequest = { viewModel.setLastShownAnnouncementId(newestUnread.id) },
            title = { 
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(Icons.Default.Announcement, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("New Announcement", style = MaterialTheme.typography.titleLarge)
                }
            },
            text = {
                Column {
                    Text(newestUnread.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    AnnouncementContent(newestUnread.content)
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.markAnnouncementAsRead(newestUnread.id)
                    viewModel.setLastShownAnnouncementId(newestUnread.id)
                }) {
                    Text("Got it")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    viewModel.setLastShownAnnouncementId(newestUnread.id)
                }) {
                    Text("Close")
                }
            }
        )
    }

    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val detailContext = LocalContext.current
    var birthdayAlertShown by remember { mutableStateOf(hasBirthdayAlertBeenShownToday(detailContext)) }
    val birthdaysToday = remember(employees) {
        employees.filter { emp ->
            emp.position != "Excrew" && calculateDaysToBirthday(emp.birthday) == 0L
        }
    }

    if (birthdaysToday.isNotEmpty() && !birthdayAlertShown) {
        val message = remember(birthdaysToday) {
            getBirthdayGreetingMessage("")
        }
        AlertDialog(
            onDismissRequest = { 
                setBirthdayAlertShownToday(detailContext)
                birthdayAlertShown = true 
            },
            title = { 
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("🎉 Happy Birthday! 🎂", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF4081))
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    birthdaysToday.forEach { emp ->
                        Text(
                            "${emp.firstName} ${emp.lastName}", 
                            style = MaterialTheme.typography.headlineSmall, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        message,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        setBirthdayAlertShownToday(detailContext)
                        birthdayAlertShown = true 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081))
                ) { Text("Hooray! 🎊") }
            }
        )
    }
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                viewModel.uploadProfileImage(employeeId, context.contentResolver, it) { success ->
                    if (success) imageRefreshKey = System.currentTimeMillis()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (success) "Profile picture uploaded successfully" else "Failed to upload profile picture"
                        )
                    }
                }
            }
        }
    )

    if (showAdminChangePasswordDialog && employee != null) {
        var newPassword by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAdminChangePasswordDialog = false },
            title = { Text("Admin: Change Password for ${employee.firstName ?: ""}") },
            text = {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.adminChangePassword(employee.id, newPassword) { success ->
                        if (success) {
                            showAdminChangePasswordDialog = false
                            scope.launch { snackbarHostState.showSnackbar("Password updated successfully") }
                        }
                    }
                }) { Text("Update") }
            },
            dismissButton = { TextButton(onClick = { showAdminChangePasswordDialog = false }) { Text("Cancel") } }
        )
    }

    if (showAnnouncements) {
        val announcements by viewModel.getAnnouncementsForEmployee(employeeId).collectAsState(initial = emptyList())
        ViewAnnouncementsDialog(
            announcements = announcements,
            onDismiss = { showAnnouncements = false },
            isEmployeeView = true,
            onMarkAsRead = { viewModel.markAnnouncementAsRead(it) }
        )
    }

    if (showDADialog && employee != null) {
        DADialog(
            employee = employee,
            onDismiss = { showDADialog = false },
            onConfirm = { updatedEmployee ->
                viewModel.updateEmployee(context, updatedEmployee)
                showDADialog = false
                scope.launch { snackbarHostState.showSnackbar("Disciplinary actions updated") }
            }
        )
    }

    if (showDTRDialog && employee != null) {
        DTRDialog(
            employee = employee,
            initialMonth = currentMonth,
            attendanceRecords = attendanceRecords,
            dtrRecords = dtrRecords,
            onDismiss = { showDTRDialog = false },
            onSaveDTR = { date, tin, tout, n, hasOT, otApproved ->
                viewModel.saveDTRRecord(employeeId, listOf(date), tin, tout, n, hasOT, otApproved)
            },
            onDeleteDTR = { date ->
                viewModel.deleteDTRRecord(employeeId, date)
            },
            onSetRD = { date, isRD ->
                // Task 2: DTR RD should not affect Attendance calendar
                // Removed markAttendance/deleteAttendance calls
            }
        )
    }

    if (showDeleteConfirmation && employee != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Employee") },
            text = { Text("Are you sure you want to delete ${employee.firstName ?: ""} ${employee.lastName ?: ""}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        employee?.let { viewModel.deleteEmployee(it) }
                        showDeleteConfirmation = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showChangePasswordDialog) {
        var newPassword by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Change Password") },
            text = {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.changePassword(newPassword) { success ->
                        if (success) showChangePasswordDialog = false
                    }
                }) { Text("Update") }
            },
            dismissButton = { TextButton(onClick = { showChangePasswordDialog = false }) { Text("Cancel") } }
        )
    }

    if (showRequestDialog) {
        var selectedDate by remember { mutableStateOf(LocalDate.now()) }
        var note by remember { mutableStateOf("") }
        var month by remember { mutableStateOf(YearMonth.now()) }

        val existingRequest = requests.find { it.date == selectedDate.toString() }

        AlertDialog(
            onDismissRequest = { showRequestDialog = false },
            title = { Text("Submit Request") },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp).verticalScroll(rememberScrollState())) {
                    RequestCalendar(
                        month = month,
                        requests = requests,
                        selectedDate = selectedDate,
                        onDateSelected = { selectedDate = it },
                        onMonthChange = { month = it }
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    Text("Selected Date: $selectedDate", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    
                    if (existingRequest != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = when(existingRequest.status) {
                                    com.sam.myapplication.data.RequestStatus.GRANTED -> Color.Green.copy(alpha = 0.1f)
                                    com.sam.myapplication.data.RequestStatus.REJECTED -> Color.Red.copy(alpha = 0.1f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Existing Request Status: ${existingRequest.status}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                Text("Note: ${existingRequest.note ?: "No note"}", style = MaterialTheme.typography.bodySmall)
                                if (existingRequest.adminNote != null) {
                                    Text("Admin Response: ${existingRequest.adminNote}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = note, 
                        onValueChange = { note = it }, 
                        label = { Text(if (existingRequest != null) "Update/Add New Note" else "Note/Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.submitRequest(com.sam.myapplication.data.AttendanceRequest(
                        employeeId = employeeId,
                        date = selectedDate.toString(),
                        type = com.sam.myapplication.data.RequestType.CUSTOM,
                        note = note
                    ))
                    showRequestDialog = false
                    scope.launch { snackbarHostState.showSnackbar("Request submitted for $selectedDate") }
                }) { Text("Submit") }
            },
            dismissButton = {
                TextButton(onClick = { showRequestDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (editingField != null && employee != null) {
        var showDatePickerInEdit by remember { mutableStateOf(false) }
        
        if (editingField == "Resign Date" || editingField == "Birthday" || editingField == "Date Hired" || editingField == "ID Expiration" || editingField == "Health Expiration") {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { editingField = null },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                            if (loggedInEmployee?.isAdmin == true) {
                                val updated = when (editingField) {
                                    "Birthday" -> employee.copy(birthday = selectedDate)
                                    "Date Hired" -> employee.copy(dateHired = selectedDate)
                                    "ID Expiration" -> employee.copy(mallIdExpirationDate = selectedDate)
                                    "Health Expiration" -> employee.copy(healthIdExpirationDate = selectedDate)
                                    "Resign Date" -> employee.copy(resignationDate = selectedDate, isResigned = true)
                                    else -> employee
                                }
                                viewModel.updateEmployee(context, updated)
                            } else {
                                viewModel.submitRequest(com.sam.myapplication.data.AttendanceRequest(
                                    employeeId = employeeId,
                                    date = LocalDate.now().toString(),
                                    type = com.sam.myapplication.data.RequestType.PROFILE_UPDATE,
                                    note = "Update $editingField to: $selectedDate"
                                ))
                            }
                        }
                        editingField = null
                    }) { Text("Save") }
                },
                dismissButton = { TextButton(onClick = { editingField = null }) { Text("Cancel") } }
            ) {
                DatePicker(state = datePickerState)
            }
        } else {
            AlertDialog(
                onDismissRequest = { editingField = null },
                title = { Text("Update $editingField") },
                text = {
                    OutlinedTextField(
                        value = editingValue,
                        onValueChange = { editingValue = it },
                        label = { Text("New $editingField") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (loggedInEmployee?.isAdmin == true) {
                            val updated = when (editingField) {
                                "First Name" -> employee.copy(firstName = editingValue)
                                "Middle Name" -> employee.copy(middleName = editingValue)
                                "Last Name" -> employee.copy(lastName = editingValue)
                                "Location" -> employee.copy(location = editingValue)
                                "Mall ID#" -> employee.copy(mallIdNo = editingValue)
                                "Email" -> employee.copy(email = editingValue)
                                "Contact No." -> employee.copy(cpNumber = editingValue, phoneNumber = editingValue)
                                "TIN#" -> employee.copy(tinNumber = editingValue)
                                "SSS#" -> employee.copy(sss = editingValue)
                                "PhilHealth#" -> employee.copy(philHealth = editingValue)
                                "Pag-IBIG#" -> employee.copy(pagibig = editingValue)
                                "Bank" -> employee.copy(bank = editingValue)
                                "Account No." -> employee.copy(bankAccountNumber = editingValue)
                                "Cap" -> employee.copy(uniformCap = editingValue.toIntOrNull() ?: employee.uniformCap)
                                "Apron" -> employee.copy(uniformApron = editingValue.toIntOrNull() ?: employee.uniformApron)
                                "Shirt" -> employee.copy(uniformShirt = editingValue.toIntOrNull() ?: employee.uniformShirt)
                                "Pants" -> employee.copy(uniformPants = editingValue.toIntOrNull() ?: employee.uniformPants)
                                "Marital Status" -> employee.copy(maritalStatus = editingValue)
                                "Emergency Contact" -> employee.copy(emergencyContactName = editingValue)
                                "Relationship" -> employee.copy(emergencyContactRelationship = editingValue)
                                "Emergency Phone" -> employee.copy(emergencyContactPhone = editingValue)
                                "Position" -> employee.copy(position = editingValue)
                                "Payroll Access" -> employee.copy(payrollAccessCode = editingValue)
                                "Payroll User" -> employee.copy(payrollUsername = editingValue)
                                "Payroll Pass" -> employee.copy(payrollPassword = editingValue)
                                else -> employee
                            }
                            viewModel.updateEmployee(context, updated)
                        } else {
                            if (editingField == "Payroll Pass") {
                                val updated = employee.copy(payrollPassword = editingValue)
                                viewModel.updateEmployee(context, updated)
                            } else {
                                viewModel.submitRequest(com.sam.myapplication.data.AttendanceRequest(
                                    employeeId = employeeId,
                                    date = LocalDate.now().toString(),
                                    type = com.sam.myapplication.data.RequestType.PROFILE_UPDATE,
                                    note = "Update $editingField to: $editingValue"
                                ))
                            }
                        }
                        editingField = null
                    }) {
                        Text(if (loggedInEmployee?.isAdmin == true) "Save" else "Submit Request")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingField = null }) { Text("Cancel") }
                }
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            "Employee Profile", 
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        ConnectivityStatus(viewModel)
                    }
                },
                navigationIcon = {
                    if (loggedInEmployee?.isAdmin == true) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (employee != null && employee.id != loggedInEmployee?.id) {
                        if (loggedInEmployee?.isAdmin == true && !employee.mallIdNo.isNullOrBlank()) {
                            IconButton(onClick = { viewModel.startScrapingMallId(listOf(employee.id)) }) {
                                Icon(Icons.Default.ManageSearch, contentDescription = "Scrape Mall ID")
                            }
                        }
                        IconButton(onClick = { onChatClick(employee.employeeNo ?: employee.id, employee.firstName ?: "User") }) {
                            Icon(Icons.Default.Message, contentDescription = "Message")
                        }
                    } else if (employee != null && employee.id == loggedInEmployee?.id) {
                        // User's own profile - show Inbox/Chat list button
                        IconButton(onClick = onOpenChatList) {
                            val unreadCount by viewModel.getUnreadMessageCount(loggedInEmployee?.employeeNo ?: loggedInEmployee?.id ?: "").collectAsState(0)
                            BadgedBox(badge = { if (unreadCount > 0) Badge { Text(unreadCount.toString()) } }) {
                                Icon(Icons.Default.Chat, contentDescription = "Terminal Chat")
                            }
                        }
                    }
                    val announcements by viewModel.getAnnouncementsForEmployee(employeeId).collectAsState(initial = emptyList())
                    val unreadCount = announcements.count { !it.isRead }
                    
                    val daysToBirthday = calculateDaysToBirthday(employee?.birthday ?: "")
                    val daysToExpiry = calculateDaysToExpiry(employee?.mallIdExpirationDate ?: "")
                    val daysToHealthExpiry = calculateDaysToExpiry(employee?.healthIdExpirationDate ?: "")
                    val daysToResign = if (employee?.isResigned == true && employee.resignationDate != null) calculateDaysToExpiry(employee.resignationDate) else null
                    val totalOffences = (employee?.lateOffenceLevel ?: 0) + (employee?.absentOffenceLevel ?: 0) + (employee?.hamperingOffenceLevel ?: 0) + (employee?.customOffences?.sumOf { it.level } ?: 0)
                    
                    val alertsCount = remember(unreadCount, daysToBirthday, daysToExpiry, daysToHealthExpiry, daysToResign, totalOffences) {
                        var count = unreadCount
                        if (daysToBirthday != null && daysToBirthday <= 7) count++
                        if (daysToExpiry != null && daysToExpiry <= 30) count++
                        if (daysToHealthExpiry != null && daysToHealthExpiry <= 30) count++
                        if (daysToResign != null && daysToResign <= 14) count++
                        if (totalOffences > 0) count++
                        count
                    }

                    IconButton(onClick = { showAnnouncements = true }) {
                        BadgedBox(badge = { if (alertsCount > 0) Badge { Text(alertsCount.toString()) } }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }

                    var showDetailMenu by remember { mutableStateOf(false) }

                    if (loggedInEmployee?.isAdmin == true) {
                        IconButton(onClick = { showDADialog = true }) {
                            Icon(Icons.Default.History, contentDescription = "Disciplinary Action")
                        }
                        IconButton(onClick = { onEditEmployee(employeeId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Employee")
                        }
                        IconButton(onClick = { showDetailMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = showDetailMenu,
                            onDismissRequest = { showDetailMenu = false }
                        ) {
                            if (loggedInEmployee?.id != employeeId) {
                                DropdownMenuItem(
                                    text = { Text("Change Password") },
                                    onClick = {
                                        showDetailMenu = false
                                        showAdminChangePasswordDialog = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.Lock, null) }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Daily Time Record (DTR)") },
                                onClick = {
                                    showDetailMenu = false
                                    showDTRDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.List, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Employee", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showDetailMenu = false
                                    showDeleteConfirmation = true
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                            )
                        }
                    } else {
                        IconButton(onClick = { showDetailMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = showDetailMenu,
                            onDismissRequest = { showDetailMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Daily Time Record (DTR)") },
                                onClick = {
                                    showDetailMenu = false
                                    showDTRDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.List, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Change Password") },
                                onClick = {
                                    showDetailMenu = false
                                    showChangePasswordDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    showDetailMenu = false
                                    viewModel.signOut()
                                    onBack()
                                },
                                leadingIcon = { Icon(Icons.Default.ExitToApp, null) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        if (employee != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Header Profile Section
                val daysToHealthExpiry = calculateDaysToExpiry(employee?.healthIdExpirationDate ?: "")
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                            .padding(20.s())
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier
                                    .size(80.s())
                                    .clickable {
                                        if (loggedInEmployee?.isAdmin == true || loggedInEmployee?.id == employeeId) {
                                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        }
                                    },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shadowElevation = 4.dp,
                                border = androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val model = rememberProfileImageModel(employee, context, imageRefreshKey)
                                    if (model != null) {
                                        AsyncImage(
                                            model = model,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop,
                                            placeholder = rememberVectorPainter(Icons.Default.Person),
                                            error = rememberVectorPainter(Icons.Default.Person),
                                            onError = { state ->
                                                android.util.Log.e("CoilError", "Failed to load image (Detail): $model", state.result.throwable)
                                            }
                                        )
                                    } else {
                                        Icon(Icons.Default.Person, null, modifier = Modifier.size(48.s()))
                                    }
                                }
                            }
                            
                            Spacer(Modifier.width(16.s()))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${employee.firstName ?: ""} ${employee.lastName ?: ""}",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.spS()),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                
                                if (!employee.mallIdStatus.isNullOrBlank()) {
                                    StatusBadgeCompact(employee.mallIdStatus)
                                    Spacer(Modifier.height(4.dp))
                                }

                                // Medal Display for Lates and Overbreaks
                                val medalCountdownStart by viewModel.medalCountdownStart.collectAsState()
                                val medalCounts = remember(attendanceRecords, medalCountdownStart) {
                                    val startDate = try { LocalDate.parse(medalCountdownStart) } catch (e: Exception) { LocalDate.now().minusDays(30) }
                                    val endDate = startDate.plusDays(30)
                                    
                                    val relevant = attendanceRecords.filter { 
                                        val recordDate = try { LocalDate.parse(it.date) } catch (e: Exception) { null }
                                        recordDate != null && (recordDate == startDate || recordDate.isAfter(startDate)) && recordDate.isBefore(endDate) &&
                                        (it.status == AttendanceStatus.LATE || it.status == AttendanceStatus.OVERBREAK) 
                                    }

                                    var gold = 0
                                    var silver = 0
                                    relevant.forEach { rec ->
                                        val note = rec.note ?: ""
                                        if (note.startsWith("(") && note.contains(" mins)")) {
                                            val mins = note.substringAfter("(").substringBefore(" mins)").toIntOrNull() ?: 0
                                            if (mins >= 5) gold++ else silver++
                                        } else {
                                            gold++
                                        }
                                    }
                                    silver to gold
                                }
                                MedalDisplay(silverCount = medalCounts.first, goldCount = medalCounts.second, modifier = Modifier.padding(top = 4.dp))

                                Text(
                                    text = employee.position ?: "",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                Spacer(Modifier.height(8.dp))
                                
                                Surface(
                                    color = if (employee.isCertified == true) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    shape = CircleShape
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (employee.isCertified == true) Icons.Default.Check else Icons.Default.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = if (employee.isCertified == true) Color(0xFF2E7D32) else Color(0xFFC62828)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (employee.isCertified == true) "Certified" else "Not Certified",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (employee.isCertified == true) Color(0xFF2E7D32) else Color(0xFFC62828),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (loggedInEmployee?.id == (employee?.id ?: -1)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                                onClick = {
                                    downloadAction = {
                                        employee?.let {
                                            viewModel.retrieveEmployeeData(it.employeeNo ?: "") { success ->
                                                if (success) imageRefreshKey = System.currentTimeMillis()
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(if (success) "Info retrieved from cloud" else "Retrieval failed")
                                                }
                                            }
                                        }
                                    }
                                    showDownloadWarning = true
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                            shape = MaterialTheme.shapes.medium,
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Sync, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(2.dp))
                            Text("Retrieve", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        }

                        if (loggedInEmployee?.id == employeeId || loggedInEmployee?.isAdmin == true) {
                            Button(
                                onClick = { onPayslip(employeeId) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = MaterialTheme.shapes.medium,
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Icon(Icons.Default.Description, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(2.dp))
                                Text("Payslip", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                            }
                        }

                        if (loggedInEmployee?.isAdmin == false) {
                            Button(
                                onClick = { showRequestDialog = true },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = MaterialTheme.shapes.medium,
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(2.dp))
                                Text("Request", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                            }
                        }
                        
                        OutlinedButton(
                            onClick = { isEditMode = !isEditMode },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = MaterialTheme.shapes.medium,
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            colors = if (isEditMode) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Icon(if (isEditMode) Icons.Default.Close else Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(2.dp))
                            Text(if (isEditMode) "Done" else "Profile", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        }
                    }
                }

                // DTR Actions Section
                if (loggedInEmployee?.id == employeeId) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Daily Time Record", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Button(onClick = { showDTRDialog = true }) {
                                    Icon(Icons.Default.Add, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Add/Edit DTR")
                                }
                            }
                            
                            Spacer(Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.uploadEmployeeData(employeeId) { success ->
                                            scope.launch { snackbarHostState.showSnackbar(if (success) "DTR records uploaded" else "Upload failed") }
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.FileUpload, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Upload")
                                }
                                OutlinedButton(
                                    onClick = {
                                        downloadAction = {
                                            employee?.let {
                                                viewModel.retrieveEmployeeData(it.employeeNo ?: "") { success ->
                                                    scope.launch { snackbarHostState.showSnackbar(if (success) "DTR records downloaded" else "Download failed") }
                                                }
                                            }
                                        }
                                        showDownloadWarning = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.FileDownload, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Download")
                                }
                            }
                            
                            Spacer(Modifier.height(12.dp))
                            
                            val todayLogs = dtrRecords.filter { it.date == today }
                            if (todayLogs.isNotEmpty()) {
                                todayLogs.forEach { log ->
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Today: ${log.timeIn ?: "--:--"} - ${log.timeOut ?: "--:--"}")
                                        if (log.note != null) Text(log.note, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            } else {
                                Text("No DTR for today", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
                        
                Spacer(modifier = Modifier.height(8.dp))
                        
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Active Offences Section
                    if (employee != null && employee.position != "Excrew" && ((employee.lateOffenceLevel ?: 0) > 0 || (employee.absentOffenceLevel ?: 0) > 0 || (employee.hamperingOffenceLevel ?: 0) > 0 || employee.customOffences?.any { it.level > 0 } == true)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Active Disciplinary Actions", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                if ((employee.lateOffenceLevel ?: 0) > 0) {
                                    Text("• Lates: ${employee.lateOffenceLevel}${getOrdinal(employee.lateOffenceLevel ?: 0)} Offence", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                                if ((employee.absentOffenceLevel ?: 0) > 0) {
                                    Text("• Absent: ${employee.absentOffenceLevel}${getOrdinal(employee.absentOffenceLevel ?: 0)} Offence", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                                if ((employee.hamperingOffenceLevel ?: 0) > 0) {
                                    Text("• Hampering: ${employee.hamperingOffenceLevel}${getOrdinal(employee.hamperingOffenceLevel ?: 0)} Offence", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                                employee.customOffences?.forEach { offence ->
                                    if (offence.level > 0) {
                                        Text("• ${offence.name}: ${offence.level}${getOrdinal(offence.level)} Offence", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                    }
                                }
                            }
                        }
                    }

                    // Countdowns Section
                    val daysToBirthday = calculateDaysToBirthday(employee?.birthday ?: "")
                    val daysToExpiry = calculateDaysToExpiry(employee?.mallIdExpirationDate ?: "")
                    val daysToResign = if (employee?.isResigned == true && employee.resignationDate != null) calculateDaysToExpiry(employee.resignationDate) else null
                    val timeHired = calculateTimeHired(employee?.dateHired ?: "")

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (daysToBirthday != null) {
                            StatusCard(
                                label = "Birthday",
                                value = "$daysToBirthday Days",
                                icon = Icons.Default.Cake,
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (daysToResign != null) {
                            StatusCard(
                                label = "Last Day",
                                value = "$daysToResign Days",
                                icon = Icons.Default.ExitToApp,
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (daysToExpiry != null) {
                            val color = if (daysToExpiry < 30) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
                            StatusCard(
                                label = "ID Expiry",
                                value = "$daysToExpiry Days",
                                icon = Icons.Default.CalendarToday,
                                containerColor = color.copy(alpha = 0.7f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (daysToHealthExpiry != null) {
                            val color = if (daysToHealthExpiry < 30) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer
                            StatusCard(
                                label = "Health ID",
                                value = "$daysToHealthExpiry Days",
                                icon = Icons.Default.HealthAndSafety,
                                containerColor = color.copy(alpha = 0.7f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (timeHired != null) {
                        StatusCard(
                            label = "Company Tenure",
                            value = timeHired,
                            icon = Icons.Default.Work,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                    }

                    // Detailed Information
                    SectionHeader("Personal Information")
                    InfoCard {
                        DetailItem(Icons.Default.Badge, "Employee No.", employee?.employeeNo ?: "")
                        DetailItem(
                            Icons.Default.Work, 
                            "Position", 
                            employee?.position ?: "", 
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Position"; editingValue = employee?.position ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Person, 
                            "First Name", 
                            employee?.firstName ?: "", 
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "First Name"; editingValue = employee?.firstName ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Person, 
                            "Middle Name", 
                            employee?.middleName ?: "", 
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Middle Name"; editingValue = employee?.middleName ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Person, 
                            "Last Name", 
                            employee?.lastName ?: "", 
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Last Name"; editingValue = employee?.lastName ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Cake, 
                            "Birthday", 
                            formatDate(employee?.birthday),
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Birthday"; editingValue = employee?.birthday ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Person, 
                            "Marital Status", 
                            employee?.maritalStatus ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Marital Status"; editingValue = employee?.maritalStatus ?: "" }
                        )
                    }

                    SectionHeader("Employment Details")
                    InfoCard {
                        DetailItem(
                            Icons.Default.LocationOn, 
                            "Location", 
                            employee?.location ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Location"; editingValue = employee?.location ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Work, 
                            "Date Hired", 
                            formatDate(employee?.dateHired),
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Date Hired"; editingValue = employee?.dateHired ?: "" }
                        )
                        DetailItem(
                            Icons.Default.AssignmentInd, 
                            "Mall ID#", 
                            employee?.mallIdNo ?: "", 
                            clickable = true,
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Mall ID#"; editingValue = employee?.mallIdNo ?: "" },
                            onClick = {
                                clipboardManager.setText(AnnotatedString(employee?.mallIdNo ?: ""))
                                scope.launch { snackbarHostState.showSnackbar("Mall ID copied") }
                            }
                        )
                        DetailItem(
                            Icons.Default.CalendarToday, 
                            "ID Expiration", 
                            formatDate(employee?.mallIdExpirationDate),
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "ID Expiration"; editingValue = employee?.mallIdExpirationDate ?: "" }
                        )
                        DetailItem(
                            Icons.Default.HealthAndSafety, 
                            "Health ID Expiration", 
                            formatDate(employee?.healthIdExpirationDate),
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Health Expiration"; editingValue = employee?.healthIdExpirationDate ?: "" }
                        )
                        DetailItem(
                            Icons.Default.PersonOff, 
                            "Resigned?", 
                            if (employee?.isResigned == true) "Yes (Last Day: ${employee.resignationDate})" else "No",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Resign Date"; editingValue = employee?.resignationDate ?: LocalDate.now().toString() }
                        )
                    }

                    SectionHeader("Contact & Identification")
                    InfoCard {
                        DetailItem(
                            Icons.Default.Email, 
                            "Email", 
                            employee?.email ?: "", 
                            clickable = true,
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Email"; editingValue = employee?.email ?: "" },
                            onClick = {
                                clipboardManager.setText(AnnotatedString(employee?.email ?: ""))
                                scope.launch { snackbarHostState.showSnackbar("Email copied") }
                            }
                        )
                        DetailItem(
                            Icons.Default.Phone, 
                            "Contact No.", 
                            employee?.cpNumber ?: "", 
                            clickable = true,
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Contact No."; editingValue = employee?.cpNumber ?: "" },
                            onClick = {
                                clipboardManager.setText(AnnotatedString(employee?.cpNumber ?: ""))
                                scope.launch { snackbarHostState.showSnackbar("Cp Number copied") }
                            }
                        )
                        DetailItem(
                            Icons.Default.Description, 
                            "TIN#", 
                            employee?.tinNumber ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "TIN#"; editingValue = employee?.tinNumber ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Description, 
                            "SSS#", 
                            employee?.sss ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "SSS#"; editingValue = employee?.sss ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Description, 
                            "PhilHealth#", 
                            employee?.philHealth ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "PhilHealth#"; editingValue = employee?.philHealth ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Description, 
                            "Pag-IBIG#", 
                            employee?.pagibig ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Pag-IBIG#"; editingValue = employee?.pagibig ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Person, 
                            "Emergency Contact", 
                            employee?.emergencyContactName ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Emergency Contact"; editingValue = employee?.emergencyContactName ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Person, 
                            "Relationship", 
                            employee?.emergencyContactRelationship ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Relationship"; editingValue = employee?.emergencyContactRelationship ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Phone, 
                            "Emergency Phone", 
                            employee?.emergencyContactPhone ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Emergency Phone"; editingValue = employee?.emergencyContactPhone ?: "" }
                        )
                    }

                    SectionHeader("Financial Information")
                    InfoCard {
                        DetailItem(
                            Icons.Default.AccountBalance, 
                            "Bank", 
                            employee?.bank ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Bank"; editingValue = employee?.bank ?: "" }
                        )
                        DetailItem(
                            Icons.Default.CreditCard, 
                            "Account No.", 
                            employee?.bankAccountNumber ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Account No."; editingValue = employee?.bankAccountNumber ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Lock, 
                            "Payroll Access Code", 
                            employee?.payrollAccessCode ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Payroll Access"; editingValue = employee?.payrollAccessCode ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Person, 
                            "Payroll Username", 
                            employee?.payrollUsername ?: "",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Payroll User"; editingValue = employee?.payrollUsername ?: "" }
                        )
                        DetailItem(
                            Icons.Default.Password, 
                            "Payroll Password", 
                            if (employee?.payrollPassword.isNullOrBlank()) "" else "********",
                            showEditIcon = isEditMode,
                            onEdit = { editingField = "Payroll Pass"; editingValue = employee?.payrollPassword ?: "" }
                        )
                    }

                    SectionHeader("Uniform Inventory")
                    InfoCard {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            UniformStatusItem(
                                "Cap", 
                                employee?.uniformCap ?: 0,
                                showEditIcon = isEditMode,
                                onEdit = { editingField = "Cap"; editingValue = (employee?.uniformCap ?: 0).toString() }
                            )
                            UniformStatusItem(
                                "Apron", 
                                employee?.uniformApron ?: 0,
                                showEditIcon = isEditMode,
                                onEdit = { editingField = "Apron"; editingValue = (employee?.uniformApron ?: 0).toString() }
                            )
                            UniformStatusItem(
                                "Shirt", 
                                employee?.uniformShirt ?: 0,
                                showEditIcon = isEditMode,
                                onEdit = { editingField = "Shirt"; editingValue = (employee?.uniformShirt ?: 0).toString() }
                            )
                            UniformStatusItem(
                                "Pants", 
                                employee?.uniformPants ?: 0,
                                showEditIcon = isEditMode,
                                onEdit = { editingField = "Pants"; editingValue = (employee?.uniformPants ?: 0).toString() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "${employee?.firstName ?: ""}'s Attendance",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.uploadEmployeeData(employeeId) { success ->
                                    scope.launch { snackbarHostState.showSnackbar(if (success) "Attendance uploaded" else "Upload failed") }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FileUpload, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Upload")
                        }
                        OutlinedButton(
                            onClick = {
                                downloadAction = {
                                    employee?.let {
                                        viewModel.retrieveEmployeeData(it.employeeNo ?: "") { success ->
                                            if (success) imageRefreshKey = System.currentTimeMillis()
                                            scope.launch { snackbarHostState.showSnackbar(if (success) "Attendance & Photo downloaded" else "Download failed") }
                                        }
                                    }
                                }
                                showDownloadWarning = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FileDownload, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Download")
                        }
                    }
                    
                    AttendanceCalendar(
                        month = currentMonth,
                        attendanceRecords = attendanceRecords,
                        onMonthChange = { currentMonth = it },
                        isAdmin = loggedInEmployee?.isAdmin == true,
                        onStatusChange = { date, statusNotes ->
                            if (loggedInEmployee?.isAdmin == true) {
                                viewModel.markAttendance(employeeId, date.toString(), statusNotes)
                            } else {
                                // Requirement: Employee can edit/propose, but needs admin approval
                                val proposedString = statusNotes.entries.joinToString("; ") { "${it.key.name}: ${it.value ?: "No Note"}" }
                                viewModel.submitRequest(com.sam.myapplication.data.AttendanceRequest(
                                    employeeId = employeeId,
                                    date = date.toString(),
                                    type = com.sam.myapplication.data.RequestType.ATTENDANCE_CORRECTION,
                                    note = "Proposed: $proposedString"
                                ))
                                scope.launch { snackbarHostState.showSnackbar("Correction request submitted for $date") }
                            }
                        },
                        onDeleteAttendance = { date ->
                            if (loggedInEmployee?.isAdmin == true) {
                                viewModel.deleteAttendance(employeeId, date.toString())
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    SectionHeader("Recent Records")
                    attendanceRecords.sortedByDescending { it.date }.take(5).forEach { record ->
                        ListItem(
                            headlineContent = { Text(record.date, fontWeight = FontWeight.Medium) },
                            supportingContent = record.note?.let { { Text(it) } },
                            trailingContent = { StatusBadge(record.status) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                    
                    if (requests.isNotEmpty()) {
                        SectionHeader("Requests Status")
                        requests.sortedByDescending { it.createdAt }.take(5).forEach { req ->
                            ListItem(
                                headlineContent = { Text("${req.type.name} on ${req.date}") },
                                supportingContent = { 
                                    Column {
                                        Text(req.note ?: "")
                                        if (req.adminNote != null) Text("Admin: ${req.adminNote}", fontWeight = FontWeight.Bold)
                                    }
                                },
                                trailingContent = {
                                    val color = when(req.status) {
                                        com.sam.myapplication.data.RequestStatus.PENDING -> Color.Gray
                                        com.sam.myapplication.data.RequestStatus.GRANTED -> Color.Green
                                        com.sam.myapplication.data.RequestStatus.REJECTED -> Color.Red
                                    }
                                    Text(req.status.name, color = color, fontWeight = FontWeight.Bold)
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun ConnectivityStatus(viewModel: AttendanceViewModel) {
    val isOnline by viewModel.isOnline.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (isOnline) Color(0xFF4CAF50) else Color.Gray)
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
        )
        if (syncStatus != null) {
            Spacer(Modifier.width(6.dp))
            Text(
                text = syncStatus!!,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun DailySummaryDialog(
    viewModel: AttendanceViewModel,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var month by remember { mutableStateOf(YearMonth.now()) }
    val attendanceByDate by viewModel.getAttendanceByDate(selectedDate.toString()).collectAsState(initial = emptyList<AttendanceRecord>())
    val dailyNote by viewModel.getDailyNote(selectedDate.toString()).collectAsState(initial = null as com.sam.myapplication.data.DailySummaryNote?)
    val employees by viewModel.allEmployees.collectAsState()
    
    val allAttendance by viewModel.allAttendanceRecords.collectAsState(initial = emptyList())
    val allNotes by viewModel.allDailyNotes.collectAsState(initial = emptyList())
    
    var noteText by remember { mutableStateOf("") }
    
    // Load month data when month changes
    LaunchedEffect(month) {
        viewModel.syncAttendanceForMonth(month.toString())
    }

    // Only load from database when the date changes
    LaunchedEffect(selectedDate) {
        noteText = viewModel.getDailyNote(selectedDate.toString()).first()?.note ?: ""
    }

    // Debounce the save operation so it doesn't "jiggle" while typing
    LaunchedEffect(noteText) {
        if (noteText != (viewModel.getDailyNote(selectedDate.toString()).first()?.note ?: "")) {
            delay(500) // Wait for half a second of inactivity
            viewModel.saveDailyNote(selectedDate.toString(), noteText)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Attendance Summary") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 550.dp).verticalScroll(rememberScrollState())) {
                SummaryCalendar(
                    month = month,
                    selectedDate = selectedDate,
                    attendanceRecords = allAttendance,
                    dailyNotes = allNotes,
                    onDateSelected = { selectedDate = it },
                    onMonthChange = { month = it }
                )
                
                Spacer(Modifier.height(16.dp))
                Text("Selected Date: $selectedDate", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { 
                        noteText = it
                    },
                    label = { Text("Admin Note for this Day") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    minLines = 2
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                val filteredAttendance = attendanceByDate.filter { it.status != com.sam.myapplication.data.AttendanceStatus.RD }
                
                if (filteredAttendance.isEmpty()) {
                    Text("No attendance records for this day.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                } else {
                    filteredAttendance.groupBy { it.status }.forEach { entry ->
                        val status = entry.key
                        val records = entry.value
                        Text(
                            text = "${status.name}: ${records.size}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = when(status) {
                                com.sam.myapplication.data.AttendanceStatus.ABSENT -> Color.Red
                                com.sam.myapplication.data.AttendanceStatus.LATE -> Color(0xFFFFA000)
                                com.sam.myapplication.data.AttendanceStatus.IR -> Color(0xFF4CAF50)
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                        records.sortedWith(compareBy({ 
                            val emp = employees.find { e -> e.id == it.employeeId }
                            getPositionRank(emp?.position)
                        }, {
                            val emp = employees.find { e -> e.id == it.employeeId }
                            emp?.firstName ?: ""
                        })).forEach { rec ->
                            val emp = employees.find { it.id == rec.employeeId }
                            Text(
                                " • ${emp?.firstName ?: "Unknown"} ${emp?.lastName ?: ""} ${if (rec.note != null) "(${rec.note})" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun SummaryCalendar(
    month: YearMonth,
    selectedDate: LocalDate,
    attendanceRecords: List<com.sam.myapplication.data.AttendanceRecord>,
    dailyNotes: List<com.sam.myapplication.data.DailySummaryNote>,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(month.minusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(20.dp))
            }
            Text(
                text = "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onMonthChange(month.plusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(20.dp))
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        val firstDayOfMonth = month.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val daysInMonth = month.lengthOfMonth()
        val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7

        Column {
            for (i in 0 until totalCells step 7) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (j in 0 until 7) {
                        val dayIndex = i + j
                        val dayNumber = dayIndex - firstDayOfWeek + 1
                        if (dayNumber in 1..daysInMonth) {
                            val date = month.atDay(dayNumber)
                            val isSelected = date == selectedDate
                            
                            val hasAbsent = attendanceRecords.any { it.date == date.toString() && it.status == com.sam.myapplication.data.AttendanceStatus.ABSENT }
                            val hasAttendance = attendanceRecords.any { it.date == date.toString() && it.status != com.sam.myapplication.data.AttendanceStatus.RD }
                            val hasNote = dailyNotes.any { it.date == date.toString() && !it.note.isNullOrBlank() }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.2f)
                                    .padding(2.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            hasAbsent -> Color(0xFFFF0000) // Pure Red for Absent
                                            hasNote -> Color(0xFF4CAF50) // Green for Notes
                                            hasAttendance -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = if (hasAbsent || hasNote || hasAttendance) 1.dp else 0.dp,
                                        color = when {
                                            hasAbsent -> Color(0xFFFF0000)
                                            hasNote -> Color(0xFF4CAF50)
                                            hasAttendance -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                            else -> Color.Transparent
                                        },
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .clickable { onDateSelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = dayNumber.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = when {
                                            isSelected -> Color.White
                                            hasAbsent || hasNote -> Color.White
                                            hasAttendance -> MaterialTheme.colorScheme.primary
                                            else -> Color.Unspecified
                                        },
                                        fontWeight = if (isSelected || hasAbsent || hasNote || hasAttendance) FontWeight.Bold else FontWeight.Normal
                                    )
                                    // Remove the dot indicator if it's highlighted Red/Green to keep it clean
                                    if (!isSelected && !hasAbsent && !hasNote && hasAttendance) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary)
                                        )
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, containerColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
    )
}


@Composable
fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)),
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
    }
}

@Composable
fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    label: String, 
    value: String, 
    clickable: Boolean = false, 
    showEditIcon: Boolean = false,
    onEdit: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (clickable) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
        if (showEditIcon) {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit $label", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun UniformStatusItem(label: String, count: Int, showEditIcon: Boolean = false, onEdit: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(count.toString(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            if (showEditIcon) {
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MedalDisplay(silverCount: Int, goldCount: Int, modifier: Modifier = Modifier) {
    if (silverCount <= 0 && goldCount <= 0) return
    
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(goldCount) { index ->
            Icon(
                imageVector = Icons.Default.MilitaryTech,
                contentDescription = "Gold Medal ${index + 1}",
                tint = Color(0xFFFFD700), // Gold
                modifier = Modifier.size(18.dp)
            )
        }
        repeat(silverCount) { index ->
            Icon(
                imageVector = Icons.Default.MilitaryTech,
                contentDescription = "Silver Medal ${index + 1}",
                tint = Color(0xFFC0C0C0), // Silver
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun StatusBadge(status: AttendanceStatus) {
    val color = when (status) {
        AttendanceStatus.IR -> Color(0xFF4CAF50)
        AttendanceStatus.ABSENT -> Color(0xFFF44336)
        AttendanceStatus.LATE -> Color(0xFFFF9800)
        AttendanceStatus.OVERBREAK -> Color(0xFF9C27B0)
        AttendanceStatus.RTW -> Color(0xFFE91E63)
        AttendanceStatus.RD -> Color(0xFF2196F3)
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        val label = when(status) {
            AttendanceStatus.IR -> "Incident Report"
            AttendanceStatus.RD -> "Rest Day"
            AttendanceStatus.RTW -> "RTW"
            else -> status.name
        }
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun StatusBadgeCompact(status: String) {
    val color = when {
        status.contains("Active", ignoreCase = true) -> Color(0xFF4CAF50) // Green
        status.contains("Printing", ignoreCase = true) || status.contains("Pick up", ignoreCase = true) -> Color(0xFF2196F3) // Blue
        status.contains("Renewal", ignoreCase = true) || status.contains("Orientation", ignoreCase = true) -> Color(0xFFFFA000) // Amber/Orange
        status.contains("Rejected", ignoreCase = true) || status.contains("Expired", ignoreCase = true) -> Color(0xFFF44336) // Red
        else -> Color.Gray
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Composable
fun AttendanceCalendar(
    month: YearMonth,
    attendanceRecords: List<com.sam.myapplication.data.AttendanceRecord>,
    onMonthChange: (YearMonth) -> Unit,
    onStatusChange: (LocalDate, Map<AttendanceStatus, String?>) -> Unit,
    onDeleteAttendance: (LocalDate) -> Unit,
    isAdmin: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(month.minusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { onMonthChange(month.plusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        val firstDayOfMonth = month.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday
        val daysInMonth = month.lengthOfMonth()

        val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7
        
        Column {
            for (i in 0 until totalCells step 7) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (j in 0 until 7) {
                        val dayIndex = i + j
                        val dayNumber = dayIndex - firstDayOfWeek + 1
                        if (dayNumber in 1..daysInMonth) {
                            val date = month.atDay(dayNumber)
                            val records = attendanceRecords.filter { it.date == date.toString() }
                            val isRD = records.any { it.status == AttendanceStatus.RD }
                            
                            var showDialog by remember { mutableStateOf(false) }
                            
                            if (showDialog) {
                                var selectedStatusNotes by remember { 
                                    mutableStateOf(records.associate { rec ->
                                        val actualNote = if (rec.status == AttendanceStatus.LATE || rec.status == AttendanceStatus.OVERBREAK) {
                                            if (rec.note?.startsWith("(") == true && rec.note.contains(" mins) ")) {
                                                rec.note.substringAfter(" mins) ")
                                            } else rec.note ?: ""
                                        } else {
                                            rec.note ?: ""
                                        }
                                        rec.status to actualNote
                                    }) 
                                }
                                var selectedStatusMinutes by remember {
                                    mutableStateOf(records.associate { rec ->
                                        val minutes = if (rec.status == AttendanceStatus.LATE || rec.status == AttendanceStatus.OVERBREAK) {
                                            rec.note?.substringAfter("(")?.substringBefore(" mins)")?.toIntOrNull()?.toString() ?: ""
                                        } else ""
                                        rec.status to minutes
                                    })
                                }

                                AlertDialog(
                                    onDismissRequest = { showDialog = false },
                                    title = { Text(if (isAdmin) "Set Status for $date" else "Propose Correction for $date") },
                                    text = {
                                        Column {
                                            AttendanceStatus.entries.forEach { status ->
                                                if (status == AttendanceStatus.RD) return@forEach
                                                val label = when (status) {
                                                    AttendanceStatus.IR -> "Incident Report"
                                                    AttendanceStatus.RTW -> "RTW"
                                                    else -> status.name
                                                }
                                                Column {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                if (selectedStatusNotes.containsKey(status)) {
                                                                    selectedStatusNotes = selectedStatusNotes - status
                                                                    selectedStatusMinutes = selectedStatusMinutes - status
                                                                } else {
                                                                    selectedStatusNotes = selectedStatusNotes + (status to "")
                                                                    selectedStatusMinutes = selectedStatusMinutes + (status to "")
                                                                }
                                                            }
                                                            .padding(vertical = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Checkbox(
                                                            checked = selectedStatusNotes.containsKey(status),
                                                            onCheckedChange = { checked ->
                                                                if (checked) {
                                                                    selectedStatusNotes = selectedStatusNotes + (status to "")
                                                                    selectedStatusMinutes = selectedStatusMinutes + (status to "")
                                                                } else {
                                                                    selectedStatusNotes = selectedStatusNotes - status
                                                                    selectedStatusMinutes = selectedStatusMinutes - status
                                                                }
                                                            }
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(label)
                                                    }
                                                    
                                                    if (selectedStatusNotes.containsKey(status)) {
                                                        if (status == AttendanceStatus.LATE || status == AttendanceStatus.OVERBREAK) {
                                                            OutlinedTextField(
                                                                value = selectedStatusMinutes[status] ?: "",
                                                                onValueChange = { 
                                                                    if (it.all { char -> char.isDigit() }) {
                                                                        selectedStatusMinutes = selectedStatusMinutes + (status to it)
                                                                    }
                                                                },
                                                                label = { Text("Minutes") },
                                                                modifier = Modifier.fillMaxWidth().padding(start = 32.dp, bottom = 8.dp),
                                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                                textStyle = MaterialTheme.typography.bodySmall
                                                            )
                                                        }

                                                        OutlinedTextField(
                                                            value = selectedStatusNotes[status] ?: "",
                                                            onValueChange = { newNote ->
                                                                selectedStatusNotes = selectedStatusNotes + (status to newNote)
                                                            },
                                                            label = { Text("Note for $label") },
                                                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp, bottom = 8.dp),
                                                            textStyle = MaterialTheme.typography.bodySmall
                                                        )
                                                    }
                                                }
                                            }

                                            if (isAdmin && records.isNotEmpty()) {
                                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            onDeleteAttendance(date)
                                                            showDialog = false
                                                        }
                                                        .padding(vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Clear/Delete All Status", color = MaterialTheme.colorScheme.error)
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        Button(onClick = {
                                            val finalNotes = selectedStatusNotes.mapValues { (status, note) ->
                                                val mins = selectedStatusMinutes[status]
                                                val formattedNote = if (!mins.isNullOrBlank() && (status == AttendanceStatus.LATE || status == AttendanceStatus.OVERBREAK)) {
                                                    "($mins mins) ${note ?: ""}".trim()
                                                } else {
                                                    note
                                                }
                                                if (formattedNote.isNullOrBlank()) null else formattedNote
                                            }
                                            onStatusChange(date, finalNotes)
                                            showDialog = false
                                        }) {
                                            Text(if (isAdmin) "Save" else "Submit Request")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                                    }
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .let { baseModifier ->
                                        if (records.isEmpty()) {
                                            baseModifier.background(
                                                if (date == LocalDate.now()) MaterialTheme.colorScheme.primaryContainer 
                                                else Color.Transparent
                                            )
                                        } else if (records.size == 1) {
                                            val color = when (records[0].status) {
                                                AttendanceStatus.IR -> Color(0xFF4CAF50)
                                                AttendanceStatus.ABSENT -> Color(0xFFF44336)
                                                AttendanceStatus.LATE -> Color(0xFFFF9800)
                                                AttendanceStatus.OVERBREAK -> Color(0xFF9C27B0)
                                                AttendanceStatus.RD -> Color(0xFF2196F3)
                                                AttendanceStatus.RTW -> Color(0xFF795548)
                                            }.copy(alpha = 0.35f)
                                            baseModifier.background(color)
                                        } else {
                                            val colors = records.map { rec ->
                                                when (rec.status) {
                                                    AttendanceStatus.IR -> Color(0xFF4CAF50)
                                                    AttendanceStatus.ABSENT -> Color(0xFFF44336)
                                                    AttendanceStatus.LATE -> Color(0xFFFF9800)
                                                    AttendanceStatus.OVERBREAK -> Color(0xFF9C27B0)
                                                    AttendanceStatus.RD -> Color(0xFF2196F3)
                                                    AttendanceStatus.RTW -> Color(0xFF795548)
                                                }.copy(alpha = 0.45f)
                                            }
                                            baseModifier.background(Brush.linearGradient(colors))
                                        }
                                    }
                                    .border(
                                        width = if (date == LocalDate.now()) 2.dp else 1.dp,
                                        color = if (date == LocalDate.now()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .clickable { showDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    if (isRD) {
                                        Text("RD", style = MaterialTheme.typography.labelSmall, color = Color.Red, fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        text = dayNumber.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (date == LocalDate.now()) MaterialTheme.colorScheme.primary else Color.Unspecified,
                                        fontWeight = if (records.isNotEmpty()) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Summary Section
        val monthlyRecords = attendanceRecords.filter { 
            val date = LocalDate.parse(it.date)
            date.year == month.year && date.month == month.month
        }
        
        val absents = monthlyRecords.count { it.status == AttendanceStatus.ABSENT }
        val irs = monthlyRecords.count { it.status == AttendanceStatus.IR }
        val lates = monthlyRecords.count { it.status == AttendanceStatus.LATE }
        val overbreaks = monthlyRecords.count { it.status == AttendanceStatus.OVERBREAK }

        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Monthly Summary",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SummaryItem(label = "Absent", count = absents, color = Color(0xFFF44336))
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    SummaryItem(label = "IR", count = irs, color = Color(0xFF4CAF50))
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    SummaryItem(label = "Late", count = lates, color = Color(0xFFFF9800))
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    SummaryItem(label = "OB", count = overbreaks, color = Color(0xFF9C27B0))
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                
                // Overall Monthly Averages
                val allMonths = attendanceRecords.groupBy { 
                    val date = LocalDate.parse(it.date)
                    YearMonth.from(date)
                }
                val numMonths = allMonths.size.coerceAtLeast(1)
                
                val avgAbsents = attendanceRecords.count { it.status == AttendanceStatus.ABSENT }.toFloat() / numMonths
                val avgIrs = attendanceRecords.count { it.status == AttendanceStatus.IR }.toFloat() / numMonths
                val avgLates = attendanceRecords.count { it.status == AttendanceStatus.LATE }.toFloat() / numMonths
                val avgOverbreaks = attendanceRecords.count { it.status == AttendanceStatus.OVERBREAK }.toFloat() / numMonths

                Text(
                    text = "Overall Monthly Average",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AverageSummaryItem(label = "Avg Abs", average = avgAbsents, color = Color(0xFFF44336))
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    AverageSummaryItem(label = "Avg IR", average = avgIrs, color = Color(0xFF4CAF50))
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    AverageSummaryItem(label = "Avg Late", average = avgLates, color = Color(0xFFFF9800))
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    AverageSummaryItem(label = "Avg OB", average = avgOverbreaks, color = Color(0xFF9C27B0))
                }
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
    }
}

@Composable
fun AverageSummaryItem(label: String, average: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(
            text = "%.1f".format(average),
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeScreen(
    onEmployeeAdded: (Employee) -> Unit,
    onBack: () -> Unit
) {
    var employeeNo by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var mallIdNo by remember { mutableStateOf("") }
    var mallIdExpirationDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var dateHired by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cpNumber by remember { mutableStateOf("") }
    var tinNumber by remember { mutableStateOf("") }
    var sss by remember { mutableStateOf("") }
    var philHealth by remember { mutableStateOf("") }
    var pagibig by remember { mutableStateOf("") }
    var healthIdExpirationDate by remember { mutableStateOf("") }
    var bank by remember { mutableStateOf("") }
    var bankAccountNumber by remember { mutableStateOf("") }
    var payrollAccessCode by remember { mutableStateOf("FISC") }
    var payrollUsername by remember { mutableStateOf("") }
    var payrollPassword by remember { mutableStateOf("") }

    // Keep username in sync with employee number
    LaunchedEffect(employeeNo) {
        payrollUsername = employeeNo
    }
    var position by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<String?>(null) }
    
    var uniformCap by remember { mutableStateOf("0") }
    var uniformApron by remember { mutableStateOf("0") }
    var uniformShirt by remember { mutableStateOf("0") }
    var uniformPants by remember { mutableStateOf("0") }
    var isCertified by remember { mutableStateOf(false) }
    var certifiedPositions by remember { mutableStateOf(emptyList<String>()) }
    var isAdmin by remember { mutableStateOf(false) }
    var isResigned by remember { mutableStateOf(false) }
    var resignationDate by remember { mutableStateOf("") }
    var maritalStatus by remember { mutableStateOf("") }
    var emergencyContactName by remember { mutableStateOf("") }
    var emergencyContactRelationship by remember { mutableStateOf("") }
    var emergencyContactPhone by remember { mutableStateOf("") }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            profileImageUri = uri?.toString()
            // To make URI persistable across reboots if needed (for file URIs)
            uri?.let {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    )


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Add Employee") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Pick Profile Picture",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
            Text("Tap to add photo", style = MaterialTheme.typography.labelSmall)
            
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = employeeNo, onValueChange = { employeeNo = it }, label = { Text("Employee #") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = middleName, onValueChange = { middleName = it }, label = { Text("Middle Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = mallIdNo, onValueChange = { mallIdNo = it }, label = { Text("Mall ID#") }, modifier = Modifier.fillMaxWidth())
            
            var showExpiryPicker by remember { mutableStateOf(false) }
            if (showExpiryPicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showExpiryPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                mallIdExpirationDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                            }
                            showExpiryPicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExpiryPicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            OutlinedTextField(
                value = mallIdExpirationDate,
                onValueChange = { },
                label = { Text("Mall ID Expiration Date") },
                modifier = Modifier.fillMaxWidth().clickable { showExpiryPicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            
            var showHiredPicker by remember { mutableStateOf(false) }
            if (showHiredPicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showHiredPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                dateHired = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                            }
                            showHiredPicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showHiredPicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            OutlinedTextField(
                value = dateHired,
                onValueChange = { },
                label = { Text("Date Hired") },
                modifier = Modifier.fillMaxWidth().clickable { showHiredPicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
            
            var showBirthdayPicker by remember { mutableStateOf(false) }
            if (showBirthdayPicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showBirthdayPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                birthday = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                            }
                            showBirthdayPicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showBirthdayPicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            OutlinedTextField(
                value = birthday,
                onValueChange = { },
                label = { Text("Birthday") },
                modifier = Modifier.fillMaxWidth().clickable { showBirthdayPicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = cpNumber, onValueChange = { cpNumber = it }, label = { Text("Cp Number") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tinNumber, onValueChange = { tinNumber = it }, label = { Text("Tin#") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = sss, onValueChange = { sss = it }, label = { Text("SSS#") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = philHealth, onValueChange = { philHealth = it }, label = { Text("PhilHealth#") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = pagibig, onValueChange = { pagibig = it }, label = { Text("Pagibig#") }, modifier = Modifier.fillMaxWidth())
            
            var showHealthPicker by remember { mutableStateOf(false) }
            if (showHealthPicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showHealthPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                healthIdExpirationDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                            }
                            showHealthPicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showHealthPicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            OutlinedTextField(
                value = healthIdExpirationDate,
                onValueChange = { },
                label = { Text("Health ID Expiration Date") },
                modifier = Modifier.fillMaxWidth().clickable { showHealthPicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            val banks = listOf("Gcash", "Union Bank", "Maya", "Security Bank")
            var bankExpanded by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = bankExpanded,
                onExpandedChange = { bankExpanded = !bankExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = bank,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Bank") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bankExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = bankExpanded,
                    onDismissRequest = { bankExpanded = false }
                ) {
                    banks.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                bank = selectionOption
                                bankExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
            
            OutlinedTextField(value = bankAccountNumber, onValueChange = { bankAccountNumber = it }, label = { Text("Bank Account #") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))
            Text("Payroll Portal Credentials", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            OutlinedTextField(
                value = payrollAccessCode, 
                onValueChange = { /* Fixed value */ }, 
                label = { Text("Payroll Access Code") }, 
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false
            )
            OutlinedTextField(
                value = payrollUsername, 
                onValueChange = { payrollUsername = it }, 
                label = { Text("Payroll Username") }, 
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Default: Employee #") }
            )
            OutlinedTextField(value = payrollPassword, onValueChange = { payrollPassword = it }, label = { Text("Payroll Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())

            val maritalStatuses = listOf("Single", "Married", "Widowed", "Solo Parent")
            var maritalExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = maritalExpanded,
                onExpandedChange = { maritalExpanded = !maritalExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = maritalStatus,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Marital Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = maritalExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = maritalExpanded,
                    onDismissRequest = { maritalExpanded = false }
                ) {
                    maritalStatuses.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                maritalStatus = selectionOption
                                maritalExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Emergency Contact Info", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            OutlinedTextField(value = emergencyContactName, onValueChange = { emergencyContactName = it }, label = { Text("Contact Person in case of Emergency") }, modifier = Modifier.fillMaxWidth())
            
            val relationships = listOf("Brother", "Sister", "Mother", "Father", "Partner", "Cousin")
            var relExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = relExpanded,
                onExpandedChange = { relExpanded = !relExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = emergencyContactRelationship,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Relationship") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = relExpanded,
                    onDismissRequest = { relExpanded = false }
                ) {
                    relationships.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                emergencyContactRelationship = selectionOption
                                relExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
            OutlinedTextField(value = emergencyContactPhone, onValueChange = { emergencyContactPhone = it }, label = { Text("Emergency Cellphone") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            
            val positions = listOf("Team Leader", "Manager", "Assistant Manager", "Cashier", "Dispatch", "SS", "DJ", "CIC", "Assembler", "Fryer", "Backup", "Noodle", "So", "Excrew", "Buzzer", "Senior Crew", "Coordinator")
            var expanded by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = position,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Position") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    positions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                position = selectionOption
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Uniform Items", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = uniformCap, onValueChange = { uniformCap = it }, label = { Text("Cap") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = uniformApron, onValueChange = { uniformApron = it }, label = { Text("Apron") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = uniformShirt, onValueChange = { uniformShirt = it }, label = { Text("Shirt") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = uniformPants, onValueChange = { uniformPants = it }, label = { Text("Pants") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isCertified = !isCertified }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isCertified, onCheckedChange = { isCertified = it })
                Text("Certified")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isAdmin = !isAdmin }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
                Text("Admin Access", fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isResigned = !isResigned }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isResigned, onCheckedChange = { isResigned = it })
                Text("Resigned")
            }

            if (isResigned) {
                var showResignPicker by remember { mutableStateOf(false) }
                if (showResignPicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showResignPicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    resignationDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                                }
                                showResignPicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showResignPicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                OutlinedTextField(
                    value = resignationDate,
                    onValueChange = { },
                    label = { Text("Last Day / Resignation Date") },
                    modifier = Modifier.fillMaxWidth().clickable { showResignPicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )
            }

            if (isCertified) {
                Text("Certified in:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.Start))
                val posList = listOf("Team Leader", "Manager", "Assistant Manager", "Cashier", "Dispatch", "SS", "DJ", "CIC", "Assembler", "Fryer", "Backup", "Noodle", "So", "Excrew", "Buzzer", "Senior Crew", "Coordinator")
                posList.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        row.forEach { pos ->
                            Row(
                                modifier = Modifier.weight(1f).clickable {
                                    certifiedPositions = if (certifiedPositions.contains(pos)) {
                                        certifiedPositions - pos
                                    } else {
                                        certifiedPositions + pos
                                    }
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = certifiedPositions.contains(pos),
                                    onCheckedChange = { checked ->
                                        certifiedPositions = if (checked) {
                                            certifiedPositions + pos
                                        } else {
                                            certifiedPositions - pos
                                        }
                                    }
                                )
                                Text(pos, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (firstName.isNotBlank() && lastName.isNotBlank() && employeeNo.isNotBlank()) {
                        onEmployeeAdded(
                            Employee(
                                id = employeeNo,
                                employeeNo = employeeNo,
                                firstName = firstName,
                                middleName = middleName,
                                lastName = lastName,
                                mallIdNo = mallIdNo,
                                mallIdExpirationDate = mallIdExpirationDate,
                                location = location,
                                dateHired = dateHired,
                                birthday = birthday,
                                email = email,
                                phoneNumber = cpNumber,
                                cpNumber = cpNumber,
                                tinNumber = tinNumber,
                                sss = sss,
                                philHealth = philHealth,
                                pagibig = pagibig,
                                bank = bank,
                                bankAccountNumber = bankAccountNumber,
                                payrollAccessCode = payrollAccessCode,
                                payrollUsername = payrollUsername,
                                payrollPassword = payrollPassword,
                                department = "", // Kept for compatibility if needed
                                position = position,
                                profileImageUri = profileImageUri,
                                uniformCap = uniformCap.toIntOrNull() ?: 0,
                                uniformApron = uniformApron.toIntOrNull() ?: 0,
                                uniformShirt = uniformShirt.toIntOrNull() ?: 0,
                                uniformPants = uniformPants.toIntOrNull() ?: 0,
                                isCertified = isCertified,
                                certifiedPositions = certifiedPositions,
                                isAdmin = isAdmin,
                                isResigned = isResigned,
                                resignationDate = if (isResigned) resignationDate else null,
                                maritalStatus = maritalStatus,
                                emergencyContactName = emergencyContactName,
                                emergencyContactRelationship = emergencyContactRelationship,
                                emergencyContactPhone = emergencyContactPhone,
                                healthIdExpirationDate = healthIdExpirationDate
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Employee")
            }
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmployeeScreen(
    employeeId: String,
    viewModel: AttendanceViewModel,
    onEmployeeUpdated: (Employee) -> Unit,
    onBack: () -> Unit
) {
    val employees by viewModel.allEmployees.collectAsState()
    val employee = employees.find { it.id == employeeId }

    if (employee != null) {
        var employeeNo by remember { mutableStateOf(employee.employeeNo ?: "") }
        var firstName by remember { mutableStateOf(employee.firstName ?: "") }
        var middleName by remember { mutableStateOf(employee.middleName ?: "") }
        var lastName by remember { mutableStateOf(employee.lastName ?: "") }
        var mallIdNo by remember { mutableStateOf(employee.mallIdNo ?: "") }
        var mallIdExpirationDate by remember { mutableStateOf(employee.mallIdExpirationDate ?: "") }
        var location by remember { mutableStateOf(employee.location ?: "") }
        var dateHired by remember { mutableStateOf(employee.dateHired ?: "") }
        var birthday by remember { mutableStateOf(employee.birthday ?: "") }
        var email by remember { mutableStateOf(employee.email ?: "") }
        var cpNumber by remember { mutableStateOf(employee.cpNumber ?: "") }
        var tinNumber by remember { mutableStateOf(employee.tinNumber ?: "") }
        var sss by remember { mutableStateOf(employee.sss ?: "") }
        var philHealth by remember { mutableStateOf(employee.philHealth ?: "") }
        var pagibig by remember { mutableStateOf(employee.pagibig ?: "") }
        var healthIdExpirationDate by remember { mutableStateOf(employee.healthIdExpirationDate ?: "") }
        var bank by remember { mutableStateOf(employee.bank ?: "") }
        var bankAccountNumber by remember { mutableStateOf(employee.bankAccountNumber ?: "") }
        var payrollAccessCode by remember { mutableStateOf(employee.payrollAccessCode ?: "") }
        var payrollUsername by remember { mutableStateOf(employee.payrollUsername ?: "") }
        var payrollPassword by remember { mutableStateOf(employee.payrollPassword ?: "") }
        var portalUsername by remember { mutableStateOf(employee.portalUsername ?: "") }
        var portalPassword by remember { mutableStateOf(employee.portalPassword ?: "") }
        var position by remember { mutableStateOf(employee.position ?: "") }
        var profileImageUri by remember { mutableStateOf(employee.profileImageUri) }

        var uniformCap by remember { mutableStateOf(employee.uniformCap?.toString() ?: "0") }
        var uniformApron by remember { mutableStateOf(employee.uniformApron?.toString() ?: "0") }
        var uniformShirt by remember { mutableStateOf(employee.uniformShirt?.toString() ?: "0") }
        var uniformPants by remember { mutableStateOf(employee.uniformPants?.toString() ?: "0") }
        var isCertified by remember { mutableStateOf(employee.isCertified == true) }
        var certifiedPositions by remember { mutableStateOf(employee.certifiedPositions ?: emptyList<String>()) }
        var isAdmin by remember { mutableStateOf(employee.isAdmin == true) }
        var isResigned by remember { mutableStateOf(employee.isResigned == true) }
        var resignationDate by remember { mutableStateOf(employee.resignationDate ?: "") }
        var maritalStatus by remember { mutableStateOf(employee.maritalStatus ?: "") }
        var emergencyContactName by remember { mutableStateOf(employee.emergencyContactName ?: "") }
        var emergencyContactRelationship by remember { mutableStateOf(employee.emergencyContactRelationship ?: "") }
        var emergencyContactPhone by remember { mutableStateOf(employee.emergencyContactPhone ?: "") }

        val context = LocalContext.current
        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                profileImageUri = uri?.toString()
                uri?.let {
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            it,
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        )


    Scaffold(
            topBar = {
                TopAppBar(title = { Text("Edit Employee") })
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Pick Profile Picture",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
                Text("Tap to change photo", style = MaterialTheme.typography.labelSmall)
                
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = employeeNo, onValueChange = { employeeNo = it }, label = { Text("Employee #") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = middleName, onValueChange = { middleName = it }, label = { Text("Middle Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = mallIdNo, onValueChange = { mallIdNo = it }, label = { Text("Mall ID#") }, modifier = Modifier.fillMaxWidth())
                
                var showExpiryPicker by remember { mutableStateOf(false) }
                if (showExpiryPicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showExpiryPicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    mallIdExpirationDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                                }
                                showExpiryPicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showExpiryPicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                OutlinedTextField(
                    value = mallIdExpirationDate,
                    onValueChange = { },
                    label = { Text("Mall ID Expiration Date") },
                    modifier = Modifier.fillMaxWidth().clickable { showExpiryPicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )

                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                
                var showHiredPicker by remember { mutableStateOf(false) }
                if (showHiredPicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showHiredPicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    dateHired = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                                }
                                showHiredPicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showHiredPicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                OutlinedTextField(
                    value = dateHired,
                    onValueChange = { },
                    label = { Text("Date Hired") },
                    modifier = Modifier.fillMaxWidth().clickable { showHiredPicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )

                var showBirthdayPicker by remember { mutableStateOf(false) }
                if (showBirthdayPicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showBirthdayPicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    birthday = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                                }
                                showBirthdayPicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showBirthdayPicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                OutlinedTextField(
                    value = birthday,
                    onValueChange = { },
                    label = { Text("Birthday") },
                    modifier = Modifier.fillMaxWidth().clickable { showBirthdayPicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cpNumber, onValueChange = { cpNumber = it }, label = { Text("Cp Number") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = tinNumber, onValueChange = { tinNumber = it }, label = { Text("Tin#") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = sss, onValueChange = { sss = it }, label = { Text("SSS#") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = philHealth, onValueChange = { philHealth = it }, label = { Text("PhilHealth#") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = pagibig, onValueChange = { pagibig = it }, label = { Text("Pagibig#") }, modifier = Modifier.fillMaxWidth())
                
                var showHealthPicker by remember { mutableStateOf(false) }
                if (showHealthPicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showHealthPicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    healthIdExpirationDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                                }
                                showHealthPicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showHealthPicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                OutlinedTextField(
                    value = healthIdExpirationDate,
                    onValueChange = { },
                    label = { Text("Health ID Expiration Date") },
                    modifier = Modifier.fillMaxWidth().clickable { showHealthPicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )

                val banks = listOf("Gcash", "Union Bank", "Maya", "Security Bank")
                var bankExpanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = bankExpanded,
                    onExpandedChange = { bankExpanded = !bankExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = bank,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Bank") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bankExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = bankExpanded,
                        onDismissRequest = { bankExpanded = false }
                    ) {
                        banks.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    bank = selectionOption
                                    bankExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
                
                OutlinedTextField(value = bankAccountNumber, onValueChange = { bankAccountNumber = it }, label = { Text("Bank Account #") }, modifier = Modifier.fillMaxWidth())
                
                Spacer(modifier = Modifier.height(8.dp))
                Text("Payroll Portal Credentials", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
                OutlinedTextField(
                    value = payrollAccessCode, 
                    onValueChange = { /* Fixed */ }, 
                    label = { Text("Payroll Access Code") }, 
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false
                )
                OutlinedTextField(
                    value = payrollUsername, 
                    onValueChange = { payrollUsername = it }, 
                    label = { Text("Payroll Username") }, 
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Default: Employee #") }
                )
                OutlinedTextField(value = payrollPassword, onValueChange = { payrollPassword = it }, label = { Text("Payroll Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())

                val isTargetAdmin = employees.find { it.id == employeeId }?.isAdmin == true
                if (isTargetAdmin) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text("Portal Auto-Login Details", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(value = portalUsername, onValueChange = { portalUsername = it }, label = { Text("Portal Username") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = portalPassword, onValueChange = { portalPassword = it }, label = { Text("Portal Password") }, modifier = Modifier.fillMaxWidth())
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }

                val maritalStatuses = listOf("Single", "Married", "Widowed", "Solo Parent")
                var maritalExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = maritalExpanded,
                    onExpandedChange = { maritalExpanded = !maritalExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = maritalStatus,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Marital Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = maritalExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = maritalExpanded,
                        onDismissRequest = { maritalExpanded = false }
                    ) {
                        maritalStatuses.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    maritalStatus = selectionOption
                                    maritalExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Emergency Contact Info", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
                OutlinedTextField(value = emergencyContactName, onValueChange = { emergencyContactName = it }, label = { Text("Contact Person in case of Emergency") }, modifier = Modifier.fillMaxWidth())
                
                val relationships = listOf("Brother", "Sister", "Mother", "Father", "Partner", "Cousin")
                var relExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = relExpanded,
                    onExpandedChange = { relExpanded = !relExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = emergencyContactRelationship,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Relationship") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = relExpanded,
                        onDismissRequest = { relExpanded = false }
                    ) {
                        relationships.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    emergencyContactRelationship = selectionOption
                                    relExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
                OutlinedTextField(value = emergencyContactPhone, onValueChange = { emergencyContactPhone = it }, label = { Text("Emergency Cellphone") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                
                val positions = listOf("Team Leader", "Manager", "Assistant Manager", "Cashier", "Dispatch", "SS", "DJ", "CIC", "Assembler", "Fryer", "Backup", "Noodle", "So", "Excrew", "Buzzer", "Senior Crew", "Coordinator")
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = position,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Position") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        positions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    position = selectionOption
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Uniform Items", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = uniformCap, onValueChange = { uniformCap = it }, label = { Text("Cap") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = uniformApron, onValueChange = { uniformApron = it }, label = { Text("Apron") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = uniformShirt, onValueChange = { uniformShirt = it }, label = { Text("Shirt") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = uniformPants, onValueChange = { uniformPants = it }, label = { Text("Pants") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCertified = !isCertified }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isCertified, onCheckedChange = { isCertified = it })
                    Text("Certified")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAdmin = !isAdmin }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
                    Text("Admin Access", fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isResigned = !isResigned }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isResigned, onCheckedChange = { isResigned = it })
                    Text("Resigned")
                }

                if (isResigned) {
                    var showResignPicker by remember { mutableStateOf(false) }
                    if (showResignPicker) {
                        val datePickerState = rememberDatePickerState()
                        DatePickerDialog(
                            onDismissRequest = { showResignPicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        resignationDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                                    }
                                    showResignPicker = false
                                }) { Text("OK") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showResignPicker = false }) { Text("Cancel") }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                    OutlinedTextField(
                        value = resignationDate,
                        onValueChange = { },
                        label = { Text("Last Day / Resignation Date") },
                        modifier = Modifier.fillMaxWidth().clickable { showResignPicker = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }

                if (isCertified) {
                    Text("Certified in:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.Start))
                    val posList = listOf("Team Leader", "Manager", "Assistant Manager", "Cashier", "Dispatch", "SS", "DJ", "CIC", "Assembler", "Fryer", "Backup", "Noodle", "So", "Excrew", "Buzzer", "Senior Crew", "Coordinator")
                    posList.chunked(2).forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            row.forEach { pos ->
                                Row(
                                    modifier = Modifier.weight(1f).clickable {
                                        certifiedPositions = if (certifiedPositions.contains(pos)) {
                                            certifiedPositions - pos
                                        } else {
                                            certifiedPositions + pos
                                        }
                                    },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = certifiedPositions.contains(pos),
                                        onCheckedChange = { checked ->
                                            certifiedPositions = if (checked) {
                                                certifiedPositions + pos
                                            } else {
                                                certifiedPositions - pos
                                            }
                                        }
                                    )
                                    Text(pos, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (firstName.isNotBlank() && lastName.isNotBlank() && employeeNo.isNotBlank()) {
                            onEmployeeUpdated(
                                employee.copy(
                                    id = employeeNo,
                                    employeeNo = employeeNo,
                                    firstName = firstName,
                                    middleName = middleName,
                                    lastName = lastName,
                                    mallIdNo = mallIdNo,
                                    mallIdExpirationDate = mallIdExpirationDate,
                                    location = location,
                                    dateHired = dateHired,
                                    birthday = birthday,
                                    email = email,
                                    phoneNumber = cpNumber,
                                    cpNumber = cpNumber,
                                    tinNumber = tinNumber,
                                    sss = sss,
                                    philHealth = philHealth,
                                    pagibig = pagibig,
                                    bank = bank,
                                    bankAccountNumber = bankAccountNumber,
                                    payrollAccessCode = payrollAccessCode,
                                    payrollUsername = payrollUsername,
                                    payrollPassword = payrollPassword,
                                    department = employee.department ?: "",
                                    position = position,
                                    profileImageUri = profileImageUri,
                                    uniformCap = uniformCap.toIntOrNull() ?: 0,
                                    uniformApron = uniformApron.toIntOrNull() ?: 0,
                                    uniformShirt = uniformShirt.toIntOrNull() ?: 0,
                                    uniformPants = uniformPants.toIntOrNull() ?: 0,
                                    isCertified = isCertified,
                                    certifiedPositions = certifiedPositions,
                                isAdmin = isAdmin,
                                    isResigned = isResigned,
                                    resignationDate = if (isResigned) resignationDate else null,
                                    maritalStatus = maritalStatus,
                                    emergencyContactName = emergencyContactName,
                                    emergencyContactRelationship = emergencyContactRelationship,
                                    emergencyContactPhone = emergencyContactPhone,
                                    lateOffenceLevel = employee.lateOffenceLevel ?: 0,
                                    absentOffenceLevel = employee.absentOffenceLevel ?: 0,
                                    hamperingOffenceLevel = employee.hamperingOffenceLevel ?: 0,
                                    customOffences = employee.customOffences ?: emptyList(),
                                    healthIdExpirationDate = healthIdExpirationDate,
                                    portalUsername = portalUsername,
                                    portalPassword = portalPassword
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Employee")
                }
                TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancel")
                }
            }
        }
    }
}

fun hasBirthdayAlertBeenShownToday(context: android.content.Context): Boolean {
    val prefs = context.getSharedPreferences("attendance_app_prefs", android.content.Context.MODE_PRIVATE)
    val savedDate = prefs.getString("birthday_dismissed_date", "")
    val todayStr = java.time.LocalDate.now().toString()
    return savedDate == todayStr
}

fun setBirthdayAlertShownToday(context: android.content.Context) {
    val prefs = context.getSharedPreferences("attendance_app_prefs", android.content.Context.MODE_PRIVATE)
    val todayStr = java.time.LocalDate.now().toString()
    prefs.edit().putString("birthday_dismissed_date", todayStr).apply()
}

fun getBirthdayGreetingMessage(employeeName: String): String {
    val messages = listOf(
        "On behalf of the entire Chowking team, we wish you a day filled with joy, laughter, and wonderful surprises! May this special year be as amazing as you are. Keep shining and continue to inspire everyone around you!",
        "Wishing you the happiest of birthdays! Thank you for bringing your amazing energy and dedication to our Chowking family every single day. May all your dreams and aspirations turn into reality!",
        "Happy Birthday! Your hard work, passion, and positive attitude make a huge difference in our team. Enjoy your special day to the fullest, and may the year ahead be filled with endless opportunities and success!",
        "Warmest birthday wishes to an incredible member of our team! May your day be blessed with love, happiness, and sweet moments. We are so proud and grateful to have you with us!",
        "Cheers to another wonderful year! May this birthday bring you abundant joy, good health, and fantastic memories. Thank you for being such an awesome colleague. Have a blast today!",
        "Happy Birthday! We hope your day is as bright, cheerful, and wonderful as your presence is to all of us. Wishing you a fantastic celebration and a blessed year ahead!",
        "Best birthday wishes! You are a truly valued member of the Chowking family. May your day be filled with cake, laughter, and the company of loved ones!",
        "It's your special day! We hope you get to relax and enjoy every moment. You deserve all the best for everything you do for the team. Happy Birthday!",
        "Sending you tons of good vibes on your birthday! May this new chapter of your life be filled with even more growth and happiness. Enjoy your day!",
        "To our amazing birthday celebrant: Thank you for being you! Your unique personality makes our workplace so much better. Have the best birthday ever!"
    )
    return messages.random()
}


fun calculateDaysToBirthday(birthdayStr: String?): Long? {
    if (birthdayStr == null) return null
    return try {
        val today = LocalDate.now()
        val birthday = parseDate(birthdayStr) ?: return null
        
        var nextBirthday = birthday.withYear(today.year)
        if (nextBirthday.isBefore(today)) {
            nextBirthday = nextBirthday.plusYears(1)
        }
        
        java.time.temporal.ChronoUnit.DAYS.between(today, nextBirthday)
    } catch (e: Exception) {
        null
    }
}

fun calculateDaysToExpiry(expiryStr: String?): Long? {
    if (expiryStr == null) return null
    return try {
        val today = LocalDate.now()
        val expiry = parseDate(expiryStr) ?: return null
        
        java.time.temporal.ChronoUnit.DAYS.between(today, expiry)
    } catch (e: Exception) {
        null
    }
}

fun calculateTimeHired(hiredDateStr: String?): String? {
    if (hiredDateStr == null) return null
    return try {
        val today = LocalDate.now()
        val hiredDate = parseDate(hiredDateStr) ?: return null
        val period = java.time.Period.between(hiredDate, today)
        
        val years = period.years
        val months = period.months
        val days = period.days
        
        val result = mutableListOf<String>()
        if (years > 0) result.add("${years}y")
        if (months > 0) result.add("${months}m")
        if (days > 0 || result.isEmpty()) result.add("${days}d")
        
        result.joinToString(" ")
    } catch (e: Exception) {
        null
    }
}

// ChatDialog removed

fun parseDate(dateStr: String?): LocalDate? {
    if (dateStr == null) return null
    val formats = listOf(
        java.time.format.DateTimeFormatter.ISO_LOCAL_DATE,
        java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        java.time.format.DateTimeFormatter.ofPattern("M/d/yyyy"),
        java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy"),
        java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
    )
    
    for (format in formats) {
        try {
            return LocalDate.parse(dateStr, format)
        } catch (e: Exception) {
            continue
        }
    }
    return null
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: com.sam.myapplication.ui.theme.AppTheme,
    onThemeSelected: (com.sam.myapplication.ui.theme.AppTheme) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Application Theme", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                com.sam.myapplication.ui.theme.AppTheme.entries.forEach { theme ->
                    val color = when(theme) {
                        com.sam.myapplication.ui.theme.AppTheme.RED -> Color(0xFFD32F2F)
                        com.sam.myapplication.ui.theme.AppTheme.BLUE -> Color(0xFF1976D2)
                        com.sam.myapplication.ui.theme.AppTheme.GREEN -> Color(0xFF388E3C)
                        com.sam.myapplication.ui.theme.AppTheme.PURPLE -> Color(0xFF7B1FA2)
                        com.sam.myapplication.ui.theme.AppTheme.DARK -> Color(0xFF121212)
                    }
                    
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(theme) },
                        shape = MaterialTheme.shapes.medium,
                        color = if (currentTheme == theme) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        border = if (currentTheme == theme) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = theme.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (currentTheme == theme) FontWeight.Bold else FontWeight.Normal
                            )
                            if (currentTheme == theme) {
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

fun convertTo12Hour(time24: String?): String {
    if (time24.isNullOrBlank()) return "--:--"
    return try {
        val parts = time24.split(":")
        if (parts.size < 2) return time24
        val hour = parts[0].toInt()
        val minute = parts[1]
        val suffix = if (hour >= 12) "PM" else "AM"
        val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        "$hour12:$minute $suffix"
    } catch (e: Exception) {
        time24 ?: "--:--"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermitWebViewScreen(
    viewModel: AttendanceViewModel, 
    portalUser: String? = "smtp_smckmall_058184",
    portalPass: String? = "cJEjZn",
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    var isLoading by remember { mutableStateOf(true) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showExpiredList by remember { mutableStateOf(false) }
    
    val permits by viewModel.allWorkPermits.collectAsState(initial = emptyList())
    
    val permitUrl = "https://oma.smphi.com/TP_WorkPermit/"
    val standardUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    // Interface to receive data from WebView
    val webInterface = remember {
        object {
            @android.webkit.JavascriptInterface
            fun onPermitsScraped(json: String) {
                try {
                    val jsonConfig = Json { ignoreUnknownKeys = true }
                    val scrapedList = jsonConfig.decodeFromString<List<WorkPermit>>(json)
                    if (scrapedList.isNotEmpty()) {
                        viewModel.syncWorkPermits(scrapedList)
                    }
                } catch (e: Exception) {
                    Log.e("PermitScrape", "Error parsing scraped permits", e)
                }
            }

            @android.webkit.JavascriptInterface
            fun onCaptureManual(ref: String, tabName: String, tabHtml: String, shellHtml: String) {
                if (ref == "Unknown" || ref.isBlank()) {
                    scope.launch {
                        Toast.makeText(context, "Could not find Reference Number. Please ensure the page is fully loaded.", Toast.LENGTH_LONG).show()
                    }
                    return
                }

                viewModel.appendPermitCapture(ref, tabName, tabHtml, shellHtml)
                scope.launch {
                    Toast.makeText(context, "Saved '$tabName' to permit $ref", Toast.LENGTH_SHORT).show()
                }
            }

            @android.webkit.JavascriptInterface
            fun onPageSaved(ref: String, html: String) {
                if (ref == "Unknown" || ref.isBlank()) {
                    scope.launch {
                        Toast.makeText(context, "Could not find Reference Number. Please ensure the page is fully loaded.", Toast.LENGTH_LONG).show()
                    }
                    return
                }

                viewModel.updatePermitCache(ref, html)
                scope.launch {
                    Toast.makeText(context, "Permit $ref fully saved for offline viewing!", Toast.LENGTH_SHORT).show()
                }
            }
            
            @android.webkit.JavascriptInterface
            fun notifySavingStarted(ref: String) {
                scope.launch {
                    Toast.makeText(context, "Saving Permit $ref and all its tabs... Please wait.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Automated Login Script
    val loginJs = """
        (function() {
            var user = '$portalUser';
            var pass = '$portalPass';
            
            function simulateHumanInput(el, value) {
                if (!el) return;
                el.focus();
                el.value = ""; // Clear existing
                
                // Use execCommand to bypass framework-level validation/state locks
                // This makes the website think a real human is typing
                try {
                    el.focus();
                    document.execCommand('selectAll', false, null);
                    document.execCommand('delete', false, null);
                    document.execCommand('insertText', false, value);
                } catch (e) {
                    console.error("ExecCommand failed, falling back to event dispatch", e);
                }
                
                // Fallback for value assignment if execCommand didn't work
                if (el.value !== value) {
                    el.value = value;
                }

                // Dispatch a comprehensive set of events to satisfy all frameworks (React, OutSystems, etc.)
                var events = ['input', 'change', 'blur', 'keyup', 'keydown', 'keypress'];
                events.forEach(function(type) {
                    var evt = document.createEvent('HTMLEvents');
                    evt.initEvent(type, true, true);
                    el.dispatchEvent(evt);
                });
            }
            
            function fill() {
                var u = document.getElementById('Input_UsernameVal') || document.querySelector('input[id*="User"]');
                var p = document.getElementById('Input_PasswordVal') || document.querySelector('input[id*="Pass"]');
                var b = document.querySelector('#b5-Button button') || document.querySelector('button.btn-primary');
                
                if (u && p && b) {
                    // Stop if error is visible (don't loop login if it actually failed)
                    var err = document.querySelector('.feedback-message-error') || 
                              document.querySelector('.os-internal-Feedback_Message_Error');
                    if (err && err.innerText.trim().length > 0) return true;

                    simulateHumanInput(u, user);
                    setTimeout(function() {
                        simulateHumanInput(p, pass);
                        setTimeout(function() { 
                            b.focus(); 
                            b.click(); 
                            // Dispatch a real MouseEvent to be sure
                            var clickEvt = new MouseEvent('click', { bubbles: true, cancelable: true, view: window });
                            b.dispatchEvent(clickEvt);
                        }, 1200);
                    }, 1000);
                    return true;
                }
                return false;
            }
            
            var attempts = 0;
            function auto() {
                if (attempts++ < 30 && !fill()) setTimeout(auto, 1200);
            }
            auto();
        })();
    """.trimIndent()

    val scrapeJs = """
        (function() {
            window.scrapePage = function() {
                if (window.location.href.indexOf('WorkPermitDetails') !== -1 || window.location.href.indexOf('Login') !== -1) {
                   return; 
                }

                // Try to set page size to 20 or 50 if dropdown is visible
                var dropdown = document.querySelector('select[id*="Dropdown"]') || 
                               document.querySelector('.pagination-dropdown select');
                if (dropdown && dropdown.value !== "2" && dropdown.value !== "3") {
                    dropdown.value = "2"; 
                    dropdown.dispatchEvent(new Event('change', { bubbles: true }));
                    // Don't return, just continue scraping what's there
                }

                var rows = document.querySelectorAll('.table-row, tr.table-row, .table tr');
                var currentBatch = [];
                
                rows.forEach(function(row) {
                    // Search for reference number pattern WP-20xx...
                    var text = row.innerText || "";
                    var match = text.match(/WP-20\d{2}-\d+/);
                    
                    if (match) {
                        var ref = match[0];
                        var linkEl = row.querySelector('a[href*="WorkPermitDetails"]');
                        var detailUrl = linkEl ? linkEl.href : null;
                        
                        // Try to find status by looking for specific keywords in the row text
                        var status = "Unknown";
                        if (text.toLowerCase().indexOf('approved') !== -1) status = "Approved";
                        else if (text.toLowerCase().indexOf('for approval') !== -1) status = "For Approval";
                        else if (text.toLowerCase().indexOf('rejected') !== -1) status = "Rejected";
                        else if (text.toLowerCase().indexOf('draft') !== -1) status = "Draft";
                        
                        // Try to find tradename - usually next to the reference number
                        var trade = "";
                        var cells = row.querySelectorAll('td');
                        if (cells.length > 2) {
                            trade = cells[1].innerText.trim();
                        }
                        
                        currentBatch.push({
                            reference_number: ref,
                            status: status,
                            date: "", 
                            tradename: trade,
                            detail_url: detailUrl,
                            is_approved: status === "Approved",
                            is_expired: false
                        });
                    }
                });

                if (currentBatch.length > 0) {
                    AndroidBridge.onPermitsScraped(JSON.stringify(currentBatch));
                }
            };
            
            // Run only once after load
            setTimeout(window.scrapePage, 2000);
        })();
    """.trimIndent()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { scope.launch { drawerState.close() } }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close List")
                        }
                        Text("Work Permits", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        
                        IconButton(onClick = { viewModel.fetchRemotePermits() }) {
                            Icon(Icons.Default.CloudDownload, contentDescription = "Retrieve from Cloud", tint = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(onClick = { showExpiredList = !showExpiredList }) {
                            Icon(
                                if (showExpiredList) Icons.Default.VisibilityOff else Icons.Default.History, 
                                contentDescription = "Toggle Expired",
                                tint = if (showExpiredList) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.clearPermitCache() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Saved", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("(Fresh 20)", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 48.dp))
                    Spacer(Modifier.height(16.dp))
                    
                    if (permits.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No permits scraped yet...", color = MaterialTheme.colorScheme.secondary)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Filter logic: Limited to latest 20, toggle between Active and Expired
                            val latest20 = permits.take(20)
                            val displayPermits = if (showExpiredList) {
                                latest20.filter { it.isExpired }
                            } else {
                                latest20.filter { !it.isExpired }
                            }
                            
                            if (displayPermits.isEmpty()) {
                                item {
                                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            if (showExpiredList) "No expired permits in latest 20" else "All latest permits are active",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                            
                            items(displayPermits) { permit ->
                                var showLabelDialog by remember { mutableStateOf(false) }
                                var isEditingItem by remember { mutableStateOf(false) }
                                var tempLabel by remember { mutableStateOf(permit.customLabel ?: "") }

                                if (showLabelDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showLabelDialog = false },
                                        title = { Text("Permit Note") },
                                        text = {
                                            OutlinedTextField(
                                                value = tempLabel,
                                                onValueChange = { tempLabel = it },
                                                label = { Text("Label/Note") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        },
                                        confirmButton = {
                                            Button(onClick = {
                                                viewModel.updatePermitLabel(permit.referenceNumber, tempLabel)
                                                showLabelDialog = false
                                            }) { Text("Save") }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showLabelDialog = false }) { Text("Cancel") }
                                        }
                                    )
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        scope.launch { drawerState.close() }
                                        
                                    // If we have cached content
                                    if (!permit.cachedHtml.isNullOrBlank()) {
                                        isLoading = false
                                        
                                        var htmlToLoad = permit.cachedHtml!!
                                        
                                        // Check if it's a multi-tab bundle
                                        if (htmlToLoad.startsWith("{\"_is_permit_bundle\":true")) {
                                            try {
                                                val bundle = Json.parseToJsonElement(htmlToLoad).jsonObject
                                                val tabs = bundle["tabs"]?.jsonArray
                                                val shell = bundle["shell"]?.jsonPrimitive?.content ?: ""
                                                
                                                if (tabs != null && tabs.isNotEmpty()) {
                                                    // This script will run inside the offline page to make original buttons work
                                                    val offlineScript = """
                                                        <script>
                                                            (function() {
                                                                var savedTabs = ${tabs.toString()};
                                                                
                                                                function switchOfflineTab(name) {
                                                                    var tabData = savedTabs.find(t => t.name.toLowerCase().indexOf(name.toLowerCase()) !== -1);
                                                                    if (!tabData) return;
                                                                    
                                                                    var container = document.querySelector('.osui-deprecated.tabs-content') || 
                                                                                    document.querySelector('.tabs-content') || 
                                                                                    document.querySelector('.osui-tabs__content') ||
                                                                                    document.body;
                                                                    
                                                                    if (container) {
                                                                        container.innerHTML = tabData.html;
                                                                        window.scrollTo(0,0);
                                                                    }
                                                                    
                                                                    // Update visual 'active' state on original buttons
                                                                    document.querySelectorAll('.tabs-header-tab, .osui-tabs__header-item').forEach(btn => {
                                                                        if (btn.innerText.trim().toLowerCase().indexOf(name.toLowerCase()) !== -1) {
                                                                            btn.classList.add('active', 'is--active');
                                                                            btn.setAttribute('aria-selected', 'true');
                                                                        } else {
                                                                            btn.classList.remove('active', 'is--active');
                                                                            btn.setAttribute('aria-selected', 'false');
                                                                        }
                                                                    });
                                                                }

                                                                window.addEventListener('load', function() {
                                                                    // Hijack all original tab clicks
                                                                    document.querySelectorAll('.tabs-header-tab, .osui-tabs__header-item, [role="tab"]').forEach(btn => {
                                                                        btn.onclick = function(e) {
                                                                            e.preventDefault();
                                                                            e.stopPropagation();
                                                                            switchOfflineTab(this.innerText.trim());
                                                                        };
                                                                    });

                                                                    // Hijack activity log flag
                                                                    var flag = document.querySelector('.fa-flag');
                                                                    if (flag) {
                                                                        var btn = flag.closest('a') || flag.closest('button');
                                                                        if (btn) {
                                                                            btn.onclick = function(e) {
                                                                                e.preventDefault();
                                                                                switchOfflineTab("Activity Log");
                                                                            };
                                                                        }
                                                                    }

                                                                    // Show first tab by default
                                                                    switchOfflineTab("General Information");
                                                                });
                                                            })();
                                                        </script>
                                                    """.trimIndent()
                                                    
                                                    htmlToLoad = shell.replace("<!--OFFLINE_CONTENT_HERE-->", "<div id='offline-main-container'></div>" + offlineScript)
                                                }
                                            } catch (e: Exception) {
                                                Log.e("OfflineLoad", "Failed to parse bundle", e)
                                            }
                                        }

                                        webViewInstance?.loadDataWithBaseURL(
                                            "https://oma.smphi.com/",
                                            htmlToLoad,
                                            "text/html",
                                            "UTF-8",
                                            null
                                        )
                                    } else {
                                        val url = if (!permit.detailUrl.isNullOrBlank()) {
                                            if (permit.detailUrl.startsWith("http")) permit.detailUrl 
                                            else "https://oma.smphi.com" + permit.detailUrl
                                        } else permitUrl
                                        webViewInstance?.loadUrl(url)
                                    }
                                    },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(permit.referenceNumber, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                            if (!permit.cachedHtml.isNullOrBlank()) {
                                                Icon(
                                                    Icons.Default.DownloadDone,
                                                    contentDescription = "Saved Offline",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp).padding(end = 4.dp)
                                                )
                                            }
                                            if (permit.isExpired) {
                                                Text(
                                                    "EXPIRED ", 
                                                    style = MaterialTheme.typography.labelSmall, 
                                                    color = MaterialTheme.colorScheme.error,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                            StatusBadgeCompact(permit.status)
                                        }
                                        if (!permit.customLabel.isNullOrBlank()) {
                                            Text(
                                                text = permit.customLabel, 
                                                style = MaterialTheme.typography.labelSmall, 
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                        }
                                        
                                        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                                            TextButton(
                                                onClick = { showLabelDialog = true },
                                                modifier = Modifier.height(32.dp).weight(1f),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text(if (permit.customLabel.isNullOrBlank()) "Add Note" else "Edit Note", fontSize = 11.sp)
                                            }

                                            TextButton(
                                                onClick = { viewModel.markPermitExpired(permit.referenceNumber, !permit.isExpired) },
                                                modifier = Modifier.height(32.dp).weight(1f),
                                                contentPadding = PaddingValues(0.dp),
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = if (permit.isExpired) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                                )
                                            ) {
                                                Icon(if (permit.isExpired) Icons.Default.Restore else Icons.Default.Check, null, modifier = Modifier.size(14.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text(if (permit.isExpired) "Un-expire" else "Mark Expired", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Permit Portal", style = MaterialTheme.typography.titleMedium) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val fullDownloadJs = """
                                (async function() {
                                    var findRef = function() {
                                        var refEl = document.querySelector('.heading6') || 
                                                    document.querySelector('span[data-expression*="WP-"]') || 
                                                    document.querySelector('.reference-number') ||
                                                    document.querySelector('h1') || 
                                                    document.querySelector('.title');
                                        if (refEl && refEl.innerText.indexOf('WP-') !== -1) return refEl.innerText.trim();
                                        var match = document.body.innerText.match(/WP-20\d{2}-\d+/);
                                        return match ? match[0] : "Unknown";
                                    };

                                    var ref = findRef();
                                    if (ref === "Unknown") {
                                        AndroidBridge.onPageSaved(ref, "");
                                        return;
                                    }
                                    
                                    AndroidBridge.notifySavingStarted(ref);

                                    async function inlineImages(container) {
                                        var imgs = container.querySelectorAll('img');
                                        for (var i = 0; i < imgs.length; i++) {
                                            var img = imgs[i];
                                            if (img.src && !img.src.startsWith('data:')) {
                                                try {
                                                    const res = await fetch(img.src);
                                                    const blob = await res.blob();
                                                    const reader = new FileReader();
                                                    const dataUrl = await new Promise(resolve => {
                                                        reader.onloadend = () => resolve(reader.result);
                                                        reader.readAsDataURL(blob);
                                                    });
                                                    img.src = dataUrl;
                                                } catch (e) { }
                                            }
                                        }
                                    }

                                    var bundle = { _is_permit_bundle: true, tabs: [] };
                                    
                                    async function captureTab(name) {
                                        var content = document.querySelector('.osui-deprecated.tabs-content') || 
                                                      document.querySelector('.tabs-content') || 
                                                      document.querySelector('.osui-tabs__content') || 
                                                      document.querySelector('.MainContent') ||
                                                      document.body;
                                        
                                        var clone = content.cloneNode(true);
                                        await inlineImages(clone);
                                        bundle.tabs.push({ name: name, html: clone.innerHTML });
                                    }

                                    // 1. Capture General Information (current view)
                                    await captureTab("General Information");

                                    // 2. Capture other tabs
                                    var tabs = document.querySelectorAll('.osui-deprecated.tabs-header-tab, .tabs-header-tab');
                                    for (var i = 0; i < tabs.length; i++) {
                                        var name = tabs[i].innerText.trim();
                                        if (name && name !== "General Information") {
                                            tabs[i].click();
                                            tabs[i].dispatchEvent(new MouseEvent('click', { bubbles: true }));
                                            await new Promise(r => setTimeout(r, 4000));
                                            await captureTab(name);
                                        }
                                    }

                                    // 3. Capture Activity Log (Flag)
                                    var flag = document.querySelector('.fa-flag');
                                    if (flag) {
                                        var btn = flag.closest('a') || flag.closest('button');
                                        if (btn) {
                                            btn.click();
                                            btn.dispatchEvent(new MouseEvent('click', { bubbles: true }));
                                            await new Promise(r => setTimeout(r, 8000));
                                            await captureTab("Activity Log");
                                        }
                                    }

                                    // Capture Shell
                                    var mainContent = document.querySelector('.osui-deprecated.tabs-content') || 
                                                      document.querySelector('.tabs-content') || 
                                                      document.querySelector('.osui-tabs__content');
                                    
                                    var shell = document.documentElement.outerHTML;
                                    if (mainContent) {
                                        shell = document.documentElement.outerHTML.replace(mainContent.innerHTML, "<!--OFFLINE_CONTENT_HERE-->");
                                    }
                                    bundle.shell = shell;

                                    AndroidBridge.onPageSaved(ref, JSON.stringify(bundle));
                                })();
                            """.trimIndent()
                            webViewInstance?.evaluateJavascript(fullDownloadJs, null)
                        }) {
                            Icon(Icons.Default.Download, contentDescription = "Save Offline")
                        }
                        IconButton(onClick = { webViewInstance?.loadUrl(permitUrl) }) {
                            Icon(Icons.Default.Home, contentDescription = "Dashboard")
                        }
                        IconButton(onClick = { webViewInstance?.reload() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                        IconButton(onClick = {
                            webViewInstance?.evaluateJavascript("""
                                (function() {
                                    if (typeof window.scrapePage === 'function') {
                                        window.scrapePage();
                                    } else {
                                        var rows = document.querySelectorAll('.table-row, tr.table-row, .table tr');
                                        var currentBatch = [];
                                        rows.forEach(function(row) {
                                            var text = row.innerText || "";
                                            var match = text.match(/WP-20\d{2}-\d+/);
                                            if (match) {
                                                var ref = match[0];
                                                var linkEl = row.querySelector('a[href*="WorkPermitDetails"]');
                                                var detailUrl = linkEl ? linkEl.href : null;
                                                var status = "Unknown";
                                                if (text.toLowerCase().indexOf('approved') !== -1) status = "Approved";
                                                else if (text.toLowerCase().indexOf('for approval') !== -1) status = "For Approval";
                                                else if (text.toLowerCase().indexOf('rejected') !== -1) status = "Rejected";
                                                else if (text.toLowerCase().indexOf('draft') !== -1) status = "Draft";
                                                var trade = "";
                                                var cells = row.querySelectorAll('td');
                                                if (cells.length > 2) {
                                                    trade = cells[1].innerText.trim();
                                                }
                                                currentBatch.push({
                                                    reference_number: ref,
                                                    status: status,
                                                    date: "", 
                                                    tradename: trade,
                                                    detail_url: detailUrl,
                                                    is_approved: status === "Approved",
                                                    is_expired: false
                                                });
                                            }
                                        });
                                        if (currentBatch.length > 0) {
                                            AndroidBridge.onPermitsScraped(JSON.stringify(currentBatch));
                                        }
                                    }
                                })();
                            """.trimIndent(), null)
                            scope.launch {
                                Toast.makeText(context, "Scraping permits...", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.Sync, contentDescription = "Scrape Permits")
                        }
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            BadgedBox(badge = { if (permits.isNotEmpty()) Badge { Text(permits.take(20).size.toString()) } }) {
                                Icon(Icons.Default.List, contentDescription = "Permit List")
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(top = padding.calculateTopPadding()).fillMaxSize().background(Color.White)) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            webViewInstance = this
                            addJavascriptInterface(webInterface, "AndroidBridge")
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            
                            // Ensure focus and touch
                            isFocusable = true
                            isFocusableInTouchMode = true
                            isClickable = true

                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    allowFileAccess = true
                                    allowContentAccess = true
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                    setSupportZoom(true)
                                    builtInZoomControls = true
                                    displayZoomControls = false
                                    userAgentString = standardUserAgent
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    
                                    // Reset to default scaling behaviors to allow overview mode to work
                                    cacheMode = WebSettings.LOAD_DEFAULT
                                    setRenderPriority(WebSettings.RenderPriority.HIGH)
                                    setEnableSmoothTransition(true)
                                    
                                    // Additional desktop-like settings
                                    defaultTextEncodingName = "utf-8"
                                    textZoom = 100
                                }
                            
                            // Enable hardware acceleration and scrolling
                            setLayerType(View.LAYER_TYPE_HARDWARE, null)
                            isHorizontalScrollBarEnabled = true
                            isVerticalScrollBarEnabled = true
                            
                            val cookieManager = android.webkit.CookieManager.getInstance()
                            cookieManager.setAcceptCookie(true)
                            cookieManager.setAcceptThirdPartyCookies(this, true)
                            
                            webViewClient = OfflineWebViewClient(
                                context = ctx,
                                onPageStarted = { isLoading = true },
                                onPageFinished = { url ->
                                    isLoading = false
                                    // Comprehensive Zoom & Scaling Fix
                                    webViewInstance?.evaluateJavascript("""
                                        (function() {
                                            // 1. Force a wide viewport to allow manual zooming on desktop-like layouts
                                            var metas = document.getElementsByTagName('meta');
                                            for (var i=0; i<metas.length; i++) {
                                                if (metas[i].name == "viewport") metas[i].parentNode.removeChild(metas[i]);
                                            }
                                            var meta = document.createElement('meta');
                                            meta.name = "viewport";
                                            meta.content = "width=1280, initial-scale=0.3, minimum-scale=0.1, maximum-scale=5.0, user-scalable=yes";
                                            document.getElementsByTagName('head')[0].appendChild(meta);
                                            
                                            // 2. Fix the "Crooked" and "Blank Space" issues
                                            var style = document.getElementById('zoom-unblocker') || document.createElement('style');
                                            style.id = 'zoom-unblocker';
                                            style.innerHTML = "html, body { overflow: auto !important; width: 1280px !important; min-width: 1280px !important; margin: 0 auto !important; padding: 0 !important; position: relative !important; left: 0 !important; right: 0 !important; height: auto !important; min-height: 100% !important; background-color: white !important; } .PageContainer, .MainContainer, .os-internal-ui-view, .tabs-content { overflow: visible !important; width: 100% !important; min-width: 100% !important; height: auto !important; min-height: 100% !important; max-height: none !important; margin: 0 !important; padding: 0 !important; left: 0 !important; display: block !important; } * { max-width: none !important; user-select: auto !important; box-sizing: border-box !important; } .layout-top { width: 1280px !important; min-height: 100% !important; }";
                                            document.head.appendChild(style);
                                            
                                            // 3. Force document-level scrollability and reset scroll position
                                            document.documentElement.style.overflow = 'auto';
                                            document.body.style.overflow = 'auto';
                                            window.scrollTo(0,0);
                                        })();
                                    """.trimIndent(), null)

                                    cookieManager.flush()
                                },
                                onLoginTrigger = { view -> view?.evaluateJavascript(loginJs, null) },
                                onScrapeTrigger = { view -> view?.evaluateJavascript(scrapeJs, null) }
                            )
                            loadUrl(permitUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
             if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
                }
            }
        }
    }
}

@Composable
fun LocalPdfViewer(file: File) {
    val context = LocalContext.current
    val bitmaps = remember(file) {
        val list = mutableListOf<Bitmap>()
        try {
            val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(pfd)
            for (i in 0 until renderer.pageCount) {
                renderer.openPage(i).use { page ->
                    // 3x resolution for extremely sharp text
                    val bitmap = Bitmap.createBitmap(page.width * 3, page.height * 3, Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(bitmap)
                    canvas.drawColor(android.graphics.Color.WHITE)
                    // Scale the draw to match high res
                    val matrix = android.graphics.Matrix()
                    matrix.setScale(3f, 3f)
                    page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    list.add(bitmap)
                }
            }
            renderer.close()
            pfd.close()
        } catch (e: Exception) {
            Log.e("PdfRenderer", "Error", e)
        }
        list
    }

    if (bitmaps.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(8.dp))
                Text("Could not display payslip. Please try again.")
            }
        }
    } else {
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
        
        // Improved zoom logic with snap-back
        val state = rememberTransformableState { zoomChange, offsetChange, _ ->
            scale = (scale * zoomChange).coerceIn(1f, 5f)
            
            // Only allow panning if zoomed in
            if (scale > 1f) {
                offset += offsetChange
            } else {
                offset = androidx.compose.ui.geometry.Offset.Zero
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF212121))
                .transformable(state = state)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                bitmaps.forEach { bitmap ->
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .shadow(8.dp),
                        shape = MaterialTheme.shapes.extraSmall,
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Payslip Page",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth,
                            filterQuality = androidx.compose.ui.graphics.FilterQuality.High
                        )
                    }
                }
            }
            
            if (scale > 1f) {
                IconButton(
                    onClick = { scale = 1f; offset = androidx.compose.ui.geometry.Offset.Zero },
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(Icons.Default.Close, null, tint = Color.White)
                }
            }
        }
    }
}

@Serializable
data class PayoutOption(val id: String, val date: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayslipWebViewScreen(
    employee: Employee,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var payoutOptions by remember { mutableStateOf<List<PayoutOption>>(emptyList()) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showWebView by remember { mutableStateOf(false) }
    var pdfFile by remember { mutableStateOf<File?>(null) }
    val scope = rememberCoroutineScope()

    val payslipUrl = "https://portal.backendpayroll.com/Payslip.aspx"

    fun downloadPdf(url: String) {
        isLoading = true
        scope.launch(Dispatchers.IO) {
            try {
                val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                connection.setRequestProperty("Cookie", android.webkit.CookieManager.getInstance().getCookie(url))
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                
                val tempFile = File(context.cacheDir, "current_payslip.pdf")
                connection.inputStream.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                if (tempFile.length() < 100) {
                    throw Exception("Invalid PDF content")
                }

                withContext(Dispatchers.Main) {
                    pdfFile = tempFile
                    isLoading = false
                    showWebView = false 
                }
            } catch (e: Exception) {
                Log.e("PdfDownload", "Failed", e)
                withContext(Dispatchers.Main) {
                    isLoading = false
                    Toast.makeText(context, "Failed to download PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // JavaScript to extract payout dates with retry logic
    val scrapeJs = """
        (function() {
            var retryCount = 0;
            function tryScrape() {
                var select = document.getElementById('ctl00_ContentPlaceHolder1_ddlPayout') || 
                             document.querySelector('select[id*="ddlPayout"]');
                
                if (select && select.options && select.options.length > 1) { 
                    var options = [];
                    for (var i = 0; i < select.options.length; i++) {
                        var opt = select.options[i];
                        if (opt.value && opt.value !== '0' && opt.text.indexOf('Select') === -1) {
                            options.push({ id: opt.value, date: opt.text.trim() });
                        }
                    }
                    if (options.length > 0) {
                        AndroidPortal.onPayoutsFetched(JSON.stringify(options));
                        return;
                    }
                }
                
                if (retryCount < 20) {
                    retryCount++;
                    setTimeout(tryScrape, 1000);
                } else {
                    AndroidPortal.notifyDone();
                }
            }
            tryScrape();
        })();
    """.trimIndent()

    // JavaScript to inject credentials and hijack popups
    val loginJs = """
        (function() {
            var accessCode = '${(employee.payrollAccessCode ?: "").replace("'", "\\'")}';
            var username = '${(employee.payrollUsername ?: "").replace("'", "\\'")}';
            var password = '${(employee.payrollPassword ?: "").replace("'", "\\'")}';
            
            window.open = function(url) { window.location.href = url; return window; };

            function trigger(el, type) {
                el.dispatchEvent(new Event(type, { bubbles: true }));
            }

            function setValue(id, value) {
                var el = document.getElementById(id) || 
                         document.querySelector('input[id*="' + id + '"]') ||
                         document.querySelector('input[name*="' + id + '"]');
                if (el) {
                    el.focus();
                    el.value = "";
                    
                    try {
                        document.execCommand('selectAll', false, null);
                        document.execCommand('delete', false, null);
                        document.execCommand('insertText', false, value);
                    } catch(e) {}
                    
                    if (el.value !== value) el.value = value;
                    
                    ['input', 'change', 'blur', 'keyup', 'keydown', 'keypress'].forEach(function(e) {
                        var evt = document.createEvent('HTMLEvents');
                        evt.initEvent(e, true, true);
                        el.dispatchEvent(evt);
                    });
                    return true;
                }
                return false;
            }

            function attemptLogin() {
                var a = setValue('txtCompanyCode', accessCode) || 
                        setValue('txtAccessCode', accessCode) || 
                        setValue('txtCompany', accessCode) ||
                        setValue('CompanyCode', accessCode);
                
                var b = setValue('txtUserName', username) || 
                        setValue('txtUserID', username) || 
                        setValue('txtUser', username) ||
                        setValue('UserName', username);
                
                var c = setValue('txtPassword', password) ||
                        setValue('Password', password);

                if (a && b && c) {
                    var btn = document.getElementById('LoginButton') || 
                              document.getElementById('btnLogin') || 
                              document.querySelector('input[id*="Login"]') ||
                              document.querySelector('button[id*="Login"]') ||
                              document.querySelector('input[type="submit"]');
                    if (btn) {
                        setTimeout(function() { btn.click(); }, 600);
                    }
                }
            }
            
            if (accessCode && username && password) {
                // Wait for document ready
                if (document.readyState === 'complete') attemptLogin();
                else window.addEventListener('load', attemptLogin);
                // Also try immediate for single-page-app style loads
                setTimeout(attemptLogin, 1000);
            }
        })();
    """.trimIndent()

    class PortalInterface(
        private val context: Context,
        private val onPayouts: (List<PayoutOption>) -> Unit,
        private val onDone: () -> Unit
    ) {
        @android.webkit.JavascriptInterface
        fun onPayoutsFetched(json: String) {
            try {
                val options = Json.decodeFromString<List<PayoutOption>>(json)
                android.os.Handler(context.mainLooper).post {
                    onPayouts(options)
                    onDone()
                }
            } catch (e: Exception) {
                android.util.Log.e("PortalInterface", "Error parsing payouts", e)
                android.os.Handler(context.mainLooper).post { onDone() }
            }
        }

        @android.webkit.JavascriptInterface
        fun notifyDone() {
            android.os.Handler(context.mainLooper).post { onDone() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        when {
                            pdfFile != null -> "Payslip Document"
                            showWebView -> "Payroll Portal"
                            else -> "Select Payout Date"
                        }
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when {
                            pdfFile != null -> pdfFile = null
                            showWebView -> showWebView = false 
                            else -> onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (pdfFile == null && !showWebView) {
                        IconButton(onClick = { showWebView = true }) {
                            Icon(Icons.Default.Visibility, contentDescription = "Show Web")
                        }
                    }
                    if (pdfFile != null) {
                        IconButton(onClick = {
                            pdfFile?.let { file ->
                                try {
                                    val uri = androidx.core.content.FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "application/pdf"
                                        putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(android.content.Intent.createChooser(intent, "Share Payslip"))
                                } catch (e: Exception) {
                                    Log.e("PdfShare", "Error", e)
                                }
                            }
                        }) {
                            Icon(Icons.Default.FileUpload, contentDescription = "Share")
                        }
                    }
                    IconButton(onClick = { 
                        webViewInstance?.reload()
                        payoutOptions = emptyList()
                        pdfFile = null
                        isLoading = true
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (pdfFile != null) {
                LocalPdfViewer(pdfFile!!)
            } else {
                AndroidView(
                    factory = { ctx ->
                        val webView = WebView(ctx)
                        android.webkit.CookieManager.getInstance().setAcceptCookie(true)
                        android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
                        
                        webView.apply {
                            webViewInstance = this
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            settings.builtInZoomControls = true
                            settings.displayZoomControls = false
                            settings.setSupportZoom(true)
                            settings.defaultTextEncodingName = "utf-8"
                            settings.setSupportMultipleWindows(false)
                            settings.javaScriptCanOpenWindowsAutomatically = true
                            settings.databaseEnabled = true
                            settings.setGeolocationEnabled(true)
                            settings.allowFileAccess = true
                            settings.allowContentAccess = true
                            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            
                            settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                            
                            addJavascriptInterface(PortalInterface(
                                context = ctx,
                                onPayouts = { payoutOptions = it },
                                onDone = { isLoading = false }
                            ), "AndroidPortal")

                            setDownloadListener { url, _, _, _, _ ->
                                downloadPdf(url)
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    if (!showWebView) isLoading = false
                                    
                                    if (url?.contains("backendpayroll.com") == true) {
                                        view?.evaluateJavascript(loginJs, null)
                                        if (url.contains("Payslip.aspx")) {
                                            view?.evaluateJavascript(scrapeJs, null)
                                        }
                                    }
                                }

                                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                                    handler?.proceed()
                                }

                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    if (url.contains("ReportViewer.aspx", ignoreCase = true) || 
                                        url.contains(".pdf", ignoreCase = true) ||
                                        url.contains("Reserved.ReportViewerWebControl.axd", ignoreCase = true)) {
                                        downloadPdf(url)
                                        return true 
                                    }
                                    return false
                                }
                            }
                            
                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    if (newProgress == 100 && !showWebView) isLoading = false
                                }
                            }
                            
                            loadUrl(payslipUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Native UI Layer
            if (!showWebView && pdfFile == null) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                            if (payoutOptions.isEmpty() && !isLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Lock, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(16.dp))
                            Text("Logging in to Payroll Portal...", textAlign = TextAlign.Center)
                            Spacer(Modifier.height(24.dp))
                            
                            Button(
                                onClick = { showWebView = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Visibility, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Open Application Viewer")
                            }
                            
                            Spacer(Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = { 
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val text = "User: ${employee.payrollUsername}\nPass: ${employee.payrollPassword}"
                                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Payroll Credentials", text))
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CreditCard, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Copy Credentials")
                            }
                        }
                    } else if (payoutOptions.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Available Payslips",
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            items(payoutOptions) { option ->
                                ListItem(
                                    headlineContent = { Text(option.date, fontWeight = FontWeight.SemiBold) },
                                    leadingContent = { Icon(Icons.Default.Description, null, tint = MaterialTheme.colorScheme.primary) },
                                    trailingContent = { Icon(Icons.Default.FileDownload, null) },
                                    modifier = Modifier.clickable {
                                        if (webViewInstance == null) return@clickable
                                        
                                        isLoading = true
                                        showWebView = true
                                        
                                        val script = """
                                            (function() {
                                                var select = document.getElementById('ctl00_ContentPlaceHolder1_ddlPayout') || 
                                                             document.querySelector('select[id*="ddlPayout"]');
                                                
                                                var btn = document.getElementById('ctl00_ContentPlaceHolder1_btnViewReport') || 
                                                          document.querySelector('input[id*="btnViewReport"]') ||
                                                          document.querySelector('input[value="View"]');
                                                
                                                if (select && btn) {
                                                    select.value = '${option.id}';
                                                    
                                                    // Trigger all necessary events for ASP.NET
                                                    var events = ['focus', 'change', 'blur'];
                                                    events.forEach(function(e) {
                                                        select.dispatchEvent(new Event(e, { bubbles: true }));
                                                    });

                                                    setTimeout(function() {
                                                        btn.focus();
                                                        btn.click();
                                                        AndroidPortal.notifyDone(); 
                                                    }, 800);
                                                } else {
                                                    AndroidPortal.notifyDone();
                                                }
                                            })();
                                        """.trimIndent()
                                        webViewInstance?.evaluateJavascript(script, null)
                                    }
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f))) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalWebViewScreen(
    url: String,
    title: String,
    portalUser: String? = "smtp_smckmall_058184",
    portalPass: String? = "cJEjZn",
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    val standardUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    val loginJs = """
        (function() {
            var user = '$portalUser';
            var pass = '$portalPass';
            
            function simulateHumanInput(el, value) {
                if (!el) return;
                el.focus();
                el.value = "";
                try {
                    document.execCommand('selectAll', false, null);
                    document.execCommand('delete', false, null);
                    document.execCommand('insertText', false, value);
                } catch (e) {}
                if (el.value !== value) el.value = value;
                ['input', 'change', 'blur', 'keyup', 'keydown', 'keypress'].forEach(function(type) {
                    var evt = document.createEvent('HTMLEvents');
                    evt.initEvent(type, true, true);
                    el.dispatchEvent(evt);
                });
            }
            
            function fill() {
                var u = document.getElementById('Input_UsernameVal') || document.querySelector('input[id*="User"]');
                var p = document.getElementById('Input_PasswordVal') || document.querySelector('input[id*="Pass"]');
                var b = document.querySelector('#b5-Button button') || document.querySelector('button.btn-primary');
                if (u && p && b) {
                    var err = document.querySelector('.feedback-message-error') || document.querySelector('.os-internal-Feedback_Message_Error');
                    if (err && err.innerText.trim().length > 0) return true;
                    simulateHumanInput(u, user);
                    setTimeout(function() {
                        simulateHumanInput(p, pass);
                        setTimeout(function() { 
                            b.focus(); 
                            b.click(); 
                            var clickEvt = new MouseEvent('click', { bubbles: true, cancelable: true, view: window });
                            b.dispatchEvent(clickEvt);
                        }, 1200);
                    }, 1000);
                    return true;
                }
                return false;
            }
            var attempts = 0;
            function auto() { if (attempts++ < 30 && !fill()) setTimeout(auto, 1200); }
            auto();
        })();
    """.trimIndent()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { webViewInstance?.reload() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewInstance = this
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            allowFileAccess = true
                            allowContentAccess = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            userAgentString = standardUserAgent
                            
                            // Advanced settings for OutSystems/Modern sites
                            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                        }
                        
                        // Enable hardware acceleration
                        setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                            }
                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                if (url != null && (url.contains("Login") || url.endsWith(".com/"))) {
                                    view?.evaluateJavascript(loginJs, null)
                                }
                                
                                // Comprehensive Zoom & Scaling Fix (Permit Style)
                                view?.evaluateJavascript("""
                                    (function() {
                                        // 1. Force a wide viewport to allow manual zooming on desktop-like layouts
                                        var metas = document.getElementsByTagName('meta');
                                        for (var i=0; i<metas.length; i++) {
                                            if (metas[i].name == "viewport") metas[i].parentNode.removeChild(metas[i]);
                                        }
                                        var meta = document.createElement('meta');
                                        meta.name = "viewport";
                                        meta.content = "width=1200, initial-scale=0.3, minimum-scale=0.1, maximum-scale=10.0, user-scalable=yes";
                                        document.head.appendChild(meta);
                                        
                                        // 2. Unlock all container constraints that block scaling/scrolling
                                        var style = document.getElementById('zoom-unblocker-ext') || document.createElement('style');
                                        style.id = 'zoom-unblocker-ext';
                                        style.innerHTML = `
                                            html, body { 
                                                overflow: auto !important; 
                                                width: 1200px !important; 
                                                min-width: 1200px !important; 
                                                position: relative !important; 
                                                height: auto !important; 
                                                -webkit-overflow-scrolling: touch !important;
                                                touch-action: auto !important;
                                            }
                                            .PageContainer, .MainContainer, .os-internal-ui-view, .tabs-content { 
                                                overflow: visible !important; 
                                                width: auto !important; 
                                                min-width: 1200px !important; 
                                            }
                                            * { 
                                                max-width: none !important; 
                                                user-select: auto !important;
                                            }
                                        `;
                                        document.head.appendChild(style);
                                        
                                        // 3. Force document-level scrollability
                                        document.documentElement.style.overflow = 'auto';
                                        document.body.style.overflow = 'auto';
                                    })();
                                """.trimIndent(), null)
                            }
                            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: android.net.http.SslError?) {
                                handler?.proceed()
                            }
                        }
                        loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }
        }
    }
}

// --- REAL-TIME CHAT SYSTEM ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: AttendanceViewModel,
    onChatSelected: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val employees by viewModel.allEmployees.collectAsState()
    val loggedInEmployee by viewModel.loggedInEmployee.collectAsState()
    val allMessages by viewModel.allAttendanceRecords.collectAsState(initial = emptyList()) // Using repository.allMessages but flow name varies
    
    val myId = loggedInEmployee?.employeeNo ?: loggedInEmployee?.id ?: ""
    
    // Group chats with each employee
    val chatContacts = remember(employees, allMessages) {
        employees.filter { it.employeeNo != myId && it.position != "Excrew" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transmissions") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Group Chat Node
            item {
                val groupMessages by viewModel.groupChatMessages.collectAsState(initial = emptyList())
                val lastGroupMsg = groupMessages.lastOrNull()
                
                ListItem(
                    modifier = Modifier.clickable { onChatSelected("group", "Fleet Broadcast") },
                    headlineContent = { Text("Fleet Broadcast (Group)", fontWeight = FontWeight.Bold) },
                    supportingContent = { 
                        Text(
                            text = lastGroupMsg?.message ?: "Open channel...",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingContent = {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Groups, tint = MaterialTheme.colorScheme.onPrimaryContainer, contentDescription = null)
                        }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }

            // Private Chats
            items(chatContacts) { emp ->
                val contactId = emp.employeeNo ?: emp.id
                val unreadMessages by viewModel.getUnreadMessages(myId).collectAsState(initial = emptyList())
                val hasUnread = unreadMessages.any { it.senderId == contactId }

                ListItem(
                    modifier = Modifier.clickable { onChatSelected(contactId, emp.firstName ?: "Unit") },
                    headlineContent = { Text("${emp.firstName} ${emp.lastName}") },
                    supportingContent = { 
                        Text(
                            text = if (hasUnread) "New message" else "Start secure uplink...",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (hasUnread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (hasUnread) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingContent = {
                        AsyncImage(
                            model = getProfilePicUrl(emp.employeeNo ?: ""),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp).clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    viewModel: AttendanceViewModel,
    otherId: String,
    chatName: String,
    onBack: () -> Unit
) {
    val messages by (if (otherId == "group") viewModel.groupChatMessages else viewModel.getPrivateMessages(otherId)).collectAsState(initial = emptyList())
    val loggedInEmployee by viewModel.loggedInEmployee.collectAsState()
    val myId = loggedInEmployee?.employeeNo ?: loggedInEmployee?.id ?: ""
    
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                Toast.makeText(context, "Uploading image...", Toast.LENGTH_SHORT).show()
                val url = viewModel.uploadImageToImgBB(it)
                if (url != null) {
                    viewModel.sendChatMessage(otherId, url)
                } else {
                    Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (otherId != "group") {
                            AsyncImage(
                                model = getProfilePicUrl(otherId),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp).clip(CircleShape),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(chatName, fontSize = 16.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var showEmojiPicker by remember { mutableStateOf(false) }
                    val emojis = listOf("😊", "😂", "👍", "🙏", "🔥", "✨", "🙌", "❤️", "✅", "⚠️", "🕒", "📍")

                    Column(modifier = Modifier.weight(1f)) {
                        if (showEmojiPicker) {
                            androidx.compose.foundation.lazy.LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(emojis) { emoji ->
                                    Text(
                                        text = emoji,
                                        modifier = Modifier
                                            .clickable { 
                                                text += emoji
                                                showEmojiPicker = false
                                            }
                                            .padding(4.dp),
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { showEmojiPicker = !showEmojiPicker },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Emojis",
                                    tint = if (showEmojiPicker) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = { launcher.launch("image/*") },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = "Photo",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://file.kiwi/8802af9a#tMiEz85jzjOOKdMfNYEw3g"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.AttachFile,
                                    contentDescription = "Shared Folder",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            androidx.compose.foundation.text.BasicTextField(
                                value = text,
                                onValueChange = { text = it },
                                modifier = Modifier.weight(1f).heightIn(min = 36.dp),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (text.isEmpty()) {
                                            Text("Secure transmission...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (text.isNotBlank()) {
                                viewModel.sendChatMessage(otherId, text)
                                text = ""
                            }
                        },
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = "Send", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 8.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Bottom
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == myId
                ChatBubble(msg, isMe, viewModel)
                Spacer(Modifier.height(2.dp))
            }
            item { Spacer(Modifier.height(4.dp)) }
        }
    }
}

@Composable
fun ChatBubble(message: com.sam.myapplication.data.ChatMessage, isMe: Boolean, viewModel: AttendanceViewModel) {
    val employees by viewModel.allEmployees.collectAsState()
    val sender = employees.find { it.employeeNo == message.senderId }
    var showMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editedText by remember { mutableStateOf(message.message) }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Message") },
            text = {
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.editChatMessage(message, editedText)
                    showEditDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        if (!isMe) {
            Text(
                text = sender?.firstName ?: "Unit",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 1.dp)
            )
        }
        
        Box {
            Surface(
                color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                shape = if (isMe) RoundedCornerShape(12.dp, 12.dp, 0.dp, 12.dp) else RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp),
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .let {
                        if (isMe) {
                            it.clickable { showMenu = true }
                        } else it
                    },
                tonalElevation = 1.dp
            ) {
                val isImage = message.message.startsWith("https://i.ibb.co/") || message.message.contains("imgbb.com")
                val isKiwi = message.message.contains("https://file.kiwi/8802af9a")

                Column {
                    if (isImage) {
                        AsyncImage(
                            model = message.message,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    } else if (isKiwi) {
                        val context = androidx.compose.ui.platform.LocalContext.current
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .clickable {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://file.kiwi/8802af9a#tMiEz85jzjOOKdMfNYEw3g"))
                                    context.startActivity(intent)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Shared Web Folder",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Open secure repository",
                                    fontSize = 10.sp,
                                    color = (if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = message.message,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        showMenu = false
                        showEditDialog = true
                    },
                    leadingIcon = { Icon(Icons.Default.Edit, null) }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        showMenu = false
                        viewModel.deleteChatMessage(message)
                    },
                    leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                )
            }
        }
    }
}

fun getProfilePicUrl(empNo: String): String {
    if (empNo.isBlank()) return "https://ui-avatars.com/api/?name=User&background=0b0f19&color=22d3ee"
    val safeEmpNo = empNo.trim().replace("/", "_").replace("#", "_").replace(" ", "_")
    return "https://uhmjgnwpzsemksxcjpiz.supabase.co/storage/v1/object/public/backups/$safeEmpNo/$safeEmpNo.jpg"
}



