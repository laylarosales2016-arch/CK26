package com.sam.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE (senderId = :userId AND receiverId = :otherId) OR (senderId = :otherId AND receiverId = :userId) ORDER BY timestamp ASC")
    fun getMessagesWith(userId: String, otherId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE receiverId = :userId AND isRead = 0")
    fun getUnreadMessages(userId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Update
    suspend fun updateMessage(message: ChatMessage)

    @Delete
    suspend fun deleteMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)

    @Query("UPDATE chat_messages SET isRead = 1 WHERE receiverId = :userId AND senderId = :senderId")
    suspend fun markAsRead(userId: String, senderId: String)

    @Query("SELECT * FROM chat_messages")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Query("DELETE FROM chat_messages WHERE senderId = :employeeId OR receiverId = :employeeId")
    suspend fun deleteAllMessagesForEmployee(employeeId: String)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()
}
