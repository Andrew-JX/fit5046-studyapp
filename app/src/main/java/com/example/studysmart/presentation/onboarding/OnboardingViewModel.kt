package com.example.studysmart.presentation.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.data.datastore.UserPreferencesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed interface OnboardingEvent {
    data object Saved : OnboardingEvent
    data class Error(val message: String) : OnboardingEvent
}

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UserPreferencesRepository(application)

    private val _ui = MutableStateFlow(OnboardingUiState())
    val ui: StateFlow<OnboardingUiState> = _ui.asStateFlow()

    private val _events = Channel<OnboardingEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // 供 UI 使用的选项
    val majors = listOf("English", "Maths", "Physics", "CS", "Art")
    val difficulties = listOf("Beginner", "Intermediate", "Advanced")

    fun completeOnboarding(selectedMajor: String, selectedDifficulty: String, weeklyHoursStr: String) {
        val hours = weeklyHoursStr.toIntOrNull() ?: 10
        viewModelScope.launch {
            try {
                _ui.update { it.copy(isSaving = true, error = null) }
                repo.saveStudyProfile(selectedMajor, selectedDifficulty)
                repo.saveWeeklyTargetHours(hours)
                repo.setHasSeenOnboarding(true)
                _ui.update { it.copy(isSaving = false) }
                _events.send(OnboardingEvent.Saved)
            } catch (e: Exception) {
                _ui.update { it.copy(isSaving = false, error = e.message ?: "Save failed") }
                _events.send(OnboardingEvent.Error(e.message ?: "Save failed"))
            }
        }
    }
}