package cl.libroypunto.libroypunto.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import cl.libroypunto.libroypunto.ui.theme.MaterialSymbols
import cl.libroypunto.libroypunto.ui.theme.MaterialSymbolText
import cl.libroypunto.libroypunto.ui.theme.PrimaryLight
import cl.libroypunto.libroypunto.ui.theme.SurfaceLight

@Composable
fun QrGenerator(
    content: String,
    modifier: Modifier = Modifier,
    size: Int = 300
) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    LaunchedEffect(content) {
        if (content.isNotEmpty()) {
            qrBitmap = generateQrCode(content, size)
        }
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (qrBitmap != null) {
            Image(
                bitmap = qrBitmap!!.asImageBitmap(),
                contentDescription = "Código QR",
                modifier = Modifier.size(size.dp)
            )
        } else {
            CircularProgressIndicator()
        }
    }
}

private suspend fun generateQrCode(content: String, size: Int): Bitmap = withContext(Dispatchers.Default) {
    val hints = hashMapOf<EncodeHintType, Any>().apply {
        put(EncodeHintType.MARGIN, 1)
        put(EncodeHintType.CHARACTER_SET, "UTF-8")
    }
    
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    
    bitmap
} 