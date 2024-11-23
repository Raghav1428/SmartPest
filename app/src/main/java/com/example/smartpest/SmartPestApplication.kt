package com.example.smartpest

import android.app.Application
import com.example.smartpest.database.AppDatabase

class SmartPestApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        // Initialize database
        database
    }
}