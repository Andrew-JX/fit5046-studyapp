package com.example.studysmart.presentation.theme

import androidx.compose.ui.graphics.Color

data class ColorSet(val start: Color, val end: Color)

object SubjectPalettes {

    // 所有配色方案
    val options: List<ColorSet> = listOf(
        ColorSet(Color(0xFF81E8FF), Color(0xFF4DB3FF)), // 蓝青
        ColorSet(Color(0xFFFFE08A), Color(0xFFFFBB4D)), // 橙黄
        ColorSet(Color(0xFFFFA1C9), Color(0xFFFF6D8E)), // 粉
        ColorSet(Color(0xFF8AFFB3), Color(0xFF53E68C)), // 绿
        ColorSet(Color(0xFFD1C2FF), Color(0xFF9D8CFF))  // 紫
    )

    // 通过 ARGB 值反查配色方案
    fun fromArgb(startColorArgb: Int, endColorArgb: Int): ColorSet? {
        return options.find {
            it.start.value.toInt() == startColorArgb && it.end.value.toInt() == endColorArgb
        }
    }
}
