package com.example.cod.ui.reports

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cod.data.CODRepository
import com.example.cod.model.IncidentReport
import com.example.cod.model.ReportStatus
import com.example.cod.model.ReportValidity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.cod.model.RuleItem
import com.example.cod.ui.employees.EmployeePickerDialog
import com.example.cod.ui.rulebook.getCategoryColor
import com.example.cod.util.PhotoStorageUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IncidentLogsScreen(
    onRefresh: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val reports by CODRepository.reportsFlow.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<ReportStatus?>(null) }
    var selectedSourceFilter by remember { mutableStateOf<String?>(null) }

    val incidentSourceOptions = listOf(
        "Store Complaint", "Qualtrics", "Grab", "Food Panda", "Tillster", "Absent", "Late", "House Rule", "Other"
    )

    var startDateFilter by remember { mutableStateOf("") }
    var endDateFilter by remember { mutableStateOf("") }

    var isFilterPanelExpanded by remember { mutableStateOf(false) }

    var selectedReportForDetail by remember { mutableStateOf<IncidentReport?>(null) }
    var reportToEdit by remember { mutableStateOf<IncidentReport?>(null) }
    var previewingPhotoUri by remember { mutableStateOf<Pair<String, String>?>(null) }

    val hasActiveFilters = searchQuery.isNotBlank() || selectedStatusFilter != null ||
            selectedSourceFilter != null || startDateFilter.isNotBlank() || endDateFilter.isNotBlank()

    fun showFilterDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                if (isStartDate) startDateFilter = selected else endDateFilter = selected
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun clearAllFilters() {
        searchQuery = ""
        selectedStatusFilter = null
        selectedSourceFilter = null
        startDateFilter = ""
        endDateFilter = ""
    }

    val filteredReports = remember(reports, searchQuery, selectedStatusFilter, selectedSourceFilter, startDateFilter, endDateFilter) {
        reports.filter { report ->
            val matchesStatus = selectedStatusFilter == null || report.status == selectedStatusFilter
            val matchesSource = selectedSourceFilter == null || report.incidentSource.equals(selectedSourceFilter, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    report.title.contains(searchQuery, ignoreCase = true) ||
                    report.formattedOffenderNames.contains(searchQuery, ignoreCase = true) ||
                    report.incidentSource.contains(searchQuery, ignoreCase = true) ||
                    report.id.contains(searchQuery, ignoreCase = true)

            val matchesStartDate = startDateFilter.isBlank() || report.incidentDate >= startDateFilter
            val matchesEndDate = endDateFilter.isBlank() || report.incidentDate <= endDateFilter

            matchesStatus && matchesSource && matchesSearch && matchesStartDate && matchesEndDate
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "INCIDENT LOGS & RECORDS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Total: ${reports.size} • Showing: ${filteredReports.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (!isRefreshing) {
                                    isRefreshing = true
                                    onRefresh?.invoke()
                                    scope.launch {
                                        kotlinx.coroutines.delay(1200)
                                        isRefreshing = false
                                    }
                                }
                            },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            if (isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Syncing...", style = MaterialTheme.typography.labelMedium)
                            } else {
                                Text("🔄 Refresh", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { isFilterPanelExpanded = !isFilterPanelExpanded },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasActiveFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (hasActiveFilters) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (hasActiveFilters) "🔍 Filtered" else "🔍 Search & Filter",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        if (hasActiveFilters && !isFilterPanelExpanded) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎯 Active Filters Applied",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        TextButton(
                            onClick = { clearAllFilters() },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text("Clear All ❌", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            AnimatedVisibility(
                visible = isFilterPanelExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔍 Search & Filter Options",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            TextButton(onClick = { isFilterPanelExpanded = false }) {
                                Text("Hide ▲", fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search by offender, title, source, or ID...") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Filter by Date Range:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                if (startDateFilter.isNotBlank() || endDateFilter.isNotBlank()) {
                                    TextButton(onClick = { startDateFilter = ""; endDateFilter = "" }) {
                                        Text("Clear Dates", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = startDateFilter,
                                    onValueChange = { startDateFilter = it },
                                    placeholder = { Text("Start Date") },
                                    trailingIcon = { TextButton(onClick = { showFilterDatePicker(true) }) { Text("📅") } },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = endDateFilter,
                                    onValueChange = { endDateFilter = it },
                                    placeholder = { Text("End Date") },
                                    trailingIcon = { TextButton(onClick = { showFilterDatePicker(false) }) { Text("📅") } },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Filter by Source / Channel:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (selectedSourceFilter != null) {
                                    TextButton(onClick = { selectedSourceFilter = null }) {
                                        Text("Clear Source", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = selectedSourceFilter == null,
                                        onClick = { selectedSourceFilter = null },
                                        label = { Text("All Sources") }
                                    )
                                }
                                items(incidentSourceOptions) { source ->
                                    FilterChip(
                                        selected = selectedSourceFilter == source,
                                        onClick = {
                                            selectedSourceFilter = if (selectedSourceFilter == source) null else source
                                        },
                                        label = {
                                            Text(
                                                when (source) {
                                                    "Qualtrics" -> "📊 Qualtrics"
                                                    "Store Complaint" -> "🏪 Store Complaint"
                                                    "Grab" -> "🍔 Grab"
                                                    "Food Panda" -> "🛵 Food Panda"
                                                    "Tillster" -> "📱 Tillster"
                                                    "Absent" -> "🚫 Absent"
                                                    "Late" -> "⏰ Late"
                                                    "House Rule" -> "🏠 House Rule"
                                                    else -> source
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Filter Status:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = selectedStatusFilter == null,
                                        onClick = { selectedStatusFilter = null },
                                        label = { Text("All") }
                                    )
                                }
                                items(ReportStatus.entries.toTypedArray()) { status ->
                                    FilterChip(
                                        selected = selectedStatusFilter == status,
                                        onClick = {
                                            selectedStatusFilter = if (selectedStatusFilter == status) null else status
                                        },
                                        label = { Text(status.label) }
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { clearAllFilters() }) {
                                Text("Clear All Filters ❌", color = MaterialTheme.colorScheme.error)
                            }

                            Button(onClick = { isFilterPanelExpanded = false }) {
                                Text("Apply & Close", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (filteredReports.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No incident reports found for selected filters.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredReports, key = { it.id }) { report ->
                IncidentReportCard(
                    report = report,
                    onClick = { selectedReportForDetail = report },
                    onStatusChange = { newStatus ->
                        CODRepository.updateReportStatus(report.id, newStatus, context)
                    }
                )
            }
        }
    }

    if (selectedReportForDetail != null) {
        val currentReport = reports.find { it.id == selectedReportForDetail!!.id } ?: selectedReportForDetail!!
        ReportDetailDialog(
            report = currentReport,
            onDismiss = { selectedReportForDetail = null },
            onEdit = {
                reportToEdit = currentReport
                selectedReportForDetail = null
            },
            onDelete = {
                CODRepository.deleteReport(currentReport.id, context)
                selectedReportForDetail = null
            },
            onViewPhoto = { title, uri ->
                previewingPhotoUri = title to uri
            }
        )
    }

    if (reportToEdit != null) {
        EditReportDialog(
            report = reportToEdit!!,
            onDismiss = { reportToEdit = null },
            onSave = { updatedReport ->
                CODRepository.updateReport(updatedReport, context)
                reportToEdit = null
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
}

@Composable
fun IncidentReportCard(
    report: IncidentReport,
    onClick: () -> Unit,
    onStatusChange: (ReportStatus) -> Unit
) {
    var showStatusDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${report.id} • ${report.incidentDate} ${report.incidentTime}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Box {
                    Surface(
                        color = getStatusColor(report.status).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable { showStatusDropdown = true }
                    ) {
                        Text(
                            text = report.status.label,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = getStatusColor(report.status)
                        )
                    }

                    DropdownMenu(
                        expanded = showStatusDropdown,
                        onDismissRequest = { showStatusDropdown = false }
                    ) {
                        ReportStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.label) },
                                onClick = {
                                    onStatusChange(status)
                                    showStatusDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = report.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (report.incidentSource) {
                            "Qualtrics" -> "📊 Qualtrics"
                            "Store Complaint" -> "🏪 Store Complaint"
                            "Grab" -> "🍔 Grab"
                            "Food Panda" -> "🛵 Food Panda"
                            "Tillster" -> "📱 Tillster"
                            "Absent" -> "🚫 Absent"
                            "Late" -> "⏰ Late"
                            "House Rule" -> "🏠 House Rule"
                            else -> report.incidentSource
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Employee(s): ${report.formattedOffenderNames}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (report.validity) {
                        ReportValidity.VALID -> Color(0xFFE8F5E9)
                        ReportValidity.INVALID -> Color(0xFFFFEBEE)
                        ReportValidity.PENDING_EVALUATION -> Color(0xFFFFF3E0)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (report.validity) {
                            ReportValidity.VALID -> "✅ Valid"
                            ReportValidity.INVALID -> "❌ Invalid"
                            ReportValidity.PENDING_EVALUATION -> "⏳ Pending"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (report.validity) {
                            ReportValidity.VALID -> Color(0xFF2E7D32)
                            ReportValidity.INVALID -> Color(0xFFC62828)
                            ReportValidity.PENDING_EVALUATION -> Color(0xFFEF6C00)
                        }
                    )
                }

                Surface(
                    color = getCategoryColor(report.category).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = report.category?.label ?: "Rule: None",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = getCategoryColor(report.category)
                    )
                }

                Surface(
                    color = if (report.recommendedPenalty.contains("TERMINATION"))
                        Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (report.category != null && report.offenseCount != "None") "${report.offenseCount}: ${report.recommendedPenalty}" else "Penalty: None",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (report.recommendedPenalty.contains("TERMINATION"))
                            Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (report.complaintFormUris.isNotEmpty()) {
                    Text(text = "📄 ${report.complaintFormUris.size}", style = MaterialTheme.typography.labelSmall)
                }
                if (report.employeeIrPhotoUris.isNotEmpty()) {
                    Text(text = "👤 ${report.employeeIrPhotoUris.size}", style = MaterialTheme.typography.labelSmall)
                }
                if (report.rtwFusionFormUris.isNotEmpty()) {
                    Text(text = "📋 ${report.rtwFusionFormUris.size}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun ReportDetailDialog(
    report: IncidentReport,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewPhoto: (title: String, uri: String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Report Details: ${report.id}")
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = report.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                HorizontalDivider()

                DetailRow("Employee(s) Involved:", report.formattedOffenderNames)
                DetailRow("Complaint Source / Channel:", report.incidentSource)
                DetailRow("Complaint Validity:", when (report.validity) {
                    ReportValidity.VALID -> "✅ Valid Complaint"
                    ReportValidity.INVALID -> "❌ Invalid Complaint"
                    ReportValidity.PENDING_EVALUATION -> "⏳ Pending Evaluation"
                })
                DetailRow("Incident Date & Time:", "${report.incidentDate} at ${report.incidentTime}")
                DetailRow("Report Date:", report.date)
                DetailRow("Status:", report.status.label)
                DetailRow("Rule Category:", report.category?.label ?: "None")
                DetailRow("Offense Count:", if (report.ruleId == null) "None" else report.offenseCount)
                DetailRow("Penalty Matrix:", report.recommendedPenalty)

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "COD Rule Violated:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text(text = report.ruleDescription, style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Incident Statement:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text(text = report.details, style = MaterialTheme.typography.bodySmall)

                if (report.complaintFormUris.isNotEmpty() || report.employeeIrPhotoUris.isNotEmpty() || report.rtwFusionFormUris.isNotEmpty()) {
                    HorizontalDivider()
                    Text(text = "Attached Photos:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)

                    if (report.complaintFormUris.isNotEmpty()) {
                        Text(text = "Complaint Form Photos (${report.complaintFormUris.size}):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(report.complaintFormUris) { uri ->
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onViewPhoto("Complaint Form Photo", uri) }
                                ) {
                                    ImageFromUri(uriString = uri, contentDescription = "Complaint Form Photo", modifier = Modifier.fillMaxSize())
                                }
                            }
                        }
                    }

                    if (report.employeeIrPhotoUris.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Employee IR Photos (${report.employeeIrPhotoUris.size}):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(report.employeeIrPhotoUris) { uri ->
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onViewPhoto("Employee IR Photo", uri) }
                                ) {
                                    ImageFromUri(uriString = uri, contentDescription = "Employee IR Photo", modifier = Modifier.fillMaxSize())
                                }
                            }
                        }
                    }

                    if (report.rtwFusionFormUris.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "RTW / Fusion Form Photos (${report.rtwFusionFormUris.size}):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(report.rtwFusionFormUris) { uri ->
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onViewPhoto("RTW / Fusion Form Photo", uri) }
                                ) {
                                    ImageFromUri(uriString = uri, contentDescription = "RTW / Fusion Form Photo", modifier = Modifier.fillMaxSize())
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onEdit) {
                    Text("✏️ Edit Report")
                }
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDelete) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditReportDialog(
    report: IncidentReport,
    onDismiss: () -> Unit,
    onSave: (IncidentReport) -> Unit
) {
    val context = LocalContext.current
    val rules = remember { CODRepository.getAllRules() }

    val incidentSourceOptions = listOf(
        "Store Complaint", "Qualtrics", "Grab", "Food Panda", "Tillster", "Absent", "Late", "House Rule", "Other"
    )
    var selectedSource by remember { mutableStateOf(report.incidentSource) }

    var title by remember { mutableStateOf(report.title) }
    val involvedEmployees = remember {
        mutableStateListOf<String>().apply {
            if (report.offenderNames.isNotEmpty()) addAll(report.offenderNames)
            else if (report.offenderName.isNotBlank()) addAll(report.offenderName.split(", "))
        }
    }
    var customEmployeeInput by remember { mutableStateOf("") }

    var validity by remember { mutableStateOf(report.validity) }
    var incidentDate by remember { mutableStateOf(report.incidentDate) }
    var incidentTime by remember { mutableStateOf(report.incidentTime) }
    var details by remember { mutableStateOf(report.details) }
    var status by remember { mutableStateOf(report.status) }

    val complaintFormUris = remember { mutableStateListOf<String>().apply { addAll(report.complaintFormUris) } }
    val employeeIrPhotoUris = remember { mutableStateListOf<String>().apply { addAll(report.employeeIrPhotoUris) } }
    val rtwFusionFormUris = remember { mutableStateListOf<String>().apply { addAll(report.rtwFusionFormUris) } }

    var selectedRule by remember {
        mutableStateOf<RuleItem?>(rules.find { it.id == report.ruleId })
    }

    val offenseCountOptions = listOf("1st Offense", "2nd Offense", "3rd Offense", "4th Offense", "5th Offense")
    var selectedOffenseCount by remember { mutableStateOf(if (selectedRule == null) "None" else report.offenseCount) }

    var showRulePicker by remember { mutableStateOf(false) }
    var showOffenseCountPicker by remember { mutableStateOf(false) }
    var showEmployeePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Incident Report (${report.id})") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Incident Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Complaint Source / Channel", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(incidentSourceOptions) { source ->
                            FilterChip(
                                selected = selectedSource == source,
                                onClick = { selectedSource = source },
                                label = { Text(source, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Complaint Validity", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ReportValidity.entries.forEach { valOption ->
                            FilterChip(
                                selected = validity == valOption,
                                onClick = { validity = valOption },
                                label = {
                                    Text(
                                        text = when (valOption) {
                                            ReportValidity.VALID -> "✅ Valid"
                                            ReportValidity.INVALID -> "❌ Invalid"
                                            ReportValidity.PENDING_EVALUATION -> "⏳ Pending"
                                        },
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = incidentDate,
                        onValueChange = { incidentDate = it },
                        label = { Text("Incident Date *") },
                        trailingIcon = { TextButton(onClick = { showDatePicker() }) { Text("📅") } },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = incidentTime,
                        onValueChange = { incidentTime = it },
                        label = { Text("Incident Time *") },
                        trailingIcon = { TextButton(onClick = { showTimePicker() }) { Text("⏰") } },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Employee(s) Involved", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { showEmployeePicker = true }) {
                                Text("👥 + Add", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        if (involvedEmployees.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                involvedEmployees.forEachIndexed { idx, name ->
                                    InputChip(
                                        selected = true,
                                        onClick = { involvedEmployees.removeAt(idx) },
                                        label = { Text(name, style = MaterialTheme.typography.labelSmall) },
                                        trailingIcon = { Text(" ❌") }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customEmployeeInput,
                                onValueChange = { customEmployeeInput = it },
                                placeholder = { Text("Type custom name...", style = MaterialTheme.typography.bodySmall) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            Button(
                                onClick = {
                                    if (customEmployeeInput.isNotBlank()) {
                                        involvedEmployees.add(customEmployeeInput.trim())
                                        customEmployeeInput = ""
                                    }
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Add", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showRulePicker = true },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "COD Rule Violation", style = MaterialTheme.typography.labelMedium)
                            Text(text = "Change", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        if (selectedRule != null) {
                            Text(
                                text = "Rule ${selectedRule!!.ruleNumber} • ${selectedRule!!.category.label}",
                                fontWeight = FontWeight.Bold,
                                color = getCategoryColor(selectedRule!!.category)
                            )
                            Text(text = selectedRule!!.description, style = MaterialTheme.typography.bodySmall)
                        } else {
                            Text(text = "NONE (No Rule Selected)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                if (selectedRule != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showOffenseCountPicker = true },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Offense Count: $selectedOffenseCount", fontWeight = FontWeight.Bold)
                            Text(text = "Change", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Text(
                    text = "Recommended Penalty: $recommendedPenalty",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Details / Statement *") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                MultiPhotoUploadCard(
                    title = "Complaint Form Photos",
                    description = "Upload 1 or more complaint form photos.",
                    photoUris = complaintFormUris,
                    onAddPhotos = { complaintFormLauncher.launch("image/*") },
                    onRemovePhoto = { index -> complaintFormUris.removeAt(index) }
                )

                MultiPhotoUploadCard(
                    title = "Employee IR Photos",
                    description = "Upload 1 or more Employee IR photos.",
                    photoUris = employeeIrPhotoUris,
                    onAddPhotos = { employeeIrPhotoLauncher.launch("image/*") },
                    onRemovePhoto = { index -> employeeIrPhotoUris.removeAt(index) }
                )

                MultiPhotoUploadCard(
                    title = "RTW / Fusion Form Photos",
                    description = "Upload 1 or more RTW / Fusion form photos.",
                    photoUris = rtwFusionFormUris,
                    onAddPhotos = { rtwFusionFormLauncher.launch("image/*") },
                    onRemovePhoto = { index -> rtwFusionFormUris.removeAt(index) }
                )

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || involvedEmployees.isEmpty() || details.isBlank() || incidentDate.isBlank() || incidentTime.isBlank()) {
                        errorMessage = "Please fill in all required fields and specify at least 1 employee involved."
                    } else {
                        scope.launch(Dispatchers.IO) {
                            val finalComplaintUris = complaintFormUris.mapIndexed { idx, uri ->
                                val fileName = buildDriveFileName("ComplaintForm_$idx")
                                com.sam.myapplication.util.GoogleDriveSyncManager.uploadPhotoToGoogleDriveIfNotUploaded(context, uri, fileName)
                            }
                            val finalIrUris = employeeIrPhotoUris.mapIndexed { idx, uri ->
                                val fileName = buildDriveFileName("EmployeeIR_$idx")
                                com.sam.myapplication.util.GoogleDriveSyncManager.uploadPhotoToGoogleDriveIfNotUploaded(context, uri, fileName)
                            }
                            val finalRtwUris = rtwFusionFormUris.mapIndexed { idx, uri ->
                                val fileName = buildDriveFileName("RTWFusion_$idx")
                                com.sam.myapplication.util.GoogleDriveSyncManager.uploadPhotoToGoogleDriveIfNotUploaded(context, uri, fileName)
                            }

                            val updated = report.copy(
                                title = title.trim(),
                                offenderName = involvedEmployees.joinToString(", "),
                                offenderNames = involvedEmployees.toList(),
                                incidentSource = selectedSource,
                                incidentDate = incidentDate.trim(),
                                incidentTime = incidentTime.trim(),
                                ruleId = selectedRule?.id,
                                ruleDescription = selectedRule?.description ?: "None (No Rule Violation Selected)",
                                category = selectedRule?.category,
                                offenseCount = if (selectedRule != null) selectedOffenseCount else "None",
                                recommendedPenalty = recommendedPenalty,
                                details = details.trim(),
                                complaintFormUris = finalComplaintUris,
                                employeeIrPhotoUris = finalIrUris,
                                rtwFusionFormUris = finalRtwUris,
                                status = status,
                                validity = validity
                            )
                            kotlinx.coroutines.withContext(Dispatchers.Main) {
                                onSave(updated)
                            }
                        }
                    }
                }
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

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
                            Text(text = "🚫 NONE", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "No rule violation selected.", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    HorizontalDivider()

                    rules.forEach { rule ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRule = rule
                                    if (selectedOffenseCount == "None") selectedOffenseCount = "1st Offense"
                                    showRulePicker = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedRule?.id == rule.id)
                                    MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Rule ${rule.ruleNumber} - ${rule.category.label}",
                                    fontWeight = FontWeight.Bold,
                                    color = getCategoryColor(rule.category)
                                )
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
                            RadioButton(selected = selectedOffenseCount == count, onClick = null)
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
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

fun getStatusColor(status: ReportStatus): Color {
    return when (status) {
        ReportStatus.PENDING -> Color(0xFFF57C00)
        ReportStatus.UNDER_INVESTIGATION -> Color(0xFF0288D1)
        ReportStatus.RESOLVED -> Color(0xFF388E3C)
        ReportStatus.DISMISSED -> Color(0xFF757575)
    }
}
