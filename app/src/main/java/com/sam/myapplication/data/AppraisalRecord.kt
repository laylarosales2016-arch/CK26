package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.util.UUID

@Entity(tableName = "appraisal_records")
@Serializable
data class AppraisalRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @SerialName("employeeId") val employeeId: String,
    @SerialName("score") val score: Double,
    @SerialName("comments") val comments: String,
    @SerialName("rating1") val rating1: Int,
    @SerialName("rating2") val rating2: Int,
    @SerialName("rating3") val rating3: Int,
    @SerialName("rating4") val rating4: Int,
    @SerialName("rating5") val rating5: Int,
    @SerialName("month") val month: String,
    @SerialName("quarter") val quarter: Int,
    @SerialName("year") val year: Int,
    @SerialName("createdAt") val createdAt: Long = System.currentTimeMillis()
)
