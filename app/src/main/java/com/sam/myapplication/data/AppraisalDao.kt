package com.sam.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppraisalDao {
    @Query("SELECT * FROM appraisal_records WHERE employeeId = :employeeId ORDER BY createdAt DESC")
    fun getAppraisalsForEmployee(employeeId: String): Flow<List<AppraisalRecord>>

    @Query("SELECT * FROM appraisal_records ORDER BY createdAt DESC")
    fun getAllAppraisals(): Flow<List<AppraisalRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appraisal: AppraisalRecord)

    @Delete
    suspend fun delete(appraisal: AppraisalRecord)

    @Query("DELETE FROM appraisal_records WHERE employeeId = :employeeId")
    suspend fun deleteAllForEmployee(employeeId: String)

    @Query("DELETE FROM appraisal_records")
    suspend fun clearAll()
}
