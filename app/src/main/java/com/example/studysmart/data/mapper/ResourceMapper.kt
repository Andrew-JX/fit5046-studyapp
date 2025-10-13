package com.example.studysmart.data.mapper

import com.example.studysmart.data.local.entity.ResourceEntity
import com.example.studysmart.domain.model.Resource
import com.example.studysmart.domain.model.ResourceType

private fun List<String>.toCsv(): String =
    this.joinToString(",") { it.trim() }.trim(',')

private fun String.toTags(): List<String> =
    if (isBlank()) emptyList() else split(",").map { it.trim() }.filter { it.isNotEmpty() }

fun ResourceEntity.toDomain(): Resource =
    Resource(
        id = id,
        title = title,
        url = url,
        type = runCatching { ResourceType.valueOf(type) }.getOrDefault(ResourceType.OTHER),
        subjectId = subjectId,
        provider = provider,
        note = note,
        tags = tagsCsv.toTags(),
        isFavorite = isFavorite,
        createdAt = createdAt
    )

fun Resource.toEntity(): ResourceEntity =
    ResourceEntity(
        id = id,
        title = title,
        url = url,
        type = type.name,
        subjectId = subjectId,
        provider = provider,
        note = note,
        tagsCsv = tags.toCsv(),
        isFavorite = isFavorite,
        createdAt = createdAt
    )
