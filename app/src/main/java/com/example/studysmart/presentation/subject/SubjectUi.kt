package com.example.studysmart.presentation.subject

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.studysmart.domain.model.Subject


data class SubjectUi(
    val id: Long?,
    val name: String,
    val goalHours: Float,
    val startColorArgb: Int,               // 新增
    val endColorArgb: Int,                 // 新增
    val gradient: List<Color>              // 保留 gradient
)


fun Subject.asUi(): SubjectUi = SubjectUi(
    id = subjectId,
    name = name,
    goalHours = goalHours,
    startColorArgb = colors.firstOrNull()!!.toArgb(),
    endColorArgb = colors.lastOrNull()!!.toArgb(),
    gradient = colors
)

