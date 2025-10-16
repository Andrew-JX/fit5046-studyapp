package com.example.studysmart.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.data.repo.FamousQuotesRepository
import com.example.studysmart.data.repo.impl.FamousQuotesRetrofit
import com.example.studysmart.domain.model.FamousQuote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: FamousQuotesRepository
) :  ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        _authState.value = _authState.value.copy(
            currentUser = auth.currentUser
        )
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(
                    isLoading = true,
                    error = null
                )

                val result = auth.signInWithEmailAndPassword(email, password).await()

                _authState.value = _authState.value.copy(
                    isLoading = false,
                    currentUser = result.user,
                    isSuccess = true,
                    error = null
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed",
                    isSuccess = false
                )
            }
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(
                    isLoading = true,
                    error = null
                )

                val result = auth.createUserWithEmailAndPassword(email, password).await()

                _authState.value = _authState.value.copy(
                    isLoading = false,
                    currentUser = result.user,
                    isSuccess = true,
                    error = null
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Registration failed",
                    isSuccess = false
                )
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState()
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }

    fun clearSuccess() {
        _authState.value = _authState.value.copy(isSuccess = false)
    }



        private val _quote = MutableStateFlow<FamousQuote?>(null)
        val quote: StateFlow<FamousQuote?> = _quote

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading

        private val _error = MutableStateFlow<String?>(null)
        val error: StateFlow<String?> = _error




    fun fetchQuote() {
            viewModelScope.launch {
                _isLoading.value = true
                _error.value = null
                try {
                    val result = repository.getQuote()
                    _quote.value = result
                } catch (e: Exception) {
                    _error.value = "无法获取名言：${e.localizedMessage ?: "未知错误"}"
                } finally {
                    _isLoading.value = false
                }
            }
        }


}