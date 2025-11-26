package com.example.kinora

import java.io.Serializable

data class PeliculaCartelera (
    val id_pelicula: String,
    val titulo: String,
    val urlPoster: String
) : Serializable