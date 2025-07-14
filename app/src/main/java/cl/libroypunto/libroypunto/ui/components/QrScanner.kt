package cl.libroypunto.libroypunto.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import cl.libroypunto.libroypunto.ui.theme.MaterialSymbols
import cl.libroypunto.libroypunto.ui.theme.MaterialSymbolText
import cl.libroypunto.libroypunto.ui.theme.PrimaryLight
import cl.libroypunto.libroypunto.ui.theme.SurfaceLight

@OptIn(ExperimentalGetImage::class)
@Composable
fun QrScanner(
    onQrCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )
    
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
    
    Box(modifier = modifier) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { context ->
                    PreviewView(context).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }.also { previewView ->
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                        
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            
                            val preview = Preview.Builder()
                                .setTargetResolution(Size(640, 480))
                                .build()
                                .also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                            
                            val imageAnalyzer = ImageAnalysis.Builder()
                                .setTargetResolution(Size(640, 480))
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also { imageAnalysis ->
                                    imageAnalysis.setAnalyzer(cameraExecutor, object : ImageAnalysis.Analyzer {
                                        @androidx.camera.core.ExperimentalGetImage
                                        override fun analyze(imageProxy: ImageProxy) {
                                            val mediaImage = imageProxy.image
                                            if (mediaImage != null) {
                                                val image = InputImage.fromMediaImage(
                                                    mediaImage,
                                                    imageProxy.imageInfo.rotationDegrees
                                                )
                                                val scanner = BarcodeScanning.getClient()
                                                scanner.process(image)
                                                    .addOnSuccessListener { barcodes ->
                                                        for (barcode in barcodes) {
                                                            if (barcode.format == Barcode.FORMAT_QR_CODE) {
                                                                barcode.rawValue?.let { qrContent ->
                                                                    onQrCodeScanned(qrContent)
                                                                }
                                                            }
                                                        }
                                                    }
                                                    .addOnCompleteListener {
                                                        imageProxy.close()
                                                    }
                                            } else {
                                                imageProxy.close()
                                            }
                                        }
                                    })
                                }
                            
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    imageAnalyzer
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(context))
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Se requiere permiso de cámara para escanear códigos QR")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Solicitar permiso")
                }
            }
        }
    }
}

@Composable
fun QrScannerDialog(
    onDismiss: () -> Unit,
    onQrCodeScanned: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Escanear código QR") },
        text = {
            QrScanner(
                onQrCodeScanned = { qrContent ->
                    onQrCodeScanned(qrContent)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight)
            ) {
                MaterialSymbolText(
                    text = MaterialSymbols.Icons.CLOSE,
                    fontFamily = MaterialSymbols.Outlined,
                    color = SurfaceLight
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar", color = SurfaceLight)
            }
        }
    )
} 