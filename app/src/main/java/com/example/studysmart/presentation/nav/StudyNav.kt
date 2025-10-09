// app/src/main/java/com/example/studysmart/presentation/nav/StudyNav.kt
package com.example.studysmart.presentation.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.studysmart.presentation.AppDrawer.appDrawerItems
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
import kotlinx.coroutines.launch

sealed class Route(val route: String) {
    data object Login : Route("login")
    data object SignUp : Route("signup")
    data object Onboarding : Route("onboarding")

    data object Dashboard : Route("dashboard")
    data object Subjects : Route("subjects")
    data object Planner : Route("planner")
    data object Resources : Route("resources")
    data object Tasks : Route("tasks")
    data object Profile : Route("profile")

    // 子页
    data object Session : Route("session")
    data object TaskEdit : Route("taskEdit")
}

data class BottomItem(val route: String, val label: String, val icon: ImageVector)

/**
 Main app composable with navigation
  Features:
  - Modal Navigation Drawer for main navigation
  - NavHost for screen routing
  - Bottom navigation bar (optional)
  - Authentication flow (Login → SignUp → Onboarding → Dashboard)
  @param drawerInitiallyOpen Whether drawer should be open initially
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyApp(drawerInitiallyOpen: Boolean = false) {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(
        initialValue = if (drawerInitiallyOpen) DrawerValue.Open else DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    // 底部栏项目（保持你原来的）
//    val bottomItems = listOf(
//        BottomItem(Route.Dashboard.route, "Home", Icons.Filled.Home),
//        BottomItem(Route.Subjects.route,  "Courses", Icons.Filled.MenuBook),
//        BottomItem(Route.Planner.route,   "Planner", Icons.Filled.Schedule),
//        BottomItem(Route.Resources.route, "Resources", Icons.Filled.List),
//        BottomItem(Route.Profile.route,   "Profile", Icons.Filled.AccountCircle),
//    )

    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    // 登录/注册/引导页：隐藏抽屉 + 底栏
    val chromeHidden = when (currentRoute) {
        Route.Login.route, Route.SignUp.route, Route.Onboarding.route -> true
        else -> false
    }

    // 抽屉标题高亮（你的 AppDrawer.kt 里应有 appDrawerItems）
    val currentTitle = appDrawerItems.firstOrNull { it.route == currentRoute }?.label ?: "StudySmart"

    // 核心页面内容（上方标题 + 下方底栏 + 中间 NavHost）
    @Composable
    fun AppScaffoldContent() {
        Scaffold(
            topBar = {
                if (!chromeHidden) {
                    CenterAlignedTopAppBar(
                        title = { Text(currentTitle) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                }
            },
//            bottomBar = {
//                if (!chromeHidden) {
//                    NavigationBar {
//                        val current = currentRoute
//                        bottomItems.forEach { item ->
//                            NavigationBarItem(
//                                selected = current == item.route,
//                                onClick = {
//                                    nav.navigate(item.route) {
//                                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
//                                        launchSingleTop = true
//                                        restoreState = true
//                                    }
//                                },
//                                icon = { Icon(item.icon, contentDescription = item.label) },
//                                label = { Text(item.label) }
//                            )
//                        }
//                    }
//                }
//            }
        ) { inner ->
            NavHost(
                navController = nav,
                startDestination = Route.SignUp.route,
                modifier = Modifier.padding(inner)
            ) {
                // ===== 你的路由保持不变（唯一建议：把 Planner 打开） =====
                composable(Route.Login.route)      { LoginScreen(onLoggedIn = { nav.navigate(Route.Onboarding.route) }) }
                composable(Route.SignUp.route)     { SignUpScreen(onSignedUp = { nav.navigate(Route.Onboarding.route) }) }
                composable(Route.Onboarding.route) { OnboardingScreen(onFinish   = { nav.navigate(Route.Dashboard.route) }) }

                composable(Route.Dashboard.route)  { DashboardScreen() }
                composable(Route.Subjects.route)   { SubjectScreen() }
                composable(Route.Planner.route) { TaskScreen() }
                composable(Route.Resources.route)  { ResourcesScreen() }
                composable(Route.Tasks.route) { TaskScreen() }
                composable(Route.Profile.route)    { ProfileScreen(onLogout = { nav.navigate(Route.Login.route) }) }

                // 子页
                composable(Route.Session.route)    { SessionScreen() }
                composable(Route.TaskEdit.route)   { TaskCreateEditScreen(onDone = { nav.popBackStack() }) }
            }
        }
    }

    // 抽屉包裹整个 App（在 login/signup/onboarding 隐藏）
    if (chromeHidden) {
        AppScaffoldContent()
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text(
                        text = "StudySmart",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    appDrawerItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                scope.launch { drawerState.close() }
                                nav.navigate(item.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        ) {
            AppScaffoldContent()
        }
    }
}
