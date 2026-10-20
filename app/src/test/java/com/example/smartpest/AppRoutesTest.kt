package com.example.smartpest

import com.example.smartpest.view.AppRoutes
import org.junit.Assert.*
import org.junit.Test

class AppRoutesTest {
    @Test fun `returning authenticated user lands on the registered Home route`() {
        assertEquals("Home", AppRoutes.startDestination(firstLaunch = false, authenticated = true))
    }
    @Test fun `first launch shows onboarding and returning guest sees login`() {
        assertEquals("onboarding", AppRoutes.startDestination(firstLaunch = true, authenticated = true))
        assertEquals("login", AppRoutes.startDestination(firstLaunch = false, authenticated = false))
    }
    @Test fun `internal routes display readable tomato focused titles`() {
        assertEquals("Tomato scan", AppRoutes.title("PestDisease.AI"))
        assertEquals("Tomato guide", AppRoutes.title("Farm Guide"))
        assertEquals("Tomato assistant", AppRoutes.title("AI Assistant"))
    }
}
