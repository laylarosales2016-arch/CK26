package com.sam.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees")
    fun getAllEmployees(): Flow<List<Employee>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee): Long

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getEmployeeById(id: String): Employee?

    @Query("SELECT * FROM employees WHERE employeeNo = :employeeNo")
    suspend fun getEmployeeByNo(employeeNo: String): Employee?

    @Query("SELECT * FROM attendance_records WHERE date = :date")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord)

    @Query("SELECT * FROM attendance_records WHERE employeeId = :employeeId")
    fun getAttendanceForEmployee(employeeId: String): Flow<List<AttendanceRecord>>

    @Query("DELETE FROM attendance_records WHERE employeeId = :employeeId AND date = :date")
    suspend fun deleteAttendance(employeeId: String, date: String)

    @Query("UPDATE attendance_records SET note = :note WHERE employeeId = :employeeId AND date = :date")
    suspend fun updateAttendanceNote(employeeId: String, date: String, note: String?)

    @Query("DELETE FROM attendance_records WHERE employeeId = :employeeId")
    suspend fun deleteAllAttendanceForEmployee(employeeId: String)

    @Query("DELETE FROM daily_time_records WHERE employeeId = :employeeId")
    suspend fun deleteAllDTRForEmployee(employeeId: String)

    @Query("DELETE FROM requests WHERE employeeId = :employeeId")
    suspend fun deleteAllRequestsForEmployee(employeeId: String)

    @Query("SELECT * FROM attendance_records")
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecord>>

    // Requests
    @Query("SELECT * FROM requests")
    fun getAllRequests(): Flow<List<AttendanceRequest>>

    @Query("SELECT * FROM requests WHERE employeeId = :employeeId")
    fun getRequestsForEmployee(employeeId: String): Flow<List<AttendanceRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: AttendanceRequest): Long

    @Update
    suspend fun updateRequest(request: AttendanceRequest)

    @Delete
    suspend fun deleteRequest(request: AttendanceRequest)

    @Query("SELECT * FROM requests WHERE id = :id")
    suspend fun getRequestById(id: Int): AttendanceRequest?

    // Daily Time Records
    @Query("SELECT * FROM daily_time_records")
    fun getAllDTRRecords(): Flow<List<DailyTimeRecord>>

    @Query("SELECT * FROM daily_time_records WHERE employeeId = :employeeId")
    fun getDTRForEmployee(employeeId: String): Flow<List<DailyTimeRecord>>

    @Query("SELECT * FROM daily_time_records WHERE employeeId = :employeeId AND date = :date")
    fun getDTRForEmployeeByDate(employeeId: String, date: String): Flow<DailyTimeRecord?>

    @Query("SELECT * FROM daily_time_records WHERE date = :date")
    fun getDTRByDate(date: String): Flow<List<DailyTimeRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDTR(record: DailyTimeRecord)

    @Query("DELETE FROM daily_time_records WHERE employeeId = :employeeId AND date = :date")
    suspend fun deleteDTR(employeeId: String, date: String)

    @Update
    suspend fun updateDTR(record: DailyTimeRecord)

    @Query("SELECT * FROM daily_time_records WHERE employeeId = :employeeId AND date = :date")
    suspend fun getDTR(employeeId: String, date: String): DailyTimeRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyNote(note: DailySummaryNote)

    @Query("SELECT * FROM daily_summary_notes WHERE date = :date")
    fun getDailyNote(date: String): Flow<DailySummaryNote?>
    
    @Query("SELECT * FROM daily_summary_notes")
    fun getAllDailyNotes(): Flow<List<DailySummaryNote>>

    @Query("UPDATE attendance_records SET employeeId = :newId WHERE employeeId = :oldId")
    suspend fun updateAttendanceEmployeeId(oldId: String, newId: String)

    @Query("UPDATE daily_time_records SET employeeId = :newId WHERE employeeId = :oldId")
    suspend fun updateDTREmployeeId(oldId: String, newId: String)

    @Query("UPDATE attendance_records SET employeeId = :newId WHERE employeeId = :oldId")
    suspend fun updateRequestEmployeeId(oldId: String, newId: String)

    @Query("DELETE FROM employees")
    suspend fun clearAllEmployees()

    @Query("DELETE FROM attendance_records")
    suspend fun clearAllAttendance()

    @Query("DELETE FROM daily_time_records")
    suspend fun clearAllDTR()

    @Query("DELETE FROM requests")
    suspend fun clearAllRequests()

    @Query("DELETE FROM daily_summary_notes")
    suspend fun clearAllDailyNotes()
}
