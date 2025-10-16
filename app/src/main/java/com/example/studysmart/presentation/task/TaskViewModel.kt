// app/src/main/java/com/example/studysmart/presentation/task/TaskViewModel.kt
package com.example.studysmart.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.data.repo.TaskRepo
import com.example.studysmart.domain.model.Task
import com.example.studysmart.util.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TaskEvent {
    data class Saved(val id: Long) : TaskEvent
    data class Error(val message: String) : TaskEvent
}

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repo: TaskRepo
) : ViewModel() {

    // Task List (Room Flow → StateFlow)
    val tasks = repo.observeTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    // Single event: save success/failure
    private val _events = Channel<TaskEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    /** Create or update a task (here for creating a new one) */
    fun saveNewTask(
        title: String,
        description: String,
        dueDateMillis: Long,
        priority: Priority,
        subjectId: Long?
    ) = viewModelScope.launch {
        try {
            require(title.isNotBlank()) { "Title is required" }
            val id = repo.upsertTask(
                Task(
                    id = null,
                    subjectId = subjectId,
                    title = title.trim(),
                    description = description.trim(),
                    dueDateMillis = dueDateMillis,
                    priority = priority,
                    isCompleted = false
                )
            )
            _events.send(TaskEvent.Saved(id))
        } catch (e: Exception) {
            _events.send(TaskEvent.Error(e.message ?: "Save failed"))
        }
    }

    fun toggleCompleted(taskId: Long, nowCompleted: Boolean) = viewModelScope.launch {
        repo.setCompleted(taskId, nowCompleted)
    }
}