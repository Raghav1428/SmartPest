package com.example.smartpest.viewmodels

data class MessageModel(
    val message: String,
    val role: String,
    val imageUrl: String? = null
)
