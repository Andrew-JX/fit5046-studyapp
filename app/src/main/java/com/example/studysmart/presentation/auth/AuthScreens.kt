package com.example.studysmart.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studysmart.util.calcStrength
import com.example.studysmart.util.isValidEmail
import com.example.studysmart.util.isValidPassword

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) {
            viewModel.clearSuccess()
            onLoggedIn()
        }
    }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; emailError = null },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it) } },
            enabled = !authState.isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it; passError = null },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { show = !show }) {
                    Icon(
                        if (show) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (show) "Hide" else "Show"
                    )
                }
            },
            isError = passError != null,
            supportingText = passError?.let { { Text(it) } },
            enabled = !authState.isLoading
        )

        if (authState.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = authState.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                var hasError = false
                if (email.isBlank()) {
                    emailError = "Email cannot be empty"
                    hasError = true
                } else if (!isValidEmail(email)) {
                    emailError = "Invalid email format"
                    hasError = true
                }
                if (pass.isBlank()) {
                    passError = "Password cannot be empty"
                    hasError = true
                }
                if (!hasError) {
                    viewModel.signInWithEmail(email, pass)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !authState.isLoading
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Sign in")
            }
        }

        TextButton(onClick = onLoggedIn, enabled = !authState.isLoading) {
            Text("Continue as demo →")
        }
    }
}

@Composable
fun SignUpScreen(
    onSignedUp: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }

    val strength = calcStrength(pass)
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) {
            viewModel.clearSuccess()
            onSignedUp()
        }
    }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; emailError = null },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it) } },
            enabled = !authState.isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it; passError = null },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { show = !show }) {
                    Icon(
                        if (show) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (show) "Hide" else "Show"
                    )
                }
            },
            isError = passError != null,
            supportingText = passError?.let { { Text(it) } },
            enabled = !authState.isLoading
        )

        if (pass.isNotBlank()) {
            Text(
                "Password strength: ${strength.label}",
                color = strength.color,
                style = MaterialTheme.typography.bodySmall
            )
        }

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

        if (authState.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = authState.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                var hasError = false
                if (email.isBlank()) {
                    emailError = "Email cannot be empty"
                    hasError = true
                } else if (!isValidEmail(email)) {
                    emailError = "Invalid email format"
                    hasError = true
                }
                if (pass.isBlank()) {
                    passError = "Password cannot be empty"
                    hasError = true
                } else if (!isValidPassword(pass)) {
                    passError = "Password must be at least 8 characters"
                    hasError = true
                }
                if (mismatch) hasError = true

                if (!hasError) {
                    viewModel.signUpWithEmail(email, pass)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !authState.isLoading && !mismatch && pass.isNotBlank() && email.isNotBlank()
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Create account")
            }
        }

        TextButton(onClick = onSignedUp, enabled = !authState.isLoading) {
            Text("Skip for now →")
        }
    }
}