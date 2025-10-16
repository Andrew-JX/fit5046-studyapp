// app/src/main/java/com/example/studysmart/presentation/dashboard/DashboardViewModel.kt
package com.example.studysmart.presentation.dashboard

import android.content.Context
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
import androidx.compose.ui.graphics.toArgb
import com.example.studysmart.domain.model.AlarmItem
import com.example.studysmart.util.AlarmScheduler
import com.example.studysmart.util.changeMillisToDateString
import com.example.studysmart.work.SessionAlarmMannager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val subjectRepo: SubjectRepo,
    private val taskRepo: TaskRepo,
    private val sessionRepo: SessionRepo,
    @ApplicationContext private val context: Context

) : ViewModel() {

    // List data
    val subjects: StateFlow<List<Subject>> =
        subjectRepo.observeSubjects()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val tasks: StateFlow<List<Task>> =
        taskRepo.observeTasks(null) // If need to filter by subjectId, pass the id
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val sessions: StateFlow<List<SessionUi>> =
        sessionRepo.observeSessions()
            .combine(subjectRepo.observeSubjects()) { sessions, subjects ->
                sessions.map { it.asUi(subjects) }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    // Event channel
    private val _events = Channel<DashboardEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun saveSubject(ui: SubjectUiState) = viewModelScope.launch {
        try {
            require(ui.name.isNotBlank()) { "Subject name is required" }
            val goal = ui.goalHours.toFloatOrNull()
                ?: error("Goal hours must be a number")

            // Get colours
            val start = ui.colors.firstOrNull()?.toArgb() ?: 0xFF81E8FF.toInt()
            val end   = ui.colors.lastOrNull()?.toArgb()  ?: 0xFF4DB3FF.toInt()

            val id = subjectRepo.upsertSubject(
                Subject(
                    id = ui.id,
                    name = ui.name.trim(),
                    goalHours = goal,
                    startColorArgb = start,
                    endColorArgb = end
                )
            )
            _events.trySend(DashboardEvent.SubjectSaved(id))
        } catch (e: Exception) {
            _events.trySend(DashboardEvent.Error(e.message ?: "Save subject failed"))
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

    fun toggleTaskDone(id: Long) = viewModelScope.launch {
        try {
            val current = tasks.value.firstOrNull { it.id == id } ?: return@launch
            taskRepo.setCompleted(id, !current.isCompleted)
        } catch (e: Exception) {
            _events.send(DashboardEvent.Error(e.message ?: "Update task failed"))
        }
    }

    fun alarm() = viewModelScope.launch {

        val alarmScheduler: AlarmScheduler = SessionAlarmMannager(context = context)

        val todayString = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))

        val alarmItems = tasks.value
            .filter { task ->
                task.dueDateMillis.changeMillisToDateString() == todayString
            }
            .map { task ->
                val dueDateTime = Instant.ofEpochMilli(task.dueDateMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()

                AlarmItem(
                    time = dueDateTime,
                    message = "You have task Due Today：${task.title}"
                )
            }

        alarmItems.forEach { alarmItem ->
            alarmScheduler.schedule(alarmItem)
        }
    }
}
