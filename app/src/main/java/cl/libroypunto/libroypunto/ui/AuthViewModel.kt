package cl.libroypunto.libroypunto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null,
    val isEmailVerified: Boolean = false,
    val verificationEmailSent: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    init {
        // Observar cambios en el estado de autenticación
        auth.addAuthStateListener { firebaseAuth ->
            _authState.value = _authState.value.copy(
                user = firebaseAuth.currentUser,
                isLoading = false,
                isEmailVerified = firebaseAuth.currentUser?.isEmailVerified ?: false
            )
        }
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                auth.signInWithEmailAndPassword(email, password).await()
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al iniciar sesión"
                )
            }
        }
    }
    
    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    // Actualizar el perfil del usuario con el nombre
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    user.updateProfile(profileUpdates).await()
                    // Enviar correo de verificación
                    sendEmailVerification()
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al crear cuenta"
                )
            }
        }
    }
    
    fun sendEmailVerification() {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                auth.currentUser?.sendEmailVerification()?.await()
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    verificationEmailSent = true,
                    error = null
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al enviar correo de verificación"
                )
            }
        }
    }

    fun checkEmailVerification() {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                auth.currentUser?.reload()?.await()
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isEmailVerified = auth.currentUser?.isEmailVerified ?: false,
                    error = null
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al verificar el correo"
                )
            }
        }
    }
    
    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                auth.sendPasswordResetEmail(email).await()
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = "Correo de recuperación enviado"
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al enviar correo de recuperación"
                )
            }
        }
    }
    
    fun signOut() {
        auth.signOut()
    }
    
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
} 