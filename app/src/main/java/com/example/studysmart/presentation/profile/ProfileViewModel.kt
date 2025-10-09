package com.example.studysmart.presentation.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.data.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
data class ProfileState(
    val focusLength: Int = 25,
    val breakLength: Int = 5,
    val isSaving: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    // DataStore Repository
    private val preferencesRepository = UserPreferencesRepository(application)

    // UI State
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        // Load saved preferences when ViewModel is created
        // ViewModel 创建时加载已保存的偏好设置
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            preferencesRepository.userPreferencesFlow.collect { preferences ->
                _profileState.value = _profileState.value.copy(
                    focusLength = preferences.focusLength,
                    breakLength = preferences.breakLength
                )
            }
        }
    }


    fun updateFocusLength(minutes: Int) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(
                focusLength = minutes,
                isSaving = true
            )
            preferencesRepository.saveFocusLength(minutes)
            _profileState.value = _profileState.value.copy(isSaving = false)
        }
    }


    fun updateBreakLength(minutes: Int) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(
                breakLength = minutes,
                isSaving = true
            )
            preferencesRepository.saveBreakLength(minutes)
            _profileState.value = _profileState.value.copy(isSaving = false)
        }
    }

    fun clearPreferences() {
        viewModelScope.launch {
            preferencesRepository.clearPreferences()
        }
    }
}

