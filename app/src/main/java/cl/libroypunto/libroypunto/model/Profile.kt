package cl.libroypunto.libroypunto.model

data class Profile(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val rut: String = "",
    val qrId: String = "",
    val role: UserRole = UserRole.STUDENT,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class UserRole {
    STUDENT,
    LIBRARIAN,
    ADMIN
} 