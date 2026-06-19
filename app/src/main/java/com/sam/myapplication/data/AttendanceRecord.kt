package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class AttendanceStatus {
    IR, ABSENT, LATE, OVERBREAK, RTW, RD
}

@Entity(
    tableName = "attendance_records",
    foreignKeys = [
        ForeignKey(
            entity = Employee::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["employeeId", "date", "status"], unique = true)]
)
@Serializable
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("employee_id") val employeeId: String,
    val date: String, // Storing as ISO string YYYY-MM-DD
    val status: AttendanceStatus,
    val note: String? = null
)
