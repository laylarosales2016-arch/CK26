package com.sam.myapplication.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sam.myapplication.data.AppDatabase
import com.sam.myapplication.data.AttendanceRepository
import com.sam.myapplication.supabase.SupabaseSyncManager

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val employeeId = inputData.getString("employee_id") ?: return Result.failure()
        
        Log.d("SyncWorker", "Auto-trigger sync started for employee: $employeeId")
        
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = AttendanceRepository(
                database.employeeDao(),
                database.announcementDao(),
                database.chatMessageDao(),
                database.workPermitDao(),
                database.attritionDao(),
                database.daDao(),
                database.activityLogDao(),
                database.scheduleDao()
            )
            val supabaseSync = SupabaseSyncManager(repository)
            
            supabaseSync.uploadEmployeeData(employeeId)
            
            Log.d("SyncWorker", "Auto-trigger sync successful for $employeeId")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Auto-trigger sync failed for $employeeId: ${e.message}")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
