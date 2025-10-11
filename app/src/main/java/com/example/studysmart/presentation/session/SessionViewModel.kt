package com.example.studysmart.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.data.repo.SessionRepo
import com.example.studysmart.data.repo.SubjectRepo
import com.example.studysmart.domain.model.Session
import com.example.studysmart.domain.model.Subject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

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
    private val sessionRepo: SessionRepo,
    private val subjectRepo: SubjectRepo
) : ViewModel() {

    // ====== 学科与筛选 ======
    /** 用于底部选择学科的列表（Domain，方便直接喂给你们现有的 BottomSheet） */
    val subjects: StateFlow<List<Subject>> =
        subjectRepo.observeSubjects()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** 当前筛选（历史列表用），null 表示全部 */
    private val selectedFilterSubjectId = MutableStateFlow<Long?>(null)
    fun setFilterSubject(id: Long?) { selectedFilterSubjectId.value = id }

    // ====== 历史记录：SessionUi 列表（合并 Subject 得到 subjectName） ======
    val sessionsUi: StateFlow<List<SessionUi>> =
        selectedFilterSubjectId
            .flatMapLatest { sid -> sessionRepo.observeSessions(sid) }
            .combine(subjectRepo.observeSubjects()) { sessions, subs ->
                sessions.map { it.asUi(subs) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ====== 计时器状态 ======
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _elapsedMillis = MutableStateFlow(0L)
    val elapsedMillis: StateFlow<Long> = _elapsedMillis

    /** 本次学习所选学科（用于保存会话） */
    private val currentSubjectId = MutableStateFlow<Long?>(null)
    fun selectCurrentSubject(id: Long?) { currentSubjectId.value = id }

    private var timerJob: Job? = null

    // ====== 事件通道（SnackBar/Toast） ======
    private val _events = Channel<SessionEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // ====== 计时控制 ======
    fun startTimer() {
        if (_isRunning.value) return
        _isRunning.value = true
        timerJob = viewModelScope.launch {
            _events.send(SessionEvent.TimerStarted)
            // 精度：1 秒累加
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
    }

    fun cancelTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        timerJob = null
        _elapsedMillis.value = 0L
        viewModelScope.launch { _events.send(SessionEvent.TimerCanceled) }
    }

    fun finishAndSave() = viewModelScope.launch {
        try {
            val sid = currentSubjectId.value
            require(sid != null) { "Please choose a subject before finishing." }

            // 修改这里：转换为 Int
            val mins = max(1, _elapsedMillis.value / 60_000L).toInt()
            val id = sessionRepo.upsertSession(
                Session(
                    id = null,
                    subjectId = sid,
                    dateMillis = System.currentTimeMillis(),
                    durationMinutes = mins
                )
            )

            // Reset timer
            _isRunning.value = false
            timerJob?.cancel()
            timerJob = null
            _elapsedMillis.value = 0L

            _events.send(SessionEvent.Saved(id))
            _events.send(SessionEvent.TimerFinished)
        } catch (e: Exception) {
            _events.send(SessionEvent.Error(e.message ?: "Save failed"))
        }
    }

    // ====== 删除会话 ======
    fun deleteSession(id: Long) = viewModelScope.launch {
        try {
            sessionRepo.deleteSession(id)
            _events.send(SessionEvent.Deleted)
        } catch (e: Exception) {
            _events.send(SessionEvent.Error(e.message ?: "Delete failed"))
        }
    }
}