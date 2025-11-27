package com.example.kinora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
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

        cargarPeliculasEnCartelera()
    }

    private fun cargarPeliculasEnCartelera() {
        val stringRequest = object : com.android.volley.toolbox.StringRequest(
            Request.Method.GET,
            URL_CARTELERA,
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