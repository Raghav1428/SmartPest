package com.example.smartpest.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.smartpest.BuildConfig
import com.example.smartpest.ui.components.LeafIllustration
import com.example.smartpest.ui.components.SectionLabel
import com.example.smartpest.viewmodels.AlertViewModel
import com.example.smartpest.viewmodels.UserViewModel
import com.example.smartpest.viewmodels.WeatherViewModel
import com.example.smartpest.weatherapi.NetworkResponse
import java.time.LocalTime

@Composable
fun HomePage(navController: NavHostController, userViewModel: UserViewModel,
             weatherViewModel: WeatherViewModel, alertViewModel: AlertViewModel) {
    val user by userViewModel.state.collectAsState()
    val weather by weatherViewModel.weatherResult.observeAsState()
    val alerts by alertViewModel.allAlerts.observeAsState(emptyList())
    LaunchedEffect(user.location) {
        if (user.location.isNotBlank() && BuildConfig.WEATHER_API_KEY.isNotBlank()) weatherViewModel.getData(user.location)
    }
    val greeting = when (LocalTime.now().hour) {
        in 4..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(Modifier.widthIn(max = 720.dp).fillMaxSize(), contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(26.dp)) {
            item {
                SectionLabel(if (user.name.isBlank()) greeting else "$greeting, ${user.name.trim()}")
                Spacer(Modifier.height(10.dp))
                Text("A closer look at your tomatoes.", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(10.dp))
                Text("Understand leaf symptoms. Take the next step with care.", style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                SectionLabel("START WITH A LEAF")
                                Text("Spot a change?", style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("Check a tomato leaf photo against the model's supported conditions.", style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            LeafIllustration(Modifier.size(86.dp))
                        }
                        Button(onClick = { navController.navigate(AppRoutes.SCAN) { launchSingleTop = true } },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp)) {
                            Icon(Icons.Outlined.CenterFocusStrong, null, Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Scan a tomato leaf")
                        }
                        Text("On-device analysis · Photos aren't uploaded for scanning", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Know the signs", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = { navController.navigate(AppRoutes.GUIDE) { launchSingleTop = true } }) { Text("View guide") }
                }
                Text("Explore 8 diseases, mite damage and the healthy-leaf category. Compare symptoms before deciding what to do.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                HomeLink(Icons.AutoMirrored.Outlined.MenuBook, "Your tomato field notes", "Symptoms, prevention and practical care",
                    onClick = { navController.navigate(AppRoutes.GUIDE) { launchSingleTop = true } })
            }
            item {
                SectionLabel("A SMALL HABIT THAT HELPS")
                Spacer(Modifier.height(12.dp))
                Text("Water the roots. Keep leaves dry.", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text("Water at soil level and give plants room for airflow. Wet foliage and splashing soil can help several tomato diseases spread.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(22.dp))
                Text("Around your patch", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(14.dp))
                val success = weather as? NetworkResponse.Success
                val weatherSummary = when {
                    user.location.isBlank() -> "Add your location in Profile"
                    BuildConfig.WEATHER_API_KEY.isBlank() -> "Weather service isn't configured"
                    weather is NetworkResponse.Loading -> "Loading local weather…"
                    success != null -> "${success.data.current.temp_c}°C · ${success.data.current.condition.text}"
                    weather is NetworkResponse.Error -> "Weather unavailable · Search a location"
                    else -> "View conditions for your growing area"
                }
                HomeLink(Icons.Outlined.CloudQueue, "Growing weather", weatherSummary) {
                    navController.navigate(if (user.location.isBlank()) AppRoutes.PROFILE else AppRoutes.WEATHER) { launchSingleTop = true }
                }
                Spacer(Modifier.height(10.dp))
                HomeLink(Icons.Outlined.NotificationsNone, "Local notices",
                    if (alerts.isEmpty()) "No notices saved · General local advisories" else "${alerts.size} saved · General local advisories") {
                    navController.navigate(AppRoutes.ALERTS) { launchSingleTop = true }
                }
            }
            item {
                Text("A leaf photo is a starting point, not a confirmed diagnosis. Consult a plant clinic when symptoms persist or spread.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun HomeLink(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(onClick = onClick, color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        }
    }
}
