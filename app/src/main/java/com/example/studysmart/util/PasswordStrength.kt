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
/**
 * 验证邮箱格式
 */
fun isValidEmail(email: String): Boolean {
    if (email.isBlank()) return false
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return email.matches(emailRegex)
}

/**
 * 验证密码格式
 * 要求：至少8个字符
 */
fun isValidPassword(password: String): Boolean {
    return password.length >= 8
}