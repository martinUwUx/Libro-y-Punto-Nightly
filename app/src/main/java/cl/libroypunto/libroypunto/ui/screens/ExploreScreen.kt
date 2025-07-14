package cl.libroypunto.libroypunto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.Color as ComposeColor
import cl.libroypunto.libroypunto.ui.theme.MaterialSymbolText
import cl.libroypunto.libroypunto.ui.theme.MaterialSymbols
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Book
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.PagerDefaults
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.clickable
import cl.libroypunto.libroypunto.model.LibroCatalogo

// Modelo de datos para un libro destacado
data class LibroDestacado(
    val id: String = "",
    val titulo: String = "",
    val subtitulo: String = "",
    val imagenUrl: String = "",
    val chips: List<String> = emptyList(),
    val visible: Boolean = true,
    val orden: Int = 0
)

class ExploreViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _destacados = MutableStateFlow<List<LibroDestacado>>(emptyList())
    val destacados: StateFlow<List<LibroDestacado>> = _destacados
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Nuevo: catálogo completo
    private val _catalogo = MutableStateFlow<List<LibroCatalogo>>(emptyList())
    val catalogo: StateFlow<List<LibroCatalogo>> = _catalogo

    // Nuevo: destacados y digitales filtrados
    val catalogoDestacados: StateFlow<List<LibroCatalogo>> = MutableStateFlow(emptyList())
    val catalogoDigitales: StateFlow<List<LibroCatalogo>> = MutableStateFlow(emptyList())

    init {
        cargarDestacados()
        cargarCatalogo()
    }

    private fun cargarDestacados() {
        viewModelScope.launch {
            try {
                db.collection("destacados").document("libro").collection("libro")
                    .whereEqualTo("visible", true)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            _error.value = "Error al cargar destacados: ${e.message}"
                            _isLoading.value = false
                            android.util.Log.e("ExploreViewModel", "Firestore error", e)
                            return@addSnapshotListener
                        }
                        val libros = mutableListOf<LibroDestacado>()
                        snapshot?.documents?.forEach { doc ->
                            val ordenValue = try {
                                val value = doc.get("orden")
                                when (value) {
                                    is Number -> value.toInt()
                                    is String -> value.toIntOrNull() ?: 0
                                    else -> 0
                                }
                            } catch (e: Exception) { 0 }
                            val libro = LibroDestacado(
                                id = doc.id,
                                titulo = doc.getString("titulo") ?: "",
                                subtitulo = doc.getString("subtitulo") ?: "",
                                imagenUrl = doc.getString("imagenUrl") ?: "",
                                chips = (doc.get("chips") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                                visible = doc.getBoolean("visible") ?: true,
                                orden = ordenValue
                            )
                            libros.add(libro)
                        }
                        // Ordenar manualmente si no existe el campo 'orden'
                        _destacados.value = libros.sortedBy { it.orden }
                        _isLoading.value = false
                        _error.value = if (libros.isEmpty()) "No hay libros destacados disponibles." else null
                        android.util.Log.d("ExploreViewModel", "Libros destacados cargados: ${libros.size}")
                    }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Error inesperado: ${e.message}"
                android.util.Log.e("ExploreViewModel", "Excepción inesperada", e)
            }
        }
    }

    private fun cargarCatalogo() {
        viewModelScope.launch {
            try {
                db.collection("catalogo")
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            _error.value = "Error al cargar catálogo: ${e.message}"
                            return@addSnapshotListener
                        }
                        val libros = mutableListOf<LibroCatalogo>()
                        snapshot?.documents?.forEach { doc ->
                            val libro = LibroCatalogo(
                                id = doc.id,
                                titulo = doc.getString("titulo") ?: "",
                                autor = doc.getString("autor") ?: "",
                                editorial = doc.getString("editorial") ?: "",
                                imagenUrl = doc.getString("imagenUrl") ?: "",
                                destacado = doc.getBoolean("destacado") ?: false,
                                digital = doc.getBoolean("digital") ?: false,
                                chips = (doc.get("chips") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                                disponible = (doc.getLong("disponible") ?: 0L).toInt(),
                                sinopsis = doc.getString("sinopsis") ?: ""
                            )
                            libros.add(libro)
                        }
                        _catalogo.value = libros
                        // Actualiza los flows filtrados
                        (catalogoDestacados as MutableStateFlow).value = libros.filter { it.destacado }
                        (catalogoDigitales as MutableStateFlow).value = libros.filter { it.digital }
                    }
            } catch (e: Exception) {
                _error.value = "Error inesperado catálogo: ${e.message}"
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ExploreScreen(
    onLibroClick: (LibroCatalogo) -> Unit = {}
) {
    val viewModel = remember { ExploreViewModel() }
    val destacados by viewModel.destacados.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Catálogo", "Destacados", "Digitales")
    var searchQuery by remember { mutableStateOf("") }
    val catalogo = viewModel.catalogo.collectAsState().value
    val catalogoDestacados = viewModel.catalogoDestacados.collectAsState().value
    val catalogoDigitales = viewModel.catalogoDigitales.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Buscar libros...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = CircleShape,
            singleLine = true
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp), // quitar paddings laterales
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                BannerCarruselDestacados(destacados)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.padding(top = 0.dp, bottom = 8.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
            if (selectedTab == 0) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LazyRow(
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filtros = listOf("Más populares", "Novedades", "Clásicos", "Premiados")
                        items(filtros) { filtro ->
                            FilterChip(
                                selected = false,
                                onClick = { },
                                label = { Text(filtro) },
                                shape = CircleShape
                            )
                        }
                    }
                }
            }
            // Estado y libros
            when {
                isLoading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                }
                error != null -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { Text(error ?: "Error") }
                }
                catalogo.isEmpty() -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { Text("No hay libros disponibles") }
                }
                else -> items(catalogo) { libro ->
                    CardLibroCatalogoVertical(
                        libro,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onLibroClick = onLibroClick
                    )
                }
            }
            // Spacer para scroll extra
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BannerCarruselDestacados(destacados: List<LibroDestacado>) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val pagerState = rememberPagerState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp) // Mantener altura, sin bordes ni paddings
    ) {
        if (destacados.isNotEmpty()) {
            HorizontalPager(
                count = destacados.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                itemSpacing = 0.dp // sin espacio entre banners
            ) { page ->
                BannerDestacadoCompleto(destacados[page], screenWidth)
            }
        } else {
            // Banner placeholder si no hay destacados
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "¡Sin destacados!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun BannerDestacadoCompleto(destacado: LibroDestacado, width: Dp) {
    val context = LocalContext.current
    var dominantColor by remember { mutableStateOf(ComposeColor(0xFF6750A4)) }
    var textColor by remember { mutableStateOf(ComposeColor.White) }
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            // Sin clip ni padding para ocupar todo el ancho
    ) {
        // Imagen de fondo
        AsyncImage(
            model = destacado.imagenUrl,
            contentDescription = "Portada ${destacado.titulo}",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            onSuccess = { state ->
                val drawable = state.painter as? BitmapDrawable
                drawable?.bitmap?.let { bitmap ->
                    Palette.from(bitmap).generate { palette ->
                        palette?.let { p ->
                            val dominant = p.getDominantColor(ComposeColor.Gray.toArgb())
                            dominantColor = ComposeColor(dominant)
                            val luminance = (0.299 * (dominant shr 16 and 0xFF) + 0.587 * (dominant shr 8 and 0xFF) + 0.114 * (dominant and 0xFF)) / 255
                            textColor = if (luminance > 0.5) ComposeColor.Black else ComposeColor.White
                        }
                    }
                }
            }
        )
        // Overlay para contraste
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            dominantColor.copy(alpha = 0.7f),
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )
        // Contenido editorial sobre la imagen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = destacado.titulo.ifBlank { "¡Libro destacado!" },
                    style = MaterialTheme.typography.headlineLarge,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                if (destacado.subtitulo.isNotEmpty()) {
                    Text(
                        text = destacado.subtitulo,
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor.copy(alpha = 0.9f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    destacado.chips.take(4).forEach { chip ->
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = chip,
                                    color = textColor,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.Black.copy(alpha = 0.4f)
                            ),
                            shape = CircleShape
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CatalogoScreen(libros: List<LibroCatalogo>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Chips de filtros
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filtros = listOf("Más populares", "Novedades", "Clásicos", "Premiados")
            items(filtros) { filtro ->
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text(filtro) },
                    shape = CircleShape
                )
            }
        }
        // Grid real de libros (2 columnas)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(libros) { libro ->
                CardLibroCatalogoVertical(libro)
            }
        }
    }
}

