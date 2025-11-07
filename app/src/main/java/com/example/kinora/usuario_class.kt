package com.example.kinora

data class usuario_class(
    val id_u: Int,

    val documento: String,

    val id_tipo_doc: Int,

    val rol_id: Int,

    val usuario: String,

    val nombre: String,

    val email: String,

    val clave: String,

    val codigo: String?
)