package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Entity(
    tableName = "announcements",
    indices = [Index(value = ["title", "createdAt"], unique = true)]
)
@Serializable
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("title") val title: String,
    @SerialName("content") val content: String,
    @SerialName("target_employee_id") val targetEmployeeId: String? = null, // null means all
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis(),
    @SerialName("is_read") val isRead: Boolean = false
)
