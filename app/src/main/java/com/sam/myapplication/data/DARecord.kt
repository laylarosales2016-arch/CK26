package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@Entity(tableName = "da_records")
data class DARecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("employee_id") val employeeId: String,
    @SerialName("employee_name") val employeeName: String,
    @SerialName("date_report") val dateReport: String? = null,
    @SerialName("da_type") val daType: String,
    @SerialName("date_awol") val dateAwol: String? = null,
    @SerialName("status") val status: String,
    @SerialName("verdict") val verdict: String? = null,
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis()
)
