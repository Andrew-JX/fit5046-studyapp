// data/repo/impl/SubjectRepositoryImpl.kt
package com.example.studysmart.data.repo.impl

import com.example.studysmart.data.local.dao.SubjectDao
import com.example.studysmart.data.mapper.toDomain
import com.example.studysmart.data.mapper.toEntity
import com.example.studysmart.data.repo.SubjectRepo
import com.example.studysmart.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepoRoomImpl @Inject constructor(
    private val dao: SubjectDao
) : SubjectRepo {

    override fun observeSubjects(): Flow<List<Subject>> =
        dao.observeSubjects().map { list -> list.map { it.toDomain() } }

    override suspend fun getSubject(id: Long): Subject? =
        dao.get(id)?.toDomain()

    override suspend fun upsertSubject(s: Subject): Long {
        // DAO upsert is actually @Insert(onConflict = REPLACE)
        // - When id == null: insert a new record and return the new rowId
        // - When id != null and exists: REPLACE (underlying delete+insert), return the new rowId (usually equal to the id)
        // - When id != null and does not exist: insert a new record and return the rowId (usually equal to the id)
        return dao.upsert(s.toEntity())
    }

    override suspend fun deleteSubject(id: Long) {
        dao.delete(id)
    }
}
