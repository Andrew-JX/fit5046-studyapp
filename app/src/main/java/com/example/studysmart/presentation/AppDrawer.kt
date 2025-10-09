package com.example.studysmart.presentation

// app/src/main/java/com/example/studysmart/presentation/AppDrawer.kt


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.studysmart.presentation.nav.Route

data class AppDrawerItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

object AppDrawer {
    val appDrawerItems = listOf(
        AppDrawerItem(
            route = Route.Dashboard.route,
            label = "Dashboard",
            icon = Icons.Filled.Dashboard
        ),
        AppDrawerItem(
            route = Route.Subjects.route,
            label = "Subjects",
            icon = Icons.Filled.MenuBook
        ),
        AppDrawerItem(
            route = Route.Planner.route,
            label = "Planner",
            icon = Icons.Filled.Schedule
        ),
        AppDrawerItem(
            route = Route.Resources.route,
            label = "Resources",
            icon = Icons.Filled.MenuBook
        ),
        AppDrawerItem(
            route = Route.Session.route,
            label = "Sessions",
            icon = Icons.Filled.PlayArrow
        ),
        AppDrawerItem(
            route = Route.Profile.route,
            label = "Profile",
            icon = Icons.Filled.AccountCircle
        )
    )
}