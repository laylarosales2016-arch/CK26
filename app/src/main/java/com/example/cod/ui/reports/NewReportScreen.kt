package com.example.cod.ui.reports

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cod.data.CODRepository
import com.example.cod.model.IncidentReport
import com.example.cod.model.ReportStatus
import com.example.cod.model.ReportValidity
import com.example.cod.model.RuleItem
import com.example.cod.ui.employees.EmployeePickerDialog
import com.example.cod.ui.rulebook.getCategoryColor
import com.example.cod.util.PhotoStorageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewReportScreen(
    initialSelectedRule: RuleItem? = null,
    onReportSubmitted: () -> Unit,
    onOpenEmployeeRoster: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val rules = remember { CODRepository.getAllRules() }

    val currentDateStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val currentTimeStr = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()) }

    val incidentSourceOptions = listOf(
        "Store Complaint", "Qualtrics", "Grab", "Food Panda", "Tillster", "Absent", "Late", "House Rule", "Other"
    )
    var selectedSource by remember { mutableStateOf("Store Complaint") }

    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var selectedValidity by remember { mutableStateOf(ReportValidity.VALID) }

    var incidentDate by remember { mutableStateOf(currentDateStr) }
    var incidentTime by remember { mutableStateOf(currentTimeStr) }

    val involvedEmployees = remember { mutableStateListOf<String>() }
    var customEmployeeInput by remember { mutableStateOf("") }

    val complaintFormUris = remember { mutableStateListOf<String>() }
    val employeeIrPhotoUris = remember { mutableStateListOf<String>() }
    val rtwFusionFormUris = remember { mutableStateListOf<String>() }

    var previewingPhotoUri by remember { mutableStateOf<Pair<String, String>?>(null) }

    var selectedRule by remember { mutableStateOf<RuleItem?>(initialSelectedRule) }

    val offenseCountOptions = listOf("1st Offense", "2nd Offense", "3rd Offense", "4th Offense", "5th Offense")
    var selectedOffenseCount by remember { mutableStateOf(if (initialSelectedRule != null) "1st Offense" else "None") }

    var showRulePicker by remember { mutableStateOf(false) }
    var showOffenseCountPicker by remember { mutableStateOf(false) }
    var showEmployeePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    fun buildDriveFileName(photoType: String): String {
        val empNames = if (involvedEmployees.isNotEmpty()) involvedEmployees.joinToString("-") else "Unassigned"
        val cleanEmp = empNames.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(40)
        val cleanSource = selectedSource.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val cleanDate = incidentDate.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        return "${cleanEmp}_${cleanSource}_${cleanDate}_${photoType}_${System.currentTimeMillis()}.jpg"
    }

    val complaintFormLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            val saved = PhotoStorageUtil.saveImageToAppStorage(context, uri)
            complaintFormUris.add(saved)
            scope.launch(Dispatchers.IO) {
                val fileName = buildDriveFileName("ComplaintForm")
                com.sam.myapplication.util.GoogleDriveSyncManager.uploadPhotoToGoogleDrive(context, uri, fileName)
            }
        }
    }

    val employeeIrPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            val saved = PhotoStorageUtil.saveImageToAppStorage(context, uri)
            employeeIrPhotoUris.add(saved)
            scope.launch(Dispatchers.IO) {
                val fileName = buildDriveFileName("EmployeeIR")
                com.sam.myapplication.util.GoogleDriveSyncManager.uploadPhotoToGoogleDrive(context, uri, fileName)
            }
        }
    }

    val rtwFusionFormLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            val saved = PhotoStorageUtil.saveImageToAppStorage(context, uri)
            rtwFusionFormUris.add(saved)
            scope.launch(Dispatchers.IO) {
                val fileName = buildDriveFileName("RTWFusion")
                com.sam.myapplication.util.GoogleDriveSyncManager.uploadPhotoToGoogleDrive(context, uri, fileName)
            }
        }
    }

    val recommendedPenalty = remember(selectedRule, selectedOffenseCount) {
        if (selectedRule == null || selectedOffenseCount == "None") "None"
        else CODRepository.getPenaltyForOffense(selectedRule, selectedOffenseCount)
    }

    val scrollState = rememberScrollState()

    fun showDatePicker() {
        val calendar = Calendar.getInstance()
        try {
            val parts = incidentDate.split("-")
            if (parts.size == 3) {
                calendar.set(Calendar.YEAR, parts[0].toInt())
                calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
                calendar.set(Calendar.DAY_OF_MONTH, parts[2].toInt())
            }
        } catch (_: Exception) {}

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                incidentDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun showTimePicker() {
        val calendar = Calendar.getInstance()

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val tempCal = Calendar.getInstance()
                tempCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                tempCal.set(Calendar.MINUTE, minute)
                incidentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(tempCal.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 840.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CREATE INCIDENT REPORT / COMPLAINT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Log an offense against the FISC Code of Discipline rules.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Incident Title / Summary *") },
                placeholder = { Text("e.g. Rude behavior at front desk") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Complaint Source / Channel *",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(incidentSourceOptions) { source ->
                            FilterChip(
                                selected = selectedSource == source,
                                onClick = { selectedSource = source },
                                label = {
                                    Text(
                                        text = when (source) {
                                            "Qualtrics" -> "📊 Qualtrics"
                                            "Store Complaint" -> "🏪 Store Complaint"
                                            "Grab" -> "🍔 Grab"
                                            "Food Panda" -> "🛵 Food Panda"
                                            "Tillster" -> "📱 Tillster"
                                            "Absent" -> "🚫 Absent"
                                            "Late" -> "⏰ Late"
                                            "House Rule" -> "🏠 House Rule"
                                            else -> "📝 $source"
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Complaint Validity *",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReportValidity.entries.forEach { validity ->
                            FilterChip(
                                selected = selectedValidity == validity,
                                onClick = { selectedValidity = validity },
                                label = {
                                    Text(
                                        text = when (validity) {
                                            ReportValidity.VALID -> "✅ Valid"
                                            ReportValidity.INVALID -> "❌ Invalid"
                                            ReportValidity.PENDING_EVALUATION -> "⏳ Pending"
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = incidentDate,
                    onValueChange = { incidentDate = it },
                    label = { Text("Incident Date *") },
                    placeholder = { Text("YYYY-MM-DD") },
                    trailingIcon = {
                        TextButton(onClick = { showDatePicker() }) {
                            Text("📅")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = incidentTime,
                    onValueChange = { incidentTime = it },
                    label = { Text("Incident Time *") },
                    placeholder = { Text("hh:mm AM/PM") },
                    trailingIcon = {
                        TextButton(onClick = { showTimePicker() }) {
                            Text("⏰")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Employee(s) Involved *",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedButton(onClick = { showEmployeePicker = true }) {
                            Text("👥 + Add Employee", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (involvedEmployees.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            involvedEmployees.forEachIndexed { index, name ->
                                InputChip(
                                    selected = true,
                                    onClick = { involvedEmployees.removeAt(index) },
                                    label = { Text(name, fontWeight = FontWeight.Bold) },
                                    trailingIcon = { Text(" ❌") }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customEmployeeInput,
                            onValueChange = { customEmployeeInput = it },
                            placeholder = { Text("Or type custom name...") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Button(
                            onClick = {
                                if (customEmployeeInput.isNotBlank()) {
                                    involvedEmployees.add(customEmployeeInput.trim())
                                    customEmployeeInput = ""
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Add")
                        }
                    }
                }
            }

            MultiPhotoUploadCard(
                title = "Complaint Form Photos",
                description = "Upload 1 or more photos/scans of complaint forms.",
                photoUris = complaintFormUris,
                onAddPhotos = { complaintFormLauncher.launch("image/*") },
                onRemovePhoto = { index -> complaintFormUris.removeAt(index) },
                onViewPhoto = { uri -> previewingPhotoUri = "Complaint Form Photo" to uri }
            )

            MultiPhotoUploadCard(
                title = "Employee IR Photos",
                description = "Upload 1 or more Employee IR photos.",
                photoUris = employeeIrPhotoUris,
                onAddPhotos = { employeeIrPhotoLauncher.launch("image/*") },
                onRemovePhoto = { index -> employeeIrPhotoUris.removeAt(index) },
                onViewPhoto = { uri -> previewingPhotoUri = "Employee IR Photo" to uri }
            )

            MultiPhotoUploadCard(
                title = "RTW / Fusion Form Photos",
                description = "Upload 1 or more Return to Work (RTW) / Fusion form photos.",
                photoUris = rtwFusionFormUris,
                onAddPhotos = { rtwFusionFormLauncher.launch("image/*") },
                onRemovePhoto = { index -> rtwFusionFormUris.removeAt(index) },
                onViewPhoto = { uri -> previewingPhotoUri = "RTW / Fusion Form Photo" to uri }
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showRulePicker = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select COD Rule Violation",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Change",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    if (selectedRule != null) {
                        val rule = selectedRule!!
                        Text(
                            text = "Rule ${rule.ruleNumber} (Page ${rule.pageNumber}) • ${rule.category.label}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = getCategoryColor(rule.category)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = rule.description,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "NONE",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "None (No rule violation selected - can edit later)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            if (selectedRule != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showOffenseCountPicker = true },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Offense Count",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedOffenseCount,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Change",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (recommendedPenalty.contains("TERMINATION"))
                        Color(0xFFFFEBEE) else MaterialTheme.colorScheme.tertiaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recommended Penalty (COD Rulebook Matrix):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (recommendedPenalty.contains("TERMINATION")) Color(0xFFC62828) else MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recommendedPenalty,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (recommendedPenalty.contains("TERMINATION")) Color(0xFFB71C1C) else MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Incident Details / Statement *") },
                placeholder = { Text("Describe what happened, location, witnesses, details...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                shape = RoundedCornerShape(10.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    if (title.isBlank() || involvedEmployees.isEmpty() || details.isBlank() || incidentDate.isBlank() || incidentTime.isBlank()) {
                        errorMessage = "Please fill in all required fields marked with * and select at least 1 employee involved."
                    } else {
                        errorMessage = null
                        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val initialComplaintUris = complaintFormUris.toList()
                        val initialIrUris = employeeIrPhotoUris.toList()
                        val initialRtwUris = rtwFusionFormUris.toList()

                        val newReport = IncidentReport(
                            id = "REP-${System.currentTimeMillis().toString().takeLast(6)}",
                            title = title.trim(),
                            offenderName = involvedEmployees.joinToString(", "),
                            offenderNames = involvedEmployees.toList(),
                            incidentSource = selectedSource,
                            date = currentDate,
                            incidentDate = incidentDate.trim(),
                            incidentTime = incidentTime.trim(),
                            ruleId = selectedRule?.id,
                            ruleDescription = selectedRule?.description ?: "None (No Rule Violation Selected)",
                            category = selectedRule?.category,
                            offenseCount = if (selectedRule != null) selectedOffenseCount else "None",
                            recommendedPenalty = recommendedPenalty,
                            details = details.trim(),
                            complaintFormUris = initialComplaintUris,
                            employeeIrPhotoUris = initialIrUris,
                            rtwFusionFormUris = initialRtwUris,
                            status = ReportStatus.PENDING,
                            validity = selectedValidity
                        )
                        CODRepository.addReport(newReport, context)
                        showSuccessDialog = true

                        scope.launch(Dispatchers.IO) {
                            val finalComplaintUris = initialComplaintUris.mapIndexed { idx, uri ->
                                val fileName = buildDriveFileName("ComplaintForm_$idx")
                                com.sam.myapplication.util.GoogleDriveSyncManager.uploadPhotoToGoogleDriveIfNotUploaded(context, uri, fileName)
                            }
                            val finalIrUris = initialIrUris.mapIndexed { idx, uri ->
                                val fileName = buildDriveFileName("EmployeeIR_$idx")
                                com.sam.myapplication.util.GoogleDriveSyncManager.uploadPhotoToGoogleDriveIfNotUploaded(context, uri, fileName)
                            }
                            val finalRtwUris = initialRtwUris.mapIndexed { idx, uri ->
                                val fileName = buildDriveFileName("RTWFusion_$idx")
                                com.sam.myapplication.util.GoogleDriveSyncManager.uploadPhotoToGoogleDriveIfNotUploaded(context, uri, fileName)
                            }

                            val updatedWithDrive = newReport.copy(
                                complaintFormUris = finalComplaintUris,
                                employeeIrPhotoUris = finalIrUris,
                                rtwFusionFormUris = finalRtwUris
                            )
                            withContext(Dispatchers.Main) {
                                CODRepository.updateReport(updatedWithDrive, context)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "SUBMIT INCIDENT REPORT",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showEmployeePicker) {
        EmployeePickerDialog(
            onDismiss = { showEmployeePicker = false },
            onSelectEmployee = { emp ->
                val nameWithPos = emp.name + if (emp.position.isNotBlank()) " (${emp.position})" else ""
                if (!involvedEmployees.contains(nameWithPos)) {
                    involvedEmployees.add(nameWithPos)
                }
                showEmployeePicker = false
            },
            onOpenManager = {
                showEmployeePicker = false
                onOpenEmployeeRoster?.invoke()
            }
        )
    }

    if (showRulePicker) {
        AlertDialog(
            onDismissRequest = { showRulePicker = false },
            title = { Text("Select Rule Violation") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedRule = null
                                selectedOffenseCount = "None"
                                showRulePicker = false
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedRule == null)
                                MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🚫 NONE",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "No specific rule violation selected. Offense count will be None.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    HorizontalDivider()

                    rules.forEach { rule ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRule = rule
                                    if (selectedOffenseCount == "None") {
                                        selectedOffenseCount = "1st Offense"
                                    }
                                    showRulePicker = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedRule?.id == rule.id)
                                    MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Rule ${rule.ruleNumber} (Page ${rule.pageNumber}) - ${rule.category.label}",
                                    fontWeight = FontWeight.Bold,
                                    color = getCategoryColor(rule.category)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = rule.description, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRulePicker = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showOffenseCountPicker) {
        AlertDialog(
            onDismissRequest = { showOffenseCountPicker = false },
            title = { Text("Select Offense Progression") },
            text = {
                Column {
                    offenseCountOptions.forEach { count ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedOffenseCount = count
                                    showOffenseCountPicker = false
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOffenseCount == count,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = count, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showOffenseCountPicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (previewingPhotoUri != null) {
        FullPhotoPreviewDialog(
            title = previewingPhotoUri!!.first,
            photoUri = previewingPhotoUri!!.second,
            onDismiss = { previewingPhotoUri = null }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onReportSubmitted()
            },
            title = { Text("Report Submitted Successfully!") },
            text = { Text("The incident report has been recorded and saved.") },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    onReportSubmitted()
                }) {
                    Text("View Incident Logs")
                }
            }
        )
    }
}
