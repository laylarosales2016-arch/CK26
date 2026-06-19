package com.sam.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkPermitDao {
    @Query("SELECT * FROM work_permits ORDER BY lastUpdated DESC")
    fun getAllPermits(): Flow<List<WorkPermit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermit(permit: WorkPermit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermits(permits: List<WorkPermit>)

    @Query("SELECT * FROM work_permits WHERE referenceNumber = :ref")
    suspend fun getPermitByRef(ref: String): WorkPermit?

    @Query("DELETE FROM work_permits WHERE referenceNumber = :ref")
    suspend fun deletePermit(ref: String)

    @Update
    suspend fun updatePermit(permit: WorkPermit)

    @Query("UPDATE work_permits SET cachedHtml = :html WHERE referenceNumber = :ref")
    suspend fun updateCachedHtml(ref: String, html: String)

    @Query("UPDATE work_permits SET customLabel = :label WHERE referenceNumber = :ref")
    suspend fun updateLabel(ref: String, label: String)

    @Query("UPDATE work_permits SET status = :status WHERE referenceNumber = :ref")
    suspend fun updateStatus(ref: String, status: String)

    @Query("UPDATE work_permits SET isExpired = :expired WHERE referenceNumber = :ref")
    suspend fun updateExpiredStatus(ref: String, expired: Boolean)

    @Query("UPDATE work_permits SET cachedHtml = NULL")
    suspend fun clearCache()

    @Query("DELETE FROM work_permits")
    suspend fun clearAll()
}
