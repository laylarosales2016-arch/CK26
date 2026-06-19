package com.sam.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Entity(tableName = "chat_messages")
@Serializable
data class ChatMessage(
    @PrimaryKey val id: Long, // Use Supabase BigInt ID as primary key
    @SerialName("sender_id") val senderId: String,
    @SerialName("receiver_id") val receiverId: String,
    @SerialName("message") val message: String,
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis(),
    @SerialName("is_read") val isRead: Boolean = false
)
