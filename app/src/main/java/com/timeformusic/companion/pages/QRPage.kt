package com.timeformusic.companion.pages

import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.timeformusic.companion.QRScannerScreen
import com.timeformusic.companion.R
import com.timeformusic.companion.RequestCameraPermission
import com.timeformusic.companion.navigation.Routes
import com.timeformusic.companion.viewModels.SpotifyViewModel
import java.util.concurrent.Executors

@Composable
fun QRPage(spotifyViewModel: SpotifyViewModel, onNavigateToNext: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    var preview by remember { mutableStateOf<Preview?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }


    Box(modifier = Modifier
        .fillMaxSize()
        ) {
        RequestCameraPermission { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
        Box(Modifier.fillMaxSize()) {
            AndroidView(
                factory = { androidViewContext ->
                    PreviewView(androidViewContext).apply {
                        this.scaleType = PreviewView.ScaleType.FILL_CENTER
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                    cameraProviderFuture.addListener({
                        preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }
                        val cameraProvider = cameraProviderFuture.get()
                        val barcodeAnalyser = QRScannerScreen { barcodes ->
                            println(barcodes.toString())
                            barcodes.forEach { barcode ->
                                barcode.rawValue?.let { barcodeValue ->
                                    spotifyViewModel.playTrack(
                                        barcodeValue,
                                        onSuccess = {
                                            onNavigateToNext(Routes.PLAYING_PAGE)
                                        },
                                        onError = {
                                            Toast.makeText(context, "The scanned QR code is invalid", Toast.LENGTH_SHORT).show()
                                        })
                                }
                            }
                        }
                        val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                            }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            Log.d("TAG", "CameraPreview: ${e.localizedMessage}")
                        }
                    }, ContextCompat.getMainExecutor(context))
                }
            )
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.camera_solid),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(100.dp)
                )
                Text(
                    "Scan a QR code to play the song",
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}