package com.example.kinora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class peticiones_enc : nav_bar() {

    private lateinit var rvPeliculas: RecyclerView
    private val baseUrl: String = "http://192.168.1.4/Kinora/kinora_php/" //Cristhian
    private var btnEnEspera: Button? = null
    private var btnRechazadas: Button? = null
    private var btnCreacion: ImageButton? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        administradorSesiones.verificarAcceso(this, listOf(Roles.ENCARGADO))
        setContentView(R.layout.activity_peticiones_enc)
        configurarNavBar()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvPeliculas = findViewById(R.id.rvPeliculas)
        rvPeliculas.layoutManager = LinearLayoutManager(this)
        btnEnEspera = findViewById(R.id.enEspera)
        btnRechazadas = findViewById(R.id.rechazadas)
        btnCreacion = findViewById(R.id.btnCreacion)


        btnCreacion?.setOnClickListener {
            val intent = Intent(this, crear_peticion_enc::class.java)
            startActivity(intent)
        }


        btnEnEspera?.setOnClickListener {
            cargarEnEspera()
        }

        btnRechazadas?.setOnClickListener {
            cargarRechazadas()
        }
    }


    private fun cargarEnEspera() {
        Log.d("PeliculasActivity", "Iniciando la carga de películas desde: $baseUrl")

        val phpUrl = "${baseUrl}obtener_enc_peticiones.php?action=obtener_enEspera"


        val idEncargado = administradorSesiones.obtenerIdUsuario()
        val urlId = "$phpUrl&id_u=$idEncargado"


        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlId, null,
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

                    val adapter = EncPeliculasAdapter(listaPeliculas)
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



    private fun cargarRechazadas() {
        Log.d("PeliculasActivity", "Iniciando la carga de películas desde: $baseUrl")

        val phpUrl = "${baseUrl}obtener_enc_peticiones.php?action=obtener_enProgreso"


        val idEncargado = administradorSesiones.obtenerIdUsuario()
        val urlId = "$phpUrl&id_u=$idEncargado"


        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlId, null,
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

                    val adapter = EncPeliculasAdapter(listaPeliculas)
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