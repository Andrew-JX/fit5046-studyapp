// app/src/main/java/com/example/studysmart/presentation/dashboard/SubjectUiState.kt
package com.example.studysmart.presentation.dashboard

import androidx.compose.ui.graphics.Color

data class SubjectUiState(
    val id: Long? = null,
    val name: String = "",
    val goalHours: String = "",
    val colors: List<Color> = emptyList()
)
