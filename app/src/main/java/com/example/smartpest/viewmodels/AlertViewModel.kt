package com.example.smartpest.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpest.database.Alert
import com.example.smartpest.database.AlertDao
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AlertViewModel(private val alertDao: AlertDao): ViewModel() {

    val allAlerts: Flow<List<Alert>> = alertDao.getAllAlerts()

    // Function to handle Firebase Remote Message and save to database
    fun handleRemoteMessage(remoteMessage: RemoteMessage) {
        viewModelScope.launch {
            // Extract title and message from the remote message
            val title = remoteMessage.notification?.title ?: "SmartPest Notification"
            val message = remoteMessage.notification?.body ?: "You have a new update"

            // Create and insert the alert
            val alert = Alert(
                title = title,
                message = message,
                timestamp = System.currentTimeMillis()
            )
            alertDao.insertAlert(alert)
        }
    }

    fun deleteAlert(alertId: Int) {
        viewModelScope.launch {
            alertDao.deleteAlert(alertId)
        }
    }

    fun clearOldAlerts() {
        viewModelScope.launch {
            val oneWeekAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(15)
            alertDao.deleteOldAlerts(oneWeekAgo)
        }
    }

}