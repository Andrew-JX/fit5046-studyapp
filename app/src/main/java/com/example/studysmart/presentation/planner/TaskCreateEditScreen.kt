// app/src/main/java/com/example/studysmart/presentation/planner/TaskCreateEditScreen.kt
package com.example.studysmart.presentation.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.studysmart.util.Priority
import com.example.studysmart.util.changeMillisToDateString
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreateEditScreen(onDone: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    // DatePicker
    val today = Calendar.getInstance().timeInMillis
    val dateState = rememberDatePickerState(initialSelectedDateMillis = today)
    var showPicker by remember { mutableStateOf(false) }

    // Priority Dropdown
    val options = Priority.entries
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(Priority.MEDIUM) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Task", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        // Form Rule #2: Use a date picker, not manual input
        OutlinedTextField(
            value = dateState.selectedDateMillis.changeMillisToDateString(),
            onValueChange = {}, readOnly = true, label = { Text("Due date") },
            trailingIcon = { TextButton(onClick = { showPicker = true }) { Text("Pick") } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        Text("Priority", style = MaterialTheme.typography.bodySmall)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selected.title, onValueChange = {}, readOnly = true,
                label = { Text("Choose") }, modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach {
                    DropdownMenuItem(
                        text = { Text(it.title) },
                        onClick = { selected = it; expanded = false },
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
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank()) { Text("Save") }
    }

    if (showPicker) {
        DatePickerDialog(onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = { showPicker = false }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = dateState) }
    }
}
