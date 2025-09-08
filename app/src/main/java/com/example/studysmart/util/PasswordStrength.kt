// app/src/main/java/com/example/studysmart/util/PasswordStrength.kt
package com.example.studysmart.util

import androidx.compose.ui.graphics.Color
import com.example.studysmart.presentation.theme.Green
import com.example.studysmart.presentation.theme.Orange
import com.example.studysmart.presentation.theme.Red

enum class Strength(val label: String, val color: Color) { WEAK("Weak", Red), MEDIUM("Medium", Orange), STRONG("Strong", Green) }

fun calcStrength(pw: String): Strength {
    var s = 0
    if (pw.length >= 8) s++
    if (pw.any(Char::isDigit) && pw.any(Char::isLetter)) s++
    if (pw.any { !it.isLetterOrDigit() }) s++
    return when (s) {
        0,1 -> Strength.WEAK
        2 -> Strength.MEDIUM
        else -> Strength.STRONG
    }
}
