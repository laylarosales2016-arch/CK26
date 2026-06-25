package com.sam.myapplication.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sam.myapplication.data.Employee
import com.sam.myapplication.data.EmployeeSchedule
import com.sam.myapplication.data.ShiftTemplate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen(
    viewModel: AttendanceViewModel,
    onBack: () -> Unit
) {
    val allEmployees by viewModel.allEmployees.collectAsState()
    val allSchedules by viewModel.allSchedules.collectAsState()
    val allShiftTemplates by viewModel.allShiftTemplates.collectAsState()
    val settingsUpdated by viewModel.settingsUpdated.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val startOfWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val weekDates = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    var showShiftTemplateManager by remember { mutableStateOf(false) }
    var showAddEmployeeDialog by remember { mutableStateOf(false) }
    var exportTargetEmployee by remember { mutableStateOf<Employee?>(null) }
    var showExportDatePicker by remember { mutableStateOf(false) }
    var exportStartDate by remember { mutableStateOf(LocalDate.now()) }
    var exportEndDate by remember { mutableStateOf(LocalDate.now().plusDays(7)) }
    var showReadoutDialog by remember { mutableStateOf(false) }
    var readoutSchedules by remember { mutableStateOf<List<EmployeeSchedule>>(emptyList()) }
    var showHiddenEmployeesDialog by remember { mutableStateOf(false) }
    var showDeleteIcons by remember { mutableStateOf(false) }
    var showPrintButtons by remember { mutableStateOf(false) }
    var showOrderNumbers by remember { mutableStateOf(true) }
    var editingCell by remember { mutableStateOf<Pair<Employee, LocalDate>?>(null) }
    var editingOrderEmployee by remember { mutableStateOf<Employee?>(null) }
    var employeeToDelete by remember { mutableStateOf<Employee?>(null) }
    var positionToColor by remember { mutableStateOf<String?>(null) }
    var hiddenDayIndices by remember { mutableStateOf(setOf<Int>()) }
    var showMenu by remember { mutableStateOf(false) }

    val syncStatus by viewModel.syncStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val excelLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.ms-excel"),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.openOutputStream(it)?.let { outputStream ->
                    val excluded = listOf("manager", "administrator", "excrew", "assistant manager")
                    val positions = listOf("Dine In", "SS", "CIC", "DJ", "Dispatch", "Cashier", "SO", "Regular", "Assembler", "Fryman", "Noodles", "Backup", "SC")
                    
                    val orderedEmps = mutableListOf<Employee>()
                    
                    // 1. Group by positions in order
                    positions.forEach { pos ->
                        val posEmps = allEmployees.filter { emp ->
                            val mondayDate = weekDates[0].toString()
                            val weeklyOverride = allSchedules.find { it.employeeId == emp.id && it.date == mondayDate }?.position
                            val effectivePos = weeklyOverride ?: emp.schedulerPosition ?: emp.position
                            
                            (effectivePos == pos || emp.department == pos) &&
                            !emp.isHiddenFromScheduler &&
                            allSchedules.none { s -> s.employeeId == emp.id && s.date == weekDates[0].toString() && s.tag == "HIDDEN" } &&
                            effectivePos?.lowercase() !in excluded &&
                            emp.department?.lowercase() !in excluded
                        }.sortedWith(compareBy({ it.schedulerOrder }, { it.firstName }))
                        orderedEmps.addAll(posEmps)
                    }
                    
                    // 2. Add "Other" employees
                    val otherEmps = allEmployees.filter { emp ->
                        val mondayDate = weekDates[0].toString()
                        val weeklyOverride = allSchedules.find { it.employeeId == emp.id && it.date == mondayDate }?.position
                        val effectivePos = weeklyOverride ?: emp.schedulerPosition ?: emp.position

                        effectivePos !in positions && 
                        emp.department !in positions &&
                        !emp.isHiddenFromScheduler &&
                        allSchedules.none { s -> s.employeeId == emp.id && s.date == weekDates[0].toString() && s.tag == "HIDDEN" } &&
                        effectivePos?.lowercase() !in excluded &&
                        emp.department?.lowercase() !in excluded
                    }.filter { it !in orderedEmps }.sortedWith(compareBy({ it.schedulerOrder }, { it.firstName }))
                    
                    orderedEmps.addAll(otherEmps)

                    viewModel.exportSchedulerToExcel(outputStream, orderedEmps, allSchedules, weekDates) { success ->
                        // ViewModel sets syncStatus
                    }
                }
            }
        }
    )

    LaunchedEffect(syncStatus) {
        syncStatus?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSyncStatus()
        }
    }

    Scaffold(
        topBar = {
            if (!isLandscape) {
                TopAppBar(
                    title = { Text("Employee Scheduler") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showPrintButtons = !showPrintButtons }) {
                            Icon(
                                if (showPrintButtons) Icons.Default.Print else Icons.Default.PrintDisabled,
                                contentDescription = "Toggle Print Icons",
                                tint = if (showPrintButtons) Color(0xFF00ACC1) else LocalContentColor.current
                            )
                        }
                        IconButton(onClick = { showDeleteIcons = !showDeleteIcons }) {
                            Icon(
                                if (showDeleteIcons) Icons.Default.DeleteForever else Icons.Default.DeleteOutline,
                                contentDescription = "Toggle Delete Icons",
                                tint = if (showDeleteIcons) Color.Red else LocalContentColor.current
                            )
                        }
                        IconButton(onClick = { showAddEmployeeDialog = true }) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Add Employee")
                        }
                        
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Upload to Supabase") },
                                    onClick = { viewModel.uploadSchedulerData(); showMenu = false },
                                    leadingIcon = { Icon(Icons.Default.Upload, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Download from Supabase") },
                                    onClick = { viewModel.downloadSchedulerData(); showMenu = false },
                                    leadingIcon = { Icon(Icons.Default.Download, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Save to Excel") },
                                    onClick = { excelLauncher.launch("Chowking_Schedule_${startOfWeek.toString()}.xls"); showMenu = false },
                                    leadingIcon = { Icon(Icons.Default.TableChart, null) }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Manage Shifts") },
                                    onClick = { showShiftTemplateManager = true; showMenu = false },
                                    leadingIcon = { Icon(Icons.Default.Settings, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Manage Hidden") },
                                    onClick = { showHiddenEmployeesDialog = true; showMenu = false },
                                    leadingIcon = { Icon(Icons.Default.Visibility, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text(if (showOrderNumbers) "Hide Rank Numbers" else "Show Rank Numbers") },
                                    onClick = { showOrderNumbers = !showOrderNumbers; showMenu = false },
                                    leadingIcon = { Icon(if (showOrderNumbers) Icons.Default.Filter1 else Icons.Default.FormatListNumbered, null) }
                                )
                            }
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(if (isLandscape) PaddingValues(0.dp) else padding).fillMaxSize()) {
            // Week Selector
            Row(
                modifier = Modifier.fillMaxWidth().padding(if (isLandscape) 2.dp else 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedDate = selectedDate.minusWeeks(1) }, modifier = Modifier.size(if (isLandscape) 32.dp else 40.dp)) {
                    Icon(Icons.Default.ChevronLeft, null)
                }
                TextButton(onClick = { selectedDate = LocalDate.now() }) {
                    Text(
                        text = "Week of ${startOfWeek.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = if (isLandscape) 12.sp else 14.sp
                    )
                }
                IconButton(onClick = { selectedDate = selectedDate.plusWeeks(1) }, modifier = Modifier.size(if (isLandscape) 32.dp else 40.dp)) {
                    Icon(Icons.Default.ChevronRight, null)
                }
            }

            // Day Filter Toggles (New Dedicated Row)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                days.forEachIndexed { index, label ->
                    val isHidden = hiddenDayIndices.contains(index)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isLandscape) 24.dp else 32.dp)
                            .background(
                                if (isHidden) Color.LightGray else MaterialTheme.colorScheme.primary,
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                            .clickable {
                                hiddenDayIndices = if (isHidden) {
                                    hiddenDayIndices - index
                                } else {
                                    hiddenDayIndices + index
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            fontSize = if (isLandscape) 10.sp else 13.sp,
                            color = if (isHidden) Color.DarkGray else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Table Header
                    Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(vertical = 4.dp)) {
                        Text("Employee", modifier = Modifier.weight(1.5f).padding(start = 4.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        weekDates.forEachIndexed { index, date ->
                            if (!hiddenDayIndices.contains(index)) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(date.dayOfWeek.name.take(3), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(date.format(DateTimeFormatter.ofPattern("MMM d")), fontSize = 9.sp)
                                }
                            }
                        }
                    }

                    val diningPositions = listOf("Dine In", "CIC", "DJ", "Dispatch", "Cashier", "SC")
                    val kitchenPositions = listOf("SO", "Regular", "Assembler", "Fryman", "Noodles", "Backup")
                    
                    val excluded = listOf("manager", "administrator", "excrew", "assistant manager")

                    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)) {
                        // Section: DINING
                        item {
                            Surface(
                                color = Color(0xFFE0F7FA), 
                                modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.Black)
                            ) {
                                Text(
                                    "DINING UNIT", 
                                    modifier = Modifier.padding(8.dp), 
                                    fontWeight = FontWeight.ExtraBold, 
                                    color = Color(0xFF006064), 
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        
                        diningPositions.forEach { pos ->
                            val posEmployees = allEmployees.filter { emp ->
                                val mondayDate = weekDates[0].toString()
                                val weeklyOverride = allSchedules.find { it.employeeId == emp.id && it.date == mondayDate }?.position
                                val effectivePos = weeklyOverride ?: emp.schedulerPosition ?: emp.position
                                
                                val isPosMatch = (effectivePos == pos || emp.department == pos)
                                if (effectivePos?.lowercase() in excluded || emp.department?.lowercase() in excluded) return@filter false
                                val hasActualSchedule = allSchedules.any { s -> s.employeeId == emp.id && s.date in weekDates.map { it.toString() } && s.tag != "HIDDEN" }
                                val isHiddenThisWeek = allSchedules.any { s -> s.employeeId == emp.id && s.date == mondayDate && s.tag == "HIDDEN" }
                                isPosMatch && (hasActualSchedule || (!emp.isHiddenFromScheduler && !isHiddenThisWeek))
                            }.sortedWith(compareBy({ it.schedulerOrder }, { it.firstName }))

                            if (posEmployees.isNotEmpty()) {
                                item {
                                    val bgColor = viewModel.getPositionColor(pos).let { if (it != 0) Color(it) else Color(0xFFF1F8E9) }
                                    val fontColor = viewModel.getPositionFontColor(pos).let { if (it != 0) Color(it) else Color.Black }
                                    Surface(
                                        color = bgColor, 
                                        modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.Black).clickable { positionToColor = pos }
                                    ) {
                                        Text(text = pos, modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Black, fontSize = 12.sp, color = fontColor)
                                    }
                                }
                                items(posEmployees) { employee ->
                                    EmployeeScheduleRow(
                                        employee = employee,
                                        dates = weekDates,
                                        hiddenDayIndices = hiddenDayIndices,
                                        schedules = allSchedules.filter { it.employeeId == employee.id },
                                        onClick = { date -> editingCell = Pair(employee, date) },
                                        onDeleteEmployee = { employeeToDelete = it },
                                        onExportClick = { 
                                            exportTargetEmployee = it
                                            showExportDatePicker = true
                                        },
                                        onEditOrder = { editingOrderEmployee = it },
                                        showDeleteIcon = showDeleteIcons,
                                        showPrintIcon = showPrintButtons,
                                        showOrderNumber = showOrderNumbers
                                    )
                                    HorizontalDivider(color = Color.Black, thickness = 0.5.dp)
                                }
                            }
                        }

                        // Section: KITCHEN
                        item {
                            Spacer(Modifier.height(16.dp))
                            Surface(
                                color = Color(0xFFFFF3E0), 
                                modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.Black)
                            ) {
                                Text(
                                    "KITCHEN UNIT", 
                                    modifier = Modifier.padding(8.dp), 
                                    fontWeight = FontWeight.ExtraBold, 
                                    color = Color(0xFFE65100), 
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        
                        kitchenPositions.forEach { pos ->
                            val posEmployees = allEmployees.filter { emp ->
                                val mondayDate = weekDates[0].toString()
                                val weeklyOverride = allSchedules.find { it.employeeId == emp.id && it.date == mondayDate }?.position
                                val effectivePos = weeklyOverride ?: emp.schedulerPosition ?: emp.position

                                val isPosMatch = (effectivePos == pos || emp.department == pos)
                                if (effectivePos?.lowercase() in excluded || emp.department?.lowercase() in excluded) return@filter false
                                val hasActualSchedule = allSchedules.any { s -> s.employeeId == emp.id && s.date in weekDates.map { it.toString() } && s.tag != "HIDDEN" }
                                val isHiddenThisWeek = allSchedules.any { s -> s.employeeId == emp.id && s.date == mondayDate && s.tag == "HIDDEN" }
                                isPosMatch && (hasActualSchedule || (!emp.isHiddenFromScheduler && !isHiddenThisWeek))
                            }.sortedWith(compareBy({ it.schedulerOrder }, { it.firstName }))

                            if (posEmployees.isNotEmpty()) {
                                item {
                                    val bgColor = viewModel.getPositionColor(pos).let { if (it != 0) Color(it) else Color(0xFFFFFDE7) }
                                    val fontColor = viewModel.getPositionFontColor(pos).let { if (it != 0) Color(it) else Color.Black }
                                    Surface(
                                        color = bgColor, 
                                        modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.Black).clickable { positionToColor = pos }
                                    ) {
                                        Text(text = pos, modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Black, fontSize = 12.sp, color = fontColor)
                                    }
                                }
                                items(posEmployees) { employee ->
                                    EmployeeScheduleRow(
                                        employee = employee,
                                        dates = weekDates,
                                        hiddenDayIndices = hiddenDayIndices,
                                        schedules = allSchedules.filter { it.employeeId == employee.id },
                                        onClick = { date -> editingCell = Pair(employee, date) },
                                        onDeleteEmployee = { employeeToDelete = it },
                                        onExportClick = { 
                                            exportTargetEmployee = it
                                            showExportDatePicker = true
                                        },
                                        onEditOrder = { editingOrderEmployee = it },
                                        showDeleteIcon = showDeleteIcons,
                                        showPrintIcon = showPrintButtons,
                                        showOrderNumber = showOrderNumbers
                                    )
                                    HorizontalDivider(color = Color.Black, thickness = 0.5.dp)
                                }
                            }
                        }
                        
                        // Section: LEADERSHIP / OTHER
                        val combinedOther = diningPositions + kitchenPositions
                        val otherEmployees = allEmployees.filter { emp ->
                            val mondayDate = weekDates[0].toString()
                            val weeklyOverride = allSchedules.find { it.employeeId == emp.id && it.date == mondayDate }?.position
                            val effectivePos = weeklyOverride ?: emp.schedulerPosition ?: emp.position

                            val isOtherPos = effectivePos !in combinedOther && emp.department !in combinedOther
                            if (effectivePos?.lowercase() in excluded || emp.department?.lowercase() in excluded) return@filter false
                            val hasActualSchedule = allSchedules.any { s -> s.employeeId == emp.id && s.date in weekDates.map { it.toString() } && s.tag != "HIDDEN" }
                            val isHiddenThisWeek = allSchedules.any { s -> s.employeeId == emp.id && s.date == mondayDate && s.tag == "HIDDEN" }
                            isOtherPos && (hasActualSchedule || (!emp.isHiddenFromScheduler && !isHiddenThisWeek))
                        }.sortedWith(compareBy({ it.schedulerOrder }, { it.firstName }))

                        if (otherEmployees.isNotEmpty()) {
                            item {
                                Spacer(Modifier.height(16.dp))
                                Surface(
                                    color = Color(0xFFF5F5F5), 
                                    modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.Black)
                                ) {
                                    Text(
                                        "MANAGEMENT / SUPPORT", 
                                        modifier = Modifier.padding(8.dp), 
                                        fontWeight = FontWeight.ExtraBold, 
                                        color = Color.DarkGray, 
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            items(otherEmployees) { employee ->
                                EmployeeScheduleRow(
                                    employee = employee,
                                    dates = weekDates,
                                    hiddenDayIndices = hiddenDayIndices,
                                    schedules = allSchedules.filter { it.employeeId == employee.id },
                                    onClick = { date -> editingCell = Pair(employee, date) },
                                    onDeleteEmployee = { employeeToDelete = it },
                                    onExportClick = { 
                                        exportTargetEmployee = it
                                        showExportDatePicker = true
                                    },
                                    onEditOrder = { editingOrderEmployee = it },
                                    showDeleteIcon = showDeleteIcons,
                                    showPrintIcon = showPrintButtons,
                                    showOrderNumber = showOrderNumbers
                                )
                                HorizontalDivider(color = Color.Black, thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (editingOrderEmployee != null) {
        var newOrder by remember { mutableStateOf(editingOrderEmployee!!.schedulerOrder.toString()) }
        val mondayDate = weekDates[0].toString()
        val currentWeeklyPos = allSchedules.find { it.employeeId == editingOrderEmployee!!.id && it.date == mondayDate }?.position
        var selectedWeeklyPos by remember { mutableStateOf(currentWeeklyPos ?: editingOrderEmployee!!.schedulerPosition ?: editingOrderEmployee!!.position ?: "") }
        var expandedPos by remember { mutableStateOf(false) }

        val stationPositions = listOf("Dine In", "CIC", "DJ", "Dispatch", "Cashier", "SC", "SO", "Regular", "Assembler", "Fryman", "Noodles", "Backup", "SS")

        AlertDialog(
            onDismissRequest = { editingOrderEmployee = null },
            title = { Text("Update Node: ${editingOrderEmployee?.firstName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Rank / Order Number:")
                    OutlinedTextField(
                        value = newOrder,
                        onValueChange = { if (it.all { char -> char.isDigit() }) newOrder = it },
                        label = { Text("Rank") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )

                    HorizontalDivider()

                    Text("Weekly Station / Position Override:")
                    Box {
                        OutlinedTextField(
                            value = selectedWeeklyPos,
                            onValueChange = { selectedWeeklyPos = it },
                            label = { Text("Station") },
                            trailingIcon = { IconButton(onClick = { expandedPos = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = expandedPos, onDismissRequest = { expandedPos = false }) {
                            stationPositions.forEach { pos ->
                                DropdownMenuItem(
                                    text = { Text(pos) },
                                    onClick = {
                                        selectedWeeklyPos = pos
                                        expandedPos = false
                                    }
                                )
                            }
                        }
                    }
                    Text("Changes to Station only apply to the week of $mondayDate", fontSize = 11.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(onClick = {
                    editingOrderEmployee?.let { emp ->
                        // 1. Update global rank
                        viewModel.updateEmployee(context, emp.copy(schedulerOrder = newOrder.toIntOrNull() ?: 0))
                        
                        // 2. Update weekly position (stored in Monday's schedule record)
                        val existingMonday = allSchedules.find { it.employeeId == emp.id && it.date == mondayDate }
                        val updatedMonday = existingMonday?.copy(position = selectedWeeklyPos) 
                            ?: EmployeeSchedule(employeeId = emp.id, date = mondayDate, position = selectedWeeklyPos)
                        
                        viewModel.saveSchedule(updatedMonday)
                    }
                    editingOrderEmployee = null
                }) { Text("Save Changes") }
            },
            dismissButton = {
                TextButton(onClick = { editingOrderEmployee = null }) { Text("Cancel") }
            }
        )
    }

    if (showAddEmployeeDialog) {
        AddEmployeeDialog(
            allEmployees = allEmployees,
            onSave = { employee, position ->
                viewModel.updateEmployee(context, employee.copy(schedulerPosition = position, isHiddenFromScheduler = false))
                showAddEmployeeDialog = false
            },
            onDismiss = { showAddEmployeeDialog = false }
        )
    }

    if (showHiddenEmployeesDialog) {
        val hiddenEmployees = allEmployees.filter { it.isHiddenFromScheduler && it.position?.lowercase() != "excrew" }
        AlertDialog(
            onDismissRequest = { showHiddenEmployeesDialog = false },
            title = { Text("Restore Hidden Employees") },
            text = {
                if (hiddenEmployees.isEmpty()) {
                    Text("No hidden employees.")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(hiddenEmployees) { emp ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${emp.firstName}")
                                Button(onClick = { viewModel.toggleEmployeeSchedulerVisibility(context, emp) }) {
                                    Text("Restore")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHiddenEmployeesDialog = false }) { Text("Done") }
            }
        )
    }

    if (employeeToDelete != null) {
        AlertDialog(
            onDismissRequest = { employeeToDelete = null },
            title = { Text("Remove from Scheduler") },
            text = { 
                Column {
                    Text("How do you want to remove ${employeeToDelete?.firstName}?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. 'This Week Only' will hide them for this specific week but keep them in others.", fontSize = 12.sp)
                    Text("2. 'Globally' will hide them from all schedules until restored.", fontSize = 12.sp)
                }
            },
            confirmButton = {
                Column {
                    TextButton(onClick = {
                        employeeToDelete?.let { emp ->
                            // Insert a HIDDEN tag on the Monday of this week
                            viewModel.saveSchedule(EmployeeSchedule(
                                employeeId = emp.id,
                                date = weekDates[0].toString(),
                                tag = "HIDDEN"
                            ))
                        }
                        employeeToDelete = null
                    }) { Text("This Week Only") }
                    
                    TextButton(onClick = {
                        employeeToDelete?.let { viewModel.toggleEmployeeSchedulerVisibility(context, it) }
                        employeeToDelete = null
                    }) { Text("Globally", color = Color.Red) }
                }
            },
            dismissButton = {
                TextButton(onClick = { employeeToDelete = null }) { Text("Cancel") }
            }
        )
    }

    if (showShiftTemplateManager) {
        ShiftTemplateManagerDialog(
            templates = allShiftTemplates,
            onSave = { viewModel.saveShiftTemplate(it) },
            onDelete = { viewModel.deleteShiftTemplate(it) },
            onDismiss = { showShiftTemplateManager = false }
        )
    }

    editingCell?.let { (employee, date) ->
        ScheduleEditDialog(
            employee = employee,
            date = date,
            currentSchedule = allSchedules.find { it.employeeId == employee.id && it.date == date.toString() },
            templates = allShiftTemplates,
            onSave = { sched ->
                viewModel.saveSchedule(sched)
                editingCell = null
            },
            onDelete = { sched ->
                viewModel.deleteSchedule(sched)
                editingCell = null
            },
            onPostToDTR = { sched ->
                if (sched.tag == "READOUT") {
                    exportTargetEmployee = employee
                    showExportDatePicker = true
                } else {
                    viewModel.syncScheduleToDTR(employee, date.toString(), sched.scheduleText, sched.tag)
                }
                editingCell = null
            },
            onDismiss = { editingCell = null }
        )
    }

    if (showExportDatePicker) {
        val datePickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showExportDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val start = datePickerState.selectedStartDateMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                    val end = datePickerState.selectedEndDateMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                    if (start != null && end != null) {
                        exportStartDate = start
                        exportEndDate = end
                        scope.launch {
                            val schedules = viewModel.getSchedulesInRange(exportTargetEmployee!!.id, start.toString(), end.toString()).first()
                            readoutSchedules = schedules
                            showReadoutDialog = true
                        }
                        showExportDatePicker = false
                    }
                }) { Text("Confirm") }
            },
            dismissButton = { TextButton(onClick = { showExportDatePicker = false }) { Text("Cancel") } }
        ) {
            DateRangePicker(state = datePickerState, modifier = Modifier.weight(1f))
        }
    }

    if (showReadoutDialog && exportTargetEmployee != null) {
        AlertDialog(
            onDismissRequest = { showReadoutDialog = false },
            title = { 
                Column {
                    Text("Schedule Readout", fontWeight = FontWeight.ExtraBold)
                    Text("${exportTargetEmployee!!.firstName} ${exportTargetEmployee!!.lastName}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Period: ${exportStartDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))} — ${exportEndDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val sortedSchedules = readoutSchedules.sortedBy { it.date }
                    
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(sortedSchedules) { sched ->
                            val dateObj = LocalDate.parse(sched.date)
                            val isRD = sched.tag == "RD" || sched.tag == "RRD" || sched.scheduleText == "RD" || sched.scheduleText == "RRD"
                            
                            Card(
                                colors = CardDefaults.cardColors(containerColor = if (isRD) Color.Blue.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface),
                                border = BorderStroke(0.5.dp, if (isRD) Color.Blue.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = dateObj.format(DateTimeFormatter.ofPattern("EEEE")),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = sched.date,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    
                                    Surface(
                                        color = if (isRD) Color.Blue else MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = sched.scheduleText ?: sched.tag ?: "--",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = if (isRD) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    OutlinedButton(
                        onClick = {
                            val text = StringBuilder()
                            text.append("SCHEDULE READOUT: ${exportTargetEmployee!!.firstName} ${exportTargetEmployee!!.lastName}\n")
                            text.append("Period: $exportStartDate to $exportEndDate\n\n")
                            readoutSchedules.sortedBy { it.date }.forEach {
                                text.append("${it.date} (${LocalDate.parse(it.date).dayOfWeek.name.take(3)}): ${it.scheduleText ?: it.tag ?: "--"}\n")
                            }
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Schedule Readout", text.toString())
                            clipboard.setPrimaryClip(clip)
                            scope.launch { snackbarHostState.showSnackbar("Readout copied to clipboard") }
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Copy")
                    }

                    Button(
                        onClick = {
                            viewModel.batchSyncSchedulesToDTR(exportTargetEmployee!!, readoutSchedules)
                            showReadoutDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.Sync, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sync to DTR")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showReadoutDialog = false }) { Text("Close") }
            }
        )
    }

    if (positionToColor != null) {
        PositionColorDialog(
            position = positionToColor!!,
            initialBgColor = viewModel.getPositionColor(positionToColor!!).let { if (it != 0) Color(it) else Color.Gray },
            initialFontColor = viewModel.getPositionFontColor(positionToColor!!).let { if (it != 0) Color(it) else Color.White },
            onColorSelected = { bg, font ->
                viewModel.setPositionColor(positionToColor!!, bg.toArgb())
                viewModel.setPositionFontColor(positionToColor!!, font.toArgb())
                positionToColor = null
            },
            onDismiss = { positionToColor = null }
        )
    }
}

@Composable
fun PositionColorDialog(
    position: String,
    initialBgColor: Color,
    initialFontColor: Color,
    onColorSelected: (Color, Color) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedBgColor by remember { mutableStateOf(initialBgColor) }
    var selectedFontColor by remember { mutableStateOf(initialFontColor) }

    val colors = listOf(
        Color(0xFFFFCDD2), Color(0xFFF8BBD0), Color(0xFFE1BEE7), Color(0xFFD1C4E9),
        Color(0xFFC5CAE9), Color(0xFFBBDEFB), Color(0xFFB3E5FC), Color(0xFFB2EBF2),
        Color(0xFFB2DFDB), Color(0xFFC8E6C9), Color(0xFFDCEDC8), Color(0xFFF0F4C3),
        Color(0xFFFFF9C4), Color(0xFFFFECB3), Color(0xFFFFE0B2), Color(0xFFFFCCBC),
        // Rainbow colors
        Color.Red, Color(0xFFFFA500), Color.Yellow, Color.Green, Color.Blue, Color(0xFF4B0082), Color(0xFFEE82EE),
        Color.Black, Color.White, Color.DarkGray, Color.Gray, Color.LightGray
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Colors for $position") },
        text = {
            Column {
                Text("Background Color:", fontWeight = FontWeight.Bold)
                FlowRow(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, shape = androidx.compose.foundation.shape.CircleShape)
                                .border(
                                    if (selectedBgColor == color) 2.dp else 1.dp,
                                    if (selectedBgColor == color) MaterialTheme.colorScheme.primary else Color.Gray,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .clickable { selectedBgColor = color }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Font Color:", fontWeight = FontWeight.Bold)
                FlowRow(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(Color.White, Color.Black, Color.Yellow, Color.Cyan, Color.Red).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, shape = androidx.compose.foundation.shape.CircleShape)
                                .border(
                                    if (selectedFontColor == color) 2.dp else 1.dp,
                                    if (selectedFontColor == color) MaterialTheme.colorScheme.primary else Color.Gray,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .clickable { selectedFontColor = color }
                        )
                    }
                }
            }
        },
        confirmButton = { 
            Button(onClick = { onColorSelected(selectedBgColor, selectedFontColor) }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EmployeeScheduleRow(
    employee: Employee,
    dates: List<LocalDate>,
    hiddenDayIndices: Set<Int>,
    schedules: List<EmployeeSchedule>,
    onClick: (LocalDate) -> Unit,
    onDeleteEmployee: (Employee) -> Unit,
    onExportClick: (Employee) -> Unit,
    onEditOrder: (Employee) -> Unit,
    showDeleteIcon: Boolean,
    showPrintIcon: Boolean,
    showOrderNumber: Boolean
) {
    val rowColor = employee.schedulerRowColor?.let { Color(it) } ?: Color.White
    val fontColor = employee.schedulerFontColor?.let { Color(it) } ?: Color.Black

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isLandscape) Modifier.height(30.dp) else Modifier.height(IntrinsicSize.Min))
            .background(rowColor)
    ) {
        Row(
            modifier = Modifier.weight(1.5f).padding(if (isLandscape) 2.dp else 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showOrderNumber) "${employee.schedulerOrder}. ${employee.firstName}" else "${employee.firstName}",
                modifier = Modifier.weight(1f).clickable { onEditOrder(employee) },
                fontSize = if (isLandscape) 11.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = if (isLandscape) 12.sp else 14.sp,
                color = fontColor
            )
            if (showDeleteIcon) {
                IconButton(
                    onClick = { onDeleteEmployee(employee) },
                    modifier = Modifier.size(if (isLandscape) 16.dp else 20.dp)
                ) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(if (isLandscape) 10.dp else 14.dp))
                }
            }
            if (showPrintIcon) {
                IconButton(
                    onClick = { onExportClick(employee) },
                    modifier = Modifier.size(if (isLandscape) 16.dp else 20.dp)
                ) {
                    Icon(
                        Icons.Default.Print,
                        null,
                        tint = Color(0xFF00ACC1).copy(alpha = 0.7f),
                        modifier = Modifier.size(if (isLandscape) 10.dp else 14.dp)
                    )
                }
            }
        }
        dates.forEachIndexed { index, date ->
            if (!hiddenDayIndices.contains(index)) {
                val schedule = schedules.find { it.date == date.toString() }
                val boxColor = schedule?.color?.let { Color(it) } ?: when (schedule?.tag) {
                    "RD", "RRD" -> Color(0xFF64B5F6) // Blue
                    "SICK" -> Color(0xFFE57373) // Red
                    "NS" -> Color(0xFFFFB74D) // Orange
                    "HIDDEN" -> Color.Transparent
                    else -> Color.White
                }
                val boxFontColor = schedule?.fontColor?.let { Color(it) } ?: if (schedule?.tag != null && schedule.tag != "HIDDEN") Color.White else Color.Black

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(boxColor)
                        .border(0.5.dp, Color.Black)
                        .clickable { onClick(date) }
                        .padding(if (isLandscape) 1.dp else 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = schedule?.scheduleText ?: (if (schedule?.tag != "HIDDEN") schedule?.tag else "") ?: "",
                        fontSize = if (isLandscape) 8.sp else 10.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = boxFontColor
                    )
                }
            }
        }
    }
}

@Composable
fun AddEmployeeDialog(
    allEmployees: List<Employee>,
    onSave: (Employee, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedEmployee by remember { mutableStateOf<Employee?>(null) }
    var position by remember { mutableStateOf("") }
    var expandedName by remember { mutableStateOf(false) }
    var expandedPos by remember { mutableStateOf(false) }
    
    val positions = listOf("Dine In", "DJ", "CIC", "Cashier", "Dispatch", "SO", "Regular", "Fryman", "Noodles", "Assembler", "Backup", "SC", "SS")
    val filteredEmployees = allEmployees.filter { 
        (it.isHiddenFromScheduler || (it.position.isNullOrBlank() && it.schedulerPosition.isNullOrBlank())) &&
        it.position?.lowercase() != "excrew"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Employee to List") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Name Selection
                Box {
                    OutlinedTextField(
                        value = selectedEmployee?.firstName ?: "",
                        onValueChange = { },
                        label = { Text("Select Employee") },
                        readOnly = true,
                        trailingIcon = { IconButton(onClick = { expandedName = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(expanded = expandedName, onDismissRequest = { expandedName = false }) {
                        filteredEmployees.forEach { emp ->
                            DropdownMenuItem(
                                text = { Text("${emp.firstName} ${emp.lastName}") },
                                onClick = {
                                    selectedEmployee = emp
                                    position = emp.position ?: ""
                                    expandedName = false
                                }
                            )
                        }
                    }
                }

                // Position Selection
                Box {
                    OutlinedTextField(
                        value = position,
                        onValueChange = { position = it },
                        label = { Text("Position") },
                        trailingIcon = { IconButton(onClick = { expandedPos = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(expanded = expandedPos, onDismissRequest = { expandedPos = false }) {
                        positions.forEach { pos ->
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
            }
        },
        confirmButton = {
            Button(onClick = { 
                selectedEmployee?.let { onSave(it, position) }
            }, enabled = selectedEmployee != null && position.isNotBlank()) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEditDialog(
    employee: Employee,
    date: LocalDate,
    currentSchedule: EmployeeSchedule?,
    templates: List<ShiftTemplate>,
    onSave: (EmployeeSchedule) -> Unit,
    onDelete: (EmployeeSchedule) -> Unit,
    onPostToDTR: (EmployeeSchedule) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(currentSchedule?.color?.let { Color(it) } ?: Color.Transparent) }
    var expandedDropdown by remember { mutableStateOf(false) }
    
    val sortedTemplates = templates.sortedBy { it.timeRange }

    val colors = listOf(
        Color.Transparent, Color(0xFFE3F2FD), Color(0xFFF1F8E9), Color(0xFFFFF3E0), 
        Color(0xFFFFEBEE), Color(0xFFF3E5F5), Color(0xFFEFEBE9), Color(0xFFECEFF1),
        // Rainbow colors
        Color.Red.copy(alpha = 0.2f), Color(0xFFFFA500).copy(alpha = 0.2f), Color.Yellow.copy(alpha = 0.2f), 
        Color.Green.copy(alpha = 0.2f), Color.Blue.copy(alpha = 0.2f), Color(0xFF4B0082).copy(alpha = 0.2f), Color(0xFFEE82EE).copy(alpha = 0.2f)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule for ${employee.firstName} on ${date.format(DateTimeFormatter.ofPattern("MMM d"))}") },
        text = {
            Column {
                Text("Select Shift:", fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    OutlinedButton(
                        onClick = { expandedDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(currentSchedule?.scheduleText ?: "Select Shift")
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        sortedTemplates.forEach { template ->
                            DropdownMenuItem(
                                text = { Text(template.timeRange) },
                                onClick = {
                                    onSave(EmployeeSchedule(
                                        employeeId = employee.id, 
                                        date = date.toString(), 
                                        scheduleText = template.timeRange,
                                        color = template.color ?: selectedColor.toArgb().takeIf { it != Color.Transparent.toArgb() },
                                        fontColor = template.fontColor
                                    ))
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Box Color:", fontWeight = FontWeight.Bold)
                FlowRow(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color, shape = androidx.compose.foundation.shape.CircleShape)
                                .border(
                                    if (selectedColor == color) 2.dp else 1.dp,
                                    if (selectedColor == color) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Quick Tags:", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("RD", "RRD", "SICK", "NS").forEach { tag ->
                        Button(
                            onClick = { onSave(EmployeeSchedule(employee.id, date.toString(), tag = tag)) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when(tag) {
                                    "RD", "RRD" -> Color(0xFF1E88E5)
                                    "SICK" -> Color(0xFFD32F2F)
                                    "NS" -> Color(0xFFF57C00)
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(tag, fontSize = 12.sp)
                        }
                    }
                }

                if (currentSchedule != null) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Button(
                        onClick = { onPostToDTR(currentSchedule) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Post to DTR Terminal")
                    }
                }

                Button(
                    onClick = { 
                        onDismiss()
                        // This triggers the export date picker in the parent
                        onPostToDTR(EmployeeSchedule(employee.id, date.toString(), tag = "READOUT")) 
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Print, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Schedule Readout Output")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (currentSchedule != null && selectedColor.toArgb() != currentSchedule.color) {
                    onSave(currentSchedule.copy(color = selectedColor.toArgb().takeIf { it != Color.Transparent.toArgb() }))
                }
                onDismiss()
            }) { Text("Apply Color") }
        },
        dismissButton = {
            if (currentSchedule != null) {
                TextButton(onClick = { onDelete(currentSchedule) }) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShiftTemplateManagerDialog(
    templates: List<ShiftTemplate>,
    onSave: (ShiftTemplate) -> Unit,
    onDelete: (ShiftTemplate) -> Unit,
    onDismiss: () -> Unit
) {
    var newShift by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manage Shift Templates") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = newShift,
                        onValueChange = { newShift = it },
                        placeholder = { Text("e.g. 11-10") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (newShift.isNotBlank()) {
                            onSave(ShiftTemplate(newShift))
                            newShift = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Existing Shifts:", fontWeight = FontWeight.Bold)
                val sortedTemplates = templates.sortedBy { it.timeRange }
                FlowRow(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    sortedTemplates.forEach { template ->
                        InputChip(
                            selected = false,
                            onClick = { },
                            label = { Text(template.timeRange) },
                            trailingIcon = {
                                IconButton(onClick = { onDelete(template) }, modifier = Modifier.size(18.dp)) {
                                    Icon(Icons.Default.Close, null)
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}
