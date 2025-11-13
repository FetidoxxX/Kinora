package com.example.kinora

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import org.json.JSONException
import android.util.Log

class Peliculas : nav_bar() {

    private lateinit var adminSesiones: AdministradorSesiones
    private lateinit var rvPeliculas: RecyclerView
    private val url: String = "http://172.20.10.3/kinora_php/obtener_peliculas.php"
    //private val url: String = "http://10.0.2.2/kinora_php/obtener_peliculas.php" //Emulador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        adminSesiones = AdministradorSesiones(this)
        adminSesiones.verificarAcceso(this, listOf( Roles.ADMINISTRADOR, Roles.ENCARGADO))

        setContentView(R.layout.activity_peliculas)
        configurarNavBar()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvPeliculas = findViewById(R.id.rvPeliculas)
        rvPeliculas.layoutManager = LinearLayoutManager(this)

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