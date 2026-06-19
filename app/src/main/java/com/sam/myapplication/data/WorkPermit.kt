package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@Entity(tableName = "work_permits")
data class WorkPermit(
    @PrimaryKey @SerialName("reference_number") val referenceNumber: String,
    @SerialName("status") val status: String,
    @SerialName("date") val date: String? = null,
    @SerialName("tradename") val tradename: String? = null,
    @SerialName("custom_label") val customLabel: String? = null,
    @SerialName("detail_url") val detailUrl: String? = null,
    @SerialName("cached_html") val cachedHtml: String? = null,
    @SerialName("is_approved") val isApproved: Boolean = false,
    @SerialName("is_expired") val isExpired: Boolean = false,
    @SerialName("last_updated") val lastUpdated: Long = System.currentTimeMillis()
)
