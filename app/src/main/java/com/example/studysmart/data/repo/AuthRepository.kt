package com.example.studysmart.data.repo

import kotlinx.coroutines.flow.Flow

//firebase
interface AuthRepository {
    val isLoggedIn: Flow<Boolean>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signOut()
}
