package com.example.kinora

import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageButton

open class nav_bar : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    protected fun configurarNavBar() {
        val btnPeliculas = findViewById<ImageButton>(R.id.btnPeliculas)
        val btnCines = findViewById<ImageButton>(R.id.btnCines)
        val btnPlus = findViewById<ImageButton>(R.id.btnPlus)
        val btnUsuario = findViewById<ImageButton>(R.id.btnUsuario)
        val btnPeticiones = findViewById<ImageButton>(R.id.btnPeticiones)
        val btnReporteClientes =  findViewById<ImageButton>(R.id.btnRegistroClientes)
        val btnReporteCines =  findViewById<ImageButton>(R.id.btnRegistroCines)
        val rolUsuario = administradorSesiones.obtenerIdRol()
        val vistaDesNav: View? = findViewById<View>(R.id.despliegue_nav_plus)
        val vistaDesNavEnc: View? = findViewById<View>(R.id.despliegue_nav_plus_enc)
        val btnPeticiones_enc = findViewById<ImageButton?>(R.id.btnPeticiones_enc)
        val btnUsuario_enc = findViewById<ImageButton?>(R.id.btnUsuario_enc)
        val btnRegistroCines_enc = findViewById<ImageButton?>(R.id.btnReporteCines_enc)
        val btnPromocionDia = findViewById<ImageButton?>(R.id.btnPromocionDia)
        val btnGestionarSalas = findViewById<ImageButton?>(R.id.btnGestionarSalas)
        val btnGestionarFunciones = findViewById<ImageButton?>(R.id.btnGestionarFunciones)

        btnPeliculas?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val destinoClase = when (rolUsuario) {
                Roles.ENCARGADO -> peliculas_encargado::class.java
                Roles.ADMINISTRADOR -> Peliculas::class.java
                else -> Cartelera_Cliente::class.java
            }
            startActivity(Intent(this, destinoClase))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnCines?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, cines_admin::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnPlus?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if (rolUsuario == Roles.ENCARGADO) {
                if (vistaDesNavEnc?.visibility == View.VISIBLE) {
                    vistaDesNavEnc.visibility = View.GONE
                } else {
                    vistaDesNav?.visibility = View.GONE
                    vistaDesNavEnc?.visibility = View.VISIBLE
                }
            } else if (rolUsuario == Roles.ADMINISTRADOR) {
                if (vistaDesNav?.visibility == View.VISIBLE) {
                    vistaDesNav.visibility = View.GONE
                } else {
                    vistaDesNavEnc?.visibility = View.GONE
                    vistaDesNav?.visibility = View.VISIBLE
                }
            } else {
                vistaDesNav?.visibility = View.GONE
                vistaDesNavEnc?.visibility = View.GONE
            }
        }

        btnUsuario?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, Usuario::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnPeticiones?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, Peticiones::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        btnReporteClientes?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, Reporte_clientes::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        btnReporteCines?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, Reporte_por_cine::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        btnPeticiones_enc?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, peticiones_enc::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        btnUsuario_enc?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, Usuario::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        btnRegistroCines_enc?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, Reporte_cine::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnPromocionDia?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, promocion_dia::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnGestionarSalas?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, GestionarSalas::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnGestionarFunciones?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, GestionarFunciones::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}