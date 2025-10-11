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
import com.example.studysmart.R
import com.example.studysmart.domain.model.Session
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.domain.model.Task
import com.example.studysmart.presentation.components.AddSubjectDialog
import com.example.studysmart.presentation.components.CountCard
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.SubjectCard
import com.example.studysmart.presentation.components.studySessionsList
import com.example.studysmart.presentation.components.tasksList
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studysmart.presentation.theme.SubjectPalettes
import androidx.compose.runtime.collectAsState


@Composable
fun DashboardScreen(
    vm: DashboardViewModel = hiltViewModel()
) {
    // 收集 ViewModel 的 Flow
    val subjectList by vm.subjects.collectAsState()
    val taskList by vm.tasks.collectAsState()
    val sessionList by vm.sessions.collectAsState()

    var isAddSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSessionDialogOpen by rememberSaveable { mutableStateOf(false) }

    var subjectName by remember { mutableStateOf("") }
    var goalHours by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(SubjectPalettes.options.random()) } // List<Color>

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
            // TODO: 组装一个 Subject 调用 vm.addSubject()
            // viewModelScope 不在 Composable，建议用 LaunchedEffect 或传事件出去
            isAddSubjectDialogOpen = false
        }
    )

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            // …省略上面的统计卡片…

            item {
                SubjectCardsSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = subjectList,              // ✅ 用真实数据
                    onAddIconClicked = { isAddSubjectDialogOpen = true }
                )
            }

            // 任务
            tasksList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any upcoming tasks.\nClick + to add.",
                tasks = taskList,                          // ✅
                onCheckBoxClick = {},
                onTaskCardClick = {}
            )

            // 最近会话
            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "No recent study sessions.",
                sessions = sessionList,                    // ✅
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
                SubjectCard(
                    subjectName = subject.name,
                    gradientColors = listOf(
                        Color(subject.startColorArgb),
                        Color(subject.endColorArgb)
                    ),
                    onClick = {}
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
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
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
