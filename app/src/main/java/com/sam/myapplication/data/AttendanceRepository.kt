package com.sam.myapplication.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AttendanceRepository(
    private val employeeDao: EmployeeDao,
    private val announcementDao: AnnouncementDao,
    private val chatMessageDao: ChatMessageDao,
    private val workPermitDao: WorkPermitDao,
    private val attritionDao: AttritionDao,
    private val daDao: DADao,
    private val activityLogDao: ActivityLogDao,
    private val scheduleDao: ScheduleDao
) {
    val allEmployees: Flow<List<Employee>> = employeeDao.getAllEmployees()

    // Activity Logs
    val allActivityLogs: Flow<List<ActivityLog>> = activityLogDao.getAllLogs()

    suspend fun insertActivityLog(log: ActivityLog) = activityLogDao.insertLog(log)
    suspend fun insertActivityLogs(logs: List<ActivityLog>) = activityLogDao.insertLogs(logs)
    suspend fun clearActivityLogs() = activityLogDao.clearAll()

    // DA Records
    val allDA: Flow<List<DARecord>> = daDao.getAllDA()
    suspend fun insertDA(record: DARecord) = daDao.insertDA(record)
    suspend fun deleteDA(record: DARecord) = daDao.deleteDA(record)
    suspend fun clearAllDA() = daDao.clearAll()

    // Attrition
    val allAttrition: Flow<List<AttritionRecord>> = attritionDao.getAllAttrition()
    suspend fun insertAttrition(record: AttritionRecord) = attritionDao.insertAttrition(record)
    suspend fun deleteAttrition(record: AttritionRecord) = attritionDao.deleteAttrition(record)
    suspend fun clearAllAttrition() = attritionDao.clearAll()

    // Work Permits
    val allPermits: Flow<List<WorkPermit>> = workPermitDao.getAllPermits()
    suspend fun insertPermit(permit: WorkPermit) = workPermitDao.insertPermit(permit)
    suspend fun insertPermits(permits: List<WorkPermit>) = workPermitDao.insertPermits(permits)
    suspend fun updatePermitLabel(ref: String, label: String) = workPermitDao.updateLabel(ref, label)
    suspend fun updatePermitStatus(ref: String, status: String) = workPermitDao.updateStatus(ref, status)
    suspend fun updatePermitExpiredStatus(ref: String, expired: Boolean) = workPermitDao.updateExpiredStatus(ref, expired)
    suspend fun updatePermitCache(ref: String, html: String) = workPermitDao.updateCachedHtml(ref, html)
    suspend fun getPermitByRef(ref: String) = workPermitDao.getPermitByRef(ref)
    suspend fun clearPermitCache() = workPermitDao.clearCache()

    // Chat Messages
    fun getMessagesWith(userId: String, otherId: String) = chatMessageDao.getMessagesWith(userId, otherId)
    fun getUnreadMessages(userId: String) = chatMessageDao.getUnreadMessages(userId)
    suspend fun insertMessage(message: ChatMessage) = chatMessageDao.insertMessage(message)
    suspend fun updateMessage(message: ChatMessage) = chatMessageDao.updateMessage(message)
    suspend fun deleteMessage(message: ChatMessage) = chatMessageDao.deleteMessage(message)
    suspend fun deleteMessageById(id: Long) = chatMessageDao.deleteMessageById(id)
    suspend fun markAsRead(userId: String, senderId: String) = chatMessageDao.markAsRead(userId, senderId)
    val allMessages: Flow<List<ChatMessage>> = chatMessageDao.getAllMessages()
    suspend fun deleteAllMessagesForEmployee(employeeId: String) = chatMessageDao.deleteAllMessagesForEmployee(employeeId)

    // ... (rest of employee methods)

    // Announcements
    val allAnnouncements: Flow<List<Announcement>> = announcementDao.getAllAnnouncements()

    fun getAnnouncementsForEmployee(employeeId: String): Flow<List<Announcement>> {
        return announcementDao.getAnnouncementsForEmployee(employeeId)
    }

    suspend fun insertAnnouncement(announcement: Announcement): Long {
        return announcementDao.insertAnnouncement(announcement)
    }

    suspend fun deleteAnnouncement(announcement: Announcement) {
        announcementDao.deleteAnnouncement(announcement)
    }

    suspend fun markAnnouncementAsRead(announcementId: Int) {
        announcementDao.markAsRead(announcementId)
    }

    suspend fun insertEmployee(employee: Employee): Long {
        return employeeDao.insertEmployee(employee)
    }

    suspend fun updateEmployee(employee: Employee) {
        employeeDao.updateEmployee(employee)
    }

    suspend fun deleteEmployee(employee: Employee) {
        employeeDao.deleteEmployee(employee)
    }

    suspend fun getEmployeeByNo(employeeNo: String): Employee? {
        return employeeDao.getEmployeeByNo(employeeNo)
    }

    suspend fun getEmployeeById(id: String): Employee? {
        return employeeDao.getEmployeeById(id)
    }

    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>> {
        return employeeDao.getAttendanceByDate(date)
    }

    suspend fun insertAttendance(record: AttendanceRecord) {
        employeeDao.insertAttendance(record)
    }

    fun getAttendanceForEmployee(employeeId: String): Flow<List<AttendanceRecord>> {
        return employeeDao.getAttendanceForEmployee(employeeId)
    }

    suspend fun deleteAttendance(employeeId: String, date: String) {
        employeeDao.deleteAttendance(employeeId, date)
    }

    suspend fun updateAttendanceNote(employeeId: String, date: String, note: String?) {
        employeeDao.updateAttendanceNote(employeeId, date, note)
    }

    suspend fun deleteAllAttendanceForEmployee(employeeId: String) = employeeDao.deleteAllAttendanceForEmployee(employeeId)
    suspend fun deleteAllDTRForEmployee(employeeId: String) = employeeDao.deleteAllDTRForEmployee(employeeId)
    suspend fun deleteAllRequestsForEmployee(employeeId: String) = employeeDao.deleteAllRequestsForEmployee(employeeId)

    suspend fun clearAllEmployees() = employeeDao.clearAllEmployees()
    suspend fun clearAllAttendance() = employeeDao.clearAllAttendance()
    suspend fun clearAllDTR() = employeeDao.clearAllDTR()
    suspend fun clearAllRequests() = employeeDao.clearAllRequests()
    suspend fun clearAllDailyNotes() = employeeDao.clearAllDailyNotes()
    suspend fun clearAllAnnouncements() = announcementDao.clearAll()
    suspend fun clearAllMessages() = chatMessageDao.clearAll()
    suspend fun clearAllPermits() = workPermitDao.clearAll()

    val allAttendanceRecords: Flow<List<AttendanceRecord>> = employeeDao.getAllAttendanceRecords()

    val allDTRRecords: Flow<List<DailyTimeRecord>> = employeeDao.getAllDTRRecords()

    // Requests
    val allRequests: Flow<List<AttendanceRequest>> = employeeDao.getAllRequests()

    fun getRequestsForEmployee(employeeId: String): Flow<List<AttendanceRequest>> {
        return employeeDao.getRequestsForEmployee(employeeId)
    }

    suspend fun insertRequest(request: AttendanceRequest): Long {
        return employeeDao.insertRequest(request)
    }

    suspend fun updateRequest(request: AttendanceRequest) {
        employeeDao.updateRequest(request)
    }

    suspend fun deleteRequest(request: AttendanceRequest) {
        employeeDao.deleteRequest(request)
    }

    suspend fun getRequestById(id: Int): AttendanceRequest? {
        return employeeDao.getRequestById(id)
    }

    // Daily Time Records
    fun getDTRForEmployee(employeeId: String): Flow<List<DailyTimeRecord>> {
        return employeeDao.getDTRForEmployee(employeeId)
    }

    fun getDTRForEmployeeByDate(employeeId: String, date: String): Flow<DailyTimeRecord?> {
        return employeeDao.getDTRForEmployeeByDate(employeeId, date)
    }

    suspend fun insertDTR(record: DailyTimeRecord) {
        employeeDao.insertDTR(record)
    }

    suspend fun deleteDTR(employeeId: String, date: String) {
        employeeDao.deleteDTR(employeeId, date)
    }

    suspend fun updateDTR(record: DailyTimeRecord) {
        employeeDao.updateDTR(record)
    }

    suspend fun getDTR(employeeId: String, date: String): DailyTimeRecord? {
        return employeeDao.getDTR(employeeId, date)
    }

    suspend fun insertDailyNote(note: DailySummaryNote) = employeeDao.insertDailyNote(note)
    fun getDailyNote(date: String) = employeeDao.getDailyNote(date)
    val allDailyNotes = employeeDao.getAllDailyNotes()

    // Schedule methods
    val allSchedules: Flow<List<EmployeeSchedule>> = scheduleDao.getAllSchedules()
    suspend fun allSchedulesFirst(): List<EmployeeSchedule> = scheduleDao.getAllSchedulesSync()
    val allShiftTemplates: Flow<List<ShiftTemplate>> = scheduleDao.getAllShiftTemplates()
    suspend fun allShiftTemplatesFirst(): List<ShiftTemplate> = scheduleDao.getAllShiftTemplatesSync()

    fun getSchedulesInRange(startDate: String, endDate: String) = scheduleDao.getSchedulesInRange(startDate, endDate)
    fun getSchedulesInRange(employeeId: String, startDate: String, endDate: String) = scheduleDao.getSchedulesInRange(employeeId, startDate, endDate)
    suspend fun insertSchedule(schedule: EmployeeSchedule) = scheduleDao.insertSchedule(schedule)
    suspend fun deleteSchedule(schedule: EmployeeSchedule) = scheduleDao.deleteSchedule(schedule)
    suspend fun insertShiftTemplate(template: ShiftTemplate) = scheduleDao.insertShiftTemplate(template)
    suspend fun deleteShiftTemplate(template: ShiftTemplate) = scheduleDao.deleteShiftTemplate(template)

    suspend fun repairEmployeeLinks() {
        val employees = employeeDao.getAllEmployees().first()
        employees.forEach { emp ->
            val empNo = emp.employeeNo ?: return@forEach
            val localId = emp.id
            
            // 1. If internal ID is UUID but records use EmpNo, fix them
            if (empNo != localId && empNo.isNotBlank()) {
                employeeDao.updateAttendanceEmployeeId(empNo, localId)
                employeeDao.updateDTREmployeeId(empNo, localId)
                employeeDao.updateRequestEmployeeId(empNo, localId)
            }
            
            // 2. Cross-check: If any records are still using "0" or empty, they might be orphaned
        }
    }
}
