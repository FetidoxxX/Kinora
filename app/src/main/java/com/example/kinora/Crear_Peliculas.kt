package com.example.kinora

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import com.android.volley.toolbox.JsonArrayRequest
import android.app.DownloadManager.Request
class Crear_Peliculas : nav_bar(), DeplegableCreacion, crear_Cosas {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_peliculas)
        configurarNavBar()
        despliegue(this)
        tipo(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            insets
        }

        val btnCreacion = findViewById<ImageButton>(R.id.btnCreacion)
        val vistaCreacion = findViewById<View>(R.id.includeCreacion)

        btnCreacion?.setOnClickListener {
            vistaCreacion.visibility = View.VISIBLE
        }
    }

}