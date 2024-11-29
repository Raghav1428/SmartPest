package com.example.smartpest.view

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.smartpest.database.DatabaseProvider
import com.example.smartpest.viewmodels.AuthState
import com.example.smartpest.viewmodels.AuthViewModel
import com.example.smartpest.viewmodels.ThemeViewModel
import com.example.smartpest.viewmodels.UserViewModel
import com.example.smartpest.viewmodels.WeatherViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        installSplashScreen()
        enableEdgeToEdge()

        val database = DatabaseProvider.getDatabase(this)
        val userViewModelFactory = DatabaseProvider.UserViewModelFactory(database.userDao)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            MyApp(themeViewModel, userViewModel)
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(themeViewModel: ThemeViewModel, userViewModel: UserViewModel) {

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val weatherViewModel: WeatherViewModel = viewModel()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // List of routes where drawer and bottom nav bar should appear
    val mainRoutes = listOf(
        "Home",
        "PestDisease.AI",
        "AI Assistant",
        "Weather Report",
        "Local Alerts",
        "Nearby Shops",
        "Farm Guide",
        "Profile Page"
    )
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Get initial route based on auth state
    val startDestination = remember {
        if (authViewModel.authState.value is AuthState.Authenticated) "home" else "login"
    }

    SmartPestTheme(isDarkTheme = themeViewModel.isDarkTheme) {
        ModalNavigationDrawer(
            drawerContent = {
                if (currentRoute in mainRoutes) {
                    DrawerContent(navController, authViewModel, themeViewModel, drawerState)
                }
            },
            drawerState = drawerState
        ) {
            Scaffold(
                topBar = {
                    if (currentRoute in mainRoutes) {
                        TopAppBar(
                            title = { Text(currentRoute.toString()) },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                },
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    Modifier.padding(innerPadding)
                ) {
                    composable("login") {
                        LoginPage(navController, authViewModel)
                    }
                    composable("signup") {
                        SignUpPage(navController, authViewModel)
                    }
                    composable("Home") {
                        HomePage(navController)
                    }
                    composable("PestDisease.AI") {
                        PestDiseaseAI(navController)
                    }
                    composable("AI Assistant") {
                        ExpertSupport(navController)
                    }
                    composable("Weather Report") {
                        WeatherReport(weatherViewModel)
                    }
                    composable("Local Alerts") {
                        LocalAlerts(navController)
                    }
                    composable("Nearby Shops") {
                        NearbyShops(navController)
                    }
                    composable("Farm Guide") {
                        FarmGuide(navController)
                    }
                    composable("Profile Page") {
                        ProfilePage(authViewModel, userViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope()
    val items = listOf(

        NavigationItem(
            "Home",
            Icons.Filled.Home,
            Icons.Outlined.Home,
            route = "Home"
        ),
        NavigationItem(
            "PestDisease.AI",
            Icons.Filled.Camera,
            Icons.Outlined.Camera,
            route = "PestDisease.AI"
        ),
        NavigationItem(
            "AI Assistant",
            Icons.AutoMirrored.Filled.Chat,
            Icons.AutoMirrored.Outlined.Chat,
            route = "AI Assistant"
        ),
        NavigationItem(
            "Weather Report",
            Icons.Filled.Cloud,
            Icons.Outlined.Cloud,
            route = "Weather Report"
        ),
        NavigationItem(
            "Nearby Shops",
            Icons.Filled.ShoppingCart,
            Icons.Outlined.ShoppingCart,
            route = "Nearby Shops"
        ),
        NavigationItem("Guide", Icons.Filled.Book, Icons.Outlined.Book, route = "Farm Guide"),
        NavigationItem(
            "Local Alerts",
            Icons.Filled.Notifications,
            Icons.Outlined.Notifications,
            route = "Local Alerts"
        ),
        NavigationItem(
            "Profile",
            Icons.Filled.AccountCircle,
            Icons.Outlined.AccountCircle,
            route = "Profile Page"
        ),
        NavigationItem(
            "Log Out",
            Icons.AutoMirrored.Filled.ExitToApp,
            Icons.AutoMirrored.Outlined.ExitToApp,
            route = "login"
        )
    )
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        ModalDrawerSheet {
            Spacer(modifier = Modifier.height(32.dp))

            items.forEachIndexed { index, item ->
                NavigationDrawerItem(
                    label = { Text(item.title) },
                    selected = index == selectedItemIndex,
                    onClick = {
                        if (item.title == "Log Out") {
                            authViewModel.signout()
                        }
                        navController.navigate(item.route) {
                            popUpTo("home") { inclusive = true }
                        }
                        selectedItemIndex = index
                        scope.launch { drawerState.close() }
                    },
                    icon = {
                        Icon(
                            imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { themeViewModel.toggleTheme() },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = if (themeViewModel.isDarkTheme) Color.White else Color.Gray
                )
            }
        }
    }
}

@Composable
fun SmartPestTheme(isDarkTheme: Boolean, content: @Composable () -> Unit) {
    val colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean = false,
    val route: String
)