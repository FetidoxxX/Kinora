package com.example.kinora

interface FuncionCallback {
    fun onSuccess(listaFunciones: List<Funcion>)
    fun onError(mensaje: String)
}
