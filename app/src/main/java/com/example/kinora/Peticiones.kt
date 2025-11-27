package com.example.kinora

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley


class Peticiones : nav_bar(), OnPeticionUpdateListener {

    private lateinit var adminSesiones: AdministradorSesiones

    private lateinit var rvPeticiones: RecyclerView
    private lateinit var etBuscarPeticiones: EditText

    private val url = "http://192.168.1.34/kinora_php/buscar_peticiones.php"

    override fun onUpdateSuccess() {
        etBuscarPeticiones.setText("")
        rvPeticiones.adapter = PeticionesAdapter(mutableListOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        adminSesiones = AdministradorSesiones(this)
        adminSesiones.verificarAcceso(this, listOf(Roles.ADMINISTRADOR))

        setContentView(R.layout.activity_peticiones)
        configurarNavBar()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvPeticiones = findViewById(R.id.rv_peticiones)
        rvPeticiones.layoutManager = LinearLayoutManager(this)


        buscarPeticionesEnDB("")


        etBuscarPeticiones=findViewById(R.id.et_buscar_peticion)

        etBuscarPeticiones.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val textoEscrito = s?.toString() ?: ""
                if (textoEscrito.length > 3) {
                    buscarPeticionesEnDB(textoEscrito)
                } else if (textoEscrito.isEmpty()) {
                    buscarPeticionesEnDB("")
                }
            }

        })
    }

    private fun buscarPeticionesEnDB(query: String) {


        val urlConQuery = "$url?nombre=${Uri.encode(query)}"
        Log.d("PeticionesActivity", "URL de la petición: $urlConQuery")

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlConQuery, null,
            { response ->

                val listaPeticiones = mutableListOf<Peticion>()

                for (i in 0 until response.length()) {

                    val json = response.getJSONObject(i)

                    val id_cine = json.optInt("id_cine")
                    val nombre_cine = json.optString("nombre_cine")
                    val nombre_encargado = json.optString("nombre_usuario")
                    val id_pelicula = json.optInt("id_pelicula")
                    val nombre_pelicula = json.optString("nombre_pelicula")
                    val director = json.optString("director_nombre")
                    val genero = json.optString("genero_nombre")
                    val clasificacion = json.optString("clasificacion_nombre")
                    val tipo = json.optString("tipo_nombre")
                    val actores = json.optString("actores")

                    val peticion = Peticion(
                        id_cine,
                        nombre_cine,
                        nombre_encargado,
                        id_pelicula,
                        nombre_pelicula,
                        director,
                        genero,
                        clasificacion,
                        tipo,
                        actores
                    )

                    listaPeticiones.add(peticion)
                }


                val adapter = PeticionesAdapter(listaPeticiones)
                adapter.setOnPeticionUpdateListener(this)

                rvPeticiones.adapter = adapter

                Log.d("PeticionesActivity", "Peticiones obtenidas: $listaPeticiones")
            },
            { error ->
                Log.e("PeticionesActivity", "Error de Volley: ", error)
            }
        )

        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

}