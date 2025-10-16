// app/src/main/java/com/example/studysmart/presentation/session/SessionViewModel.kt
package com.example.studysmart.presentation.session

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.data.repo.SessionRepo
import com.example.studysmart.data.repo.SubjectRepo
import com.example.studysmart.domain.model.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.max
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.studysmart.work.StudySessionWorker

sealed interface SessionEvent {
    data class Saved(val id: Long): SessionEvent
    data object Deleted: SessionEvent
    data object TimerStarted: SessionEvent
    data object TimerCanceled: SessionEvent
    data object TimerFinished: SessionEvent
    data class Error(val message: String): SessionEvent
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val sessionRepo: SessionRepo,
    private val subjectRepo: SubjectRepo
) : ViewModel() {



    // ====== Subjects ======
    val subjects: StateFlow<List<com.example.studysmart.domain.model.Subject>> =
        subjectRepo.observeSubjects()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val selectedFilterSubjectId = MutableStateFlow<Long?>(null)
    fun setFilterSubject(id: Long?) { selectedFilterSubjectId.value = id }

    // ====== Sessions (UI) ======
    val sessionsUi: StateFlow<List<SessionUi>> =
        selectedFilterSubjectId
            .flatMapLatest { sid -> sessionRepo.observeSessions(sid) }
            .combine(subjectRepo.observeSubjects()) { sessions, subs ->
                sessions.map { it.asUi(subs) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ====== Timer state ======
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _elapsedMillis = MutableStateFlow(0L)
    val elapsedMillis: StateFlow<Long> = _elapsedMillis

    private val currentSubjectId = MutableStateFlow<Long?>(null)
    fun selectCurrentSubject(id: Long?) { currentSubjectId.value = id }

    private var timerJob: Job? = null

    private val _events = Channel<SessionEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // ====== WorkManager  ======
    private fun startBackgroundTrackingIfPossible() {
        val sid = currentSubjectId.value
        if (sid == null) {
            viewModelScope.launch { _events.send(SessionEvent.Error("Please choose a subject first")) }
            return
        }
        StudySessionWorker.startTracking(appContext, subjectId = sid, chunkMinutes = 1)//auto record time
    }

    private fun stopBackgroundTrackingIfPossible() {
        currentSubjectId.value?.let { sid ->
            StudySessionWorker.stopTracking(appContext, subjectId = sid)
        }
    }

    // ====== Timer control ======
    fun startTimer() {
        if (_isRunning.value) return
        startBackgroundTrackingIfPossible()
        _isRunning.value = true
        timerJob = viewModelScope.launch {
            _events.send(SessionEvent.TimerStarted)
            while (_isRunning.value) {
                kotlinx.coroutines.delay(1000L)
                _elapsedMillis.value += 1000L
            }
        }
    }

    fun pauseTimer() {
        if (!_isRunning.value) return
        _isRunning.value = false
        timerJob?.cancel()
        timerJob = null
        // stopBackgroundTrackingIfPossible()
    }

    fun cancelTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        timerJob = null
        _elapsedMillis.value = 0L
        stopBackgroundTrackingIfPossible()      //stops the background when canceled
        viewModelScope.launch { _events.send(SessionEvent.TimerCanceled) }
    }

    fun finishAndSave() = viewModelScope.launch {
        try {
            val sid = currentSubjectId.value
            require(sid != null) { "Please choose a subject before finishing." }

            val mins = max(1, _elapsedMillis.value / 60_000L).toInt()
            val id = sessionRepo.upsertSession(
                Session(
                    id = null,
                    subjectId = sid,
                    dateMillis = System.currentTimeMillis(),
                    durationMinutes = mins
                )
            )

            _isRunning.value = false
            timerJob?.cancel()
            timerJob = null
            _elapsedMillis.value = 0L
            stopBackgroundTrackingIfPossible()  // stops the background when ended

            _events.send(SessionEvent.Saved(id))
            _events.send(SessionEvent.TimerFinished)
        } catch (e: Exception) {
            _events.send(SessionEvent.Error(e.message ?: "Save failed"))
        }
    }

    fun deleteSession(id: Long) = viewModelScope.launch {
        try {
            sessionRepo.deleteSession(id)
            _events.send(SessionEvent.Deleted)
        } catch (e: Exception) {
            _events.send(SessionEvent.Error(e.message ?: "Delete failed"))
        }
    }
}
