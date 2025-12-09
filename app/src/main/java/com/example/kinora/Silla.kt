package com.example.kinora

data class Silla(
    val id: Int,
    val fila: String,
    val columna: Int,
    var estado: EstadoSilla
)

enum class EstadoSilla {
    DISPONIBLE,
    OCUPADA,
    SELECCIONADA
}