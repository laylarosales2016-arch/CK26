package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Entity(tableName = "appraisal_records")
@Serializable
data class AppraisalRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val employeeId: String,
    val score: Double,
    val comments: String,
    val rating1: Int,
    val rating2: Int,
    val rating3: Int,
    val rating4: Int,
    val rating5: Int,
    val month: String,
    val quarter: Int,
    val year: Int,
    val createdAt: Long = System.currentTimeMillis()
)
