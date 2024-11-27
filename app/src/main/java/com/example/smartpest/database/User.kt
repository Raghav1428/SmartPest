package com.example.smartpest.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val name : String,
    val location : String,
    val phone : String,
    val language : String
)