package com.example.studysmart.data.repo.impl

import com.example.studysmart.data.local.dao.SessionDao
import com.example.studysmart.data.mapper.toDomain
import com.example.studysmart.data.mapper.toEntity
import com.example.studysmart.data.repo.SessionRepo
import com.example.studysmart.domain.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepoRoomImpl @Inject constructor(
    private val dao: SessionDao
) : SessionRepo {

    override fun observeSessions(subjectId: Long?): Flow<List<Session>> =
        dao.observeSessions(subjectId).map { it.map { e -> e.toDomain() } }

    override suspend fun getSession(id: Long): Session? =
        dao.get(id)?.toDomain()

    override suspend fun upsertSession(s: Session): Long =
        dao.upsert(s.toEntity())

    override suspend fun deleteSession(id: Long) {
        dao.delete(id)
    }
}
