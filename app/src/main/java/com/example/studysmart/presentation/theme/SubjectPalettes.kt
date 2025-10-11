package com.example.studysmart.presentation.theme

import androidx.compose.ui.graphics.Color

object SubjectPalettes {
    fun fromArgb(startColorArgb: Any, endColorArgb: Any): List<Color>? {

    }

    // 每个条目是一组用于渐变的颜色（上->下）
    val options: List<List<Color>> = listOf(
        listOf(Color(0xFF81E8FF), Color(0xFF4DB3FF)), // 蓝青
        listOf(Color(0xFFFFE08A), Color(0xFFFFB84D)), // 橙黄
        listOf(Color(0xFFFFA1C9), Color(0xFFFD6F8E)), // 粉
        listOf(Color(0xFFA8FFB3), Color(0xFF53E68C)), // 绿
        listOf(Color(0xFFD1C2FF), Color(0xFF9D8CFF))  // 紫
    )
}
