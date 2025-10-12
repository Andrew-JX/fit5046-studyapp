// app/src/main/java/com/example/studysmart/presentation/dashboard/DashboardViewModel.kt
package com.example.studysmart.presentation.dashboard

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.domain.model.Session
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.domain.model.Task
import com.example.studysmart.data.repo.SessionRepo
import com.example.studysmart.data.repo.SubjectRepo
import com.example.studysmart.data.repo.TaskRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.studysmart.presentation.session.SessionUi
import com.example.studysmart.presentation.session.asUi


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val subjectRepo: SubjectRepo,
    private val taskRepo: TaskRepo,
    private val sessionRepo: SessionRepo
) : ViewModel() {

    // 列表数据 —— 和你的 TaskViewModel 一致，用 Flow 暴露 + 在 UI 层 collect
    val subjects: StateFlow<List<Subject>> =
        subjectRepo.observeSubjects()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val tasks: StateFlow<List<Task>> =
        taskRepo.observeTasks(null) // 如需按 subjectId 过滤，传 id
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val sessions: StateFlow<List<SessionUi>> =
        sessionRepo.observeSessions()
            .combine(subjectRepo.observeSubjects()) { sessions, subjects ->
                sessions.map { it.asUi(subjects) }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    // 事件通道 —— 和 TaskViewModel 同款
    private val _events = Channel<DashboardEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // 保存/更新 Subject（与 TaskViewModel.save 一样的 try/catch + Channel 通知）
    fun saveSubject(ui: SubjectUiState) = viewModelScope.launch {
        try {
            require(ui.name.isNotBlank()) { "Subject name is required" }
            require(ui.goalHours.toFloatOrNull() != null) { "Goal hours must be a number" }

            val id = subjectRepo.upsertSubject(
                Subject(
                    id = ui.id?: 0L,
                    name = ui.name.trim(),
                    goalHours = ui.goalHours.toFloat(),   // 按你的模型改类型
                    startColorArgb = ui.colors.firstOrNull()?.value?.toInt() ?: 0xFF81E8FF.toInt(),
                    endColorArgb = ui.colors.lastOrNull()?.value?.toInt() ?: 0xFF4DB3FF.toInt()
                )
            )
            _events.send(DashboardEvent.SubjectSaved(id))
        } catch (e: Exception) {
            _events.send(DashboardEvent.Error(e.message ?: "Save subject failed"))
        }
    }

    fun deleteSession(id: Long) = viewModelScope.launch {
        try {
            sessionRepo.deleteSession(id)
            _events.send(DashboardEvent.SessionDeleted(id))
        } catch (e: Exception) {
            _events.send(DashboardEvent.Error(e.message ?: "Delete session failed"))
        }
    }
}
