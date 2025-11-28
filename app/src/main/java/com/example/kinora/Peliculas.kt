package com.example.kinora

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import org.json.JSONException
import android.util.Log
import android.widget.ImageButton
import android.view.View
import androidx.annotation.RequiresApi
import android.widget.LinearLayout
import android.widget.ImageView
import android.view.animation.AnimationUtils
import android.view.animation.Animation
class Peliculas : nav_bar(), DeplegableCreacion, crear_Cosas {


    private lateinit var rvPeliculas: RecyclerView

    //private val url: String = "http://192.168.80.25/kinora_php/obtener_peliculas.php" // breyner
    //private val urlcrear_Cosas: String = "http://192.168.80.25/kinora_php/" // breyner"
    //private val url: String = "http://10.0.2.2/kinora_php/obtener_peliculas.php" //Emulador
    private val url: String = "http://192.168.1.4/Kinora/kinora_php/obtener_peliculas.php" //Cristhian
    private val urlcrear_Cosas: String = "http://92.168.1.4/Kinora/kinora_php/" // breyner"


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        administradorSesiones.verificarAcceso(this, listOf(Roles.ADMINISTRADOR))

        setContentView(R.layout.activity_peliculas)
        configurarNavBar()
        despliegue(this)

        //Creación de Tipo
        val vistaCreacionTipo = findViewById<View>(R.id.crearTipo)
        inicializarCreacionTipo(
            context = this,
            vistaCreacionTipo = vistaCreacionTipo,
            baseUrl = urlcrear_Cosas
        )

        //Creación de Genero
        val vistaCreacionGenero = findViewById<View>(R.id.crearGenero)
        inicializarCreacionGenero(
            context = this,
            vistaCreacionGenero = vistaCreacionGenero,
            baseUrl = urlcrear_Cosas
        )

        //Creación de com.example.kinora.Clasificacion
        val vistaCreacionClasificacion = findViewById<View>(R.id.crearClasificacion)
        inicializarCreacionClasificacion(
            context = this,
            vistaCreacionClasificacion = vistaCreacionClasificacion,
            baseUrl = urlcrear_Cosas
        )

        //Creación de Actores
        val vistaCreacionActor = findViewById<View>(R.id.crearActor)
        inicializarCreacionActor(
            context = this,
            vistaCreacionActor = vistaCreacionActor,
            baseUrl = urlcrear_Cosas
        )

        //Creación de Directores
        val vistaCreacionDirector = findViewById<View>(R.id.crearDirector)
        inicializarCreacionDirector(
            context = this,
            vistaCreacionDirector = vistaCreacionDirector,
            baseUrl = urlcrear_Cosas
        )


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.peliculas)) { v, insets ->
            insets
        }
        rvPeliculas = findViewById(R.id.rvPeliculas)
        rvPeliculas.layoutManager = LinearLayoutManager(this)

        val btnCreacion = findViewById<ImageButton>(R.id.btnCreacion)
        val vistaCreacion = findViewById<View>(R.id.includeCreacion)
        val vistaFiltros = findViewById<View>(R.id.filtros)
        val btnFiltros = findViewById<LinearLayout>(R.id.btnFiltros)
        val btnFondoFiltros = findViewById<ImageView>(R.id.btnfondoOscurofiltros)

        btnCreacion?.setOnClickListener {
            vistaCreacion.visibility = View.VISIBLE
        }

        btnFiltros?.setOnClickListener {
            val slideIn = AnimationUtils.loadAnimation(this, R.anim.surge_up)
            vistaFiltros.visibility = View.VISIBLE
            vistaFiltros.startAnimation(slideIn)
            slideIn.setAnimationListener(null)
        }

        btnFondoFiltros?.setOnClickListener {
            vistaFiltros.visibility = View.GONE
        }

        cargarPeliculas()
    }
    private fun cargarPeliculas() {
        Log.d("PeliculasActivity", "Iniciando la carga de películas desde: $url")

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("PeliculasActivity", "Respuesta del servidor: ${response.toString()}")

                try {
                    if (response.length() == 0) {
                        Log.w("PeliculasActivity", "El servidor devolvió una lista vacía. ¿La tabla 'pelicula' tiene datos?")
                    }

                    val listaPeliculas = mutableListOf<Pelicula>()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val id = jsonObject.getString("id_pelicula")
                        val nombre = jsonObject.getString("nombre")
                        val pelicula = Pelicula(id, nombre)
                        listaPeliculas.add(pelicula)
                    }

                    val adapter = PeliculasAdapter(listaPeliculas)
                    rvPeliculas.adapter = adapter
                    Log.d("PeliculasActivity", "Adaptador asignado con ${listaPeliculas.size} películas.")

                } catch (e: JSONException) {
                    Log.e("PeliculasActivity", "Error al procesar el JSON. ¿El formato es correcto?", e)
                }
            },
            { error ->
                Log.e("PeliculasActivity", "Error de Volley: ", error)
            }
        )

        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }
}