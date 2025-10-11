package com.example.studysmart.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val profileState by viewModel.profileState.collectAsState()

    // UI states
    var isEditingUsername by remember { mutableStateOf(false) }
    var tempUsername by remember { mutableStateOf("") }

    var isChangingPassword by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // User Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Account Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email (read-only)
                OutlinedTextField(
                    value = profileState.userEmail,
                    onValueChange = {},
                    label = { Text("Email") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Username (editable)
                if (isEditingUsername) {
                    OutlinedTextField(
                        value = tempUsername,
                        onValueChange = { tempUsername = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = tempUsername.length > 20,
                        supportingText = {
                            Text("${tempUsername.length}/20 characters")
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                tempUsername = profileState.username
                                isEditingUsername = false
                            }
                        ) {
                            Text("Cancel")
                        }

                        TextButton(
                            onClick = {
                                viewModel.updateUsername(tempUsername)
                                isEditingUsername = false
                            },
                            enabled = tempUsername.isNotBlank() && tempUsername.length <= 20
                        ) {
                            Text("Save")
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = profileState.username,
                        onValueChange = {},
                        label = { Text("Username") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = {
                                tempUsername = profileState.username
                                isEditingUsername = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password Change Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Security",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    TextButton(onClick = {
                        isChangingPassword = !isChangingPassword
                        if (!isChangingPassword) {
                            currentPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                        }
                    }) {
                        Text(if (isChangingPassword) "Cancel" else "Change Password")
                    }
                }

                if (isChangingPassword) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Current Password
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (showCurrentPassword)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                                Icon(
                                    if (showCurrentPassword) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = if (showCurrentPassword) "Hide" else "Show"
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // New Password
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (showNewPassword)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showNewPassword = !showNewPassword }) {
                                Icon(
                                    if (showNewPassword) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = if (showNewPassword) "Hide" else "Show"
                                )
                            }
                        },
                        isError = newPassword.isNotEmpty() && newPassword.length < 8,
                        supportingText = {
                            if (newPassword.isNotEmpty() && newPassword.length < 8) {
                                Text("Password must be at least 8 characters")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm New Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = confirmPassword.isNotEmpty() && confirmPassword != newPassword,
                        supportingText = {
                            if (confirmPassword.isNotEmpty() && confirmPassword != newPassword) {
                                Text("Passwords do not match")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.changePassword(currentPassword, newPassword)
                            isChangingPassword = false
                            currentPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = currentPassword.isNotEmpty() &&
                                newPassword.length >= 8 &&
                                newPassword == confirmPassword
                    ) {
                        Text("Update Password")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Study Settings Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Study Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Focus Length
                Text(
                    text = "Focus length",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${profileState.focusLength} minutes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Slider(
                    value = profileState.focusLength.toFloat(),
                    onValueChange = { viewModel.updateFocusLength(it.toInt()) },
                    valueRange = 15f..60f,
                    steps = 44,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Break Length
                Text(
                    text = "Break length",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${profileState.breakLength} minutes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Slider(
                    value = profileState.breakLength.toFloat(),
                    onValueChange = { viewModel.updateBreakLength(it.toInt()) },
                    valueRange = 5f..20f,
                    steps = 14,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Status messages
        profileState.error?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        profileState.successMessage?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        if (profileState.isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(16.dp))

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