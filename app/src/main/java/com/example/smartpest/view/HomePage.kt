package com.example.smartpest.view

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RawRes
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Button
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetLayout
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetValue
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.smartpest.R
import com.example.smartpest.database.alert.Alert
import com.example.smartpest.viewmodels.AlertViewModel
import com.example.smartpest.viewmodels.UserViewModel
import com.example.smartpest.viewmodels.WeatherViewModel
import com.example.smartpest.weatherapi.NetworkResponse
import kotlinx.coroutines.launch
import java.time.LocalTime

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(
    navController: NavHostController,
    userViewModel: UserViewModel,
    weatherViewModel: WeatherViewModel,
    alertViewModel: AlertViewModel
) {
    val currentHour = LocalTime.now().hour
    val greeting = when (currentHour) {
        in 4..11 -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        else -> "Good Evening"
    }
    val userState = userViewModel.state.collectAsState().value
    val weatherState by weatherViewModel.weatherResult.observeAsState()
    val alerts by alertViewModel.allAlerts.observeAsState(emptyList())
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val isProfileComplete = remember(userState) {
        userViewModel.isProfileComplete()
    }
    var showProfilePrompt by remember {
        mutableStateOf(!isProfileComplete)
    }

    LaunchedEffect(userState.location, isProfileComplete) {
        if (isProfileComplete) {
            weatherViewModel.getData(userState.location)
            showProfilePrompt = false
        }
    }

    if (showProfilePrompt) {
        LaunchedEffect(Unit) {
            scope.launch {
                sheetState.show()
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Profile Incomplete", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Your profile details are incomplete. Please update your profile for better functionality.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.navigate("Profile Page")
                        scope.launch { sheetState.hide() }
                        showProfilePrompt = false
                    }
                ) {
                    Text("Complete Now")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        scope.launch { sheetState.hide() }
                        showProfilePrompt = false
                    }
                ) {
                    Text("Later")
                }
            }
        }
    ) {
        Scaffold { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    Text(
                        text = "$greeting, ${userState.name}",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                item {
                    when (val result = weatherState) {
                        is NetworkResponse.Loading -> {
                            WeatherLoadingPlaceholder()
                        }
                        is NetworkResponse.Success -> {
                            result.data.let { weatherModel ->
                                WeatherWidget(
                                    weatherData = WeatherData(
                                        location = weatherModel.location.name,
                                        date = weatherModel.current.last_updated.split(" ")[0],
                                        temperature = "${weatherModel.current.temp_c}°C",
                                        condition = weatherModel.current.condition.text,
                                        conditionIcon = "https:${weatherModel.current.condition.icon}",
                                        precipitation = "${weatherModel.current.precip_mm}mm",
                                        gust = "${weatherModel.current.gust_kph}km/hr"
                                    ),
                                    onClick = { navController.navigate("Weather Report") }
                                )
                            }
                        }
                        is NetworkResponse.Error -> {
                            WeatherErrorPlaceholder(result.message)
                        }
                        null -> {
                            WeatherUnavailablePlaceholder()
                        }
                    }
                }

                item {
                    LocalAlertsWidget(
                        topAlert = alerts.firstOrNull(),
                        onClick = { navController.navigate("Local Alerts") }
                    )
                }

                item {
                    DiseaseScanWidget(
                        lottieResId = R.raw.plantscan,
                        onClick = { navController.navigate("PestDisease.AI") }
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherLoadingPlaceholder() {
    val roundedCornerShape = RoundedCornerShape(30.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Color.Gray, roundedCornerShape),
        shape = roundedCornerShape,
        tonalElevation = 1.dp
    ) {
        Text(
            text = "Loading weather...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun WeatherErrorPlaceholder(message: String?) {
    val roundedCornerShape = RoundedCornerShape(30.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Color.Gray, roundedCornerShape),
        shape = roundedCornerShape,
        tonalElevation = 1.dp
    ) {
        Text(
            text = "Error: $message",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun WeatherUnavailablePlaceholder() {
    val roundedCornerShape = RoundedCornerShape(30.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Color.Gray, roundedCornerShape),
        shape = roundedCornerShape,
        tonalElevation = 1.dp
    ) {
        Text(
            text = "No weather available",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Composable
fun WeatherWidget(weatherData: WeatherData, onClick: () -> Unit) {
    val roundedCornerShape = RoundedCornerShape(30.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
            .border(2.dp, Color.Gray, roundedCornerShape),
        shape = roundedCornerShape,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${weatherData.location}, ${weatherData.date}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = weatherData.condition,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = weatherData.gust,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = weatherData.precipitation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(22.dp))
            Text(
                text = weatherData.temperature,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(4.dp))
            Image(
                painter = rememberAsyncImagePainter(model = weatherData.conditionIcon),
                contentDescription = "Weather Condition Icon",
                modifier = Modifier.size(72.dp)
            )
        }
    }
}

data class WeatherData(
    val location: String,
    val date: String,
    val temperature: String,
    val condition: String,
    val conditionIcon: String,
    val precipitation: String,
    val gust:  String
)

@Composable
fun LocalAlertsWidget(topAlert: Alert?, onClick: () -> Unit) {
    val roundedCornerShape = RoundedCornerShape(30.dp)
    if (topAlert != null) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() }
                .border(2.dp, Color.Gray, roundedCornerShape),
            shape = roundedCornerShape,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = topAlert.title,
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = topAlert.message,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    } else {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() }
                .border(2.dp, Color.Gray, roundedCornerShape),
            shape = roundedCornerShape,
            tonalElevation = 1.dp
        ) {
            Text(
                text = "No alerts available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun DiseaseScanWidget(
    @RawRes lottieResId: Int,
    onClick: () -> Unit
) {
    val roundedCornerShape = RoundedCornerShape(30.dp)

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(lottieResId)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
            .border(2.dp, Color.Gray, roundedCornerShape),
        shape = roundedCornerShape,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Farmer.AI",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Quick disease identification for your crops",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}