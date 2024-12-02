package com.example.smartpest.models

data class MessageModel(
    val message: String,
    val role: String,
    val imageUrl: String? = null
)
