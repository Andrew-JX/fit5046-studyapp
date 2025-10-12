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
        // 你的 DAO 的 upsert 实际是 @Insert(onConflict = REPLACE)
        // - id == null 时：插入新纪录，返回新 rowId
        // - id != null 且存在：REPLACE（底层 delete+insert），返回新 rowId（通常等于该 id）
        // - id != null 且不存在：插入新纪录，返回 rowId（通常等于该 id）
        return dao.upsert(s.toEntity())
    }

    override suspend fun deleteSubject(id: Long) {
        dao.delete(id)
    }
}
