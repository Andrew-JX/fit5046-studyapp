package com.example.studysmart.data.repo

import com.example.studysmart.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepo {
    fun observeSessions(subjectId: Long? = null): Flow<List<Session>>
    suspend fun getSession(id: Long): Session?
    suspend fun upsertSession(s: Session): Long
    suspend fun deleteSession(id: Long)
}
