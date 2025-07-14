package cl.libroypunto.libroypunto.model

data class LibroCatalogo(
    val id: String = "",
    val titulo: String = "",
    val autor: String = "",
    val editorial: String = "",
    val imagenUrl: String = "",
    val destacado: Boolean = false,
    val digital: Boolean = false,
    val chips: List<String> = emptyList(),
    val disponible: Int = 0,
    val sinopsis: String = ""
) 