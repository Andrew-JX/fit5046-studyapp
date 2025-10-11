package com.example.studysmart.presentation.subject

import androidx.compose.ui.graphics.Color
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.presentation.util.toComposeColor

data class SubjectUi(
    val id: Long?,
    val name: String,
    val goalHours: Float,
    val startColorArgb: Int,               // 新增
    val endColorArgb: Int,                 // 新增
    val gradient: List<Color>              // 保留 gradient
)


fun Subject.asUi(): SubjectUi = SubjectUi(
    id = id,
    name = name,
    goalHours = goalHours,
    startColorArgb = startColorArgb,
    endColorArgb = endColorArgb,
    gradient = listOf(startColorArgb.toComposeColor(), endColorArgb.toComposeColor())
)

