package com.example.kinora

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

open class nav_bar : AppCompatActivity() {
    protected fun configurarNavBar() {
        val btnPeliculas = findViewById<ImageButton>(R.id.btnPeliculas)
        val btnCines = findViewById<ImageButton>(R.id.btnCines)
        val btnPlus = findViewById<ImageButton>(R.id.btnPlus)

        btnPeliculas?.setOnClickListener {
            startActivity(Intent(this, Peliculas::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnCines?.setOnClickListener {
            startActivity(Intent(this, cines_admin::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnPlus?.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}