package com.example.studysmart.presentation.subject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studysmart.presentation.components.AddSubjectDialog
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.theme.SubjectPalettes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    vm: SubjectViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val subjects by vm.subjects.collectAsState()

    var dialogOpen by remember { mutableStateOf(false) }
    var deleteOpen by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<Long?>(null) }

    var subjectName by remember { mutableStateOf("") }
    var goalHoursText by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(SubjectPalettes.options.first()) }

    fun openForCreate() {
        editingId = null
        subjectName = ""
        goalHoursText = "0"
        selectedColor = SubjectPalettes.options.first()
        dialogOpen = true
    }

    fun openForEdit(s: SubjectUi) {
        editingId = s.id
        subjectName = s.name
        goalHoursText = s.goalHours.toString()
        selectedColor = SubjectPalettes.fromArgb(s.startColorArgb, s.endColorArgb)
            ?: SubjectPalettes.options.first()
        dialogOpen = true
    }

    // —— Scaffold —— //
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                is SubjectEvent.Saved   -> snackbar.showSnackbar("Saved")
                is SubjectEvent.Deleted -> snackbar.showSnackbar("Deleted")
                is SubjectEvent.Error   -> snackbar.showSnackbar(ev.message)
            }
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text("Subjects") })
            CenterAlignedTopAppBar(
                title = { Text("Subjects") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { openForCreate() },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Subject") }
            )
        }
    ) { padding ->
        if (subjects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No subjects yet. Tap + to add.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(subjects, key = { it.id ?: it.hashCode().toLong() }) { s ->
                    SubjectRow(
                        subject = s,
                        onClick = { openForEdit(s) },
                        onEdit = { openForEdit(s) },
                        onDelete = {
                            editingId = s.id
                            deleteOpen = true
                        }
                    )
                }
            }
        }
    }


    AddSubjectDialog(
        isOpen = dialogOpen,
        subjectName = subjectName,
        goalHours = goalHoursText,
        selectedColors = selectedColor,
        onSubjectNameChange = { subjectName = it },
        onGoalHoursChange = { goalHoursText = it },
        onColorChange = { selectedColor = it },
        onDismissRequest = { dialogOpen = false },
        onConfirmButtonClick = {
            val gh = goalHoursText.toFloatOrNull() ?: 0f
            vm.upsert(
                id = editingId,
                name = subjectName,
                goalHours = gh,
                startColorArgb = selectedColor.start.toArgb(),
                endColorArgb = selectedColor.end.toArgb()
            )
            dialogOpen = false
        }
    )


    DeleteDialog(
        isOpen = deleteOpen,
        title = "Delete Subject?",
        bodyText = "All related tasks and study sessions will be removed. This cannot be undone.",
        onDismissRequest = { deleteOpen = false },
        onConfirmButtonClick = {
            editingId?.let { vm.delete(it) }
            deleteOpen = false
        }
    )
}

// Subject List
@Composable
private fun SubjectRow(
    subject: SubjectUi,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Goal: ${subject.goalHours} h",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = { menuOpen = false; onEdit() },
                        leadingIcon = { Icon(Icons.Default.Edit, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = { menuOpen = false; onDelete() },
                        leadingIcon = { Icon(Icons.Default.Delete, null) }
                    )
                }
            }
        }
    }
}
