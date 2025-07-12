package cl.libroypunto.libroypunto.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import cl.libroypunto.libroypunto.R
import androidx.compose.ui.graphics.Color

object MaterialSymbols {
    
    // Fuentes de Material Symbols
    val Outlined = FontFamily(
        Font(R.font.material_symbols_outlined, FontWeight.Normal)
    )
    
    val Rounded = FontFamily(
        Font(R.font.material_symbols_rounded, FontWeight.Normal)
    )
    
    val Sharp = FontFamily(
        Font(R.font.material_symbols_sharp, FontWeight.Normal)
    )
    
    // Códigos Unicode de algunos iconos comunes
    object Icons {
        // Iconos de QR
        const val QR_CODE = "\uE8B1"
        const val QR_CODE_SCANNER = "\uE8B2"
        const val QR_CODE_2 = "\uE8B3"
        
        // Iconos de cámara
        const val CAMERA_ALT = "\uE3B0"
        const val CAMERA = "\uE3AF"
        
        // Iconos de perfil/usuario
        const val PERSON = "\uE7FD"
        const val ACCOUNT_CIRCLE = "\uE7FD"
        const val EDIT = "\uE3C9"
        
        // Iconos de navegación
        const val ARROW_BACK = "\uE5C4"
        const val ARROW_FORWARD = "\uE5C8"
        const val HOME = "\uE88A"
        
        // Iconos de acción
        const val ADD = "\uE145"
        const val CLOSE = "\uE5CD"
        const val CHECK = "\uE5CA"
        const val DELETE = "\uE872"
        
        // Iconos de comunicación
        const val EMAIL = "\uE0E1"
        const val PHONE = "\uE0CD"
        const val MESSAGE = "\uE0B7"
        
        // Iconos de configuración
        const val SETTINGS = "\uE8B8"
        const val NOTIFICATIONS = "\uE7F4"
        const val SEARCH = "\uE8B6"
        // Iconos adicionales usados en la app
        const val STAR = "\uE838"
        const val MENU = "\uE5D2"
    }
}

@Composable
fun MaterialSymbolText(
    text: String,
    fontFamily: FontFamily = MaterialSymbols.Outlined,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    color: Color = Color.Unspecified
) {
    androidx.compose.material3.Text(
        text = text,
        fontFamily = fontFamily,
        modifier = modifier,
        color = color
    )
} 