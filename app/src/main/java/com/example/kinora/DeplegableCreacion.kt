package com.example.kinora

import android.content.Intent
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import android.widget.ImageView

interface DeplegableCreacion {
    fun despliegue(activity: AppCompatActivity) {

        val btnC_Pelicula = activity.findViewById<Button>(R.id.btnC_Pelicula)
        val btnC_Director = activity.findViewById<Button>(R.id.btnC_Director)
        val btnC_Genero = activity.findViewById<Button>(R.id.btnC_Genero)
        val btnC_Clasificacion = activity.findViewById<Button>(R.id.btnC_Clasificacion)
        val btnC_Tipo = activity.findViewById<Button>(R.id.btnC_Tipo)
        val btnC_Actores = activity.findViewById<Button>(R.id.btnC_Actores)
        val btnFondoOscuro = activity.findViewById<ImageView>(R.id.btnfondoOscuro)
        val vistaCreacion = activity.findViewById<View>(R.id.includeCreacion)
        val vistaCreacionTipo = activity.findViewById<View>(R.id.crearTipo)
        val vistaCreacionGenero = activity.findViewById<View>(R.id.crearGenero)
        val vistaCreacionClasificacion = activity.findViewById<View>(R.id.crearClasificacion)
        val vistaCreacionDirector = activity.findViewById<View>(R.id.crearDirector)
        val vistaCreacionActor = activity.findViewById<View>(R.id.crearActor)

        btnC_Pelicula?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            activity.startActivity(Intent(activity, Crear_Peliculas::class.java))
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnC_Director?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionDirector.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnC_Genero?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionGenero.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnC_Clasificacion?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionClasificacion.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnC_Tipo?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionTipo.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnC_Actores?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionActor.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnFondoOscuro?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacion.visibility = View.GONE
        }
    }
}
