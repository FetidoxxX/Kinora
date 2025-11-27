package com.example.kinora

data class PeliculaDetalle(
    val id_pelicula: String,
    val titulo: String,
    val urlPoster: String,
    val sinopsis: String,
    val director: String,
    val reparto: String,
    val genero: String,
    val clasificacion: String,
    val tipo: String
) : java.io.Serializable