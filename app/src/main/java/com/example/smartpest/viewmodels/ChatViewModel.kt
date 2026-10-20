package com.example.smartpest.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpest.BuildConfig
import com.example.smartpest.models.MessageModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    val messageList = mutableStateListOf<MessageModel>()
    var isSending by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    private val successfulHistory = mutableListOf<MessageModel>()
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            systemInstruction = content {
                text("""You are TomatoCare's educational tomato-growing assistant. Discuss tomatoes only.
                    |Politely redirect questions about other crops or unrelated topics to tomato care.
                    |Do not claim to have analyzed an image: the separate on-device classifier performs image screening.
                    |Ask about visible symptoms, leaf underside, weather and spread when needed. Explain uncertainty and similar causes.
                    |Never state that a photo or chat confirms a disease, estimate measured accuracy, or invent laboratory results.
                    |Provide conservative integrated pest management advice, not pesticide dosages or guaranteed cures.
                    |Recommend a local extension adviser for confirmation, urgent spread or pesticide selection; local registration and labels apply.
                    |Viruses have no curative treatment. Spider mites cause pest damage, not an infectious disease.
                    |Keep answers concise, practical and clearly distinguish observations from possibilities.""".trimMargin())
            }
        )
    }

    fun sendMessage(question: String) {
        val text = question.trim()
        if (text.isEmpty() || isSending) return
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            errorMessage = "The optional assistant isn't configured. You can still scan leaves and use the tomato guide offline."
            return
        }
        isSending = true
        errorMessage = null
        messageList.add(MessageModel(text, "user"))
        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(history = successfulHistory.map { message ->
                    content(message.role) { text(message.message) }
                })
                val reply = chat.sendMessage(text).text?.takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException("Empty assistant response")
                val response = MessageModel(reply, "model")
                messageList.add(response)
                successfulHistory.add(MessageModel(text, "user"))
                successfulHistory.add(response)
                // Keep requests bounded; the visible conversation is not truncated.
                while (successfulHistory.size > 20) repeat(2) { successfulHistory.removeAt(0) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                errorMessage = "The assistant couldn't respond. Check your connection and try again, or use the offline tomato guide."
            } finally {
                isSending = false
            }
        }
    }
}
