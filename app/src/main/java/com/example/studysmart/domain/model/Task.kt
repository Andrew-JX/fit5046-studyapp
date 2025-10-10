package com.example.studysmart.domain.model
import com.example.studysmart.util.Priority

data class Task(
    val id: Long? = null,
    val subjectId: Long? = null,
    val title: String,
    val description: String,
    val dueDateMillis: Long,
    val priority: Priority,
    val isCompleted: Boolean,
)
