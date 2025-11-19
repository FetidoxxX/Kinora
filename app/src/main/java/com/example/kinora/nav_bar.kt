package com.example.kinora

import android.content.Intent
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageButton

open class nav_bar : BaseActivity() {
    protected fun configurarNavBar() {
        val btnPeliculas = findViewById<ImageButton>(R.id.btnPeliculas)
        val btnCines = findViewById<ImageButton>(R.id.btnCines)
        val btnPlus = findViewById<ImageButton>(R.id.btnPlus)
        val btnUsuario = findViewById<ImageButton>(R.id.btnUsuario)
        val btnPeticiones = findViewById<ImageButton>(R.id.btnPeticiones)

        val vistaDesNav: View? = findViewById<View>(R.id.despliegue_nav_plus)

        btnPeliculas?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, Peliculas::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnCines?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, cines_admin::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnPeliculas?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, Peliculas::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnPlus?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if(vistaDesNav?.visibility == View.VISIBLE){
                vistaDesNav?.visibility = View.GONE
            }else{
                vistaDesNav?.visibility = View.VISIBLE
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
    }
}