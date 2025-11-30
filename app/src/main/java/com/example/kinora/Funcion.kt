package com.example.kinora

data class Funcion(
    val id_funcion: String,
    val precio_base: String,
    val fecha_hora: String,
    val nombre_pelicula: String,
    val id_pelicula: String,
    val numero_sala: String,
    val id_sala: String,
    val capacidad: String,
    val nombre_dia: String?,
    val descuento: String?,
    val id_dia: String?,
    val precio_final: Double
)
