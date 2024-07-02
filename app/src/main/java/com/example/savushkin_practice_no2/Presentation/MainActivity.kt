package com.example.savushkin_practice_no2.Presentation

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.savushkin_practice_no2.Data.Database.SqlRepositoryImp
import com.example.savushkin_practice_no2.Data.Repository.DatabaseRepositoryImp
import com.example.savushkin_practice_no2.Data.Repository.ParserRepositoryImp
import com.example.savushkin_practice_no2.ui.theme.Savushkinpractice_No2Theme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var navController: NavHostController
    private lateinit var pLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerPermissionListener()
        checkCameraPermission()

        val fileRepository = ParserRepositoryImp()
        val repositorySQL = SqlRepositoryImp(context = applicationContext)
        val dataRepository = DatabaseRepositoryImp(repositorySQL)
        val factory = MainViewModelFactory(dataRepository, fileRepository)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        setContent {
            Savushkinpractice_No2Theme {
                navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel,
                        navController = navController,
                        onScan = ::scan
                    )
                }
            }
        }
    }

    private fun checkCameraPermission(){
        when{
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ->
            {
                Toast.makeText(this, "Camera run", Toast.LENGTH_LONG).show()
            }

            else -> {
                pLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }
    private fun registerPermissionListener(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()){
            if(it){
                Toast.makeText(this, "Camera run", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun scan(barcode: String) {
        Toast.makeText(this, "Scanned code: $barcode", Toast.LENGTH_LONG).show()
        Log.d(" Barcode", "$barcode")

        lifecycleScope.launch {
            viewModel.updateBarcode(barcode, viewModel)
        }
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavHostController,
    onScan: (String) -> Unit
){
    NavHost(navController = navController, startDestination = "main_screen" ) {
        composable("main_screen"){
            MainContent(viewModel = viewModel, navController = navController)
        }
        composable("table_screen"){
            TableScreen(viewModel = viewModel, navController = navController)
        }

        composable("camera_screen"){
            Camera(navController = navController, onScan = onScan)
        }
    }
}


