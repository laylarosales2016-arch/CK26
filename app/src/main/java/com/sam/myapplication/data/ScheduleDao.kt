package com.sam.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM employee_schedules")
    fun getAllSchedules(): Flow<List<EmployeeSchedule>>

    @Query("SELECT * FROM employee_schedules")
    suspend fun getAllSchedulesSync(): List<EmployeeSchedule>

    @Query("SELECT * FROM employee_schedules WHERE date BETWEEN :startDate AND :endDate")
    fun getSchedulesInRange(startDate: String, endDate: String): Flow<List<EmployeeSchedule>>

    @Query("SELECT * FROM employee_schedules WHERE employeeId = :employeeId AND date BETWEEN :startDate AND :endDate")
    fun getSchedulesInRange(employeeId: String, startDate: String, endDate: String): Flow<List<EmployeeSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: EmployeeSchedule)

    @Delete
    suspend fun deleteSchedule(schedule: EmployeeSchedule)

    @Query("SELECT * FROM shift_templates")
    fun getAllShiftTemplates(): Flow<List<ShiftTemplate>>

    @Query("SELECT * FROM shift_templates")
    suspend fun getAllShiftTemplatesSync(): List<ShiftTemplate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShiftTemplate(template: ShiftTemplate)

    @Delete
    suspend fun deleteShiftTemplate(template: ShiftTemplate)

    @Query("DELETE FROM employee_schedules")
    suspend fun clearAllSchedules()

    @Query("DELETE FROM shift_templates")
    suspend fun clearAllShiftTemplates()
}
