package com.example.studysmart.data.repo

import com.example.studysmart.domain.model.Resource
import com.example.studysmart.domain.model.ResourceType
import kotlinx.coroutines.flow.Flow

interface ResourceRepo {
    fun observeResources(
        subjectId: Long? = null,
        type: ResourceType? = null,
        query: String? = null
    ): Flow<List<Resource>>

    suspend fun get(id: Long): Resource?
    suspend fun upsert(r: Resource): Long
    suspend fun delete(id: Long)
    suspend fun toggleFavorite(id: Long)
}
