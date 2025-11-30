package com.example.kinora

interface DiaCallback {
    fun onSuccess(listaPromociones: List<Dia>)
    fun onError(mensaje: String)
}
