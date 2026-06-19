package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class RequestStatus {
    PENDING, GRANTED, REJECTED
}

@Serializable
enum class RequestType {
    LATE, ABSENT, CUSTOM, ATTENDANCE_CORRECTION, PROFILE_UPDATE, OVERBREAK
}

@Entity(tableName = "requests")
@Serializable
data class AttendanceRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("employee_id") val employeeId: String,
    @SerialName("date") val date: String,
    @SerialName("type") val type: RequestType,
    @SerialName("status") val status: RequestStatus = RequestStatus.PENDING,
    @SerialName("note") val note: String? = null,
    @SerialName("admin_note") val adminNote: String? = null,
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis()
)
