package com.sam.myapplication.data

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeBackup(
    val employee: Employee,
    val profileImageBase64: String? = null
)

@Serializable
data class BackupData(
    val employees: List<EmployeeBackup>,
    val attendanceRecords: List<AttendanceRecord>,
    val dailyTimeRecords: List<DailyTimeRecord> = emptyList(),
    val requests: List<AttendanceRequest> = emptyList(),
    val announcements: List<Announcement> = emptyList(),
    val attritionRecords: List<AttritionRecord> = emptyList(),
    val daRecords: List<DARecord> = emptyList(),
    val dailyNotes: List<DailySummaryNote> = emptyList(),
    val workPermits: List<WorkPermit> = emptyList(),
    val messages: List<ChatMessage> = emptyList(),
    val schedules: List<EmployeeSchedule> = emptyList(),
    val shiftTemplates: List<ShiftTemplate> = emptyList(),
    val activityLogs: List<ActivityLog> = emptyList()
)
