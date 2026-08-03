package com.sam.myapplication.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sam.myapplication.data.Employee
import com.sam.myapplication.data.EmployeeSchedule
import com.sam.myapplication.data.ShiftTemplate
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen(
    viewModel: AttendanceViewModel,
    onBack: () -> Unit
) {
    val allEmployees by viewModel.allEmployees.collectAsState()
    val allSchedules by viewModel.allSchedules.collectAsState()
    val allShiftTemplates by viewModel.allShiftTemplates.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
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
    var showOrderNumbers by remember { mutableStateOf(false) }
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
                    val excluded = listOf("excrew")
                    val positions = listOf("Dine In", "SS", "CIC", "DJ", "Dispatch", "Cashier", "SO", "Regular", "Assembler", "Fryman", "Noodles", "Backup", "SC")
                    
                    val orderedEmps = mutableListOf<Employee>()
                    
                    positions.forEach { pos ->
                        val posEmps = allEmployees.filter { emp ->
                            val mondayDate = weekDates[0].toString()
                            val weeklyOverride = allSchedules.find { it.employeeId == emp.id && it.date == mondayDate }?.position
                            val effectivePos = weeklyOverride ?: emp.schedulerPosition ?: emp.position
                            
                            (effectivePos == pos || emp.department == pos) &&
                            !emp.isHiddenFromScheduler &&
                            allSchedules.none { s -> s.employeeId == emp.id && s.date == weekDates[0].toString() && s.tag == "HIDDEN" } &&
                            effectivePos?.lowercase() !in excluded
                        }.sortedWith(compareBy({ it.schedulerOrder }, { it.firstName }))
                        orderedEmps.addAll(posEmps)
                    }
                    
                    val otherEmps = allEmployees.filter { emp ->
                        val mondayDate = weekDates[0].toString()
                        val weeklyOverride = allSchedules.find { it.employeeId == emp.id && it.date == mondayDate }?.position
                        val effectivePos = weeklyOverride ?: emp.schedulerPosition ?: emp.position

                        effectivePos !in positions && 
                        emp.department !in positions &&
                        !emp.isHiddenFromScheduler &&
                        allSchedules.none { s -> s.employeeId == emp.id && s.date == weekDates[0].toString() && s.tag == "HIDDEN" } &&
                        effectivePos?.lowercase() !in excluded
                    }.filter { it !in orderedEmps }.sortedWith(compareBy({ it.schedulerOrder }, { it.firstName }))
                    
                    orderedEmps.addAll(otherEmps)

                    viewModel.exportSchedulerToExcel(outputStream, orderedEmps, allSchedules, weekDates) { success -> }
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
            TopAppBar(
                title = { Text("Employee Scheduler") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showPrintButtons = !showPrintButtons }) {
                        Icon(if (showPrintButtons) Icons.Default.Print else Icons.Default.PrintDisabled, contentDescription = null, tint = if (showPrintButtons) Color(0xFF00ACC1) else LocalContentColor.current)
                    }
                    IconButton(onClick = { showDeleteIcons = !showDeleteIcons }) {
                        Icon(if (showDeleteIcons) Icons.Default.DeleteForever else Icons.Default.DeleteOutline, contentDescription = null, tint = if (showDeleteIcons) Color.Red else LocalContentColor.current)
                    }
                    IconButton(onClick = { showAddEmployeeDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Employee")
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(text = { Text("Upload to Supabase") }, onClick = { viewModel.uploadSchedulerData(); showMenu = false }, leadingIcon = { Icon(Icons.Default.Upload, null) })
                            DropdownMenuItem(text = { Text("Download from Supabase") }, onClick = { viewModel.downloadSchedulerData(); showMenu = false }, leadingIcon = { Icon(Icons.Default.Download, null) })
                            DropdownMenuItem(text = { Text("Save to Excel") }, onClick = { excelLauncher.launch("Chowking_Schedule_${startOfWeek.toString()}.xls"); showMenu = false }, leadingIcon = { Icon(Icons.Default.TableChart, null) })
                            HorizontalDivider()
                            DropdownMenuItem(text = { Text("Manage Shifts") }, onClick = { showShiftTemplateManager = true; showMenu = false }, leadingIcon = { Icon(Icons.Default.Settings, null) })
                            DropdownMenuItem(text = { Text("Manage Hidden") }, onClick = { showHiddenEmployeesDialog = true; showMenu = false }, leadingIcon = { Icon(Icons.Default.Visibility, null) })
                            DropdownMenuItem(text = { Text(if (showOrderNumbers) "Hide Rank" else "Show Rank") }, onClick = { showOrderNumbers = !showOrderNumbers; showMenu = false }, leadingIcon = { Icon(if (showOrderNumbers) Icons.Default.Filter1 else Icons.Default.FormatListNumbered, null) })
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Week Selector
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedDate = selectedDate.minusWeeks(1) }) { Icon(Icons.Default.ChevronLeft, null) }
                TextButton(onClick = { selectedDate = LocalDate.now() }) {
                    Text(text = "Week of ${startOfWeek.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                IconButton(onClick = { selectedDate = selectedDate.plusWeeks(1) }) { Icon(Icons.Default.ChevronRight, null) }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Table Header
                    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).background(Color(0xFF455A64)).padding(vertical = 4.dp)) {
                        Text("Employee", modifier = Modifier.weight(1.5f).fillMaxHeight().padding(start = 4.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        weekDates.forEachIndexed { index, date ->
                            if (!hiddenDayIndices.contains(index)) {
                                Column(modifier = Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(date.dayOfWeek.name.take(3), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(date.format(DateTimeFormatter.ofPattern("MMM d")), fontSize = 9.sp, color = Color.White.copy(alpha = 0.9f))
                                }
                            }
                        }
                    }

                    val diningPositions = listOf("Dine In", "CIC", "DJ", "Dispatch", "Cashier", "SC")
                    val kitchenPositions = listOf("SO", "Regular", "Assembler", "Fryman", "Noodles", "Backup")
                    val combinedOther = diningPositions + kitchenPositions
                    val excluded = listOf("excrew")

                    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)) {
                        item {
                            Surface(color = Color(0xFFE0F7FA), modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.Black)) {
                                Text("DINING UNIT", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.ExtraBold, color = Color(0xFF006064), fontSize = 15.sp, textAlign = TextAlign.Center)
                            }
                        }
                        
                        diningPositions.forEach { pos ->
                            val posEmployees = allEmployees.filter { emp ->
                                val mondayDate = weekDates[0].toString()
                                val weeklyOverride = allSchedules.find { it.employeeId == emp.id && it.date == mondayDate }?.position
                                val effectivePos = weeklyOverride ?: emp.schedulerPosition ?: emp.position
                                
                                val isPosMatch = effectivePos?.equals(pos, ignoreCase = true) == true || 
                                               emp.department?.equals(pos, ignoreCase = true) == true
                                
                                isPosMatch && 
                                effectivePos?.lowercase() !in excluded &&
                                !emp.isHiddenFromScheduler &&
                                !allSchedules.any { s -> s.employeeId == emp.id && s.date == mondayDate && s.tag == "HIDDEN" }
                            }.sortedWith(compareBy({ val mDate = weekDates[0].toString(); allSchedules.find { s -> s.employeeId == it.id && s.date == mDate }?.schedulerOrder ?: it.schedulerOrder }, { it.firstName }))

                            if (posEmployees.isNotEmpty()) {
                                item {
                                    val bgColor = Color(viewModel.getPositionColor(pos).let { if (it != 0) it else 0xFFF1F8E9.toInt() })
                                    val fontColor = Color(viewModel.getPositionFontColor(pos).let { if (it != 0) it else 0xFF000000.toInt() })
                                    Surface(color = bgColor, modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.Black).clickable { positionToColor = pos }) {
                                        Text(text = pos, modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Black, fontSize = 12.sp, color = fontColor)
                                    }
                                }
                                items(posEmployees) { employee ->
                                    EmployeeScheduleRow(employee, weekDates, hiddenDayIndices, allSchedules.filter { it.employeeId == employee.id }, { date -> editingCell = Pair(employee, date) }, { employeeToDelete = it }, { exportTargetEmployee = it; showExportDatePicker = true }, { editingOrderEmployee = it }, showDeleteIcons, showPrintButtons, showOrderNumbers)
                                    HorizontalDivider(color = Color.Black, thickness = 0.5.dp)
                                }
                            }
                        }

                        item {
                            Spacer(Modifier.height(16.dp))
                            Surface(color = Color(0xFFFFF3E0), modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.Black)) {
                                Text("KITCHEN UNIT", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.ExtraBold, color = Color(0xFFE65100), fontSize = 15.sp, textAlign = TextAlign.Center)
                            }
                        }
                        
                        kitchenPositions.forEach { pos ->
                            val posEmployees = allEmployees.filter { emp ->
                                val mondayDate = weekDates[0].toString()
                                val weeklyOverride = allSchedules.find { it.employeeId == emp.id && it.date == mondayDate }?.position
                                val effectivePos = weeklyOverride ?: emp.schedulerPosition ?: emp.position
                                
                                val isPosMatch = effectivePos?.equals(pos, ignoreCase = true) == true || 
                                               emp.department?.equals(pos, ignoreCase = true) == true
                                
                                isPosMatch && 
                                effectivePos?.lowercase() !in excluded &&
                                !emp.isHiddenFromScheduler &&
                                !allSchedules.any { s -> s.employeeId == emp.id && s.date == mondayDate && s.tag == "HIDDEN" }
                            }.sortedWith(compareBy({ val mDate = weekDates[0].toString(); allSchedules.find { s -> s.employeeId == it.id && s.date == mDate }?.schedulerOrder ?: it.schedulerOrder }, { it.firstName }))

                            if (posEmployees.isNotEmpty()) {
                                item {
                                    val bgColor = Color(viewModel.getPositionColor(pos).let { if (it != 0) it else 0xFFFFFDE7.toInt() })
                                    val fontColor = Color(viewModel.getPositionFontColor(pos).let { if (it != 0) it else 0xFF000000.toInt() })
                                    Surface(color = bgColor, modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.Black).clickable { positionToColor = pos }) {
                                        Text(text = pos, modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Black, fontSize = 12.sp, color = fontColor)
                                    }
                                }
                                items(posEmployees) { employee ->
                                    EmployeeScheduleRow(employee, weekDates, hiddenDayIndices, allSchedules.filter { it.employeeId == employee.id }, { date -> editingCell = Pair(employee, date) }, { employeeToDelete = it }, { exportTargetEmployee = it; showExportDatePicker = true }, { editingOrderEmployee = it }, showDeleteIcons, showPrintButtons, showOrderNumbers)
                                    HorizontalDivider(color = Color.Black, thickness = 0.5.dp)
                                }
                            }
                        }
                        
                    }
                }
            }
        }
    }

    if (showAddEmployeeDialog) {
        AddEmployeeDialog(allEmployees, { emp, pos ->
            viewModel.saveSchedule(EmployeeSchedule(emp.id, weekDates[0].toString(), position = pos))
            showAddEmployeeDialog = false
        }, { showAddEmployeeDialog = false })
    }

    if (showShiftTemplateManager) {
        ShiftTemplateManagerDialog(allShiftTemplates, { viewModel.saveShiftTemplate(it) }, { viewModel.deleteShiftTemplate(it) }, { showShiftTemplateManager = false })
    }

    editingCell?.let { (emp, date) ->
        ScheduleEditDialog(
            employee = emp,
            date = date,
            currentSchedule = allSchedules.find { it.employeeId == emp.id && it.date == date.toString() },
            templates = allShiftTemplates,
            onSave = { viewModel.saveSchedule(it) },
            onDelete = { viewModel.deleteSchedule(it) },
            onPostToDTR = { sched ->
                if (sched.tag == "READOUT") {
                    exportTargetEmployee = allEmployees.find { it.id == sched.employeeId }
                    showExportDatePicker = true
                } else {
                    allEmployees.find { it.id == sched.employeeId }?.let { viewModel.batchSyncSchedulesToDTR(it, listOf(sched)) }
                }
            },
            onDismiss = { editingCell = null }
        )
    }

    if (showHiddenEmployeesDialog) {
        HiddenEmployeesDialog(allEmployees, allSchedules, weekDates, { emp ->
            val mondayDate = weekDates[0].toString()
            val existing = allSchedules.find { it.employeeId == emp.id && it.date == mondayDate }
            if (existing?.tag == "HIDDEN") viewModel.deleteSchedule(existing) else viewModel.saveSchedule(EmployeeSchedule(emp.id, mondayDate, tag = "HIDDEN"))
        }, { showHiddenEmployeesDialog = false })
    }

    if (employeeToDelete != null) {
        AlertDialog(onDismissRequest = { employeeToDelete = null }, title = { Text("Hide Employee") }, text = { Text("Hide ${employeeToDelete?.firstName} from this week's scheduler?") }, confirmButton = { TextButton(onClick = { val mondayDate = weekDates[0].toString(); viewModel.saveSchedule(EmployeeSchedule(employeeToDelete!!.id, mondayDate, tag = "HIDDEN")); employeeToDelete = null }) { Text("Hide") } }, dismissButton = { TextButton(onClick = { employeeToDelete = null }) { Text("Cancel") } })
    }

    if (editingOrderEmployee != null) {
        var orderText by remember { mutableStateOf(editingOrderEmployee?.schedulerOrder?.toString() ?: "0") }
        AlertDialog(onDismissRequest = { editingOrderEmployee = null }, title = { Text("Change Rank / Order") }, text = { OutlinedTextField(value = orderText, onValueChange = { orderText = it }, label = { Text("Order Number") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)) }, confirmButton = { TextButton(onClick = { val order = orderText.toIntOrNull() ?: 0; val mondayDate = weekDates[0].toString(); viewModel.saveSchedule(EmployeeSchedule(editingOrderEmployee!!.id, mondayDate, schedulerOrder = order)); editingOrderEmployee = null }) { Text("Save") } }, dismissButton = { TextButton(onClick = { editingOrderEmployee = null }) { Text("Cancel") } })
    }

    if (showExportDatePicker) {
        AlertDialog(onDismissRequest = { showExportDatePicker = false }, title = { Text("Generate Readout") }, text = { Text("Generate schedule readout for the selected range?") }, confirmButton = { TextButton(onClick = {
            exportTargetEmployee?.let { emp ->
                val scheds = allSchedules.filter { it.employeeId == emp.id && it.date >= exportStartDate.toString() && it.date <= exportEndDate.toString() }
                readoutSchedules = scheds
                showReadoutDialog = true
            }
            showExportDatePicker = false
        }) { Text("Generate") } }, dismissButton = { TextButton(onClick = { showExportDatePicker = false }) { Text("Cancel") } })
    }

    if (showReadoutDialog) {
        AlertDialog(
            onDismissRequest = { showReadoutDialog = false },
            title = { Text("Schedule Readout") },
            text = {
                val text = remember(readoutSchedules) { readoutSchedules.joinToString("\n") { "${it.date}: ${it.scheduleText ?: it.tag ?: ""}" } }
                Box(modifier = Modifier.heightIn(max = 300.dp).verticalScroll(rememberScrollState())) { Text(text) }
            },
            confirmButton = { TextButton(onClick = {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Schedule", readoutSchedules.joinToString("\n") { "${it.date}: ${it.scheduleText ?: it.tag ?: ""}" }))
                showReadoutDialog = false
                scope.launch { snackbarHostState.showSnackbar("Readout copied to clipboard") }
            }) { Text("Copy") } },
            dismissButton = { TextButton(onClick = { showReadoutDialog = false }) { Text("Close") } }
        )
    }

    if (positionToColor != null) {
        AlertDialog(onDismissRequest = { positionToColor = null }, title = { Text("Color Settings") }, text = { Text("Modify colors for ${positionToColor}") }, confirmButton = { TextButton(onClick = { positionToColor = null }) { Text("Close") } })
    }
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val mondayDate = dates[0].toString()
    val weeklySchedule = schedules.find { it.date == mondayDate }
    val effectiveOrder = weeklySchedule?.schedulerOrder ?: employee.schedulerOrder ?: 0
    val effectivePos = weeklySchedule?.position ?: employee.schedulerPosition ?: employee.position

    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1.5f).fillMaxHeight().border(0.5.dp, Color.Black).padding(start = 4.dp), contentAlignment = Alignment.CenterStart) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showDeleteIcon) {
                    IconButton(onClick = { onDeleteEmployee(employee) }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Close, null, tint = Color.Red, modifier = Modifier.size(16.dp)) }
                }
                Column(modifier = Modifier.weight(1f).clickable { onEditOrder(employee) }) {
                    Text(
                        text = employee.firstName ?: "",
                        fontSize = if (isLandscape) 9.sp else 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1
                    )
                    if (showOrderNumber) {
                        Text(
                            text = "[$effectiveOrder]",
                            fontSize = if (isLandscape) 7.sp else 8.sp,
                            color = Color.DarkGray,
                            maxLines = 1
                        )
                    }
                }
                if (showPrintIcon) {
                    IconButton(onClick = { onExportClick(employee) }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Print, null, tint = Color(0xFF00ACC1).copy(alpha = 0.7f), modifier = Modifier.size(if (isLandscape) 10.dp else 14.dp)) }
                }
            }
        }
        dates.forEachIndexed { index, date ->
            if (!hiddenDayIndices.contains(index)) {
                val schedule = schedules.find { it.date == date.toString() }
                val boxColor = schedule?.color?.let { Color(it) } ?: when (schedule?.tag) {
                    "RD", "RRD" -> Color(0xFF64B5F6)
                    "SICK" -> Color(0xFFE57373)
                    "NS" -> Color(0xFFFFB74D)
                    else -> Color.White
                }
                val boxFontColor = schedule?.fontColor?.let { Color(it) } ?: if (schedule?.tag != null && schedule.tag != "HIDDEN") Color.White else Color.Black
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(boxColor).border(0.5.dp, Color.Black).clickable { onClick(date) }.padding(if (isLandscape) 1.dp else 2.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        if (!schedule?.superscript.isNullOrBlank()) {
                            Text(text = schedule!!.superscript!!, fontSize = if (isLandscape) 6.sp else 7.sp, fontWeight = FontWeight.ExtraBold, color = boxFontColor.copy(alpha = 0.7f), lineHeight = if (isLandscape) 6.sp else 7.sp)
                        }
                        Text(text = schedule?.scheduleText ?: (if (schedule?.tag != "HIDDEN") schedule?.tag else "") ?: "", fontSize = if (isLandscape) 8.sp else 10.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = boxFontColor)
                    }
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
    var searchQuery by remember { mutableStateOf("") }
    var selectedEmployee by remember { mutableStateOf<Employee?>(null) }
    var position by remember { mutableStateOf("") }
    var expandedPos by remember { mutableStateOf(false) }
    
    val positions = listOf("Dine In", "CIC", "DJ", "Dispatch", "Cashier", "SC", "SO", "Regular", "Assembler", "Fryman", "Noodles", "Backup", "SS")
    val excluded = listOf("excrew", "manager", "assistant manager", "coordinator")

    val filteredEmployees = remember(allEmployees, searchQuery) {
        allEmployees.filter {
            val fullName = "${it.firstName} ${it.lastName}".lowercase()
            val pos = it.position?.lowercase() ?: ""
            !excluded.any { ex -> pos.contains(ex) } &&
            (searchQuery.isEmpty() || fullName.contains(searchQuery.lowercase()))
        }.sortedBy { it.firstName }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Personnel to Schedule") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, label = { Text("Search Name") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true)
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).border(1.dp, Color.LightGray, MaterialTheme.shapes.medium)) {
                    items(filteredEmployees) { emp ->
                        val isSelected = selectedEmployee?.id == emp.id
                        Row(modifier = Modifier.fillMaxWidth().clickable { selectedEmployee = emp; if (position.isEmpty()) position = emp.position ?: "" }.background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Person, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(text = emp.firstName ?: "", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
                    }
                }
                Box {
                    OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text("Station / Position") }, trailingIcon = { IconButton(onClick = { expandedPos = true }) { Icon(Icons.Default.ArrowDropDown, null) } }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    DropdownMenu(expanded = expandedPos, onDismissRequest = { expandedPos = false }, modifier = Modifier.fillMaxWidth(0.7f)) {
                        positions.forEach { pos -> DropdownMenuItem(text = { Text(pos) }, onClick = { position = pos; expandedPos = false }) }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { selectedEmployee?.let { onSave(it, position) } }, enabled = selectedEmployee != null && position.isNotBlank(), modifier = Modifier.fillMaxWidth()) { Text("Add to Schedule") } },
        dismissButton = { TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancel") } }
    )
}

@Composable
fun HiddenEmployeesDialog(allEmployees: List<Employee>, allSchedules: List<EmployeeSchedule>, weekDates: List<LocalDate>, onToggleVisibility: (Employee) -> Unit, onDismiss: () -> Unit) {
    val mondayDate = weekDates[0].toString()
    val hiddenEmps = allEmployees.filter { emp -> allSchedules.any { s -> s.employeeId == emp.id && s.date == mondayDate && s.tag == "HIDDEN" } }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Hidden Employees (This Week)") }, text = { LazyColumn { items(hiddenEmps) { emp -> Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) { Text(emp.firstName ?: "", modifier = Modifier.weight(1f)); TextButton(onClick = { onToggleVisibility(emp) }) { Text("Unhide") } } } } }, confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } })
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEditDialog(employee: Employee, date: LocalDate, currentSchedule: EmployeeSchedule?, templates: List<ShiftTemplate>, onSave: (EmployeeSchedule) -> Unit, onDelete: (EmployeeSchedule) -> Unit, onPostToDTR: (EmployeeSchedule) -> Unit, onDismiss: () -> Unit) {
    var selectedColor by remember { mutableStateOf(currentSchedule?.color?.let { Color(it) } ?: Color.Transparent) }
    var expandedDropdown by remember { mutableStateOf(false) }
    val sortedTemplates = templates.sortedBy { it.timeRange }
    val colors = listOf(Color.Transparent, Color(0xFFE3F2FD), Color(0xFFF1F8E9), Color(0xFFFFF3E0), Color(0xFFFFEBEE), Color(0xFFF3E5F5), Color(0xFFEFEBE9), Color(0xFFECEFF1), Color.Red.copy(alpha = 0.2f), Color(0xFFFFA500).copy(alpha = 0.2f), Color.Yellow.copy(alpha = 0.2f), Color.Green.copy(alpha = 0.2f), Color.Blue.copy(alpha = 0.2f), Color(0xFF4B0082).copy(alpha = 0.2f), Color(0xFFEE82EE).copy(alpha = 0.2f))
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Schedule for ${employee.firstName} on ${date.format(DateTimeFormatter.ofPattern("MMM d"))}") }, text = {
        Column {
            Text("Select Shift:", fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                OutlinedButton(onClick = { expandedDropdown = true }, modifier = Modifier.fillMaxWidth()) { Text(currentSchedule?.scheduleText ?: "Select Shift"); Icon(Icons.Default.ArrowDropDown, null) }
                DropdownMenu(expanded = expandedDropdown, onDismissRequest = { expandedDropdown = false }, modifier = Modifier.fillMaxWidth(0.8f)) {
                    sortedTemplates.forEach { template -> DropdownMenuItem(text = { Text(template.timeRange) }, onClick = { onSave(EmployeeSchedule(employeeId = employee.id, date = date.toString(), scheduleText = template.timeRange, color = template.color ?: selectedColor.toArgb().takeIf { it != Color.Transparent.toArgb() }, fontColor = template.fontColor)); expandedDropdown = false }) }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Box Color:", fontWeight = FontWeight.Bold)
            FlowRow(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                colors.forEach { color -> Box(modifier = Modifier.size(32.dp).background(color, shape = androidx.compose.foundation.shape.CircleShape).border(if (selectedColor == color) 2.dp else 1.dp, if (selectedColor == color) MaterialTheme.colorScheme.primary else Color.LightGray, shape = androidx.compose.foundation.shape.CircleShape).clickable { selectedColor = color }) }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Mini Tag / Superscript:", fontWeight = FontWeight.Bold)
            var superText by remember { mutableStateOf(currentSchedule?.superscript ?: "") }
            OutlinedTextField(value = superText, onValueChange = { superText = it; if (currentSchedule != null) onSave(currentSchedule.copy(superscript = it)) }, label = { Text("e.g. RTF, DP, CA, DJ") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), singleLine = true)
            Row(modifier = Modifier.padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) { listOf("RTF", "DP", "CA", "DJ").forEach { tag -> AssistChip(onClick = { superText = tag; if (currentSchedule != null) onSave(currentSchedule.copy(superscript = tag)) else onSave(EmployeeSchedule(employee.id, date.toString(), superscript = tag)) }, label = { Text(tag, fontSize = 10.sp) }) } }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Quick Tags:", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("RD", "RRD", "SICK", "NS").forEach { tag -> Button(onClick = { onSave(EmployeeSchedule(employee.id, date.toString(), tag = tag)) }, colors = ButtonDefaults.buttonColors(containerColor = when(tag) { "RD", "RRD" -> Color(0xFF1E88E5); "SICK" -> Color(0xFFD32F2F); "NS" -> Color(0xFFF57C00); else -> MaterialTheme.colorScheme.primary }), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) { Text(tag, fontSize = 12.sp) } } }
            if (currentSchedule != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Button(onClick = { onPostToDTR(currentSchedule) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), shape = RoundedCornerShape(8.dp)) { Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Post to DTR Terminal") }
            }
            Button(onClick = { onDismiss(); onPostToDTR(EmployeeSchedule(employee.id, date.toString(), tag = "READOUT")) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1)), shape = RoundedCornerShape(8.dp)) { Icon(Icons.Default.Print, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Schedule Readout Output") }
        }
    }, confirmButton = { TextButton(onClick = { if (currentSchedule != null && selectedColor.toArgb() != currentSchedule.color) onSave(currentSchedule.copy(color = selectedColor.toArgb().takeIf { it != Color.Transparent.toArgb() })); onDismiss() }) { Text("Apply Color") } }, dismissButton = { if (currentSchedule != null) TextButton(onClick = { onDelete(currentSchedule) }) { Text("Clear", color = MaterialTheme.colorScheme.error) } } )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShiftTemplateManagerDialog(templates: List<ShiftTemplate>, onSave: (ShiftTemplate) -> Unit, onDelete: (ShiftTemplate) -> Unit, onDismiss: () -> Unit) {
    var newShift by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Manage Shift Templates") }, text = {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(value = newShift, onValueChange = { newShift = it }, placeholder = { Text("e.g. 11-10") }, modifier = Modifier.weight(1f))
                IconButton(onClick = { if (newShift.isNotBlank()) { onSave(ShiftTemplate(newShift)); newShift = "" } }) { Icon(Icons.Default.Add, null) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Existing Shifts:", fontWeight = FontWeight.Bold)
            val sortedTemplates = templates.sortedBy { it.timeRange }
            FlowRow(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) { sortedTemplates.forEach { template -> InputChip(selected = false, onClick = { }, label = { Text(template.timeRange) }, trailingIcon = { IconButton(onClick = { onDelete(template) }, modifier = Modifier.size(18.dp)) { Icon(Icons.Default.Close, null) } }) } }
        }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("Done") } })
}
