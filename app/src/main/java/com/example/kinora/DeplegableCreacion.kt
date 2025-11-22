package com.example.kinora

import android.content.Intent
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
interface DeplegableCreacion {
    fun despliegue(activity: AppCompatActivity) {

        val btnC_Pelicula = activity.findViewById<Button>(R.id.btnC_Pelicula)

        btnC_Pelicula?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            activity.startActivity(Intent(activity, Crear_Peliculas::class.java))
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        val btnFondoOscuro = activity.findViewById<ImageView>(R.id.btnfondoOscuro)
        val vistaCreacion = activity.findViewById<View>(R.id.includeCreacion)
        btnFondoOscuro?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacion.visibility = View.GONE
        }

        //TIPOS
        val vistaCreacionTipo = activity.findViewById<View>(R.id.crearTipo)
        val btnC_Tipo = activity.findViewById<Button>(R.id.btnC_Tipo)
        val vistaAdministracionTipos = activity.findViewById<View>(R.id.AdminTipos)
        val btnCV_Tipo = vistaAdministracionTipos.findViewById<LinearLayout>(R.id.btnCrearTipoVista)
        val btnVolverTipo = vistaAdministracionTipos.findViewById<ImageView>(R.id.btnVolverTipo)

        btnC_Tipo?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaAdministracionTipos.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnCV_Tipo?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionTipo.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnVolverTipo?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaAdministracionTipos.visibility = View.GONE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        //GENEROS
        val vistaCreacionGenero = activity.findViewById<View>(R.id.crearGenero)
        val btnC_Genero = activity.findViewById<Button>(R.id.btnC_Genero)
        val vistaAdministracionGenero = activity.findViewById<View>(R.id.AdminGeneros)
        val btnCV_Genero = vistaAdministracionGenero.findViewById<LinearLayout>(R.id.btnCrearGeneroVista)
        val btnVolverGenero = vistaAdministracionGenero.findViewById<ImageView>(R.id.btnVolverGenero)

        btnC_Genero?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaAdministracionGenero.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnCV_Genero?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionGenero.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnVolverGenero?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaAdministracionGenero.visibility = View.GONE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        //CLASIFICACIÓNES
        val vistaCreacionClasificacion = activity.findViewById<View>(R.id.crearClasificacion)
        val btnC_Clasificacion = activity.findViewById<Button>(R.id.btnC_Clasificacion)
        val vistaAdministracionClasi = activity.findViewById<View>(R.id.AdminClasi)
        val btnCV_Clasi = vistaAdministracionClasi.findViewById<LinearLayout>(R.id.btnCrearClasiVista)
        val btnVolverClasi = vistaAdministracionClasi.findViewById<ImageView>(R.id.btnVolverClasi)

        btnC_Clasificacion?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaAdministracionClasi.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnCV_Clasi?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionClasificacion.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnVolverClasi?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaAdministracionClasi.visibility = View.GONE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        //DIRECTORES
        val vistaCreacionDirector = activity.findViewById<View>(R.id.crearDirector)
        val btnC_Director = activity.findViewById<Button>(R.id.btnC_Director)
        val vistaAdministracionDirector = activity.findViewById<View>(R.id.AdminDirector)
        val btnCV_Director = vistaAdministracionDirector.findViewById<LinearLayout>(R.id.btnCrearDirectorVista)
        val btnVolverDirector = vistaAdministracionDirector.findViewById<ImageView>(R.id.btnVolverDirector)

        btnC_Director?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaAdministracionDirector.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnCV_Director?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionDirector.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnVolverDirector?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaAdministracionDirector.visibility = View.GONE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        //ACTORES
        val vistaCreacionActor = activity.findViewById<View>(R.id.crearActor)
        val btnC_Actores = activity.findViewById<Button>(R.id.btnC_Actores)
        val vistaAdministracionActores = activity.findViewById<View>(R.id.AdminActores)
        val btnCV_Actores = vistaAdministracionActores.findViewById<LinearLayout>(R.id.btnCrearActoresVista)
        val btnVolverActores = vistaAdministracionActores.findViewById<ImageView>(R.id.btnVolverActores)

        btnC_Actores?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaAdministracionActores.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnCV_Actores?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionActor.visibility = View.VISIBLE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnVolverActores?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaAdministracionActores.visibility = View.GONE
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
