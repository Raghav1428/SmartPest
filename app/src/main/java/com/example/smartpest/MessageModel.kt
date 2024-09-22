package com.example.smartpest

data class MessageModel(
    val message: String,
    val role: String,
    val imageUrl: String? = null
)
