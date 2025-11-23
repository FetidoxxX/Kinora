package com.example.kinora

interface DirectorCallback {
    fun onSuccess(listaDirectores: List<Director>)
    fun onError(mensaje: String)
}