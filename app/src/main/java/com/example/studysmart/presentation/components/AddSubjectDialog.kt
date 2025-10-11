package com.example.studysmart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.studysmart.presentation.theme.ColorSet
import com.example.studysmart.presentation.theme.SubjectPalettes

@Composable
fun AddSubjectDialog(
    isOpen: Boolean,
    title: String = "Add / Update Subject",
    selectedColors: ColorSet,
    subjectName: String,
    goalHours: String,
    onColorChange: (ColorSet) -> Unit,
    onSubjectNameChange: (String) -> Unit,
    onGoalHoursChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit
) {
    var subjectNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var goalHoursError by rememberSaveable { mutableStateOf<String?>(null) }

    subjectNameError = when {
        subjectName.isBlank() -> "Please enter subject name."
        subjectName.length < 2 -> "Subject name is too short."
        subjectName.length > 20 -> "Subject name is too long."
        else -> null
    }

    goalHoursError = when {
        goalHours.isBlank() -> "Please enter goal study hours."
        goalHours.toFloatOrNull() == null -> "Invalid number."
        goalHours.toFloat() < 1f -> "Please set at least 1 hour."
        goalHours.toFloat() > 1000f -> "Please set a maximum of 1000 hours."
        else -> null
    }

    if (isOpen) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = title) },
            text = {
                Column {
                    // ---- 颜色选择 ----
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SubjectPalettes.options.forEach { ColorSet: ColorSet ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (ColorSet == selectedColors) Color.Black else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(ColorSet.start, ColorSet.end)
                                        )
                                    )
                                    .clickable { onColorChange(ColorSet) }
                            )
                        }
                    }

                    // ---- 名称输入 ----
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = onSubjectNameChange,
                        label = { Text("Subject Name") },
                        singleLine = true,
                        isError = subjectNameError != null && subjectName.isNotBlank(),
                        supportingText = { Text(subjectNameError.orEmpty()) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ---- 学习目标输入 ----
                    OutlinedTextField(
                        value = goalHours,
                        onValueChange = onGoalHoursChange,
                        label = { Text("Goal Study Hours") },
                        singleLine = true,
                        isError = goalHoursError != null && goalHours.isNotBlank(),
                        supportingText = { Text(goalHoursError.orEmpty()) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmButtonClick,
                    enabled = subjectNameError == null && goalHoursError == null
                ) {
                    Text("Save")
                }
            }
        )
    }
}
