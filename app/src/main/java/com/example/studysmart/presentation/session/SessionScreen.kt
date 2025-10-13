// app/src/main/java/com/example/studysmart/presentation/session/SessionScreen.kt
package com.example.studysmart.presentation.session

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.SubjectListBottomSheet
import com.example.studysmart.presentation.components.studySessionsList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    vm: SessionViewModel = hiltViewModel()
) {
    // 1) 从 VM 获取数据
    val subjects by vm.subjects.collectAsState()
    val sessions by vm.sessionsUi.collectAsState()
    val isRunning by vm.isRunning.collectAsState()
    val elapsedMillis by vm.elapsedMillis.collectAsState()

    // 2) 本地 UI 状态
    var selectedSubjectId by rememberSaveable { mutableStateOf<Long?>(null) }
    val selectedSubjectName = remember(subjects, selectedSubjectId) {
        subjects.firstOrNull { it.id == selectedSubjectId }?.name ?: "Select subject"
    }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    // 删除对话框需要记录待删 id
    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }
    var pendingDeleteId by rememberSaveable { mutableStateOf<Long?>(null) }

    // 科目选择底部弹窗
    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = subjects,
        onDismissRequest = { isBottomSheetOpen = false },
        onSubjectClicked = { sub ->
            selectedSubjectId = sub.id
            vm.selectCurrentSubject(sub.id)
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
        }
    )

    // 删除确认
    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure you want to delete this session? This cannot be undone.",
        onDismissRequest = {
            isDeleteDialogOpen = false
            pendingDeleteId = null
        },
        onConfirmButtonClick = {
            pendingDeleteId?.let { vm.deleteSession(it) }
            isDeleteDialogOpen = false
            pendingDeleteId = null
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Study Sessions", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = { /* navBack() */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: overflow menu */ }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // 计时器
            item {
                TimerSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    progress = ((elapsedMillis / 60_000f) % 60) / 60f,
                    timeText = formatElapsedTime(elapsedMillis),
                    isRunning = isRunning,
                    onToggle = {
                        if (isRunning) vm.pauseTimer() else vm.startTimer()
                    }
                )
            }

            // 选择科目
            item {
                RelatedToSubjectSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    relatedToSubject = selectedSubjectName,
                    selectSubjectButtonClick = { isBottomSheetOpen = true }
                )
            }

            // 操作按钮
            item {
                ButtonsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    startButtonClick = { vm.startTimer() },
                    cancelButtonClick = { vm.cancelTimer() },
                    finishButtonClick = { vm.finishAndSave() }
                )
            }

            // 历史学习会话（把要删除的 id 回传出来）
            studySessionsList(
                sectionTitle = "STUDY SESSIONS HISTORY",
                emptyListText = "No sessions yet.\nStart a session to record your study time.",
                sessions = sessions,
                onDeleteIconClick = { session ->
                    pendingDeleteId = session.id
                    isDeleteDialogOpen = true
                }
            )
        }
    }
}

@Composable
private fun TimerSection(
    modifier: Modifier,
    progress: Float,
    timeText: String,
    isRunning: Boolean,
    onToggle: () -> Unit
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { 1f },
            strokeWidth = 12.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(260.dp)
        )
        CircularProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            strokeWidth = 12.dp,
            modifier = Modifier.size(260.dp)
        )
        Text(text = timeText, style = MaterialTheme.typography.displaySmall)
        FilledTonalIconButton(
            onClick = onToggle,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isRunning) "Pause" else "Start"
            )
        }
    }
}

@Composable
private fun RelatedToSubjectSection(
    modifier: Modifier,
    relatedToSubject: String,
    selectSubjectButtonClick: () -> Unit
) {
    Column(modifier = modifier) {
        Text("Related to subject", style = MaterialTheme.typography.bodySmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = relatedToSubject, style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = selectSubjectButtonClick) {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select Subject")
            }
        }
    }
}

@Composable
private fun ButtonsSection(
    modifier: Modifier,
    startButtonClick: () -> Unit,
    cancelButtonClick: () -> Unit,
    finishButtonClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(onClick = cancelButtonClick, modifier = Modifier.weight(1f)) { Text("Cancel") }
        Button(onClick = startButtonClick, modifier = Modifier.weight(1f)) { Text("Start") }
        FilledTonalButton(onClick = finishButtonClick, modifier = Modifier.weight(1f)) { Text("Finish") }
    }
}

private fun formatElapsedTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return "%02d:%02d:%02d".format(h, m, s)
}
