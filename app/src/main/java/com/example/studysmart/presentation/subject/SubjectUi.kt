// presentation/subject/SubjectUi.kt
package com.example.studysmart.presentation.subject

import androidx.compose.ui.graphics.Color
import com.example.studysmart.domain.model.Subject

data class SubjectUi(
    val id: Long?,
    val name: String,
    val goalHours: Float,
    val startColorArgb: Int,
    val endColorArgb: Int,
    val gradient: List<Color>
)

fun Subject.asUi(): SubjectUi = SubjectUi(
    id = id,
    name = name,
    goalHours = goalHours,
    startColorArgb = startColorArgb,
    endColorArgb = endColorArgb,
    gradient = listOf(Color(startColorArgb), Color(endColorArgb))
)
