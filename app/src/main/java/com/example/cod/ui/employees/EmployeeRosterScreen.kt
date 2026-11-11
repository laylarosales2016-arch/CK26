package com.example.cod.ui.employees

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cod.data.CODRepository
import com.example.cod.model.Employee
import com.example.cod.ui.reports.ImageFromUri
import com.example.cod.ui.reports.PhotoUploadCard
import com.example.cod.util.PhotoStorageUtil

@Composable
fun EmployeeRosterScreen() {
    val context = LocalContext.current
    val employees by CODRepository.employeesFlow.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddEmployeeDialog by remember { mutableStateOf(false) }
    var employeeToEdit by remember { mutableStateOf<Employee?>(null) }
    var employeeToDelete by remember { mutableStateOf<Employee?>(null) }

    val filteredEmployees = remember(employees, searchQuery) {
        employees.filter { emp ->
            searchQuery.isBlank() ||
                    emp.name.contains(searchQuery, ignoreCase = true) ||
                    emp.position.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "COMPANY EMPLOYEE DIRECTORY",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Total Employees: ${employees.size} • Add, edit, or manage company personnel.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search employee by name or position...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (filteredEmployees.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) "No employees added yet." else "No employees match your search.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredEmployees, key = { it.id }) { employee ->
                    EmployeeCard(
                        employee = employee,
                        onEdit = { employeeToEdit = employee },
                        onDelete = { employeeToDelete = employee }
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { showAddEmployeeDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            icon = { Text("➕", style = MaterialTheme.typography.titleMedium) },
            text = { Text("Add Employee", fontWeight = FontWeight.Bold) }
        )
    }

    if (showAddEmployeeDialog) {
        AddEmployeeDialog(
            onDismiss = { showAddEmployeeDialog = false },
            onSave = { newEmployee ->
                CODRepository.addEmployee(newEmployee, context)
                showAddEmployeeDialog = false
            }
        )
    }

    if (employeeToEdit != null) {
        EditEmployeeDialog(
            employee = employeeToEdit!!,
            onDismiss = { employeeToEdit = null },
            onSave = { updatedEmp ->
                CODRepository.updateEmployee(updatedEmp, context)
                employeeToEdit = null
            }
        )
    }

    if (employeeToDelete != null) {
        AlertDialog(
            onDismissRequest = { employeeToDelete = null },
            title = { Text("Delete Employee") },
            text = { Text("Are you sure you want to delete ${employeeToDelete!!.name} from the company directory?") },
            confirmButton = {
                Button(
                    onClick = {
                        CODRepository.deleteEmployee(employeeToDelete!!.id, context)
                        employeeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { employeeToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmployeeCard(
    employee: Employee,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val reports by CODRepository.reportsFlow.collectAsState()

    val involvedReports = remember(reports, employee.name) {
        reports.filter { r ->
            r.offenderName.contains(employee.name, ignoreCase = true) ||
            r.offenderNames.any { it.contains(employee.name, ignoreCase = true) }
        }
    }

    val sourceCounts = remember(involvedReports) {
        involvedReports.groupBy { it.incidentSource }
            .mapValues { it.value.size }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!employee.photoUri.isNullOrBlank()) {
                        ImageFromUri(
                            uriString = employee.photoUri,
                            contentDescription = employee.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = employee.name.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = employee.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (employee.position.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Position: ${employee.position}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Text("✏️", style = MaterialTheme.typography.titleMedium)
                    }
                    IconButton(onClick = onDelete) {
                        Text("🗑️", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            if (involvedReports.isEmpty()) {
                Surface(
                    color = androidx.compose.ui.graphics.Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "✅ No Incidents Logged",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color(0xFF2E7D32)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = androidx.compose.ui.graphics.Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "⚠️ Total Incidents: ${involvedReports.size}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.ui.graphics.Color(0xFFC62828)
                            )
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        sourceCounts.forEach { (source, count) ->
                            val iconText = when (source) {
                                "Qualtrics" -> "📊 Qualtrics"
                                "Store Complaint" -> "🏪 Store Complaint"
                                "Grab" -> "🍔 Grab"
                                "Food Panda" -> "🛵 Food Panda"
                                "Tillster" -> "📱 Tillster"
                                "Absent" -> "🚫 Absent"
                                "Late" -> "⏰ Late"
                                "House Rule" -> "🏠 House Rule"
                                else -> "📝 $source"
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "$iconText: $count",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddEmployeeDialog(
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) photoUri = PhotoStorageUtil.saveImageToAppStorage(context, uri) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Employee") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Employee Name *") },
                    placeholder = { Text("e.g. John Doe") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = position,
                    onValueChange = { position = it },
                    label = { Text("Position / Job Title") },
                    placeholder = { Text("e.g. Shift Lead") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                PhotoUploadCard(
                    title = "Employee Photo",
                    description = "Optional: Upload employee photo.",
                    photoUri = photoUri,
                    onPickPhoto = { photoLauncher.launch("image/*") },
                    onRemovePhoto = { photoUri = null }
                )

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Please enter Employee Name."
                    } else {
                        val newEmp = Employee(
                            id = "EMP-${System.currentTimeMillis().toString().takeLast(5)}",
                            name = name.trim(),
                            position = position.trim(),
                            photoUri = photoUri
                        )
                        onSave(newEmp)
                    }
                }
            ) {
                Text("Save Employee")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditEmployeeDialog(
    employee: Employee,
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit
) {
    var name by remember { mutableStateOf(employee.name) }
    var position by remember { mutableStateOf(employee.position) }
    var photoUri by remember { mutableStateOf(employee.photoUri) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) photoUri = PhotoStorageUtil.saveImageToAppStorage(context, uri) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Employee Info") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Employee Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = position,
                    onValueChange = { position = it },
                    label = { Text("Position / Job Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                PhotoUploadCard(
                    title = "Employee Photo",
                    description = "Upload or replace employee photo.",
                    photoUri = photoUri,
                    onPickPhoto = { photoLauncher.launch("image/*") },
                    onRemovePhoto = { photoUri = null }
                )

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Please enter Employee Name."
                    } else {
                        val updated = employee.copy(
                            name = name.trim(),
                            position = position.trim(),
                            photoUri = photoUri
                        )
                        onSave(updated)
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
}

@Composable
fun EmployeePickerDialog(
    onDismiss: () -> Unit,
    onSelectEmployee: (Employee) -> Unit,
    onOpenManager: () -> Unit
) {
    val employees by CODRepository.employeesFlow.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(employees, searchQuery) {
        employees.filter { emp ->
            searchQuery.isBlank() ||
                    emp.name.contains(searchQuery, ignoreCase = true) ||
                    emp.position.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Select Employee")
                TextButton(onClick = onOpenManager) {
                    Text("⚙️ Manage Roster", style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter employee name...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No matching employees. Tap Manage Roster to add.")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filtered, key = { it.id }) { employee ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectEmployee(employee) },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = CircleShape,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = employee.name.take(1).uppercase(),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = employee.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        if (employee.position.isNotBlank()) {
                                            Text(
                                                text = employee.position,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
