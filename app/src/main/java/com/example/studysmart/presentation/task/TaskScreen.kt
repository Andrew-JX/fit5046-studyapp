package com.example.studysmart.presentation.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.SubjectListBottomSheet
import com.example.studysmart.presentation.components.TaskCheckBox
import com.example.studysmart.presentation.components.TaskDatePicker
import com.example.studysmart.presentation.theme.Red
import com.example.studysmart.subjects
import com.example.studysmart.util.Priority
import com.example.studysmart.util.changeMillisToDateString
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen() {
    // ====== UI 状态 ======
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by rememberSaveable { mutableStateOf(Priority.MEDIUM) }

    // 截止日期：我们自己维护（允许为 null 代表“无截止日期”）
    var dueDate by rememberSaveable { mutableStateOf<Long?>(Instant.now().toEpochMilli()) }
    var isDatePickerDialogOpen by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate ?: Instant.now().toEpochMilli()
    )

    // 选择科目的 BottomSheet
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    // 删除任务对话框
    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

    // 校验
    val taskTitleError = when {
        title.isBlank() -> "Please enter task title."
        title.length < 4 -> "Task title is too short."
        title.length > 30 -> "Task title is too long."
        else -> null
    }

    // ====== 对话框们 ======
    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Task?",
        bodyText = "Are you sure, you want to delete this task? This action can not be undone.",
        onDismissRequest = { isDeleteDialogOpen = false },
        onConfirmButtonClick = { isDeleteDialogOpen = false }
    )

    // 日期选择器（点确定只改 dueDate，不碰 selectedDateMillis）
    TaskDatePicker(
        state = datePickerState,
        isOpen = isDatePickerDialogOpen,
        onDismissRequest = { isDatePickerDialogOpen = false },
        onConfirmButtonClicked = {
            val picked = datePickerState.selectedDateMillis
            // 如果想强制不早于今天，可解开下面注释
            // val todayStart = Calendar.getInstance().apply {
            //     set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            //     set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            // }.timeInMillis
            // dueDate = when {
            //     picked == null -> null
            //     picked < todayStart -> todayStart
            //     else -> picked
            // }

            dueDate = picked
            isDatePickerDialogOpen = false
        }
    )

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = subjects,
        onDismissRequest = { isBottomSheetOpen = false },
        onSubjectClicked = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
        }
    )

    // ====== 页面 ======
    Scaffold(
        topBar = {
            TaskScreenTopBar(
                isTaskExist = true,
                isComplete = false,
                checkBoxBorderColor = Red,
                onBackButtonClick = { /* navController?.popBackStack() */ },
                onDeleteButtonClick = { isDeleteDialogOpen = true },
                onCheckBoxClick = { /* TODO */ }
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(paddingValue)
                .padding(horizontal = 12.dp)
        ) {
            // 标题
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

            // 描述
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") }
            )

            Spacer(Modifier.height(20.dp))

            // Due date
            Text("Due date", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(6.dp))

            ListItem(
                leadingContent = { Icon(Icons.Default.DateRange, contentDescription = null) },
                headlineContent = { Text(dueDate.changeMillisToDateString()) },
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

            // Priority
            Text("Priority", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(10.dp))
            PriorityBar(
                selected = selectedPriority,
                onSelected = { selectedPriority = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(30.dp))

            // 关联学科
            Text("Related to subject", style = MaterialTheme.typography.bodySmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("English", style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = { isBottomSheetOpen = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Subject")
                }
            }

            // 保存
            Button(
                enabled = taskTitleError == null && title.isNotBlank(),
                onClick = { /* TODO: save */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Text("Save")
            }
        }
    }
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
