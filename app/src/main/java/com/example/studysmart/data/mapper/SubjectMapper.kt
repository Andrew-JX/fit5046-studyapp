package com.example.studysmart.data.mapper

import com.example.studysmart.data.local.entity.SubjectEntity
import com.example.studysmart.domain.model.Subject

fun SubjectEntity.toDomain(): Subject = Subject(
    id = id,
    name = name,
    goalHours = goalHours,
    startColorArgb = startColorArgb,
    endColorArgb = endColorArgb
)

fun Subject.toEntity(): SubjectEntity = SubjectEntity(
    id = id,
    name = name,
    goalHours = goalHours,
    startColorArgb = startColorArgb,
    endColorArgb = endColorArgb
)
