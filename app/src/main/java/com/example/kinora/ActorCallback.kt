package com.example.kinora

interface ActorCallback {
    fun onSuccess(listaActores: List<Actor>)
    fun onError(mensaje: String)
}