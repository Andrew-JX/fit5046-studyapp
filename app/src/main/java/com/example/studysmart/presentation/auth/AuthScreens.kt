// app/src/main/java/com/example/studysmart/presentation/auth/AuthScreens.kt
package com.example.studysmart.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studysmart.util.calcStrength
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onNavigateToSignUp: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }


    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) {
            authViewModel.clearSuccess()
            onLoggedIn()
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !authState.isLoading  // 新增：加载时禁用
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
            enabled = !authState.isLoading,
            trailingIcon = {
                IconButton(onClick = { show = !show }) {
                    Icon(if (show) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                }
            }
        )

        if (authState.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = authState.error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(20.dp))

        // 调用 Firebase 认证
        Button(
            onClick = {
                authViewModel.clearError()
                authViewModel.signInWithEmail(email, pass)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !authState.isLoading && email.isNotBlank() && pass.isNotBlank()
        ) {
            // 加载动画
            if (authState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Sign in")
            }
        }

        // 保持：Demo 模式直接跳转
        TextButton(
            onClick = onLoggedIn,
            enabled = !authState.isLoading
        ) {
            Text("Continue as demo →")
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(enabled = !authState.isLoading) {
                    onNavigateToSignUp()
                }
            )
        }
    }
}

@Composable
fun SignUpScreen(
    onSignedUp: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    val strength = calcStrength(pass)


    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) {
            authViewModel.clearSuccess()
            onSignedUp()
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Create account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !authState.isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
            enabled = !authState.isLoading,
            trailingIcon = {
                IconButton(onClick = { show = !show }) {
                    Icon(if (show) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                }
            }
        )

        Text(
            "Password strength：${strength.label}",
            color = strength.color,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("Confirm password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
            enabled = !authState.isLoading
        )

        val mismatch = confirm.isNotEmpty() && confirm != pass
        if (mismatch) {
            Text(
                "The two passwords are inconsistent",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // 显示错误信息
        if (authState.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = authState.error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(20.dp))

        // 调用 Firebase 认证
        Button(
            onClick = {
                authViewModel.clearError()
                authViewModel.signUpWithEmail(email, pass)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !authState.isLoading && !mismatch && pass.isNotBlank() && email.isNotBlank()
        ) {
            // 新增：加载动画
            if (authState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Create account")
            }
        }

        // 保持：Skip 模式直接跳转
        TextButton(
            onClick = onSignedUp,
            enabled = !authState.isLoading
        ) {
            Text("Skip for now →")
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Login",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(enabled = !authState.isLoading) {
                    onNavigateToLogin()
                }
            )
        }
    }
}

