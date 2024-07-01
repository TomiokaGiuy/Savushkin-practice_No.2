package com.example.savushkin_practice_no2.Presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController


@Composable
fun Menu(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavHostController,
) {
    var context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { navController.navigate("camera_screen")}) {
                Text(text = "Scan")
            }
            Button(
                onClick = {
                    viewModel.writeToBd(context, "NS_SEMK.xml", "NS_SEMK",viewModel)}
            ){
                Text(text = "read NS_SEMK ")
            }
            Button(
                onClick = {
                    viewModel.writeToBd(context,"NS_MC.xml","NS_MC", viewModel)}
            ){
                Text(text = "Read NS_MC")
            }
            Button(onClick = {
                viewModel.deleteDataFromBd("NS_SEMK", null, null)
                viewModel.deleteDataFromBd("NS_MC", null, null)
            }) {
                Text(text = "Clear")
            }

            MyButton(text = "create table") {
                viewModel.createTable(context, "request.json")
                viewModel.createTable(context, "requestNS_SEMK.json")
/*                viewModel.getListTableField("NS_MC")*/
            }
            MyButton(text = "next screen") {
                navController.navigate("table_screen")
            }
        }
    }
}


