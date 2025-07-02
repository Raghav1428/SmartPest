package com.example.smartpest.database

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.lifecycle.ViewModel
import com.example.smartpest.database.alert.AlertDao
import com.example.smartpest.database.user.UserDao
import com.example.smartpest.viewmodels.AlertViewModel
import com.example.smartpest.viewmodels.UserViewModel

object DatabaseProvider {
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "user_database"
            ).build().also { database = it }
        }
    }

    class UserViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(userDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    class AlertViewModelFactory(private val alertDao: AlertDao): ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(AlertViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlertViewModel(alertDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}