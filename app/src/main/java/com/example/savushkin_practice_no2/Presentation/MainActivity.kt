package com.example.savushkin_practice_no2.Presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.savushkin_practice_no2.Data.Repository.ParserRepositoryImp
import com.example.savushkin_practice_no2.Data.Database.SqlRepositoryImp
import com.example.savushkin_practice_no2.Data.Repository.DatabaseRepositoryImp
import com.example.savushkin_practice_no2.ui.theme.Savushkinpractice_No2Theme
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import java.io.IOException
import com.google.zxing.BarcodeFormat


class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        composable("menu_screen"){
            Menu(viewModel = viewModel, navController = navController)
        }
        composable("table_screen"){
            TableScreen(viewModel = viewModel, navController = navController)
        }

        composable("camera_screen"){
            Camera(viewModel = viewModel, navController = navController, onScan = onScan)
        }
    }
}

@Composable
fun RequestCameraPermission(onPermissionGranted: @Composable () -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasPermission) {
        onPermissionGranted()
    }
}
