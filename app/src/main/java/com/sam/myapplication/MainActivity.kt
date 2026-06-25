package com.sam.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sam.myapplication.data.AppDatabase
import com.sam.myapplication.data.AttendanceRepository
import com.sam.myapplication.auth.GoogleAuthService
import com.sam.myapplication.sync.WifiSyncManager
import com.sam.myapplication.supabase.SupabaseSyncManager
import com.sam.myapplication.supabase.SupabaseStorageSyncManager
import com.sam.myapplication.ui.AttendanceApp
import com.sam.myapplication.ui.AttendanceViewModel
import com.sam.myapplication.ui.theme.MyApplicationTheme

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { 
        AttendanceRepository(
            database.employeeDao(), 
            database.announcementDao(), 
            database.chatMessageDao(), 
            database.workPermitDao(),
            database.attritionDao(),
            database.daDao(),
            database.activityLogDao(),
            database.scheduleDao()
        ) 
    }
    private val authService by lazy { GoogleAuthService(applicationContext) }
    private val supabaseSync by lazy { SupabaseSyncManager(repository) }
    private val supabaseStorage by lazy { SupabaseStorageSyncManager(repository, applicationContext) }
    private val viewModel: AttendanceViewModel by viewModels {
        AttendanceViewModel.Factory(repository, authService, supabaseSync, supabaseStorage, applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        lifecycleScope.launch {
            UpdateChecker.checkForUpdates(this@MainActivity)
        }

        // Periodic Background Sync and Update Check every 2 minutes
        lifecycleScope.launch {
            while(true) {
                viewModel.performBackgroundSync()
                UpdateChecker.checkForUpdates(this@MainActivity)
                kotlinx.coroutines.delay(120_000)
            }
        }

        setContent {
            val selectedTheme by viewModel.selectedTheme.collectAsState()
            MyApplicationTheme(appTheme = selectedTheme) {
                AttendanceApp(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.performBackgroundSync()
        lifecycleScope.launch {
            UpdateChecker.checkForUpdates(this@MainActivity)
        }
    }
}
