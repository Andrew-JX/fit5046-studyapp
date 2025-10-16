package com.example.studysmart.presentation.profile


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.data.datastore.UserPreferencesRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileState(
    val username: String = "",
    val userEmail: String = "",
    val focusLength: Int = 25,
    val breakLength: Int = 5,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isSaving: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    // Firebase Auth instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // DataStore Repository for local preferences
    private val preferencesRepository = UserPreferencesRepository(application)

    // UI State
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        loadUserProfile()
        loadPreferences()
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Get email from Firebase Auth
            val email = currentUser.email ?: ""
            // Generate a default username from email (or read from local storage)
            val defaultUsername = email.substringBefore("@")

            _profileState.value = _profileState.value.copy(
                userEmail = email,
                username = defaultUsername
            )

            // Load the saved username from the local DataStore
            viewModelScope.launch {
                preferencesRepository.usernameFlow.collect { savedUsername ->
                    if (savedUsername.isNotEmpty()) {
                        _profileState.value = _profileState.value.copy(username = savedUsername)
                    }
                }
            }
        }
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

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            try {
                _profileState.value = _profileState.value.copy(isLoading = true)

                // Save to local DataStore
                preferencesRepository.saveUsername(newUsername)

                _profileState.value = _profileState.value.copy(
                    username = newUsername,
                    isLoading = false,
                    successMessage = "Username updated successfully",
                    error = null
                )

                // 清除成功消息
                kotlinx.coroutines.delay(3000)
                _profileState.value = _profileState.value.copy(successMessage = null)

            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    error = "Failed to update username: ${e.message}"
                )
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser ?: return
        val email = user.email ?: return

        viewModelScope.launch {
            try {
                _profileState.value = _profileState.value.copy(isLoading = true)

                // Reauthenticate user
                val credential = EmailAuthProvider.getCredential(email, currentPassword)
                user.reauthenticate(credential).await()

                // Update password (this updates the password in Firebase Auth)
                user.updatePassword(newPassword).await()

                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    successMessage = "Password updated successfully",
                    error = null
                )

                // Clear Success Message
                kotlinx.coroutines.delay(3000)
                _profileState.value = _profileState.value.copy(successMessage = null)

            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    error = when {
                        e.message?.contains("password is invalid") == true ->
                            "Current password is incorrect"
                        e.message?.contains("network") == true ->
                            "Network error. Please check your connection"
                        else -> "Failed to update password: ${e.message}"
                    }
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
            auth.signOut()
        }
    }

    fun clearError() {
        _profileState.value = _profileState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _profileState.value = _profileState.value.copy(successMessage = null)
    }

}