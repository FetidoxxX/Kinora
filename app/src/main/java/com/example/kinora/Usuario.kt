package com.example.kinora

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class Usuario : nav_bar() {

    private lateinit var adminSesiones: AdministradorSesiones
    private lateinit var tvNombreUsuario: TextView
    private lateinit var btnCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        adminSesiones = AdministradorSesiones(this)
        adminSesiones.verificarAcceso(this, listOf(Roles.ADMINISTRADOR, Roles.ENCARGADO, Roles.CLIENTE))

        setContentView(R.layout.activity_usuario)
        configurarNavBar()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            insets
        }

        tvNombreUsuario = findViewById(R.id.tv_nombre_usuario)
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion)

        val datosUsuario = adminSesiones.obtenerDatosUsuario()

        tvNombreUsuario.text = datosUsuario["nombre"] ?: "Usuario"
        btnCerrarSesion.setOnClickListener {
            adminSesiones.cerrarSesion()
        }

    }
}