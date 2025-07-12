package cl.libroypunto.libroypunto.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.libroypunto.libroypunto.model.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()
    
    init {
        loadProfile()
    }
    
    fun loadProfile() {
        viewModelScope.launch {
            try {
                _profileState.value = _profileState.value.copy(isLoading = true)
                val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
                
                val profileDoc = firestore.collection("profiles")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                
                val profile = profileDoc.documents.firstOrNull()?.toObject(Profile::class.java)
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    profile = profile,
                    error = null
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar el perfil"
                )
            }
        }
    }
    
    fun updateProfile(profile: Profile) {
        viewModelScope.launch {
            try {
                _profileState.value = _profileState.value.copy(isLoading = true)
                
                val updatedProfile = profile.copy(
                    updatedAt = System.currentTimeMillis()
                )
                
                if (profile.id.isEmpty()) {
                    // Crear nuevo perfil
                    firestore.collection("profiles")
                        .add(updatedProfile)
                        .await()
                } else {
                    // Actualizar perfil existente
                    firestore.collection("profiles")
                        .document(profile.id)
                        .set(updatedProfile)
                        .await()
                }
                
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    profile = updatedProfile,
                    error = null
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al actualizar el perfil"
                )
            }
        }
    }
    
    fun generateQrId(): String {
        val userId = auth.currentUser?.uid ?: return ""
        return "$userId-${System.currentTimeMillis()}"
    }
} 