package com.example.studysmart.presentation.util

import androidx.compose.ui.graphics.Color

// Safely convert ARGB Int to Compose Color (avoiding sign bit issues)
fun Int.toComposeColor(): Color =
    Color((this.toLong() and 0xFFFFFFFF).toULong())
