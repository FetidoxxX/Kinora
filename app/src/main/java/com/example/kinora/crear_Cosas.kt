package com.example.kinora

import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

interface crear_Cosas {
    fun tipo(activity: AppCompatActivity) {

        val vistaCreacionTipo = activity.findViewById<View>(R.id.crearTipo)
        val btnFondoOscuroTipo = vistaCreacionTipo.findViewById<ImageView>(R.id.btnfondoOscuroTipo)
        val vistaCreacionActores = activity.findViewById<View>(R.id.crearActor)
        val btnFondoOscuroActores = vistaCreacionActores.findViewById<ImageView>(R.id.btnfondoOscuroActores)
        val vistaCreacionClasificacion = activity.findViewById<View>(R.id.crearClasificacion)
        val btnFondoOscuroClasificacion = vistaCreacionClasificacion.findViewById<ImageView>(R.id.btnfondoOscuroClasificacion)
        val vistaCreacionDirector = activity.findViewById<View>(R.id.crearDirector)
        val btnFondoOscuroDirector = vistaCreacionDirector.findViewById<ImageView>(R.id.btnfondoOscuroDirector)
        val vistaCreacionGenero = activity.findViewById<View>(R.id.crearGenero)
        val btnFondoOscuroGenero = vistaCreacionGenero.findViewById<ImageView>(R.id.btnfondoOscuroGenero)

        btnFondoOscuroTipo?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionTipo.visibility = View.GONE
        }

        btnFondoOscuroActores?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionActores.visibility = View.GONE
        }

        btnFondoOscuroClasificacion?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionClasificacion.visibility = View.GONE
        }

        btnFondoOscuroDirector?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionDirector.visibility = View.GONE
        }

        btnFondoOscuroGenero?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionGenero.visibility = View.GONE
        }
    }
}
