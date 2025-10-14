// app/src/main/java/com/example/studysmart/presentation/nav/StudyNav.kt
package com.example.studysmart.presentation.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.studysmart.presentation.AppDrawer.appDrawerItems
import com.example.studysmart.presentation.auth.AuthViewModel
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
    data object Splash : Route("splash")

    data object Login : Route("login")
    data object SignUp : Route("signup")
    data object Onboarding : Route("onboarding")

    data object Dashboard : Route("dashboard")
    data object Subjects : Route("subjects")
    data object Planner : Route("planner")
    data object Resources : Route("resources")
    //    data object Tasks : Route("tasks")
    data object Profile : Route("profile")

    // 子页
    data object Session : Route("session")
    data object TaskEdit : Route("taskEdit")
}

data class BottomItem(val route: String, val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyApp(drawerInitiallyOpen: Boolean = false) {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(
        initialValue = if (drawerInitiallyOpen) DrawerValue.Open else DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    val chromeHidden = when (currentRoute) {
        Route.Login.route, Route.SignUp.route, Route.Onboarding.route -> true
        else -> false
    }

    val currentTitle = appDrawerItems.firstOrNull { it.route == currentRoute }?.label ?: "StudySmart"

    @Composable
    fun AppScaffoldContent() {
        Scaffold { inner ->
            NavHost(
                navController = nav,
                startDestination = Route.Splash.route,
                modifier = Modifier.padding(inner)
            ) {
                composable(Route.Splash.route) {
                    SplashGate(nav)  //
                }

                composable(Route.Login.route) {
                    LoginScreen(
                        onLoggedIn = { nav.navigate(Route.Onboarding.route) },
                        onNavigateToSignUp = { nav.navigate(Route.SignUp.route) }
                    )
                }

                composable(Route.SignUp.route) {
                    SignUpScreen(
                        onSignedUp = { nav.navigate(Route.Onboarding.route) },
                        onNavigateToLogin = { nav.navigate(Route.Login.route) }
                    )
                }

                composable(Route.Onboarding.route) {
                    OnboardingScreen(onFinish = { nav.navigate(Route.Dashboard.route) })
                }

                composable(Route.Dashboard.route) {
                    DashboardScreen(
                        onNavigateToSession = { nav.navigate(Route.Session.route) },
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }

                composable(Route.Subjects.route) {
                    SubjectScreen(
                        onNavigateBack = {
                            nav.navigate(Route.Dashboard.route) {
                                popUpTo(Route.Dashboard.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Route.Planner.route) {
                    TaskScreen(
                        onNavigateBack = {
                            nav.navigate(Route.Dashboard.route) {
                                popUpTo(Route.Dashboard.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Route.Resources.route) {
                    ResourcesScreen(
                        onNavigateBack = {
                            nav.navigate(Route.Dashboard.route) {
                                popUpTo(Route.Dashboard.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Route.Profile.route) {
                    ProfileScreen(
                        onLogout = { nav.navigate(Route.Login.route) },
                        onNavigateBack = {
                            nav.navigate(Route.Dashboard.route) {
                                popUpTo(Route.Dashboard.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Route.Session.route) {
                    SessionScreen(
                        onNavigateBack = {
                            nav.navigate(Route.Dashboard.route) {
                                popUpTo(Route.Dashboard.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Route.TaskEdit.route) {
                    TaskCreateEditScreen(
                        onDone = {
                            nav.navigate(Route.Dashboard.route) {
                                popUpTo(Route.Dashboard.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }

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

// SplashGate 函数（在 StudyApp 外面，作为顶层函数）
@Composable
private fun SplashGate(nav: NavHostController) {
    val authVm: AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val authState by authVm.authState.collectAsState()

    val ctx = androidx.compose.ui.platform.LocalContext.current
    val prefsRepo = remember(ctx) {
        com.example.studysmart.data.datastore.UserPreferencesRepository(ctx)
    }
    val hasSeen by prefsRepo.hasSeenOnboardingFlow.collectAsState(initial = false)

    LaunchedEffect(authState.currentUser, hasSeen) {
        when {
            authState.currentUser == null -> {
                nav.navigate(Route.Login.route) {
                    popUpTo(Route.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
            hasSeen -> {
                nav.navigate(Route.Dashboard.route) {
                    popUpTo(Route.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
            else -> {
                nav.navigate(Route.Onboarding.route) {
                    popUpTo(Route.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}