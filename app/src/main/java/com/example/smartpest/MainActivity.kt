package com.example.smartpest

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val weatherViewModel: WeatherViewModel = viewModel() // Create the WeatherViewModel

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginPage(navController) }
        composable("signup") { SignUpPage(navController, authViewModel) }
        composable("home") { HomePage(navController) }
        composable("pestdiseaseai") { PestDiseaseAI(navController) }
        composable("expertsupport") { ExpertSupport(navController) }
        composable("weatherreport") {
            WeatherReport(viewModel = weatherViewModel)
        }
        composable("localalerts") { LocalAlerts(navController) }
        composable("nearbyshops") { NearbyShops(navController) }
        composable("farmguide") { FarmGuide(navController) }
    }
}


