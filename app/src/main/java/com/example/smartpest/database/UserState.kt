package com.example.smartpest.database

data class UserState(
    val id: Int = 1,
    val name: String = "",
    val location: String = "",
    val phone: String = "",
    val language: String = "English",
    val isLoading: Boolean = false,
    val error: String? = null
)