package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Entity(
    tableName = "employees",
    indices = [Index(value = ["employeeNo"], unique = true)]
)
@Serializable
data class Employee(
    @PrimaryKey val id: String = "", 
    @SerialName("first_name") val firstName: String? = "",
    @SerialName("middle_name") val middleName: String? = "",
    @SerialName("last_name") val lastName: String? = "",
    @SerialName("email") val email: String? = "",
    @SerialName("phone_number") val phoneNumber: String? = "",
    @SerialName("department") val department: String? = "",
    @SerialName("position") val position: String? = "",
    @SerialName("profile_image_uri") val profileImageUri: String? = null,
    @SerialName("employee_no") val employeeNo: String? = "",
    @SerialName("mall_id_no") val mallIdNo: String? = "",
    @SerialName("mall_id_expiration_date") val mallIdExpirationDate: String? = "",
    @SerialName("location") val location: String? = "",
    @SerialName("date_hired") val dateHired: String? = "",
    @SerialName("birthday") val birthday: String? = "",
    @SerialName("cp_number") val cpNumber: String? = "",
    @SerialName("tin_number") val tinNumber: String? = "",
    @SerialName("sss") val sss: String? = "",
    @SerialName("phil_health") val philHealth: String? = "",
    @SerialName("pagibig") val pagibig: String? = "",
    @SerialName("bank") val bank: String? = "",
    @SerialName("bank_account_number") val bankAccountNumber: String? = "",
    @SerialName("late_offence_level") val lateOffenceLevel: Int? = 0,
    @SerialName("absent_offence_level") val absentOffenceLevel: Int? = 0,
    @SerialName("hampering_offence_level") val hamperingOffenceLevel: Int? = 0,
    @SerialName("custom_offences") val customOffences: List<CustomOffence>? = emptyList(),
    @SerialName("uniform_cap") val uniformCap: Int? = 0,
    @SerialName("uniform_apron") val uniformApron: Int? = 0,
    @SerialName("uniform_shirt") val uniformShirt: Int? = 0,
    @SerialName("uniform_pants") val uniformPants: Int? = 0,
    @SerialName("is_certified") val isCertified: Boolean? = false,
    @SerialName("certified_positions") val certifiedPositions: List<String>? = emptyList(),
    @SerialName("password_hash") val passwordHash: String? = null,
    @SerialName("is_admin") val isAdmin: Boolean? = false,
    @SerialName("is_resigned") val isResigned: Boolean? = false,
    @SerialName("resignation_date") val resignationDate: String? = null,
    @SerialName("marital_status") val maritalStatus: String? = "",
    @SerialName("emergency_contact_name") val emergencyContactName: String? = "",
    @SerialName("emergency_contact_relationship") val emergencyContactRelationship: String? = "",
    @SerialName("emergency_contact_phone") val emergencyContactPhone: String? = "",
    @SerialName("payroll_access_code") val payrollAccessCode: String? = "",
    @SerialName("payroll_username") val payrollUsername: String? = "",
    @SerialName("payroll_password") val payrollPassword: String? = "",
    @SerialName("health_id_expiration_date") val healthIdExpirationDate: String? = "",
    @SerialName("mall_id_status") val mallIdStatus: String? = null,
    @SerialName("portal_username") val portalUsername: String? = null,
    @SerialName("portal_password") val portalPassword: String? = null,
    @SerialName("row_color") val rowColor: Int? = null,
    @SerialName("font_color") val fontColor: Int? = null,
    @SerialName("is_hidden_from_scheduler") val isHiddenFromScheduler: Boolean = false,
    @SerialName("scheduler_position") val schedulerPosition: String? = null,
    @SerialName("scheduler_row_color") val schedulerRowColor: Int? = null,
    @SerialName("scheduler_font_color") val schedulerFontColor: Int? = null
)

@Serializable
data class CustomOffence(
    val name: String,
    val level: Int = 0
)
