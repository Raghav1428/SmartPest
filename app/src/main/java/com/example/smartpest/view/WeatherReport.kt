package com.example.smartpest.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartpest.ui.components.SectionLabel
import com.example.smartpest.viewmodels.WeatherViewModel
import com.example.smartpest.weatherapi.NetworkResponse
import com.example.smartpest.weatherapi.WeatherModel

@Composable
fun WeatherReport(@Suppress("UNUSED_PARAMETER") navController: NavController, viewModel: WeatherViewModel) {
    var city by rememberSaveable { mutableStateOf("") }
    val weather by viewModel.weatherResult.observeAsState()
    val keyboard = LocalSoftwareKeyboardController.current
    val search = { viewModel.getData(city); keyboard?.hide(); Unit }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 720.dp).fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)) {
            SectionLabel("CONDITIONS AROUND YOUR PATCH")
            Text("Plan a little ahead.", style = MaterialTheme.typography.headlineLarge)
            Text("Check local weather to plan watering and outdoor work. Weather alone cannot diagnose a tomato disease.",
                style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(city, { city = it }, label = { Text("City or growing location") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { search() }),
                trailingIcon = { IconButton(onClick = search) { Icon(Icons.Outlined.Search, "Search weather") } })
            when (val result = weather) {
                is NetworkResponse.Success -> WeatherDetails(result.data)
                is NetworkResponse.Loading -> {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Text("Loading local weather…", style = MaterialTheme.typography.bodyMedium)
                }
                is NetworkResponse.Error -> Surface(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.medium) {
                    Text(result.message, Modifier.fillMaxWidth().padding(18.dp), style = MaterialTheme.typography.bodyMedium)
                }
                null -> Text("Enter your location to see the current conditions.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun WeatherDetails(data: WeatherModel) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(data.location.name, style = MaterialTheme.typography.headlineMedium)
        Text(data.location.country, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("${data.current.temp_c}°C", style = MaterialTheme.typography.displayLarge)
        Text(data.current.condition.text, style = MaterialTheme.typography.titleMedium)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        WeatherKeyVal("Humidity", "${data.current.humidity}%")
        WeatherKeyVal("Precipitation", "${data.current.precip_mm} mm")
        WeatherKeyVal("Wind", "${data.current.wind_kph} km/h · ${data.current.wind_dir}")
        WeatherKeyVal("Last updated", data.current.last_updated)
        Text("Weather data: WeatherAPI. Current conditions are not a forecast or a disease alert.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun WeatherKeyVal(key: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(key, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
    }
}
