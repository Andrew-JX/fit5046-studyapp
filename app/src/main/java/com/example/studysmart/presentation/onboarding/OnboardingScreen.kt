package com.example.studysmart.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    vm: OnboardingViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()
    val scope = rememberCoroutineScope()

    var major by remember { mutableStateOf(vm.majors.first()) }
    var expMajor by remember { mutableStateOf(false) }

    var diff by remember { mutableStateOf(vm.difficulties.first()) }
    var expDiff by remember { mutableStateOf(false) }

    var goalHours by remember { mutableStateOf("10") }

    // 监听保存成功事件 → 导航到 Dashboard
    LaunchedEffect(Unit) {
        vm.events.collect { e ->
            when (e) {
                is OnboardingEvent.Saved -> onFinish()
                is OnboardingEvent.Error -> { /* 这里已在 UI 显示错误 */ }
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Welcome 👋", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text("Basic Preferences", style = MaterialTheme.typography.titleMedium)

        // Major
        Text("Major", style = MaterialTheme.typography.bodySmall)
        ExposedDropdownMenuBox(expanded = expMajor, onExpandedChange = { expMajor = !expMajor }) {
            OutlinedTextField(
                value = major, onValueChange = {}, readOnly = true,
                label = { Text("Choose subject") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expMajor, onDismissRequest = { expMajor = false }) {
                vm.majors.forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { major = it; expMajor = false })
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Difficulty
        Text("Difficulty", style = MaterialTheme.typography.bodySmall)
        ExposedDropdownMenuBox(expanded = expDiff, onExpandedChange = { expDiff = !expDiff }) {
            OutlinedTextField(
                value = diff, onValueChange = {}, readOnly = true,
                label = { Text("Choose level") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expDiff, onDismissRequest = { expDiff = false }) {
                vm.difficulties.forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { diff = it; expDiff = false })
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = goalHours,
            onValueChange = { goalHours = it.filter(Char::isDigit) },
            label = { Text("Weekly target duration (hours)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // 错误信息
        ui.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { vm.completeOnboarding(major, diff, goalHours) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !ui.isSaving && goalHours.isNotBlank()
        ) {
            if (ui.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Start")
            }
        }
    }
}
