package com.example.studysmart.presentation.session

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.SubjectListBottomSheet
import com.example.studysmart.presentation.components.studySessionsList
import com.example.studysmart.sessions
import com.example.studysmart.subjects
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen() {

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }


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

    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? " +
                "This action can not be undone.",
        onDismissRequest = { isDeleteDialogOpen = false },
        onConfirmButtonClick = {
            isDeleteDialogOpen = false
        }
    )

    Scaffold(
        topBar = {
            SessionScreenTopBar(onBackButtonClick = {})
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                TimerSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
            item {
                RelatedToSubjectSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    relatedToSubject = "English",
                    selectSubjectButtonClick = { isBottomSheetOpen = true }
                )
            }
            item {
                ButtonsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    startButtonClick = { },
                    cancelButtonClick = { },
                    finishButtonClick = { }
                )
            }
            studySessionsList(
                sectionTitle = "STUDY SESSIONS HISTORY",
                emptyListText = "You don't have any recent study sessions.\n " +
                        "Start a study session to begin recording your progress.",
                sessions = sessions,
                onDeleteIconClick = { isDeleteDialogOpen = true}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreenTopBar(
    onBackButtonClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        title = {
            Text("Study Sessions", style = MaterialTheme.typography.headlineSmall)
        },
        actions = {
            IconButton(onClick = { /* TODO: show menu */ }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
            }
        }
    )
}


@Composable
private fun TimerSection(
    modifier: Modifier,
    progress: Float = 0.18f,                 // 先写死个进度，后面接逻辑
    timeText: String = "00:05:32",
    isRunning: Boolean = false,
    onToggle: () -> Unit = {}
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        // 背景环
        CircularProgressIndicator(
            progress = 1f,
            strokeWidth = 12.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(260.dp)
        )
        // 前景环
        CircularProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            strokeWidth = 12.dp,
            modifier = Modifier.size(260.dp)
        )
        // 中央时间
        Text(
            text = timeText,
            style = MaterialTheme.typography.displaySmall
        )
        // 悬浮播放/暂停
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
        Text(
            text = "Related to subject",
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = relatedToSubject,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = selectSubjectButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Subject"
                )
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
        OutlinedButton(
            onClick = cancelButtonClick,
            modifier = Modifier.weight(1f)
        ) { Text("Cancel") }

        Button(
            onClick = startButtonClick,
            modifier = Modifier.weight(1f)
        ) { Text("Start") }

        FilledTonalButton(
            onClick = finishButtonClick,
            modifier = Modifier.weight(1f)
        ) { Text("Finish") }
    }
}
