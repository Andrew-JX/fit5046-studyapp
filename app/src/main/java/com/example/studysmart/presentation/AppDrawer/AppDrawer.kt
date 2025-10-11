package com.example.studysmart.presentation.AppDrawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Task // 你项目里用的是 Task 图标
import androidx.compose.material3.*          // 组件统一用 Material3
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.studysmart.presentation.nav.Route


data class DrawerItem(val label: String, val route: String, val icon: ImageVector)

val appDrawerItems = listOf(
    DrawerItem("Dashboard",  Route.Dashboard.route, Icons.Default.Dashboard),
    DrawerItem("Subjects",   Route.Subjects.route,  Icons.Default.Book),
    DrawerItem("Planner",    Route.Planner.route,   Icons.Default.EventNote),
    DrawerItem("Resources",  Route.Resources.route, Icons.Default.LibraryBooks),
    DrawerItem("Sessions",   Route.Session.route,   Icons.Default.Timer),
//    DrawerItem(label = "Tasks",Route.Tasks.route,Icons.Filled.Task),
    DrawerItem("Profile",    Route.Profile.route,   Icons.Default.AccountCircle),
)