package com.example.studysmart.presentation.planner

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studysmart.presentation.task.TaskEvent
import com.example.studysmart.presentation.task.TaskViewModel
import com.example.studysmart.util.Priority
import com.example.studysmart.util.changeMillisToDateString
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreateEditScreen(
    onDone: () -> Unit = {},                 // ✅ 兼容你原来导航里写的 onDone = { nav.popBackStack() }
    onCancel: () -> Unit = {},
    vm: TaskViewModel = hiltViewModel()      // ✅ 直接在屏幕里注入 ViewModel
) {
    // ---- State ----
    var title by rememberSaveable { mutableStateOf("") }
    var desc by rememberSaveable { mutableStateOf("") }

    // 今天 00:00
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

    // Subject（示例）
    val fakeSubjects = listOf(1L to "Math", 2L to "CS", 3L to "Chem")
    var subjectSheet by remember { mutableStateOf(false) }
    var chosenSubject: Pair<Long, String>? by rememberSaveable { mutableStateOf(fakeSubjects.first()) }

    // 校验日期
    val selectedDate = dateState.selectedDateMillis ?: today
    val dateInvalid = selectedDate < today

    // ✅ 监听保存事件：成功后回调 onDone()（让你的导航返回）
    LaunchedEffect(Unit) {
        vm.events.collect { e ->
            when (e) {
                is TaskEvent.Saved -> onDone()
                is TaskEvent.Error -> Log.e("Task", "Save failed: ${e.message}")
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Task", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            isError = title.isBlank(),
            supportingText = { if (title.isBlank()) Text("Title is required") }
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
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

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedPriority.title,
                onValueChange = {},
                readOnly = true,
                label = { Text("Choose") },
                modifier = Modifier
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
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
        // ✅ 关键改动：直接调 VM 写入 Room
        Button(
            onClick = {
                if (selectedDate < today) return@Button
                vm.saveNewTask(
                    title = title.trim(),
                    description = desc.trim(),
                    dueDateMillis = selectedDate,
                    priority = selectedPriority,
                    subjectId = chosenSubject?.first
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && !dateInvalid
        ) { Text("Save") }

        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }

    // ---- DatePicker ----
    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = { TextButton(onClick = { showPicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = dateState) }
    }

    // ---- Subject BottomSheet ----
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
