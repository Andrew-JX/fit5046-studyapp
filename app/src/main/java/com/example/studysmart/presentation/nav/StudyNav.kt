// app/src/main/java/com/example/studysmart/presentation/nav/StudyNav.kt
package com.example.studysmart.presentation.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.studysmart.presentation.dashboard.DashboardScreen
import com.example.studysmart.presentation.session.SessionScreen
import com.example.studysmart.presentation.subject.SubjectScreen
import com.example.studysmart.presentation.task.TaskScreen
import com.example.studysmart.presentation.auth.LoginScreen
import com.example.studysmart.presentation.auth.SignUpScreen
import com.example.studysmart.presentation.onboarding.OnboardingScreen
import com.example.studysmart.presentation.planner.TaskCreateEditScreen
import com.example.studysmart.presentation.profile.ProfileScreen
import com.example.studysmart.presentation.resources.ResourcesScreen

sealed class Route(val route: String) {
    data object Login : Route("login")
    data object SignUp : Route("signup")
    data object Onboarding : Route("onboarding")

    data object Dashboard : Route("dashboard")
    data object Subjects : Route("subjects")
    data object Planner : Route("planner")
    data object Resources : Route("resources")
    data object Profile : Route("profile")

    // 子页
    data object Session : Route("session")
    data object TaskEdit : Route("taskEdit")
}

data class BottomItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun StudyApp() {
    val nav = rememberNavController()
    val bottomItems = listOf(
        BottomItem(Route.Dashboard.route, "Home", Icons.Filled.Home),
        BottomItem(Route.Subjects.route, "Courses", Icons.Filled.MenuBook),
        BottomItem(Route.Planner.route, "Planner", Icons.Filled.Schedule),
        BottomItem(Route.Resources.route, "Resources", Icons.Filled.List),
        BottomItem(Route.Profile.route, "Profile", Icons.Filled.AccountCircle),
    )

    // 这里可以切到 Login 作为入口；为了演示，先从 Dashboard 进
    LaunchedEffect(Unit) {
        if (nav.currentDestination == null) {
            nav.navigate(Route.Dashboard.route)
        }
    }

    Scaffold(
        bottomBar = {
            val current by nav.currentBackStackEntryAsState()
            val showBottom =
                when (current?.destination?.route) {
                    Route.Login.route, Route.SignUp.route, Route.Onboarding.route -> false
                    else -> true
                }
            if (showBottom) {
                NavigationBar {
                    val currentRoute = current?.destination?.route
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                nav.navigate(item.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = Route.Dashboard.route,
            modifier = Modifier.padding(inner)
        ) {
            composable(Route.Login.route) { LoginScreen(onLoggedIn = { nav.navigate(Route.Onboarding.route) }) }
            composable(Route.SignUp.route) { SignUpScreen(onSignedUp = { nav.navigate(Route.Onboarding.route) }) }
            composable(Route.Onboarding.route) { OnboardingScreen(onFinish = { nav.navigate(Route.Dashboard.route) }) }

            composable(Route.Dashboard.route) { DashboardScreen() }
            composable(Route.Subjects.route) { SubjectScreen() }
            composable(Route.Planner.route) { TaskScreen(onEditTask = { nav.navigate(Route.TaskEdit.route) }) }
            composable(Route.Resources.route) { ResourcesScreen() }
            composable(Route.Profile.route) { ProfileScreen(onLogout = { nav.navigate(Route.Login.route) }) }

            composable(Route.Session.route) { SessionScreen() }
            composable(Route.TaskEdit.route) { TaskCreateEditScreen(onDone = { nav.popBackStack() }) }
        }
    }
}
