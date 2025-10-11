package com.example.studysmart.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.data.prefs.UserPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val major: String = "",
    val difficulty: String = "",
    val weeklyTargetHours: Int = 10,
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed interface OnboardingEvent {
    data object Done : OnboardingEvent
    data class Error(val message: String) : OnboardingEvent
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: UserPrefs
) : ViewModel() {

    // 是否已经看过引导页（用于决定是否跳过）
    val hasSeen = prefs.hasSeenOnboarding
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // UI 状态
    private val _ui = MutableStateFlow(OnboardingUiState())
    val ui: StateFlow<OnboardingUiState> = _ui.asStateFlow()

    // 事件（保存成功/失败 -> 屏幕导航或 Snackbar）
    private val _events = Channel<OnboardingEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // ==== 更新器 ====
    fun setMajor(v: String)        { _ui.update { it.copy(major = v) } }
    fun setDifficulty(v: String)   { _ui.update { it.copy(difficulty = v) } }
    fun setWeeklyTargetHours(v: Int) {
        _ui.update { it.copy(weeklyTargetHours = v.coerceAtLeast(0)) }
    }

    // ==== 完成引导：写入 DataStore 并发事件 ====
    fun completeOnboarding() = viewModelScope.launch {
        val s = _ui.value
        _ui.update { it.copy(isSaving = true, error = null) }
        try {
            // 这些方法在 UserPrefs.kt 里会补充（见下方补丁）
            prefs.setStudyProfile(major = s.major, difficulty = s.difficulty)
            prefs.setWeeklyTargetHours(s.weeklyTargetHours)
            prefs.setHasSeenOnboarding(true)

            _events.send(OnboardingEvent.Done)
        } catch (e: Exception) {
            val msg = e.message ?: "Save failed"
            _ui.update { it.copy(isSaving = false, error = msg) }
            _events.send(OnboardingEvent.Error(msg))
            return@launch
        }
        _ui.update { it.copy(isSaving = false) }
    }
}
