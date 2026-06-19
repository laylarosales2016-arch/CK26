package com.sam.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DADao {
    @Query("SELECT * FROM da_records ORDER BY employeeName ASC, dateReport ASC")
    fun getAllDA(): Flow<List<DARecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDA(record: DARecord)

    @Delete
    suspend fun deleteDA(record: DARecord)

    @Query("DELETE FROM da_records")
    suspend fun clearAll()
}
