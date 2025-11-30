package com.example.kinora

interface SalaCallback {
    fun onSuccess(listaSalas: List<Sala>)
    fun onError(mensaje: String)
}
