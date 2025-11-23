package com.example.kinora

interface GeneroCallback {
    fun onSuccess(listaGeneros: List<Genero>)
    fun onError(mensaje: String)
}