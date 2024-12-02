package com.example.smartpest.models

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.smartpest.ui.theme.modelMessage
import com.example.smartpest.ui.theme.userMessage
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import com.example.smartpest.viewmodels.ChatViewModel

@Composable
fun ChatPage(modifier: Modifier = Modifier, viewModel: ChatViewModel) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                )
            )
    ) {
        MessageList(
            modifier = Modifier
                .weight(1f),
            messageList = viewModel.messageList
        )
        MessageInput(onMessageSend = {
            viewModel.sendMessage(it)
        }
        )
    }
}

@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {

    var message by remember {
        mutableStateOf("")
    }

    val backgroundColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .padding(8.dp)
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .padding(start = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = {
                message = it
            },
            placeholder = {
                Text(text = "Type your message here...", color = textColor.copy(alpha = 0.6f))
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedLabelColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = textColor,
            ),
            modifier = Modifier
                .weight(1f),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),

            keyboardActions = KeyboardActions(onSend = {
                if (message.isNotEmpty()) {
                    onMessageSend(message)
                    message = ""
                }
            })
        )
        Text(
            text = "Send",
            color = Color.Blue,
            fontWeight = FontWeight.W600,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                //.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp)
                .clickable {
                    if (message.isNotEmpty()) {
                        onMessageSend(message)
                        message = ""
                    }
                }
        )
    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true
    ) {
        items(messageList.reversed()) {
            MessageRole(messageModel = it)
        }
    }
}

@Composable
fun MessageRole(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if (isModel) 8.dp else 70.dp,
                        end = if (isModel) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(48f))
                    .background(if (isModel) modelMessage else userMessage)
                    .padding(16.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = messageModel.message,
                        fontWeight = FontWeight.W500,
                        color = Color.White
                    )
                }
            }
        }
    }
}