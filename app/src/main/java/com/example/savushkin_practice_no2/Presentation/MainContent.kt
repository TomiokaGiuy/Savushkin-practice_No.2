package com.example.savushkin_practice_no2.Presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavHostController,
){
    var context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val valueLoading by viewModel.valueLoading.collectAsState()

    LaunchedEffect(valueLoading) {
        if(isLoading){

            if((valueLoading /100f)>=1  && viewModel.loadingTasksCompleted.value != 0){
                navController.navigate("camera_screen")
                viewModel.setLoading(false)
            }
        }
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Header()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    strokeWidth = 10.dp,
                    modifier = Modifier
                        .size(250.dp),
                )
            } else {
                MyButton(text = "Start") {
                    viewModel.setLoading(true)

                    viewModel.createTable(context, "request_nsSemk.json")
                    viewModel.createTable(context, "request_nsMc.json")
                    viewModel.writeToBd(context = context, fileName = "NS_SEMK.xml", nameTable = "NS_SEMK", viewModel = viewModel)
                    viewModel.writeToBd(context = context, fileName = "NS_MC.xml", nameTable = "NS_MC", viewModel = viewModel)


                    Log.d("Read XML", "Success")
                }
/*
                MyButton(text = "Test") {
                    navController.navigate("camera_screen")
                    Log.d("Read XML", "Success")
                }
                MyButton(text = "Table") {
                    navController.navigate("table_screen")
                    Log.d("Read XML", "Success")
                }*/
            }
        }
    }
}


@Composable
fun Header() {
    Row(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(Color.Blue)
            .padding(start = 10.dp, top = 5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Barcode", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}

@Composable
fun MyButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(width = 130.dp, height = 40.dp)
            .padding(top = 5.dp),
        shape = RoundedCornerShape(17.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = Color.Blue
        ),
    ) {
        Text(text)
    }
}