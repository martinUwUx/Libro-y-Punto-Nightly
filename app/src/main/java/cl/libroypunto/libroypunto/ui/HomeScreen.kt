package cl.libroypunto.libroypunto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import cl.libroypunto.libroypunto.ui.theme.MaterialSymbols
import cl.libroypunto.libroypunto.ui.theme.MaterialSymbolText

// Estado de usuario para la Home
sealed class HomeUserRoleState {
    object Loading : HomeUserRoleState()
    data class Success(val isAdmin: Boolean, val userName: String?) : HomeUserRoleState()
    data class Error(val message: String) : HomeUserRoleState()
}

class HomeViewModel : ViewModel() {
    private val _userRoleState = MutableStateFlow<HomeUserRoleState>(HomeUserRoleState.Loading)
    val userRoleState: StateFlow<HomeUserRoleState> = _userRoleState

    init {
        fetchUserRole()
    }

    private fun fetchUserRole() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            _userRoleState.value = HomeUserRoleState.Error("Usuario no autenticado")
            return
        }
        val uid = user.uid
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: ""
                val name = doc.getString("name") ?: user.displayName
                _userRoleState.value = HomeUserRoleState.Success(isAdmin = (role == "DLS"), userName = name)
            }
            .addOnFailureListener { e ->
                _userRoleState.value = HomeUserRoleState.Error(e.message ?: "Error desconocido")
            }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val viewModel: HomeViewModel = viewModel()
    val userRoleState = viewModel.userRoleState.collectAsState().value

    when (userRoleState) {
        is HomeUserRoleState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeUserRoleState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = (userRoleState as HomeUserRoleState.Error).message)
            }
        }
        is HomeUserRoleState.Success -> {
            val isAdmin = (userRoleState as HomeUserRoleState.Success).isAdmin
            val userName = (userRoleState as HomeUserRoleState.Success).userName
            if (isAdmin) {
                HomeAdminUI(userName)
            } else {
                HomeNormalUI(userName)
            }
        }
    }
}

@Composable
fun HomeNormalUI(userName: String?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Barra de búsqueda rápida
            OutlinedTextField(
                value = "",
                onValueChange = {},
                enabled = false,
                readOnly = true,
                placeholder = { Text("¿Buscas algo rápido?") },
                leadingIcon = {
                    MaterialSymbolText(
                        text = MaterialSymbols.Icons.SEARCH,
                        fontFamily = MaterialSymbols.Outlined,
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )
        }
        item {
            BienvenidaHome(nombre = userName ?: "Usuario")
        }
        item {
            AccionesRapidasHome()
        }
        item {
            CardEstadoHome(
                icon = MaterialSymbols.Icons.STAR,
                title = "Hay libros vencidos",
                subtitle = "14 Estudiantes tienen libros vencidos",
                color = MaterialTheme.colorScheme.error
            )
        }
        item {
            CardAccionHome(
                icon = MaterialSymbols.Icons.MENU,
                title = "Dona un libro",
                subtitle = "Participa y gana puntos por cada donación."
            )
        }
        item {
            CardAccionHome(
                icon = MaterialSymbols.Icons.SEARCH,
                title = "Explorar",
                subtitle = "Busca entre todo el catálogo."
            )
        }
        item {
            Text(
                text = "Libro destacado",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }
        items(1) {
            CardLibroDestacadoHome(
                titulo = "El libro salvaje",
                autor = "Juan Villoro",
                editorial = "FCE"
            )
        }
    }
}

@Composable
fun HomeAdminUI(userName: String?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BienvenidaHome(nombre = userName ?: "Usuario")
        }
        item {
            AccionesRapidasAdminHome()
        }
        item {
            CardEstadoHome(
                icon = MaterialSymbols.Icons.STAR,
                title = "Hay libros vencidos",
                subtitle = "14 Estudiantes tienen libros vencidos",
                color = MaterialTheme.colorScheme.error
            )
        }
        item {
            CardAccionHome(
                icon = MaterialSymbols.Icons.MENU,
                title = "Dona un libro",
                subtitle = "Participa y gana puntos por cada donación."
            )
        }
        item {
            CardAccionHome(
                icon = MaterialSymbols.Icons.SEARCH,
                title = "Explorar",
                subtitle = "Busca entre todo el catálogo."
            )
        }
        item {
            Text(
                text = "Libro destacado",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }
        items(1) {
            CardLibroDestacadoHome(
                titulo = "El libro salvaje",
                autor = "Juan Villoro",
                editorial = "FCE"
            )
        }
    }
}

