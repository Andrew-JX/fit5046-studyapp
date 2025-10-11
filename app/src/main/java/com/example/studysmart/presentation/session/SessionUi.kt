package com.example.studysmart.presentation.session

import com.example.studysmart.domain.model.Session
import com.example.studysmart.domain.model.Subject

data class SessionUi(
    val id: Long?,
    val subjectId: Long?,
    val subjectName: String,
    val dateMillis: Long,
    val durationMinutes: Int
)

fun Session.asUi(subjects: List<Subject>): SessionUi {
    val name = subjects.firstOrNull { it.id == subjectId }?.name ?: "Unknown"
    return SessionUi(
        id = id,
        subjectId = subjectId,
        subjectName = name,
        dateMillis = dateMillis,
        durationMinutes = durationMinutes
    )
}