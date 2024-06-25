package com.example.savushkin_practice_no2.Presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.savushkin_practice_no2.Data.Repository.ParserRepositoryImp
import com.example.savushkin_practice_no2.Data.Database.SqlRepositoryImp
import com.example.savushkin_practice_no2.Data.Repository.DatabaseRepositoryImp
import com.example.savushkin_practice_no2.ui.theme.Savushkinpractice_No2Theme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileRepository = ParserRepositoryImp()
        val repositorySQL = SqlRepositoryImp(context = applicationContext)
        val dataRepository = DatabaseRepositoryImp(repositorySQL)
        val factory = MainViewModelFactory(dataRepository, fileRepository)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        setContent {
            Savushkinpractice_No2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel,
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(    modifier: Modifier = Modifier,
                      viewModel: MainViewModel,){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_screen" ) {
        composable("main_screen"){
            MainContent(viewModel = viewModel, navController = navController)
        }
        composable("second_screen"){
            Barcode(viewModel = viewModel, navController = navController)
        }
    }
}


