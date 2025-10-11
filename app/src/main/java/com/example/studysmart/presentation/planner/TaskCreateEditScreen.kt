package com.example.studysmart.presentation.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog

import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

import com.example.studysmart.util.Priority
import com.example.studysmart.util.changeMillisToDateString
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreateEditScreen(
    onDone: (TaskUiState) -> Unit,
    onCancel: () -> Unit = {}
) {
    // ---- State ----
    var title by rememberSaveable { mutableStateOf("") }
    var desc by rememberSaveable { mutableStateOf("") }

    // 00:00 today (for verifying past dates)
    val today = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val dateState = rememberDatePickerState(initialSelectedDateMillis = today)
    var showPicker by remember { mutableStateOf(false) }

    // Priority
    val options = Priority.values()
    var expanded by remember { mutableStateOf(false) }
    var selectedPriority by rememberSaveable { mutableStateOf(Priority.MEDIUM) }

    // Subject BottomSheet
    val fakeSubjects = listOf(1L to "Math", 2L to "CS", 3L to "Chem")
    var subjectSheet by remember { mutableStateOf(false) }
    var chosenSubject: Pair<Long, String>? by rememberSaveable { mutableStateOf(fakeSubjects.first()) }

    // Is the date illegal (past dates)?
    val selectedDate = dateState.selectedDateMillis ?: today
    val dateInvalid = selectedDate < today

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Task", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            isError = title.isBlank(),
            supportingText = {
                if (title.isBlank()) Text("Title is required")
            }
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        // Date (read-only + selector)
        OutlinedTextField(
            value = selectedDate.changeMillisToDateString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Due date") },
            trailingIcon = { TextButton(onClick = { showPicker = true }) { Text("Pick") } },
            modifier = Modifier.fillMaxWidth(),
            isError = dateInvalid,
            supportingText = { if (dateInvalid) Text("Date cannot be in the past") }
        )

        Spacer(Modifier.height(12.dp))
        // Subject selection
        OutlinedTextField(
            value = chosenSubject?.second ?: "Select subject",
            onValueChange = {},
            readOnly = true,
            label = { Text("Subject") },
            trailingIcon = { TextButton(onClick = { subjectSheet = true }) { Text("Choose") } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        Text("Priority", style = MaterialTheme.typography.bodySmall)

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedPriority.title,
                onValueChange = {},
                readOnly = true,
                label = { Text("Choose") },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach {
                    DropdownMenuItem(
                        text = { Text(it.title) },
                        onClick = { selectedPriority = it; expanded = false },
                        trailingIcon = {
                            Box(
                                Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(it.color)
                            )
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                // Perform a past date check again before saving (double insurance)
                if (selectedDate < today) return@Button
                onDone(
                    TaskUiState(
                        title = title.trim(),
                        description = desc.trim(),
                        dueDateMillis = selectedDate,
                        priority = selectedPriority,
                        subjectId = chosenSubject?.first
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && !dateInvalid
        ) { Text("Save") }

        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }

    // ---- Show Layer ----
    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = { TextButton(onClick = { showPicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = dateState) }
    }

    if (subjectSheet) {
        ModalBottomSheet(onDismissRequest = { subjectSheet = false }) {
            fakeSubjects.forEach { (id, name) ->

                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            chosenSubject = id to name
                            subjectSheet = false
                        }
                        .padding(16.dp)
                ) { Text(name) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
