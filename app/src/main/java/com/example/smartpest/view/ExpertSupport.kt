package com.example.smartpest.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smartpest.models.ChatPage
import com.example.smartpest.viewmodels.ChatViewModel

@Composable
fun ExpertSupport(@Suppress("UNUSED_PARAMETER") navController: NavHostController,
                  chatViewModel: ChatViewModel = viewModel()) {
    ChatPage(viewModel = chatViewModel)
}
