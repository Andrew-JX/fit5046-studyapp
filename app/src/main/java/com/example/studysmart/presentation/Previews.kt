// app/src/main/java/com/example/studysmart/presentation/Previews.kt
package com.example.studysmart.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.studysmart.presentation.dashboard.DashboardScreen
import com.example.studysmart.presentation.subject.SubjectScreen
import com.example.studysmart.presentation.session.SessionScreen
import com.example.studysmart.presentation.task.TaskScreen
import com.example.studysmart.presentation.theme.StudySmartTheme
import com.example.studysmart.presentation.auth.LoginScreen
import com.example.studysmart.presentation.auth.SignUpScreen
import com.example.studysmart.presentation.nav.StudyApp
import com.example.studysmart.presentation.onboarding.OnboardingScreen
import com.example.studysmart.presentation.planner.TaskCreateEditScreen
import com.example.studysmart.presentation.resources.ResourcesScreen
import com.example.studysmart.presentation.profile.ProfileScreen

@Preview(showBackground = true, showSystemUi = true)
@Composable fun DashboardScreenPreview() { StudySmartTheme { DashboardScreen() } }

@Preview(showBackground = true, showSystemUi = true)
@Composable fun SubjectScreenPreview() { StudySmartTheme { SubjectScreen() } }

@Preview(showBackground = true, showSystemUi = true)
@Composable fun SessionScreenPreview() { StudySmartTheme { SessionScreen() } }

@Preview(showBackground = true, showSystemUi = true)
@Composable fun TaskScreenPreview() { StudySmartTheme { TaskScreen() } }

//@Preview(showBackground = true, showSystemUi = true)
//@Composable fun LoginPreview() { StudySmartTheme { LoginScreen(onLoggedIn = {}) } }
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable fun SignUpPreview() { StudySmartTheme { SignUpScreen(onSignedUp = {}) } }
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable fun OnboardingPreview() { StudySmartTheme { OnboardingScreen(onFinish = {}) } }
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable fun TaskEditPreview() { StudySmartTheme { TaskCreateEditScreen(onDone = {}) } }
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable fun ResourcesPreview() { StudySmartTheme { ResourcesScreen() } }
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable fun ProfilePreview() { StudySmartTheme { ProfileScreen(onLogout = {}) } }

@Preview(showBackground = true, showSystemUi = true, name = "App Shell (drawer closed)")
@Composable
fun Preview_AppShell_Closed() {
    StudySmartTheme { StudyApp() }
}

@Preview(showBackground = true, showSystemUi = true, name = "App Shell (drawer OPEN)")
@Composable
fun Preview_AppShell_Open() {
    StudySmartTheme { StudyApp(drawerInitiallyOpen = true) }
}

