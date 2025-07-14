package cl.libroypunto.libroypunto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Star
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Download
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

// Importar LibroCatalogo desde el modelo
import cl.libroypunto.libroypunto.model.LibroCatalogo
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import cl.libroypunto.libroypunto.ui.theme.Primary40
import cl.libroypunto.libroypunto.ui.theme.Outline

@Composable
fun DetalleLibroScreen(
    libro: LibroCatalogo,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface) // Fondo igual a la app
            .verticalScroll(scrollState) // Hacer scrolleable toda la pantalla
    ) {
        // AppBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
        }
        Spacer(Modifier.height(8.dp))
        // Título grande y editorial centrados
        Text(
            text = libro.titulo,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Normal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 0.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
        Text(
            text = libro.editorial,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
        )
        // Layout principal: portada a la izquierda, info y botones a la derecha
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp), // Igual que formularios
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            // Portada
            Surface(
                modifier = Modifier
                    .size(width = 140.dp, height = 210.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                color = Color.White,
                tonalElevation = 0.dp
            ) {
                AsyncImage(
                    model = libro.imagenUrl,
                    contentDescription = "Portada del libro",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(16.dp)) // Pegado a la portada
            // Columna derecha: info y botones
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Info textual
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = libro.editorial,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = libro.titulo,
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Normal),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = libro.autor,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = if (libro.disponible > 0) "${libro.disponible} Un. Disponibles" else "No disponible",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (libro.disponible > 0) MaterialTheme.colorScheme.primary else Color.Red,
                        modifier = Modifier.padding(bottom = 0.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Botones en orden correcto: primero descarga/favorito, luego arriendo
                // Barra fusionada custom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Descarga digital (izquierda)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp))
                            .clickable { /* TODO: Acción descarga digital */ },
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Outlined.Download,
                                contentDescription = "Descarga digital",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    // Favorito (derecha)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp))
                            .clickable { /* TODO: Acción favorito */ },
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Outlined.StarOutline,
                                contentDescription = "Favorito",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Botón Arriendo presencial (debajo de los otros botones)
                OutlinedButton(
                    onClick = { /* TODO: Acción arriendo presencial */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    Text(
                        "Arriendo presencial",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        // Título de sección 'Géneros y Tags Relacionados' con separación
        Text(
            text = "Géneros y Tags Relacionados",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp)
        )
        // Chips de géneros/tags con scroll horizontal
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(libro.chips) { chip ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Text(
                        chip,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        // Sinopsis
        Text(
            text = "Sinopsis",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 8.dp)
        )
        Text(
            text = libro.sinopsis,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 24.dp)
        )
        Spacer(Modifier.height(16.dp))
        // STAR-RANK y filas de información 1:1 Figma
        Divider(modifier = Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STAR-RANK",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "4.0 STARS",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal, color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
        Text(
            text = "Estrellas de la Comunidad",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
        )
        // Fila: Descarga Digital
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Descarga Digital",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            Checkbox(
                checked = libro.digital,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
        }
        Divider(modifier = Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        // Fila: Libro Destacado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Libro Destacado",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            Checkbox(
                checked = libro.destacado,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
        }
        Divider(modifier = Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        // Fila: Lista de Espera
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Lista de Espera",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "Lista de espera bajo nivel 5",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 0.dp)
                )
            }
            Checkbox(
                checked = false,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
        }
        Divider(modifier = Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "El Stock puede estar desactualizado\nID-11",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
        )
    }
} 