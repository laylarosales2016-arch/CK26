package com.sam.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AttritionDao {
    @Query("SELECT * FROM attrition_records ORDER BY lastDayOfDuty DESC")
    fun getAllAttrition(): Flow<List<AttritionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttrition(record: AttritionRecord)

    @Delete
    suspend fun deleteAttrition(record: AttritionRecord)

    @Query("DELETE FROM attrition_records")
    suspend fun clearAll()
}
