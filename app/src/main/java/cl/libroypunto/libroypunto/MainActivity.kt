package cl.libroypunto.libroypunto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cl.libroypunto.libroypunto.ui.theme.LibroYPuntoTheme
import cl.libroypunto.libroypunto.ui.HomeScreen
import cl.libroypunto.libroypunto.ui.AuthNavHost
import cl.libroypunto.libroypunto.ui.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.libroypunto.libroypunto.ui.screens.DetalleLibroScreen
import cl.libroypunto.libroypunto.ui.screens.ExploreScreen
import cl.libroypunto.libroypunto.ui.screens.LibroCatalogo
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment

sealed class BottomNavScreen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : BottomNavScreen("home", "Home", Icons.Filled.Home)
    object Noticias : BottomNavScreen("noticias", "Noticias", Icons.Filled.List)
    object Explorar : BottomNavScreen("explorar", "Explorar", Icons.Filled.Search)
    object Desafios : BottomNavScreen("desafios", "Desafíos", Icons.Filled.Star)
    object Perfil : BottomNavScreen("perfil", "Perfil", Icons.Filled.Person)
}

val bottomNavItems = listOf(
    BottomNavScreen.Home,
    BottomNavScreen.Noticias,
    BottomNavScreen.Explorar,
    BottomNavScreen.Desafios,
    BottomNavScreen.Perfil
)

@Composable
fun LibroYPuntoApp() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    
    if (authState.user == null) {
        // Usuario no autenticado, mostrar pantallas de autenticación
        AuthNavHost(
            onAuthSuccess = { /* El usuario se autenticó exitosamente */ },
            authViewModel = authViewModel
        )
    } else {
        // Usuario autenticado, mostrar la app principal
        MainAppContent()
    }
}

@Composable
fun MainAppContent() {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf("home", "noticias", "explorar", "desafios", "perfil")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute ?: "explorar"
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "explorar",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("noticias") { NoticiasScreen() }
            composable("explorar") {
                ExploreScreen(
                    onLibroClick = { libro ->
                        navController.navigate("detalle/${libro.id}")
                    }
                )
            }
            composable("desafios") { DesafiosScreen() }
            composable("perfil") { PerfilScreen() }
            composable(
                "detalle/{libroId}",
                arguments = listOf(navArgument("libroId") { type = NavType.StringType })
            ) { backStackEntry ->
                val libroId = backStackEntry.arguments?.getString("libroId") ?: ""
                val viewModel = remember { cl.libroypunto.libroypunto.ui.screens.ExploreViewModel() }
                val catalogo = viewModel.catalogo.collectAsState().value
                val libro = catalogo.find { it.id == libroId }
                if (libro != null) {
                    DetalleLibroScreen(
                        libro = libro,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    // Loading o error
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Cargando libro...")
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavHostController, currentRoute: String) {
    NavigationBar(
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                alwaysShowLabel = true
            )
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    cl.libroypunto.libroypunto.ui.HomeScreen(modifier)
}

@Composable
fun NoticiasScreen(modifier: Modifier = Modifier) {
    Text("Noticias", modifier = modifier)
}

@Composable
fun ExplorarScreen(modifier: Modifier = Modifier) {
    cl.libroypunto.libroypunto.ui.screens.ExploreScreen()
}

@Composable
fun DesafiosScreen(modifier: Modifier = Modifier) {
    Text("Desafíos", modifier = modifier)
}

@Composable
fun PerfilScreen(modifier: Modifier = Modifier) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Perfil", style = MaterialTheme.typography.headlineMedium)
        
        authState.user?.let { user ->
            Text("Email: ${user.email}")
            user.displayName?.let { name ->
                Text("Nombre: $name")
            }
        }
        
        Button(
            onClick = { authViewModel.signOut() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar sesión")
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibroYPuntoTheme {
                LibroYPuntoApp()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LibroYPuntoTheme {
        Greeting("Android")
    }
}