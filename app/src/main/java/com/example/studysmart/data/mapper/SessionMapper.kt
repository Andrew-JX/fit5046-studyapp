package com.example.studysmart.data.mapper

import com.example.studysmart.data.local.entity.SessionEntity
import com.example.studysmart.domain.model.Session

fun SessionEntity.toDomain(): Session = Session(
    id = id,
    subjectId = subjectId,
    dateMillis = dateMillis,
    durationMinutes = durationMinutes
)

fun Session.toEntity(): SessionEntity = SessionEntity(
    id = id,
    subjectId = subjectId,
    dateMillis = dateMillis,
    durationMinutes = durationMinutes
)
