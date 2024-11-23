package com.example.smartpest.database

sealed interface UserEvent {
    object SaveUser: UserEvent
    data class SetName(val name: String): UserEvent
    data class SetLocation(val location: String): UserEvent
    data class SetPhone(val phone: String): UserEvent
    data class SetLanguage(val language: String): UserEvent
}