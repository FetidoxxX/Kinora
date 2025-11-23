package com.example.kinora

interface TipoCallback {
    fun onSuccess(listaTipos: List<Tipo>)
    fun onError(mensaje: String)
}