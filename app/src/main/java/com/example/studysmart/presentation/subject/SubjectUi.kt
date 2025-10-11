package com.example.studysmart.presentation.subject

import androidx.compose.ui.graphics.Color
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.presentation.util.toComposeColor

data class SubjectUi(
    val id: Long?,
    val name: String,
    val goalHours: Float,
    // Use directly for SubjectCard/Chip
    val gradient: List<Color>
)

fun Subject.asUi(): SubjectUi = SubjectUi(
    id = id,
    name = name,
    goalHours = goalHours,
    gradient = listOf(startColorArgb.toComposeColor(), endColorArgb.toComposeColor())
)
