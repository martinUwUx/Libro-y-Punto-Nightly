package cl.libroypunto.libroypunto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import cl.libroypunto.libroypunto.ui.theme.MaterialSymbols
import cl.libroypunto.libroypunto.ui.theme.MaterialSymbolText
import cl.libroypunto.libroypunto.ui.theme.PrimaryLight
import cl.libroypunto.libroypunto.ui.theme.SurfaceLight

@Composable
fun AuthNavHost(
    navController: NavController = rememberNavController(),
    onAuthSuccess: () -> Unit,
    authViewModel: AuthViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val authState by authViewModel.authState.collectAsState()
    var showVerification by remember { mutableStateOf(false) }

    // Manejar la navegación basada en el estado de autenticación
    LaunchedEffect(authState.user, authState.isEmailVerified) {
        when {
            authState.user != null && !authState.isEmailVerified -> {
                showVerification = true
            }
            authState.user != null && authState.isEmailVerified -> {
                onAuthSuccess()
            }
            else -> {
                showVerification = false
            }
        }
    }

    if (showVerification) {
        EmailVerificationScreen(
            authViewModel = authViewModel,
            onVerificationComplete = { onAuthSuccess() }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAF6FF))
                .systemBarsPadding()
                .padding(horizontal = 0.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (selectedTab == 0) "¡Bienvenido de vuelta!" else "¡Encantados de conocerte!",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bienvenido a la App de Libro y Punto, tu nueva forma de disfrutar tus lecturas en la biblioteca en una sola App. ¡Iniciemos sesión y comencemos la aventura!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = PrimaryLight,
                modifier = Modifier.padding(horizontal = 0.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Iniciar sesión") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Crear cuenta") }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            when (selectedTab) {
                0 -> LoginScreen(authViewModel = authViewModel)
                1 -> SignupScreen(authViewModel = authViewModel)
            }
        }
    }
}

@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()
    val primary = PrimaryLight
    val surface = SurfaceLight

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo institucional") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        if (authState.error != null) {
            Text(authState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { authViewModel.signIn(email, password) },
            enabled = !authState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primary)
        ) {
            if (authState.isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = surface) else Text("Iniciar sesión")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { /* TODO: Navegar a recuperar credenciales */ }) {
            Text("Olvidé mis credenciales", color = primary)
        }
    }
}

@Composable
fun SignupScreen(authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()
    val primary = PrimaryLight
    val surface = SurfaceLight

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo institucional") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = rut,
            onValueChange = { rut = it },
            label = { Text("RUT") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Sin puntos ni guion") }
        )
        if (authState.error != null) {
            Text(authState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { authViewModel.signUp(email, password, "") },
            enabled = !authState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primary)
        ) {
            if (authState.isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = surface) else Text("Crear cuenta")
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()
    val primary = PrimaryLight
    val surface = SurfaceLight

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Recuperar contraseña", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (authState.error != null) {
                Text(authState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    authViewModel.resetPassword(email)
                },
                enabled = !authState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = primary)
            ) {
                if (authState.isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = surface) else Text("Enviar correo")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}

@Composable
fun EmailVerificationScreen(
    authViewModel: AuthViewModel,
    onVerificationComplete: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val primary = PrimaryLight
    val surface = SurfaceLight

    LaunchedEffect(authState.isEmailVerified) {
        if (authState.isEmailVerified) {
            onVerificationComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verifica tu correo electrónico",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Te hemos enviado un correo de verificación. Por favor, revisa tu bandeja de entrada y sigue las instrucciones para verificar tu cuenta.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (!authState.verificationEmailSent) {
            Button(
                onClick = { authViewModel.sendEmailVerification() },
                enabled = !authState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary)
            ) {
                if (authState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = surface
                    )
                } else {
                    Text("Reenviar correo de verificación")
                }
            }
        } else {
            Text(
                text = "¡Correo de verificación enviado!",
                color = Color(0xFF4CAF50),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { authViewModel.checkEmailVerification() },
            enabled = !authState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primary)
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = surface
                )
            } else {
                Text("Verificar estado")
            }
        }

        if (authState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = authState.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = { authViewModel.signOut() },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                "Cerrar sesión",
                color = primary
            )
        }
    }
} 