package com.example.studysmart.presentation.subject

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studysmart.R
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.presentation.components.AddSubjectDialog
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.SubjectCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    vm: SubjectViewModel = hiltViewModel()
) {
    val subjects by vm.subjects.collectAsState()

    // —— Add/Edit Dialog 状态 —— //
    var showAddEdit by remember { mutableStateOf(false) }
    var editing: Subject? by remember { mutableStateOf(null) }
    var subjectName by remember { mutableStateOf("") }
    var goalHours by remember { mutableStateOf("") }
    var selectedColors by remember { mutableStateOf(listOf(Color(0xFF4F46E5), Color(0xFF8B5CF6))) }

    // 删除确认
    var toDelete: Subject? by remember { mutableStateOf(null) }

    // 打开编辑时用当前 subject 回填
    fun openEditor(target: Subject?) {
        editing = target
        subjectName = target?.name ?: ""
        goalHours = target?.goalHours?.toString() ?: ""
        selectedColors = if (target == null) {
            listOf(Color(0xFF4F46E5), Color(0xFF8B5CF6))
        } else {
            listOf(Color(target.startColorArgb), Color(target.endColorArgb))
        }
        showAddEdit = true
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Subjects") },
                actions = {
                    IconButton(onClick = { openEditor(null) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Subject")
                    }
                }
            )
        }
    ) { inner ->
        if (subjects.isEmpty()) {
            // 空态
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(painterResource(R.drawable.img_books), null)
                Spacer(Modifier.height(12.dp))
                Text("You don't have any subjects.\nTap + to add one.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subjects, key = { it.id ?: -1L }) { s ->
                    SubjectRow(
                        subject = s,
                        onEdit = { openEditor(s) },
                        onDelete = { toDelete = s }
                    )
                }
            }
        }
    }

    // —— Add/Edit Dialog —— //
    AddSubjectDialog(
        isOpen = showAddEdit,
        subjectName = subjectName,
        goalHours = goalHours,
        onSubjectNameChange = { subjectName = it },
        onGoalHoursChange = { goalHours = it },
        selectedColors = selectedColors,
        onColorChange = { selectedColors = it },
        onDismissRequest = { showAddEdit = false },
        onConfirmButtonClick = {
            val gh = goalHours.toFloatOrNull() ?: 0f
            val model = Subject(
                id = editing?.id,
                name = subjectName.trim(),
                goalHours = gh,
                startColorArgb = selectedColors.first().toArgb(),
                endColorArgb = selectedColors.last().toArgb()
            )
            vm.upsert(model)
            showAddEdit = false
        }
    )

    // —— Delete Dialog —— //
    DeleteDialog(
        isOpen = toDelete != null,
        title = "Delete Subject?",
        bodyText = "All related tasks and sessions will be removed. This action cannot be undone.",
        onDismissRequest = { toDelete = null },
        onConfirmButtonClick = {
            toDelete?.id?.let { vm.delete(it) }
            toDelete = null
        }
    )
}

@Composable
private fun SubjectRow(
    subject: Subject,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(onClick = onEdit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubjectCard(
                subjectName = subject.name,
                gradientColors = listOf(Color(subject.startColorArgb), Color(subject.endColorArgb)),
                onClick = onEdit
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
        }
    }
}
