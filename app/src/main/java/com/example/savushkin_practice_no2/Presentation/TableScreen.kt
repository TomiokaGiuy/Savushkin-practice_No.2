package com.example.savushkin_practice_no2.Presentation

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun TableScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavHostController,
) {
    val scrollStateVertical = rememberLazyListState()
    val scrollStateHorizontal = rememberScrollState()
    val dataListProducts by viewModel.dataListProducts.observeAsState(emptyList())


    LaunchedEffect(key1 = true) {
        viewModel.getListTableField("NS_MC")
    }

    LaunchedEffect(viewModel.barcodeStr) {
        Log.d("TableScreen", "Barcode changed: ${viewModel.barcodeStr}")
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {

        Header("Result")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollStateHorizontal)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                state = scrollStateVertical
            ) {
                items(dataListProducts.size) { index ->
                    DataTableRow(index = index, contentValues = dataListProducts[index], viewModel.listReturn)
                }
            }
        }
    }
}

@Composable
fun DataTableRow(index: Int, contentValues: ContentValues, tableField: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 5.dp)
            .background(if (index % 2 == 0) Color.White else Color.LightGray)
    ) {

        tableField.forEachIndexed { index, item ->
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if(contentValues.getAsString(item).isNullOrEmpty() || item ==""){

                }else{
                    TableHeader(item)
                    TableCell(text = contentValues.getAsString(item) ?: "")
                }
            }
        }
    }
}

@Composable
fun TableHeader(text: String) {
    Column(
        modifier = Modifier
            .padding(top = 5.dp)
            .fillMaxWidth()

            .height(25.dp),
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun TableCell(text: String) {
    Column(
        modifier = Modifier
            .padding( 0.dp)
            .height(25.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = if (text.length > 50) "${text.take(49)}..." else text,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}