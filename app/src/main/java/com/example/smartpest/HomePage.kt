package com.example.smartpest

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Welcome",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) {
        val cardItems = listOf(
            CardItem("PestDisease AI", R.drawable.finalaidetector),
            CardItem("Expert Support", R.drawable.finalexpertsupport),
            CardItem("Weather Report", R.drawable.finalweatherreport),
            CardItem("Farm Guide", R.drawable.finalfarmguide),
            CardItem("Nearby Shops", R.drawable.finalnearbystores),
            CardItem("Local Alerts", R.drawable.finallocalalert)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(cardItems) { item ->
                ClickableCard(
                    navController = navController,
                    title = item.title,
                    imageResource = item.imageResource
                )
            }
        }
    }
}

@Composable
fun ClickableCard(navController: NavHostController, title: String, imageResource: Int) {
    var scale by remember { mutableStateOf(1f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(8.dp)
            .padding(horizontal = 16.dp)
            .background(Color.White)
            .clickable {
                when (title) {
                    "PestDisease AI" -> navController.navigate("PestDiseaseAI")
                    "Expert Support" -> navController.navigate("ExpertSupport")
                    "Weather Report" -> navController.navigate("WeatherReport")
                    "Farm Guide" -> navController.navigate("FarmGuide")
                    "Nearby Shops" -> navController.navigate("NearbyShops")
                    "Local Alerts" -> navController.navigate("LocalAlerts")
                }
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image with custom zoom functionality using pointerInput
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            scale *= zoom
                        }
                    }
            ) {
                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight(600),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

data class CardItem(val title: String, val imageResource: Int)

@Preview(showBackground = true)
@Composable
fun PreviewHomePage() {
    HomePage(navController = rememberNavController())
}
