package com.example.kinora

interface PeliculaSearchCallback {
    fun onSuccess(listaPeliculas: List<PeliculaSimple>)
    fun onError(mensaje: String)
}
