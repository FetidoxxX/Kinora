package com.example.kinora

import java.time.LocalDateTime

data class Funcion (
    val idFuncion: Int,
    val idPelicula: Int,
    //aquí irían atributos que vaya a usar michael para tener a la mano la info de cada pelicula en cada función
    val idSala: Int,
    val numeroSala: Int,
    val columnasSillas: Int,
    val filasSillas: Int,
    val idDia: Int,
    val precioBase: Int,
    val fecha_hora: LocalDateTime,





)