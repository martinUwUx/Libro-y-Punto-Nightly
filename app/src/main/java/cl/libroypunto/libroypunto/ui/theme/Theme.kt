package cl.libroypunto.libroypunto.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary40,
    onPrimary = OnPrimary40,
    primaryContainer = Primary90,
    onPrimaryContainer = OnPrimary90,
    secondary = Secondary40,
    onSecondary = OnSecondary40,
    secondaryContainer = Secondary90,
    onSecondaryContainer = OnSecondary90,
    tertiary = Tertiary40,
    onTertiary = OnTertiary40,
    tertiaryContainer = Tertiary90,
    onTertiaryContainer = OnTertiary90,
    error = Error40,
    onError = OnError40,
    errorContainer = Error90,
    onErrorContainer = OnError90,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    inversePrimary = InversePrimary
)

private val LightColorScheme = lightColorScheme(
    primary = Primary40,
    onPrimary = OnPrimary40,
    primaryContainer = Primary90,
    onPrimaryContainer = OnPrimary90,
    secondary = Secondary40,
    onSecondary = OnSecondary40,
    secondaryContainer = Secondary90,
    onSecondaryContainer = OnSecondary90,
    tertiary = Tertiary40,
    onTertiary = OnTertiary40,
    tertiaryContainer = Tertiary90,
    onTertiaryContainer = OnTertiary90,
    error = Error40,
    onError = OnError40,
    errorContainer = Error90,
    onErrorContainer = OnError90,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    inversePrimary = InversePrimary
)

@Composable
fun LibroYPuntoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desactivar color dinámico para respetar identidad visual
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}