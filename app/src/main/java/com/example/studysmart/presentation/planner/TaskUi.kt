package com.example.studysmart.presentation.planner
import com.example.studysmart.util.Priority

data class TaskUiState(
    val title: String = "",
    val description: String = "",
    val dueDateMillis: Long = System.currentTimeMillis(),
    val priority: Priority = Priority.MEDIUM,
    val subjectId: Long? = null
)

sealed interface TaskUiEvent {
    data class Saved(val id: Long): TaskUiEvent
    data class Error(val message: String): TaskUiEvent
}
