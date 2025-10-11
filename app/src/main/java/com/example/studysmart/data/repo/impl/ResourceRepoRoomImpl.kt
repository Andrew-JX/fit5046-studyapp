package com.example.studysmart.data.repo.impl

import com.example.studysmart.data.local.dao.ResourceDao
import com.example.studysmart.data.mapper.toDomain
import com.example.studysmart.data.mapper.toEntity
import com.example.studysmart.data.repo.ResourceRepo
import com.example.studysmart.domain.model.Resource
import com.example.studysmart.domain.model.ResourceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceRepoRoomImpl @Inject constructor(
    private val dao: ResourceDao
) : ResourceRepo {

    override fun observeResources(
        subjectId: Long?,
        type: ResourceType?,
        query: String?
    ): Flow<List<Resource>> =
        dao.observeResources(subjectId, type?.name, query).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun get(id: Long): Resource? = dao.get(id)?.toDomain()

    override suspend fun upsert(r: Resource): Long = dao.upsert(r.toEntity())

    override suspend fun delete(id: Long) = dao.delete(id)

    override suspend fun toggleFavorite(id: Long) = dao.toggleFavorite(id)
}
