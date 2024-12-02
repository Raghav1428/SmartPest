package com.example.smartpest.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartpest.database.alert.Alert
import com.example.smartpest.database.alert.AlertDao
import com.example.smartpest.database.user.User
import com.example.smartpest.database.user.UserDao

@Database(entities = [User::class, Alert::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract val userDao: UserDao
    abstract val alertDao: AlertDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                ).build().also { Instance = it }
            }
        }
    }

}