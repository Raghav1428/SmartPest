package com.example.smartpest.models

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.smartpest.ui.components.SectionLabel
import com.example.smartpest.ui.components.TomatoMark
import com.example.smartpest.viewmodels.ChatViewModel

@Composable
fun ChatPage(modifier: Modifier = Modifier, viewModel: ChatViewModel) {
    val scroll = rememberLazyListState()
    LaunchedEffect(viewModel.messageList.size, viewModel.isSending) {
        val last = viewModel.messageList.size - 1 + if (viewModel.isSending) 1 else 0
        if (last >= 0) scroll.animateScrollToItem(last)
    }
    Column(modifier.fillMaxSize().imePadding()) {
        if (viewModel.messageList.isEmpty()) {
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                item {
                    TomatoMark(Modifier.size(46.dp))
                    Spacer(Modifier.height(20.dp))
                    SectionLabel("A LITTLE TOMATO KNOW-HOW")
                    Spacer(Modifier.height(10.dp))
                    Text("Let's talk tomatoes.", style = MaterialTheme.typography.headlineLarge)
                    Spacer(Modifier.height(12.dp))
                    Text("Ask about leaf symptoms, watering or prevention. For a photo screening result, use the Scan tab.",
                        style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(listOf("What do concentric brown leaf spots mean?", "How can I keep tomato leaves dry?", "How do I check for spider mites?")) { question ->
                    OutlinedButton(onClick = { viewModel.sendMessage(question) }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                        Text(question, Modifier.padding(vertical = 8.dp))
                    }
                }
                item { Text("Educational guidance, not a confirmed diagnosis. Replies are generated online and can be wrong. Your typed messages are sent to Google's AI service.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        } else {
            LazyColumn(Modifier.weight(1f), state = scroll, contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(viewModel.messageList) { MessageBubble(it) }
                if (viewModel.isSending) item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        Text("Preparing tomato care guidance…", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        viewModel.errorMessage?.let {
            Surface(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(it, Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
        MessageInput(enabled = !viewModel.isSending, onMessageSend = viewModel::sendMessage)
    }
}

@Composable
fun MessageInput(onMessageSend: (String) -> Unit, enabled: Boolean = true) {
    var message by rememberSaveable { mutableStateOf("") }
    val send = {
        if (message.isNotBlank() && enabled) {
            onMessageSend(message.trim())
            message = ""
        }
    }
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(message, { message = it }, modifier = Modifier.weight(1f), enabled = enabled,
            placeholder = { Text("Ask about your tomatoes…") }, shape = RoundedCornerShape(20.dp), maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { send() }))
        FilledIconButton(onClick = send, enabled = enabled && message.isNotBlank(), modifier = Modifier.size(52.dp)) {
            Icon(Icons.AutoMirrored.Filled.Send, "Send tomato question")
        }
    }
}

@Composable
fun MessageBubble(messageModel: MessageModel) {
    val isAssistant = messageModel.role == "model"
    Column(Modifier.fillMaxWidth(), horizontalAlignment = if (isAssistant) Alignment.Start else Alignment.End) {
        Text(if (isAssistant) "Tomato assistant" else "You", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
        Surface(shape = RoundedCornerShape(18.dp), color = if (isAssistant) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer) {
            Text(messageModel.message, Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
