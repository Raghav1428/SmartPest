package com.example.smartpest.viewmodels

import android.app.Application
import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences("appearance", Application.MODE_PRIVATE)
    private val systemDark = application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    var isDarkTheme by mutableStateOf(preferences.getBoolean("darkTheme", systemDark))
        private set

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
        preferences.edit().putBoolean("darkTheme", isDarkTheme).apply()
    }
}
