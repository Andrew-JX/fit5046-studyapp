package com.example.studysmart.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(
    state: DatePickerState,
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmButtonClicked: () -> Unit,
    confirmButtonText: String = "OK",
    dismissButtonText: String = "Cancel"
) {
    if (!isOpen) return

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = { TextButton(onClick = onConfirmButtonClicked) { Text(confirmButtonText) } },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text(dismissButtonText) } },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        DatePicker(state = state)
    }
}