@Composable
fun BienvenidaHome(nombre: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "¡Esto es Libro y Punto!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "¡Hola!, ¿Qué quieres hacer hoy $nombre?",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun AccionesRapidasHome() {
    val primary = Color(0xFF6750A4) // Primary de Figma
    val onPrimary = Color(0xFFFFFFFF) // On Primary
    val surface = Color(0xFFFAF6FF) // Surface de Figma

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Fila superior: dos píldoras grandes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            AccesoRapidoPildora(
                text = "Activar QR-ID",
                icon = MaterialSymbols.Icons.QR_CODE_SCANNER,
                color = primary,
                textColor = onPrimary,
                border = null,
                modifier = Modifier.weight(1f)
            )
            AccesoRapidoPildora(
                text = "Escanear QR-ID",
                icon = MaterialSymbols.Icons.QR_CODE_SCANNER,
                color = primary,
                textColor = onPrimary,
                border = null,
                modifier = Modifier.weight(1f)
            )
        }
        // Fila inferior: dos outline y un botón circular
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            AccesoRapidoPildora(
                text = "Desafíos",
                icon = MaterialSymbols.Icons.STAR,
                color = surface,
                textColor = primary,
                border = BorderStroke(2.dp, primary),
                modifier = Modifier.weight(1f)
            )
            AccesoRapidoPildora(
                text = "Echo",
                icon = MaterialSymbols.Icons.PERSON,
                color = surface,
                textColor = primary,
                border = BorderStroke(2.dp, primary),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AccionesRapidasAdminHome() {
    val primary = Color(0xFF7B61FF)
    val onPrimary = Color(0xFFFFFFFF)
    val surface = Color(0xFFFAF6FF)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        AccesoRapidoPildora(
            text = "Escanear QR-ID",
            icon = MaterialSymbols.Icons.QR_CODE_SCANNER,
            color = primary,
            textColor = onPrimary,
            border = null,
            modifier = Modifier.weight(1f)
        )
        // Botón central de administración
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = primary,
            border = null,
            shadowElevation = 2.dp,
            onClick = { /* TODO: Acción admin */ }
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                MaterialSymbolText(
                    text = MaterialSymbols.Icons.STAR, // Cambia por icono de admin si lo tienes
                    fontFamily = MaterialSymbols.Outlined,
                    modifier = Modifier.size(24.dp),
                    color = onPrimary
                )
            }
        }
    }
}

@Composable
fun AccesoRapidoPildora(
    text: String,
    icon: String,
    color: Color,
    textColor: Color,
    border: BorderStroke? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        color = color,
        border = border,
        shadowElevation = if (color != Color.Transparent) 2.dp else 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            MaterialSymbolText(
                text = icon,
                fontFamily = MaterialSymbols.Outlined,
                modifier = Modifier.size(24.dp),
                color = textColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
        }
    }
}

// Cambiar la firma de CardEstadoHome y CardAccionHome para aceptar icono tipo String
@Composable
fun CardEstadoHome(icon: String, title: String, subtitle: String, color: androidx.compose.ui.graphics.Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color), // Color puro institucional
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            MaterialSymbolText(
                text = icon,
                fontFamily = MaterialSymbols.Outlined,
                modifier = Modifier.size(32.dp),
                color = Color.White // Icono blanco para contraste
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White)
            }
        }
    }
}

@Composable
fun CardAccionHome(icon: String, title: String, subtitle: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            MaterialSymbolText(
                text = icon,
                fontFamily = MaterialSymbols.Outlined,
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun CardLibroDestacadoHome(titulo: String, autor: String, editorial: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("$autor // $editorial", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Juan ya tiene planeadas las vacaciones de verano...", style = MaterialTheme.typography.bodySmall)
        }
    }
} 