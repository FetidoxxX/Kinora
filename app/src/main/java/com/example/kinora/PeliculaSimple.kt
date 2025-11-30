package com.example.kinora

data class PeliculaSimple(
    val id_pelicula: String,
    val nombre: String
) {
    override fun toString(): String {
        return nombre
    }
}
