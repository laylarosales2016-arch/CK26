package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Entity(tableName = "employee_schedules", primaryKeys = ["employeeId", "date"])
@Serializable
data class EmployeeSchedule(
    @SerialName("employee_id") val employeeId: String,
    val date: String, // YYYY-MM-DD
    @SerialName("schedule_text") val scheduleText: String? = null,
    val tag: String? = null, // RD, RRD, SICK, NS
    val color: Int? = null,
    @SerialName("font_color") val fontColor: Int? = null,
    val position: String? = null
)

@Entity(tableName = "shift_templates")
@Serializable
data class ShiftTemplate(
    @PrimaryKey @SerialName("time_range") val timeRange: String, // e.g., "11-10", "9-5"
    val color: Int? = null,
    @SerialName("font_color") val fontColor: Int? = null,
    val position: String? = null
)
