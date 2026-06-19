package com.sam.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY createdAt DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Query("SELECT * FROM announcements WHERE targetEmployeeId IS NULL OR targetEmployeeId = :employeeId ORDER BY createdAt DESC")
    fun getAnnouncementsForEmployee(employeeId: String): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement): Long

    @Update
    suspend fun updateAnnouncement(announcement: Announcement)

    @Delete
    suspend fun deleteAnnouncement(announcement: Announcement)

    @Query("UPDATE announcements SET isRead = 1 WHERE id = :announcementId")
    suspend fun markAsRead(announcementId: Int)

    @Query("DELETE FROM announcements")
    suspend fun clearAll()
}
