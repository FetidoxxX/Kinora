package com.example.kinora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import java.net.URLEncoder
import kotlin.jvm.java

class Cartelera_Cliente : AppCompatActivity(), OnItemClickListener {

    private lateinit var rvPeliculas: RecyclerView
    private lateinit var peliculaAdapter: PeliculaCarteleraAdapter
    //private val URL_CARTELERA = "http://192.168.80.25/Kinora/kinora_php/obtener_cartelera.php"
    private val URL_CARTELERA = "http://192.168.1.4/Kinora/kinora_php/obtener_cartelera.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cartelera_cliente)

        rvPeliculas = findViewById(R.id.rvPeliculas)
        rvPeliculas.layoutManager = GridLayoutManager(this, 3)

        val btnFiltros = findViewById<LinearLayout>(R.id.btnFiltros)

        btnFiltros.setOnClickListener {

            val bottomSheet = filtros()
            bottomSheet.show(supportFragmentManager, "filtros")
        }

        supportFragmentManager.setFragmentResultListener("filtros_aplicados", this) { _, bundle ->
            val clas = bundle.getString("clasificaciones", "")
            val gen = bundle.getString("generos", "")
            val tipo = bundle.getString("tipos", "")

            Log.d("CARTELERA_CLIENTEe", "Filtros recibidos -> clas: '$clas' gen: '$gen' tipo: '$tipo'")
            Toast.makeText(this, "Aplicando filtros...", Toast.LENGTH_SHORT).show()

            cargarPeliculasEnCartelera(clas ?: "", gen ?: "", tipo ?: "")
        }

        cargarPeliculasEnCartelera("", "", "")
    }

    private fun cargarPeliculasEnCartelera(clasificacionesCsv: String = "", generosCsv: String = "", tiposCsv: String = "") {
        val base = "http://192.168.1.4/Kinora/kinora_php/obtener_cartelera.php"
        val params = mutableListOf<String>()
        if (clasificacionesCsv.isNotEmpty()) params.add("clasificaciones=${URLEncoder.encode(clasificacionesCsv, "UTF-8")}")
        if (generosCsv.isNotEmpty()) params.add("generos=${URLEncoder.encode(generosCsv, "UTF-8")}")
        if (tiposCsv.isNotEmpty()) params.add("tipos=${URLEncoder.encode(tiposCsv, "UTF-8")}")

        val urlFinal = if (params.isEmpty()) base else "$base?${params.joinToString("&")}"

        android.util.Log.d("CARTELERA_CLIENTEe", "URL final -> $urlFinal")

        val stringRequest = object : com.android.volley.toolbox.StringRequest(
            Request.Method.GET,
            urlFinal,
            com.android.volley.Response.Listener<String> { response ->
                try {
                    val listaPeliculas = parsearRespuestaPeliculas(response)
                    peliculaAdapter = PeliculaCarteleraAdapter(listaPeliculas, this)
                    rvPeliculas.adapter = peliculaAdapter

                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar datos: ${e.message}", Toast.LENGTH_LONG).show()
                    android.util.Log.e("CARTELERA", "Error de parseo JSON: $response", e)
                }
            },
            com.android.volley.Response.ErrorListener { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
                android.util.Log.e("CARTELERA", "Error de Volley: ${error.message}", error)
            }
        ) {
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun parsearRespuestaPeliculas(jsonString: String): List<PeliculaCartelera> {
        val peliculas = mutableListOf<PeliculaCartelera>()
        val jsonArray = org.json.JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val pelicula = PeliculaCartelera(
                id_pelicula = jsonObject.getString("id_pelicula"),
                titulo = jsonObject.getString("titulo"),
                urlPoster = jsonObject.getString("urlPoster")
            )
            peliculas.add(pelicula)
        }
        return peliculas
    }

    override fun onItemClick(pelicula: PeliculaCartelera) {
        val intent = Intent(this, PeliculaDetalleCartelera::class.java)
        intent.putExtra("PELICULA_SELECCIONADA", pelicula)
        startActivity(intent)
    }

}