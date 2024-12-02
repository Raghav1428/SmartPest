package com.example.smartpest.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartpest.R
import com.example.smartpest.database.Alert
import com.example.smartpest.database.AppDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val CHANNEL_ID = "smartpest_notifications"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val database = AppDatabase.getDatabase(applicationContext)

        // Check notification payload
        remoteMessage.notification?.let {notification ->
            showNotification(
                notification.title ?: "SmartPest Notification",
                notification.body ?: "You have a new update"
            )
//            scope.launch {
//                val alert = Alert(
//                    title = notification.title ?: "SmartPest Notification",
//                    message = notification.body ?: "You have a new update",
//                    timestamp = System.currentTimeMillis()
//                )
//                database.alertDao.insertAlert(alert)
//            }
        }

    }

    private fun sendRegistrationToServer(token: String) {
        val sharedPrefs = getSharedPreferences("FCM_PREFS", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("FCM_TOKEN", token).apply()
    }

    private fun showNotification(title: String, message: String) {
        createNotificationChannel()

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.applogo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        val channelName = "SmartPest Notifications"
        val channelDescription = "Notifications for SmartPest app"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}