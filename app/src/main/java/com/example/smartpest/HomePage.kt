package com.example.smartpest

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome") }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top=70.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // First row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ClickableCard(
                    navController = navController,
                    title = "PestDisease AI",
                    imageResource = R.drawable.aidetector
                )
                ClickableCard(
                    navController = navController,
                    title = "Expert Support",
                    imageResource = R.drawable.expertsupport
                )
            }

            // Second row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ClickableCard(
                    navController = navController,
                    title = "Weather Report",
                    imageResource = R.drawable.weatherreport
                )
                ClickableCard(
                    navController = navController,
                    title = "Local Alerts",
                    imageResource = R.drawable.localalert
                )
            }

            // Third row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ClickableCard(
                    navController = navController,
                    title = "Nearby Shops",
                    imageResource = R.drawable.nearbyshops
                )
                ClickableCard(
                    navController = navController,
                    title = "Farm Guide",
                    imageResource = R.drawable.farmguide
                )
            }
        }
    }
}

@Composable
fun ClickableCard(navController: NavHostController, title: String, imageResource: Int) {
    Card(
        modifier = Modifier
            .size(150.dp)
            .padding(8.dp)
            .clickable {
                when (title) {
                    "PestDisease AI" -> navController.navigate("PestDiseaseAI")
                    "Expert Support" -> navController.navigate("ExpertSupport")
                    "Weather Report" -> navController.navigate("WeatherReport")
                    "Local Alerts" -> navController.navigate("LocalAlerts")
                    "Nearby Shops" -> navController.navigate("NearbyShops")
                    "Farm Guide" -> navController.navigate("FarmGuide")
                }
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black
                        ),
                        startY = 200f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = Color.Blue, // Assuming white text for better visibility
                    fontSize = 14.sp, // Adjust font size as needed
                    modifier = Modifier.padding(top = 8.dp) // Add some padding above the text
                )
                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = title,
                    modifier = Modifier
                        .size(300.dp)
                        .padding(0.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = title,
                    color = Color.Blue, // Assuming white text for better visibility
                    fontSize = 14.sp, // Adjust font size as needed
                    modifier = Modifier.padding(top = 8.dp) // Add some padding above the text
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomePage() {
    HomePage(navController = rememberNavController())
}