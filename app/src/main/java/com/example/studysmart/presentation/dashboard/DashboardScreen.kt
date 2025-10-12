package com.example.studysmart.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.example.studysmart.R
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.presentation.components.AddSubjectDialog
import com.example.studysmart.presentation.components.CountCard
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.SubjectCard
import com.example.studysmart.presentation.components.studySessionsList
import com.example.studysmart.presentation.components.tasksList
import com.example.studysmart.presentation.theme.ColorSet
import com.example.studysmart.presentation.theme.SubjectPalettes


@Composable
fun DashboardScreen(
    vm: DashboardViewModel = hiltViewModel()
) {
    // 收集 ViewModel 的 Flow
    val subjectList by vm.subjects.collectAsState()
    val taskList by vm.tasks.collectAsState()
    val sessionList by vm.sessions.collectAsState()

    // 对话框与输入状态（保持原样式/交互）
    var isAddSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSessionDialogOpen by rememberSaveable { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }
    var goalHours by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(SubjectPalettes.options.random()) } // ColorSet


    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen,
        subjectName = subjectName,
        goalHours = goalHours,
        selectedColors = selectedColor,
        onSubjectNameChange = { subjectName = it },
        onGoalHoursChange   = { goalHours = it },
        onColorChange       = { selectedColor = it },
        onDismissRequest    = { isAddSubjectDialogOpen = false },
        onConfirmButtonClick = {
            // TODO: 调用 vm.addSubject(...) 完成新增（此处只恢复原交互：关闭弹窗）
            // 例如：vm.addSubject(subjectName, goalHours.toFloatOrNull() ?: 0f, selectedColor)
            isAddSubjectDialogOpen = false
        }
    )

    DeleteDialog(
        isOpen = isDeleteSessionDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? Your studied hours will be reduced by this session time. This action can not be undone.",
        onDismissRequest = { isDeleteSessionDialogOpen = false },
        onConfirmButtonClick = {
            // TODO: 调用 vm.deleteSession(id)；这里保持与原来一致的“确认后关闭”
            isDeleteSessionDialogOpen = false
        }
    )

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 统计卡片（完全恢复原排版/样式）
            item {
                CountCardsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    subjectCount = 5,   // 如需真实数据，可改为 subjectList.size
                    studiedHours = "10",// 如需真实数据，可用 (sessionList.sumOf { it.durationMinutes } / 60f).toInt().toString()
                    goalHours = "15"    // 如需真实数据，可从你的 Subject 聚合字段汇总
                )
            }

            // Subjects 区（恢复加号按钮、空态图与文案）
            item {
                SubjectCardsSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = subjectList,
                    onAddIconClicked = { isAddSubjectDialogOpen = true }
                )
            }

            // Start Study Session 按钮（恢复原样）
            item {
                Button(
                    onClick = { /* TODO: 导航或触发开始学习会话的逻辑 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp)
                ) {
                    Text(text = "Start Study Session")
                }
            }

            // Chips（恢复原来的筛选控件）
            item { Spacer(Modifier.height(8.dp)) }
            item { TaskFilterChips() }

            // Tasks（恢复原区块标题/空态文案/列表组件）
            tasksList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any upcoming tasks.\n Click the + button in subject screen to add new task.",
                tasks = taskList,
                onCheckBoxClick = { /* TODO */ },
                onTaskCardClick = { /* TODO */ }
            )

            // Sessions（恢复原区块标题/空态文案/删除图标交互）
            item { Spacer(modifier = Modifier.height(20.dp)) }
            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent study sessions.\n Start a study session to begin recording your progress.",
                sessions = sessionList,
                onDeleteIconClick = { isDeleteSessionDialogOpen = true }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "StudySmart",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}

@Composable
private fun CountCardsSection(
    modifier: Modifier,
    subjectCount: Int,
    studiedHours: String,
    goalHours: String
) {
    Row(modifier = modifier) {
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Subject Count",
            count = "$subjectCount"
        )
        Spacer(modifier = Modifier.width(10.dp))
        ProgressCard(
            modifier = Modifier.weight(1f),
            title = "Study Progress",
            value = studiedHours.toFloatOrNull() ?: 0f,
            goal = goalHours.toFloatOrNull() ?: 0f
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalHours
        )
    }
}

@Composable
private fun SubjectCardsSection(
    modifier: Modifier,
    subjectList: List<Subject>,
    emptyListText: String = "You don't have any subjects.\n Click the + button to add new subject.",
    onAddIconClicked: () -> Unit
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "Subjects",
            action = {
                IconButton(onClick = onAddIconClicked) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Subject")
                }
            }
        )
        if (subjectList.isEmpty()) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.img_books),
                contentDescription = emptyListText
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
        ) {
            items(subjectList) { subject ->
                // 保持你现在的数据模型：用 ARGB 转 Color 供渐变
                val colors = listOf(
                    Color(subject.startColorArgb),
                    Color(subject.endColorArgb)
                )
                SubjectCard(
                    subjectName = subject.name,
                    gradientColors = colors,
                    onClick = { /* TODO: 进入 Subject 详情 */ }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        action?.invoke()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFilterChips(
    modifier: Modifier = Modifier,
    options: List<String> = listOf("All", "Today", "This week"),
) {
    var selected by remember { mutableStateOf(options.first()) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        options.forEach { label ->
            FilterChip(
                selected = selected == label,
                onClick = { selected = label },
                label = { Text(label) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressCard(
    title: String,
    value: Float,
    goal: Float,
    modifier: Modifier = Modifier
) {
    val pct = remember(value, goal) {
        if (goal <= 0f) 0f else (value / goal).coerceIn(0f, 1f)
    }
    Column(modifier) {
        Text(
            title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = pct,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text("${value.toInt()} / ${goal.toInt()} hrs", style = MaterialTheme.typography.bodySmall)
    }
}
