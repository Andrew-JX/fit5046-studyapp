package com.example.studysmart.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studysmart.R
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.presentation.components.*
import com.example.studysmart.presentation.theme.ColorSet
import com.example.studysmart.presentation.theme.SubjectPalettes
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.studysmart.presentation.components.GraphScreen
import com.example.studysmart.presentation.session.SessionUi


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    vm: DashboardViewModel = hiltViewModel(),
    onNavigateToSession: () -> Unit = {},
    onOpenDrawer: () -> Unit = {}
) {
    val subjectList by vm.subjects.collectAsState()
    val taskList by vm.tasks.collectAsState()
    val sessionList by vm.sessions.collectAsState()

    var isAddSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSessionDialogOpen by rememberSaveable { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }
    var goalHours by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(SubjectPalettes.options.random()) }
    val scope = rememberCoroutineScope()
    var sessionToDelete by remember { mutableStateOf<SessionUi?>(null) }

    vm.alarm()


    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen,
        subjectName = subjectName,
        goalHours = goalHours,
        selectedColors = selectedColor,
        onSubjectNameChange = { subjectName = it },
        onGoalHoursChange = { goalHours = it },
        onColorChange = { selectedColor = it },
        onDismissRequest = { isAddSubjectDialogOpen = false },
        onConfirmButtonClick = {
            val ui = SubjectUiState(
                name = subjectName.trim(),
                goalHours = goalHours,
                colors = listOf(
                    selectedColor.start,
                    selectedColor.end
                )
            )
            scope.launch {
                vm.saveSubject(ui)
                subjectName = ""
                goalHours = ""
                isAddSubjectDialogOpen = false
            }
        }
    )

    DeleteDialog(
        isOpen = isDeleteSessionDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? Your studied hours will be reduced by this session time. This action can not be undone.",
        onDismissRequest = { isDeleteSessionDialogOpen = false },
        onConfirmButtonClick = {
            sessionToDelete?.id?.let {id ->
                vm.deleteSession(id) // 执行删除
            }
            isDeleteSessionDialogOpen = false
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("StudySmart") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                CountCardsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    subjectCount = subjectList.size,
                    studiedHours = "10",
                    goalHours = "15"
                )
            }

            item {
                SubjectCardsSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = subjectList,
                    onAddIconClicked = { isAddSubjectDialogOpen = true }
                )
            }

            item {
                Button(
                    onClick = onNavigateToSession,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp)
                ) {
                    Text(text = "Start Study Session")
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
            item { TaskFilterChips() }

            tasksList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any upcoming tasks.\n Click the + button in subject screen to add new task.",
                tasks = taskList,
                onCheckBoxClick = { /* TODO */ },
                onTaskCardClick = { /* TODO */ }
            )

            item { Spacer(modifier = Modifier.height(20.dp)) }
            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent study sessions.\n Start a study session to begin recording your progress.",
                sessions = sessionList,
                onDeleteIconClick = {
                        sessionUi ->
                    sessionToDelete = sessionUi
                    isDeleteSessionDialogOpen = true }
            )

            item {
                Text(
                    text = "DATA VISUALISATION",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }

            item { GraphScreen(vm.sessions) }
        }

    }
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
                val colors = listOf(
                    Color(subject.startColorArgb),
                    Color(subject.endColorArgb)
                )
                SubjectCard(
                    subjectName = subject.name,
                    gradientColors = colors,
                    onClick = { /* TODO */ }
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