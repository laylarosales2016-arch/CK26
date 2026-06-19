package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@Entity(tableName = "attrition_records")
data class AttritionRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("employee_no") val employeeNo: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("middle_name") val middleName: String,
    @SerialName("location") val location: String = "CK SM CLARK L1 PMAG",
    @SerialName("last_day_of_duty") val lastDayOfDuty: String,
    @SerialName("reason_for_separation") val reasonForSeparation: String,
    @SerialName("position") val position: String,
    @SerialName("date_hired") val dateHired: String = "",
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis()
)
