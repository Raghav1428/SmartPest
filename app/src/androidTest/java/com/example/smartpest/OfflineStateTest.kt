package com.example.smartpest

import androidx.test.platform.app.InstrumentationRegistry
import com.example.smartpest.viewmodels.AuthState
import com.example.smartpest.viewmodels.AuthViewModel
import com.example.smartpest.viewmodels.ChatViewModel
import org.junit.Assert.*
import org.junit.Test

class OfflineStateTest {
    @Test fun emptyAuthInputShowsErrorAndCanBeClearedWithoutNetwork() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val auth = AuthViewModel()
            auth.login("", "")
            assertTrue(auth.authState.value is AuthState.Error)
            auth.clearError()
            assertFalse(auth.authState.value is AuthState.Error)
        }
    }
    @Test fun blankAssistantMessageDoesNotStartARequestOrAddHistory() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val chat = ChatViewModel()
            chat.sendMessage("  ")
            assertTrue(chat.messageList.isEmpty())
            assertFalse(chat.isSending)
        }
    }
}
