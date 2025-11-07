package com.example.kinora

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    private lateinit var adminSesiones: AdministradorSesiones
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private val INACTIVITY_TIMEOUT_MS: Long = 10 * 60 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adminSesiones = AdministradorSesiones(this)

        runnable = Runnable {
            adminSesiones.cerrarSesion()
        }

        // Iniciar el temporizador
        resetInactivityTimer()
    }

    protected fun resetInactivityTimer() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, INACTIVITY_TIMEOUT_MS)
    }

    protected fun stopInactivityTimer() {
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        resetInactivityTimer()
    }

    override fun onPause() {
        super.onPause()
        stopInactivityTimer()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        resetInactivityTimer()
    }
}