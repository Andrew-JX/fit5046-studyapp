// data/local/dao/SessionDao.kt
package com.example.studysmart.data.local.dao

import androidx.room.*
import com.example.studysmart.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE :subjectId IS NULL OR subjectId = :subjectId ORDER BY dateMillis DESC")
    fun observeSessions(subjectId: Long?): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun get(id: Long): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(e: SessionEntity): Long

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun delete(id: Long)
}