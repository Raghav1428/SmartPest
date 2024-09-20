package com.example.smartpest

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PestDiseaseAI(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PestDisease AI") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
        }
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPestDiseaseAI() {
    PestDiseaseAI(navController = rememberNavController())
}