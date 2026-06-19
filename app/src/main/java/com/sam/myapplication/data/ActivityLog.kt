package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("employee_id") val employeeId: String,
    @SerialName("action") val action: String,
    @SerialName("details") val details: String? = null,
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis()
)
