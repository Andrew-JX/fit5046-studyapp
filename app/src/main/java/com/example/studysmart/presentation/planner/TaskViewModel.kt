package com.example.studysmart.presentation.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.data.repo.TaskRepo
import com.example.studysmart.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repo: TaskRepo
) : ViewModel() {

    private val _events = Channel<TaskUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun save(ui: TaskUiState) = viewModelScope.launch {
        try {
            require(ui.title.isNotBlank()) { "Title is required" }
            val id = repo.upsertTask(
                Task(
                    id = null,
                    subjectId = ui.subjectId,
                    title = ui.title.trim(),
                    description = ui.description.trim(),
                    dueDateMillis = ui.dueDateMillis,
                    priority = ui.priority,
                    isCompleted = true,
                )
            )
            _events.send(TaskUiEvent.Saved(id))
        } catch (e: Exception) {
            _events.send(TaskUiEvent.Error(e.message ?: "Save failed"))
        }
    }
}
