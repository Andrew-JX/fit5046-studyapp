package com.example.studysmart.domain.model

data class Session(
    val id: Long? = null,
    val subjectId: Long?,
    val dateMillis: Long,
    val durationMinutes: Int
)
