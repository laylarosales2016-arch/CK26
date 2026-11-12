package com.example.cod.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cod.data.CODRepository
import com.example.cod.model.RuleItem
import com.example.cod.ui.backup.BackupSyncScreen
import com.example.cod.ui.employees.EmployeeRosterScreen
import com.example.cod.ui.reports.IncidentLogsScreen
import com.example.cod.ui.reports.NewReportScreen
import com.example.cod.ui.rulebook.RulebookScreen
import com.sam.myapplication.data.Employee as AppEmployee

enum class NavigationTab(val title: String, val iconText: String) {
    RULEBOOK("Rulebook", "📖"),
    NEW_REPORT("File Report", "📝"),
    INCIDENT_LOGS("Incident Logs", "📁"),
    EMPLOYEES("Employees", "👥"),
    BACKUP("Backup & Sync", "🔄")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CODMainScreen(
    appEmployees: List<AppEmployee> = emptyList(),
    onRefreshCODReports: (() -> Unit)? = null,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 600

    LaunchedEffect(appEmployees) {
        CODRepository.initPersistence(context)
        if (appEmployees.isNotEmpty()) {
            val codEmps = appEmployees.filter { emp ->
                val pos = (emp.position ?: "").lowercase()
                !pos.contains("excrew")
            }.map { emp ->
                com.example.cod.model.Employee(
                    id = emp.employeeNo ?: emp.id,
                    name = "${emp.firstName ?: ""} ${emp.lastName ?: ""}".trim(),
                    department = emp.location ?: "SM Clark",
                    position = emp.position ?: "Crew",
                    photoUri = emp.profileImageUri
                )
            }
            CODRepository.setAllData(CODRepository.reportsFlow.value, codEmps)
        }
    }

    var selectedTab by remember { mutableStateOf(NavigationTab.RULEBOOK) }
    var selectedRuleForReport by remember { mutableStateOf<RuleItem?>(null) }

    val currentTitle = when (selectedTab) {
        NavigationTab.RULEBOOK -> "FISC Code of Discipline"
        NavigationTab.NEW_REPORT -> "File Incident Report"
        NavigationTab.INCIDENT_LOGS -> "Incident Logs & Records"
        NavigationTab.EMPLOYEES -> "Company Employee Directory"
        NavigationTab.BACKUP -> "Backup, Restore & Wi-Fi Sync"
    }

    val mainContent: @Composable (PaddingValues) -> Unit = { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                com.example.cod.ui.reports.DriveUploadNotificationBanner()

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when (selectedTab) {
                    NavigationTab.RULEBOOK -> {
                        RulebookScreen(
                            onSelectRuleToReport = { rule ->
                                selectedRuleForReport = rule
                                selectedTab = NavigationTab.NEW_REPORT
                            }
                        )
                    }
                    NavigationTab.NEW_REPORT -> {
                        NewReportScreen(
                            initialSelectedRule = selectedRuleForReport,
                            onReportSubmitted = {
                                selectedRuleForReport = null
                                selectedTab = NavigationTab.INCIDENT_LOGS
                            },
                            onOpenEmployeeRoster = {
                                selectedTab = NavigationTab.EMPLOYEES
                            }
                        )
                    }
                    NavigationTab.INCIDENT_LOGS -> {
                        IncidentLogsScreen(
                            onRefresh = onRefreshCODReports
                        )
                    }
                    NavigationTab.EMPLOYEES -> {
                        EmployeeRosterScreen()
                    }
                    NavigationTab.BACKUP -> {
                        BackupSyncScreen()
                    }
                }
            }
        }
    }
}

    if (isWideScreen) {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "⚖️ COD",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                NavigationTab.entries.forEach { tab ->
                    NavigationRailItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Text(
                                text = tab.iconText,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        label = { Text(tab.title, fontWeight = FontWeight.Medium) }
                    )
                }
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = currentTitle, fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            ) { innerPadding ->
                mainContent(innerPadding)
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = currentTitle, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    NavigationTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Text(
                                    text = tab.iconText,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            label = { Text(tab.title, fontWeight = FontWeight.Medium) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            mainContent(innerPadding)
        }
    }
}
