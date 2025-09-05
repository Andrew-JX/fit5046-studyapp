// app/src/main/java/com/example/studysmart/presentation/profile/ProfileScreen.kt
package com.example.studysmart.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    var focusLen by remember { mutableStateOf(25) }
    var breakLen by remember { mutableStateOf(5) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text("Focus length (min)")
        Slider(value = focusLen.toFloat(), onValueChange = { focusLen = it.toInt() }, valueRange = 15f..60f)
        Text("Break length (min)")
        Slider(value = breakLen.toFloat(), onValueChange = { breakLen = it.toInt() }, valueRange = 5f..20f)
        Spacer(Modifier.height(20.dp))
        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Log out") }
    }
}
