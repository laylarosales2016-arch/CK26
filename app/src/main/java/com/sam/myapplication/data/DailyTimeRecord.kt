package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Entity(
    tableName = "daily_time_records",
    foreignKeys = [
        ForeignKey(
            entity = Employee::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["employeeId", "date"], unique = true)]
)
@Serializable
data class DailyTimeRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("employee_id") val employeeId: String,
    @SerialName("date") val date: String, // ISO string YYYY-MM-DD
    @SerialName("time_in") val timeIn: String? = null,
    @SerialName("time_out") val timeOut: String? = null,
    @SerialName("note") val note: String? = null,
    @SerialName("has_overtime") val hasOvertime: Boolean = false,
    @SerialName("overtime_approved_by") val overtimeApprovedBy: String? = null
)
