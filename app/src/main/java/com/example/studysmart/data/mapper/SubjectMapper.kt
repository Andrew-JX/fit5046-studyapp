// data/mapper/SubjectMapper.kt
package com.example.studysmart.data.mapper

import com.example.studysmart.data.local.entity.SubjectEntity
import com.example.studysmart.domain.model.Subject

/**
 * Convert SubjectEntity to Domain Subject
 * Simply maps all fields including the ARGB color values
 */
fun SubjectEntity.toDomain(): Subject {
    return Subject(
        id = if (id == 0L) null else id,
        name = name,
        goalHours = goalHours,
        startColorArgb = startColorArgb,
        endColorArgb = endColorArgb
    )
}

/**
 * Convert Domain Subject to SubjectEntity
 * Maps all fields, using 0L for null IDs (Room will auto-generate)
 */
fun Subject.toEntity(): SubjectEntity {
    return SubjectEntity(
        id = id ?: 0L,
        name = name,
        goalHours = goalHours,
        startColorArgb = startColorArgb,
        endColorArgb = endColorArgb
    )
}