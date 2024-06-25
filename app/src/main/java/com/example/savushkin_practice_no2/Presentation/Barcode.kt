package com.example.savushkin_practice_no2.Presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun Barcode(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavHostController,
){
    Box(modifier = Modifier.fillMaxSize())
    {
        Text(text = "Camera")
    }
}