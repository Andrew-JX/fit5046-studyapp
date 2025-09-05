// app/src/main/java/com/example/studysmart/presentation/onboarding/OnboardingScreen.kt
package com.example.studysmart.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val subjects = listOf("English","Maths","Physics","CS","Art")
    val difficulties = listOf("Beginner","Intermediate","Advanced")

    var subj by remember { mutableStateOf(subjects.first()) }
    var expanded1 by remember { mutableStateOf(false) }

    var diff by remember { mutableStateOf(difficulties.first()) }
    var expanded2 by remember { mutableStateOf(false) }

    var goalHours by remember { mutableStateOf("10") }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Welcome 👋", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text("Basic Preferences", style = MaterialTheme.typography.titleMedium)

        Text("Major", style = MaterialTheme.typography.bodySmall)
        ExposedDropdownMenuBox(expanded = expanded1, onExpandedChange = { expanded1 = !expanded1 }) {
            OutlinedTextField(
                value = subj, onValueChange = {}, readOnly = true,
                label = { Text("Choose subject") }, modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded1, onDismissRequest = { expanded1 = false }) {
                subjects.forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { subj = it; expanded1 = false })
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        Text("Difficulty", style = MaterialTheme.typography.bodySmall)
        ExposedDropdownMenuBox(expanded = expanded2, onExpandedChange = { expanded2 = !expanded2 }) {
            OutlinedTextField(
                value = diff, onValueChange = {}, readOnly = true,
                label = { Text("Choose level") }, modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded2, onDismissRequest = { expanded2 = false }) {
                difficulties.forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { diff = it; expanded2 = false })
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = goalHours, onValueChange = { goalHours = it.filter(Char::isDigit) },
            label = { Text("Weekly target duration (hours)") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) { Text("Start") }
    }
}
