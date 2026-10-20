package com.example.smartpest.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.smartpest.viewmodels.AuthViewModel

@Composable
fun SignUpPage(navController: NavHostController, authViewModel: AuthViewModel) =
    AuthenticationPage(navController, authViewModel, isSignUp = true)
