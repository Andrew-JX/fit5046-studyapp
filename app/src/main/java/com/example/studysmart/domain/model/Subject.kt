package com.example.studysmart.domain.model

data class Subject(
    val id: Long? = null,
    val name: String,
    val goalHours: Float,
    val startColorArgb: Int,
    val endColorArgb: Int
)
