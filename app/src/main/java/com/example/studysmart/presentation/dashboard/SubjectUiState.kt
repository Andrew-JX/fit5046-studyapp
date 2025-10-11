// app/src/main/java/com/example/studysmart/presentation/dashboard/SubjectUiState.kt
package com.example.studysmart.presentation.dashboard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.presentation.subject.SubjectUi

data class SubjectUiState(
    val id: Long? = null,
    val name: String = "",
    val goalHours: String = "",
    val colors: List<Color> = emptyList()
)

fun Subject.asUiState(): SubjectUiState = SubjectUiState(
    id = subjectId,
    name = name,
    goalHours = goalHours.toString(),
    colors = colors
)
