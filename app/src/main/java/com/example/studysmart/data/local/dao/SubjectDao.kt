// data/local/dao/SubjectDao.kt
package com.example.studysmart.data.local.dao

import androidx.room.*
import com.example.studysmart.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun observeSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects ORDER BY name ASC")
    suspend fun listSubjects(): List<SubjectEntity>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun get(id: Long): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(e: SubjectEntity): Long

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun delete(id: Long)
}