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
import com.example.studysmart.presentation.components.FamousQuoteDisplay
import com.example.studysmart.util.calcStrength

@Composable
fun LoginScreen(onLoggedIn: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    val viewModel: AuthViewModel = viewModel()
    val quote by viewModel.quote.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchQuote()
    }



    Column(Modifier.fillMaxSize().padding(20.dp)) {

        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        if (quote != null) {
            FamousQuoteDisplay(quote!!)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = pass, onValueChange = { pass = it },
            label = { Text("Password") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { show = !show }) {
                    Icon(if (show) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                }
            }
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onLoggedIn, modifier = Modifier.fillMaxWidth()) { Text("Sign in") }
        TextButton(onClick = onLoggedIn) { Text("Continue as demo →") }
    }
}


@Composable
fun SignUpScreen(onSignedUp: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    val strength = calcStrength(pass)

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Create account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it },
            label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = pass, onValueChange = { pass = it },
            label = { Text("Password") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
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
            value = confirm, onValueChange = { confirm = it },
            label = { Text("Confirm password") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation()
        )
        val mismatch = confirm.isNotEmpty() && confirm != pass
        if (mismatch) Text("The two passwords are inconsistent", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onSignedUp, modifier = Modifier.fillMaxWidth(), enabled = !mismatch && pass.isNotBlank() && email.isNotBlank()) {
            Text("Create account")
        }
        TextButton(onClick = onSignedUp) { Text("Skip for now →") }
    }
}