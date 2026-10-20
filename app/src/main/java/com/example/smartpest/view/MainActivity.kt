package com.example.smartpest.view

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.smartpest.database.DatabaseProvider
import com.example.smartpest.ui.components.TomatoMark
import com.example.smartpest.ui.theme.SmartPestTheme
import com.example.smartpest.viewmodels.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = DatabaseProvider.getDatabase(this)
        val userViewModel = ViewModelProvider(this,
            DatabaseProvider.UserViewModelFactory(database.userDao))[UserViewModel::class.java]
        val alertViewModel = ViewModelProvider(this,
            DatabaseProvider.AlertViewModelFactory(database.alertDao))[AlertViewModel::class.java]
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            SmartPestTheme(darkTheme = themeViewModel.isDarkTheme) {
                MyApp(themeViewModel, userViewModel, alertViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(themeViewModel: ThemeViewModel, userViewModel: UserViewModel, alertViewModel: AlertViewModel) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val weatherViewModel: WeatherViewModel = viewModel()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE) }
    val startDestination = remember {
        AppRoutes.startDestination(prefs.getBoolean("isFirstLaunch", true),
            authViewModel.authState.value is AuthState.Authenticated)
    }
    val route = navController.currentBackStackEntryAsState().value?.destination?.route
    val mainScreen = route in AppRoutes.mainRoutes
    val items = listOf(
        NavigationTab("Home", Icons.Outlined.Home, AppRoutes.HOME),
        NavigationTab("Scan", Icons.Outlined.CenterFocusStrong, AppRoutes.SCAN),
        NavigationTab("Guide", Icons.AutoMirrored.Outlined.MenuBook, AppRoutes.GUIDE),
        NavigationTab("Ask", Icons.AutoMirrored.Outlined.Chat, AppRoutes.ASSISTANT),
        NavigationTab("Profile", Icons.Outlined.AccountCircle, AppRoutes.PROFILE)
    )
    Scaffold(
        topBar = {
            if (mainScreen) TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (route == AppRoutes.HOME) TomatoMark(Modifier.size(30.dp))
                        Text(AppRoutes.title(route), style = MaterialTheme.typography.titleLarge)
                    }
                },
                navigationIcon = {
                    if (route == AppRoutes.WEATHER || route == AppRoutes.ALERTS) IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Go back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            if (mainScreen) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = route == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(AppRoutes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, null) },
                            label = { Text(item.title, maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination, Modifier.fillMaxSize().padding(padding)) {
            composable(AppRoutes.ONBOARDING) {
                TomatoOnboarding {
                    prefs.edit().putBoolean("isFirstLaunch", false).apply()
                    navController.navigate(AppRoutes.LOGIN) { popUpTo(AppRoutes.ONBOARDING) { inclusive = true } }
                }
            }
            composable(AppRoutes.LOGIN) { LoginPage(navController, authViewModel) }
            composable(AppRoutes.SIGNUP) { SignUpPage(navController, authViewModel) }
            composable(AppRoutes.HOME) { HomePage(navController, userViewModel, weatherViewModel, alertViewModel) }
            composable(AppRoutes.SCAN) { PestDiseaseAI(navController) }
            composable(AppRoutes.GUIDE) { FarmGuide(navController) }
            composable(AppRoutes.ASSISTANT) { ExpertSupport(navController) }
            composable(AppRoutes.WEATHER) { WeatherReport(navController, weatherViewModel) }
            composable(AppRoutes.ALERTS) { LocalAlerts(navController, alertViewModel) }
            composable(AppRoutes.PROFILE) { ProfilePage(navController, authViewModel, userViewModel, themeViewModel) }
        }
    }
}

private data class NavigationTab(val title: String, val icon: ImageVector, val route: String)
