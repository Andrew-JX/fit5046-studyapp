package com.example.studysmart.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    // Observe profile state
    val profileState by viewModel.profileState.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        // Focus Length Section
        Text(
            text = "Focus length",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "${profileState.focusLength} minutes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Slider(
            value = profileState.focusLength.toFloat(),
            onValueChange = { viewModel.updateFocusLength(it.toInt()) },
            valueRange = 15f..60f,
            steps = 44, // 15 to 60, step by 1
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Break Length Section
        Text(
            text = "Break length",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "${profileState.breakLength} minutes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Slider(
            value = profileState.breakLength.toFloat(),
            onValueChange = { viewModel.updateBreakLength(it.toInt()) },
            valueRange = 5f..20f,
            steps = 14, // 5 to 20, step by 1
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        // Saving indicator
        if (profileState.isSaving) {
            Text(
                text = "Saving...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.weight(1f))

        // Logout Button
        OutlinedButton(
            onClick = {
                viewModel.clearPreferences()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log out")
        }
    }
}