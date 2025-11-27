package com.example.kinora

data class FuncionCine(
    val id_cine: Int,
    val nombreCine: String,
    val horas: List<FuncionHora>
) : java.io.Serializable
