package com.example.smartpest.view

object AppRoutes {
    const val HOME = "Home"
    const val SCAN = "PestDisease.AI"
    const val ASSISTANT = "AI Assistant"
    const val GUIDE = "Farm Guide"
    const val PROFILE = "Profile Page"
    const val WEATHER = "Weather Report"
    const val ALERTS = "Local Alerts"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val ONBOARDING = "onboarding"
    val mainRoutes = setOf(HOME, SCAN, ASSISTANT, GUIDE, PROFILE, WEATHER, ALERTS)

    fun startDestination(firstLaunch: Boolean, authenticated: Boolean): String = when {
        firstLaunch -> ONBOARDING
        authenticated -> HOME
        else -> LOGIN
    }

    fun title(route: String?): String = when (route) {
        HOME -> "TomatoCare"
        SCAN -> "Tomato scan"
        ASSISTANT -> "Tomato assistant"
        GUIDE -> "Tomato guide"
        PROFILE -> "Your profile"
        WEATHER -> "Growing weather"
        ALERTS -> "Local notices"
        else -> "TomatoCare"
    }
}
