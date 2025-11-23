package com.example.kinora

interface ClasiCallback {
    fun onSuccess(listaClasi: List<Clasificacion>)
    fun onError(mensaje: String)
}