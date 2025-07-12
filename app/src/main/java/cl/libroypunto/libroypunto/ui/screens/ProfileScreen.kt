package cl.libroypunto.libroypunto.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import cl.libroypunto.libroypunto.ui.theme.MaterialSymbols
import cl.libroypunto.libroypunto.ui.theme.MaterialSymbolText
import cl.libroypunto.libroypunto.ui.theme.PrimaryLight
import cl.libroypunto.libroypunto.ui.theme.SurfaceLight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.libroypunto.libroypunto.model.Profile
import cl.libroypunto.libroypunto.ui.components.QrGenerator
import cl.libroypunto.libroypunto.ui.components.QrScannerDialog
import cl.libroypunto.libroypunto.ui.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    var showQrScanner by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = PrimaryLight) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight
                ),
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        MaterialSymbolText(
                            text = MaterialSymbols.Icons.EDIT,
                            fontFamily = MaterialSymbols.Outlined,
                            color = PrimaryLight
                        )
                    }
                }
            )
        },
        containerColor = SurfaceLight
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                profileState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                profileState.error != null -> {
                    Text(
                        text = profileState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (isEditing) {
                            ProfileEditContent(
                                profile = profileState.profile ?: Profile(),
                                onSave = { updatedProfile ->
                                    viewModel.updateProfile(updatedProfile)
                                    isEditing = false
                                },
                                onCancel = { isEditing = false }
                            )
                        } else {
                            ProfileDisplayContent(
                                profile = profileState.profile ?: Profile()
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        if (profileState.profile?.qrId?.isNotEmpty() == true) {
                            QrGenerator(
                                content = profileState.profile!!.qrId,
                                modifier = Modifier.size(200.dp)
                            )
                        }
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val profile = profileState.profile ?: Profile()
                                    viewModel.updateProfile(
                                        profile.copy(qrId = viewModel.generateQrId())
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight)
                            ) {
                                MaterialSymbolText(
                                    text = MaterialSymbols.Icons.QR_CODE_2,
                                    fontFamily = MaterialSymbols.Outlined,
                                    color = SurfaceLight
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generar QR-ID", color = SurfaceLight)
                            }
                            
                            Button(
                                onClick = { showQrScanner = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight)
                            ) {
                                MaterialSymbolText(
                                    text = MaterialSymbols.Icons.QR_CODE_SCANNER,
                                    fontFamily = MaterialSymbols.Outlined,
                                    color = SurfaceLight
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Escanear QR", color = SurfaceLight)
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showQrScanner) {
        QrScannerDialog(
            onDismiss = { showQrScanner = false },
            onQrCodeScanned = { qrContent ->
                // Aquí puedes implementar la lógica para validar y procesar el QR escaneado
                // Por ejemplo, verificar si corresponde a un usuario válido
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileEditContent(
    profile: Profile,
    onSave: (Profile) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var email by remember { mutableStateOf(profile.email) }
    var rut by remember { mutableStateOf(profile.rut) }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = rut,
            onValueChange = { rut = it },
            label = { Text("RUT") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancelar")
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = {
                    onSave(
                        profile.copy(
                            name = name,
                            email = email,
                            rut = rut
                        )
                    )
                }
            ) {
                Text("Guardar")
            }
        }
    }
}

@Composable
private fun ProfileDisplayContent(
    profile: Profile
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProfileField("Nombre", profile.name)
        ProfileField("Email", profile.email)
        ProfileField("RUT", profile.rut)
        ProfileField("Rol", profile.role.name)
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value.ifEmpty { "No especificado" },
            style = MaterialTheme.typography.bodyLarge
        )
    }
} 