// Ajustar la card para que toda la info sea visible
@Composable
fun CardLibroCatalogoVertical(
    libro: LibroCatalogo,
    modifier: Modifier = Modifier,
    onLibroClick: ((LibroCatalogo) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .then(
                if (onLibroClick != null)
                    Modifier.clickable { onLibroClick(libro) }
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Imagen cuadrada (1:1), recortada por abajo, mostrando siempre la parte superior
            AsyncImage(
                model = libro.imagenUrl,
                contentDescription = "Portada de ${libro.titulo}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // Imagen cuadrada
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter // Siempre mostrar la parte superior
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = libro.editorial,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                text = libro.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                text = libro.autor,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            // Sinopsis, máximo 2 líneas
            if (libro.sinopsis.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = libro.sinopsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Chips (si hay)
            if (libro.chips.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(libro.chips) { chip ->
                        AssistChip(
                            onClick = {},
                            label = { Text(chip, style = MaterialTheme.typography.labelSmall) },
                            shape = CircleShape,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DestacadosScreen(libros: List<LibroCatalogo>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Todos los destacados",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(libros) { libro ->
                CardLibroCatalogo(libro)
            }
        }
    }
}

@Composable
fun DigitalesScreen(libros: List<LibroCatalogo>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Libros digitales",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(libros) { libro ->
                CardLibroCatalogo(libro)
            }
        }
    }
}

@Composable
fun CardLibroCatalogo(libro: LibroCatalogo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = libro.imagenUrl,
                contentDescription = "Portada ${libro.titulo}",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    libro.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    libro.autor,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    libro.editorial,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    libro.chips.take(3).forEach { chip ->
                        AssistChip(
                            onClick = {},
                            label = { Text(chip, style = MaterialTheme.typography.labelSmall) },
                            shape = CircleShape,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
} 