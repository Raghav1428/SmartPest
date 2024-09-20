package com.example.smartpest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginPage(navController) }
        composable("signup") { SignUpPage(navController) }
        composable("home") { HomePage(navController) }
        composable("pestdiseaseai") { PestDiseaseAI(navController) }
        composable("expertsupport") { ExpertSupport(navController) }
        composable("weatherreport") { WeatherReport(navController) }
        composable("localalerts") { LocalAlerts(navController) }
        composable("nearbyshops") { NearbyShops(navController) }
        composable("farmguide") { FarmGuide(navController) }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginPage() {
    LoginPage(navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUpPage() {
    SignUpPage(navController = rememberNavController())
}

