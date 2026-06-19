package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Entity(tableName = "daily_summary_notes")
@Serializable
data class DailySummaryNote(
    @PrimaryKey val date: String, // YYYY-MM-DD
    @SerialName("note") val note: String? = null
)
