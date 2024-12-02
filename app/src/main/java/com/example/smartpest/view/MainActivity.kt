package com.example.smartpest.view

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.smartpest.R
import com.example.smartpest.database.DatabaseProvider
import com.example.smartpest.models.LoaderIntro
import com.example.smartpest.models.OnboardingData
import com.example.smartpest.viewmodels.AlertViewModel
import com.example.smartpest.viewmodels.AuthState
import com.example.smartpest.viewmodels.AuthViewModel
import com.example.smartpest.viewmodels.ThemeViewModel
import com.example.smartpest.viewmodels.UserViewModel
import com.example.smartpest.viewmodels.WeatherViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var alertViewModel: AlertViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // Get FCM token
        FirebaseMessaging.getInstance().token.addOnCompleteListener {}

        installSplashScreen()
        enableEdgeToEdge()

        val database = DatabaseProvider.getDatabase(this)
        val userViewModelFactory = DatabaseProvider.UserViewModelFactory(database.userDao)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]
        val alertViewModelFactory = DatabaseProvider.AlertViewModelFactory(database.alertDao)
        alertViewModel = ViewModelProvider(this, alertViewModelFactory)[AlertViewModel::class.java]

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()

            SmartPestTheme(isDarkTheme = themeViewModel.isDarkTheme) {
                MyApp(themeViewModel, userViewModel, alertViewModel)
            }
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun MyApp(themeViewModel: ThemeViewModel, userViewModel: UserViewModel, alertViewModel: AlertViewModel) {

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val weatherViewModel: WeatherViewModel = viewModel()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

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
    val startDestination = remember {
        if (isFirstLaunch) "onboarding"
        else if (authViewModel.authState.value is AuthState.Authenticated) "home" else "login"
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
                    composable("onboarding") {
                        MainOnBoardingFunction(
                            navController,
                            onGetStartedClicked = {
                                prefs.edit().putBoolean("isFirstLaunch", false).apply()
                                navController.navigate("login") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        )
                    }
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
                        LocalAlerts(navController, alertViewModel)
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
                    tint = if (themeViewModel.isDarkTheme) Color.White else Gray
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

@ExperimentalPagerApi
@Composable
fun MainOnBoardingFunction(
    navController: NavHostController,
    onGetStartedClicked: () -> Unit
) {

    val items = ArrayList<OnboardingData>()

    items.add(
        OnboardingData(
            R.raw.plantscan,
            "PestDisease.Ai",
            "PestDisease.Ai helps you scan the image of the leaf of any plant and tells you with accuracy about the disease it is affected by. Also it tells about the different ways you can treat the crop so that the disease can be cured."
        )
    )

    items.add(
        OnboardingData(
            R.raw.weather,
            "Instant Weather",
            "We provide you with the latest weather condition in the real time without any delay. We are focused on providing you the best experience."
        )
    )

    items.add(
        OnboardingData(
            R.raw.aiassistant,
            "AI Assistant",
            "In the era of AI, we are dedicate to provide you the best AI assistant that can assist you anytime and anywhere whether it may be doubts about your crop to the suggestions for your equipments, your AI assistant is there for you all time."
        )
    )

    items.add(
        OnboardingData(
            R.raw.alerts,
            "Local Alerts",
            "Keeping you updated is our responsibility and we are dedicated towards it by providing you real time updates with our local alerts.\nSo turn on the notifications to get the latest updates."
        )
    )

    val pagerSate = rememberPagerState(
        pageCount = items.size,
        initialPage = 0,
        infiniteLoop = false
    )

    OnBoardingPager(
        item = items,
        pagerState = pagerSate,
        modifier = Modifier.fillMaxWidth(),
        navController = navController,
        onGetStartedClicked = onGetStartedClicked
    )
}

@ExperimentalPagerApi
@Composable
fun OnBoardingPager(
    item: List<OnboardingData>,
    pagerState: PagerState,
    modifier: Modifier,
    navController: NavHostController,
    onGetStartedClicked: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize() // Ensure full screen coverage
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 30.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 60.dp, start = 60.dp, end = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoaderIntro(
                        modifier = Modifier
                            .padding(top = 0.dp)
                            .size(300.dp)
                            .fillMaxWidth()
                            .align(alignment = Alignment.CenterHorizontally),
                        item[page].image
                    )
                    Text(
                        text = item[page].title,
                        modifier = Modifier.padding(top = 50.dp),
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = item[page].desc,
                        modifier = Modifier.padding(top = 50.dp, start = 20.dp, end = 20.dp),
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            PagerIndicator(item.size, pagerState.currentPage)
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomSection(pagerState.currentPage, pagerState, navController, onGetStartedClicked)
        }
    }
}

@Composable
fun PagerIndicator(
    size: Int,
    currentPage: Int
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(top = 60.dp)
    ) {
        repeat(size) {
            Indicator(isSelected = it == currentPage)
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean) {

    val width = animateDpAsState(targetValue = if (isSelected) 25.dp else 10.dp, label = "Onboarding Indicator")

    Box(
        modifier = Modifier
            .padding(1.dp)
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(
                if (isSelected) Red else Gray.copy(alpha = 0.5f)
            )
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomSection(
    currentPage: Int,
    pagerState: PagerState,
    navController: NavHostController,
    onGetStartedClicked: () -> Unit
) {

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed to login
            onGetStartedClicked()
            navController.navigate("login") {
                popUpTo("onboarding") { inclusive = true }
            }
        } else {
            // Permission denied, still proceed to login but with a warning
            onGetStartedClicked()
            navController.navigate("login") {
                popUpTo("onboarding") { inclusive = true }
            }
            // Optionally show a toast or snackbar about permission
            Toast.makeText(
                context,
                "Notifications are disabled. You can enable them in settings.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Row(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = if (currentPage != 3) Arrangement.SpaceBetween else Arrangement.Center
    ) {
        if (currentPage == 3) {
            OutlinedButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        // For older versions, directly proceed
                        onGetStartedClicked()
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                },
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Get Started",
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 40.dp),
                    color = Gray
                )
            }
        } else {
            SkipNextButton(
                text = "Skip",
                modifier = Modifier.padding(start = 20.dp),
                pagerState = pagerState,
                isSkip = true
            )
            SkipNextButton(
                text = "Next",
                modifier = Modifier.padding(end = 20.dp),
                pagerState = pagerState,
                isSkip = false
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SkipNextButton(
    text: String,
    modifier: Modifier,
    pagerState: PagerState,
    isSkip: Boolean = false
) {
    val scope = rememberCoroutineScope()
    Text(
        text = text,
        color = Color.Black,
        modifier = modifier
            .clickable {
                if (isSkip) {
                    scope.launch {
                        pagerState.scrollToPage(pagerState.pageCount - 1)
                    }
                } else {
                    scope.launch {
                        if (pagerState.currentPage < pagerState.pageCount - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            }
            .padding(16.dp),
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium
    )
}