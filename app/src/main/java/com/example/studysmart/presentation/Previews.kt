// app/src/main/java/com/example/studysmart/presentation/Previews.kt
package com.example.studysmart.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.studysmart.presentation.dashboard.DashboardScreen
import com.example.studysmart.presentation.subject.SubjectScreen
import com.example.studysmart.presentation.session.SessionScreen
import com.example.studysmart.presentation.task.TaskScreen
import com.example.studysmart.presentation.theme.StudySmartTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    StudySmartTheme { DashboardScreen() }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SubjectScreenPreview() {
    StudySmartTheme { SubjectScreen() }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SessionScreenPreview() {
    StudySmartTheme { SessionScreen() }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TaskScreenPreview() {
    StudySmartTheme { TaskScreen() }
}
