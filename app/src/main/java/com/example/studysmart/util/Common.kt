package com.example.studysmart.util

import androidx.compose.ui.graphics.Color
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class Priority(val title: String, val color: Color, val value: Int) {
    LOW("Low",    Color(0xFF2E7D32), 0),  // 绿 800：不紧急
    MEDIUM("Medium", Color(0xFFFF8F00), 1), // 橙 800：一般
    HIGH("High",  Color(0xFFB71C1C), 2); // 红 900：紧急

    companion object {
        fun fromInt(value: Int) = values().firstOrNull { it.value == value } ?: MEDIUM
    }
}

fun Long?.changeMillisToDateString(): String {
    val date: LocalDate = this?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    } ?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
}
