package com.example.smartpest.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.smartpest.database.alert.Alert
import com.example.smartpest.ui.components.SectionLabel
import com.example.smartpest.viewmodels.AlertViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LocalAlerts(@Suppress("UNUSED_PARAMETER") navController: NavHostController, alertViewModel: AlertViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var enabled by remember { mutableStateOf(isNotificationsEnabled(context)) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) enabled = isNotificationsEnabled(context) }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(Unit) { alertViewModel.deleteOldAlerts() }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { enabled = isNotificationsEnabled(context) }
    val alerts by alertViewModel.allAlerts.observeAsState(emptyList())
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(Modifier.widthIn(max = 720.dp).fillMaxSize(), contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)) {
            item {
                SectionLabel("LOCAL NOTICES")
                Spacer(Modifier.height(10.dp))
                Text("Stay aware. Grow with care.", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(10.dp))
                Text("General advisories received from the notification service. These are not diagnoses or alerts generated from your leaf scans.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!enabled) item {
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Notifications are off", style = MaterialTheme.typography.titleMedium)
                        Text("You can still read saved notices. Turn notifications on if you'd like new advisories.", style = MaterialTheme.typography.bodyMedium)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) TextButton(onClick = { permission.launch(Manifest.permission.POST_NOTIFICATIONS) }) { Text("Enable notifications") }
                        TextButton(onClick = { openNotificationSettings(context) }) { Text("Notification settings") }
                    }
                }
            }
            if (alerts.isEmpty()) item {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(24.dp))
                Text("No saved notices", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("New advisories will appear here when the service sends them. Continue checking your tomatoes regularly.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(alerts, key = { it.id }) { alert -> AlertCard(alert) { alertViewModel.deleteAlert(alert.id) } }
        }
    }
}

@Composable
fun AlertCard(alert: Alert, onDelete: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(alert.title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDelete) { Icon(Icons.Outlined.DeleteOutline, "Delete notice", tint = MaterialTheme.colorScheme.error) }
            }
            Text(alert.message, style = MaterialTheme.typography.bodyMedium)
            Text(SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault()).format(Date(alert.timestamp)),
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

fun isNotificationsEnabled(context: Context): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled()

fun openNotificationSettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName))
}
