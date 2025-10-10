// app/src/main/java/com/example/studysmart/presentation/task/TaskScreen.kt
package com.example.studysmart.presentation.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studysmart.data.repo.subjects // 先用假数据弹出学科底单
import com.example.studysmart.domain.model.Task
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.SubjectListBottomSheet
import com.example.studysmart.presentation.components.TaskCheckBox
import com.example.studysmart.presentation.components.TaskDatePicker
import com.example.studysmart.presentation.components.tasksList
import com.example.studysmart.presentation.theme.Red
import com.example.studysmart.util.Priority
import com.example.studysmart.util.changeMillisToDateString
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    vm: TaskViewModel = hiltViewModel()
) {
    // ===== VM data (list Flow→State)=====
    val tasks by vm.tasks.collectAsState()

    // ===== Form Status =====
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by rememberSaveable { mutableStateOf(Priority.MEDIUM) }

    var dueDate by rememberSaveable { mutableStateOf<Long?>(Instant.now().toEpochMilli()) }
    var isDatePickerDialogOpen by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate ?: Instant.now().toEpochMilli()
    )

    // Subject selection BottomSheet       ( Now using fake data)
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    var chosenSubjectId by rememberSaveable { mutableStateOf<Long?>(null) }
    var chosenSubjectName by rememberSaveable { mutableStateOf("English") } // 默认显示

    // 删除对话框（当前页只演示“新建”，按钮占位）
    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

    // check
    val taskTitleError = when {
        title.isBlank() -> "Please enter task title."
        title.length < 4 -> "Task title is too short."
        title.length > 30 -> "Task title is too long."
        else -> null
    }

    // Snackbar(Save the result)
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        vm.events.collect { e ->
            when (e) {
                is TaskEvent.Saved -> {
                    snackbarHostState.showSnackbar("Saved #${e.id}")
                    // 清表单（保留优先级与学科）
                    title = ""
                    description = ""
                    dueDate = Instant.now().toEpochMilli()
                }
                is TaskEvent.Error -> snackbarHostState.showSnackbar(e.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TaskScreenTopBar(
                isTaskExist = false, // 新建页：隐藏右上角“删除/勾选”
                isComplete = false,
                checkBoxBorderColor = Red,
                onBackButtonClick = { /* TODO: navController?.popBackStack() */ },
                onDeleteButtonClick = { isDeleteDialogOpen = true },
                onCheckBoxClick = { /* noop */ }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        // 只保留一个 LazyColumn，避免纵向滚动嵌套
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
            // ---------- The form as an item ----------
            item {
                Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text("Create Task", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        singleLine = true,
                        isError = taskTitleError != null && title.isNotBlank(),
                        supportingText = { Text(taskTitleError.orEmpty()) }
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") }
                    )

                    Spacer(Modifier.height(20.dp))

                    Text("Due date", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(6.dp))

                    ListItem(
                        leadingContent = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        headlineContent = {
                            Text((dueDate ?: Instant.now().toEpochMilli()).changeMillisToDateString())
                        },
                        supportingContent = {
                            Text(if (dueDate == null) "No due date" else "Tap to change")
                        },
                        trailingContent = {
                            Row {
                                if (dueDate != null) {
                                    IconButton(onClick = { dueDate = null }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                                IconButton(onClick = { isDatePickerDialogOpen = true }) {
                                    Icon(Icons.Default.EditCalendar, contentDescription = "Pick date")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { isDatePickerDialogOpen = true }
                            .padding(horizontal = 4.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    Text("Priority", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(10.dp))
                    PriorityBar(
                        selected = selectedPriority,
                        onSelected = { selectedPriority = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    Text("Related to subject", style = MaterialTheme.typography.bodySmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(chosenSubjectName, style = MaterialTheme.typography.bodyLarge)
                        IconButton(onClick = { isBottomSheetOpen = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Subject")
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Button(
                        enabled = taskTitleError == null && title.isNotBlank(),
                        onClick = {
                            val due = dueDate ?: Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                            }.timeInMillis

                            vm.saveNewTask(
                                title = title,
                                description = description,
                                dueDateMillis = due,
                                priority = selectedPriority,
                                subjectId = chosenSubjectId
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text("Save")
                    }
                }
            }

            // ---------- Task list ----------
            tasksList(
                sectionTitle = "ALL",
                emptyListText = "No tasks yet.",
                tasks = tasks,
                onTaskCardClick = { /* TODO: open details */ },
                onCheckBoxClick = { t: Task ->
                    val id = t.id ?: return@tasksList
                    vm.toggleCompleted(id, !t.isCompleted)
                }
            )
            // Your existing tasksList will insert a section title item by itself
        }
    }

    // ===== Dialog/popup (not in a scroll container) =====
    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Task?",
        bodyText = "Are you sure, you want to delete this task? This action can not be undone.",
        onDismissRequest = { isDeleteDialogOpen = false },
        onConfirmButtonClick = { isDeleteDialogOpen = false }
    )

    TaskDatePicker(
        state = datePickerState,
        isOpen = isDatePickerDialogOpen,
        onDismissRequest = { isDatePickerDialogOpen = false },
        onConfirmButtonClicked = {
            dueDate = datePickerState.selectedDateMillis
            isDatePickerDialogOpen = false
        }
    )

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = subjects,
        onDismissRequest = { isBottomSheetOpen = false },
        onSubjectClicked = { sub ->
            chosenSubjectId = sub.subjectId?.toLong() ?: sub.subjectId?.toLong()
            chosenSubjectName = sub.name
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreenTopBar(
    isTaskExist: Boolean,
    isComplete: Boolean,
    checkBoxBorderColor: Color,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onCheckBoxClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        title = { Text("Task", style = MaterialTheme.typography.titleLarge) },
        actions = {
            if (isTaskExist) {
                TaskCheckBox(
                    isComplete = isComplete,
                    borderColor = checkBoxBorderColor,
                    onCheckBoxClick = onCheckBoxClick
                )
                IconButton(onClick = onDeleteButtonClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Task")
                }
            }
        }
    )
}

@Composable
private fun PriorityBar(
    selected: Priority,
    onSelected: (Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Priority.entries.forEach { p ->
            val isSelected = selected == p
            val bg = if (isSelected) p.color else p.color.copy(alpha = 0.15f)
            val fg = if (isSelected) Color.White else p.color
            val border = if (isSelected) null else BorderStroke(1.dp, p.color.copy(alpha = 0.5f))

            Surface(
                color = bg,
                contentColor = fg,
                shape = RoundedCornerShape(10.dp),
                tonalElevation = if (isSelected) 2.dp else 0.dp,
                border = border,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .clickable { onSelected(p) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = p.title,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
