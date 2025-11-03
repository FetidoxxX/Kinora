package com.example.kinora

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
import org.json.JSONException

class cines_admin : nav_bar() {

    private lateinit var recyclerViewCines: RecyclerView
    private lateinit var btnCrearCine: Button

    private lateinit var etBuscar: EditText
    private val url = "http://10.0.2.2/kinora_php/buscar_cines.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cines_admin)
        configurarNavBar()  //aqui se importa la funcionalidad de la barra de navegación
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerViewCines = findViewById(R.id.rv_cines)
        btnCrearCine = findViewById(R.id.btn_crear_cine)
        etBuscar=findViewById(R.id.et_buscar_cine)


        etBuscar.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val textoEscrito=s.toString()
                if(textoEscrito.length>3){
                    buscarCinesEnDB(textoEscrito)
                }
            }

        })

    }


    private fun buscarCinesEnDB(query: String) {
        val urlConQuery = "$url?nombre=$query"
        Log.d("CinesAdmin", "URL de la petición: $urlConQuery")

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlConQuery, null,
            { response ->

                val cinesEncontrados = mutableListOf<Cine>()

                for(i in 0 until response.length()){
                    val jsonObjectCine = response.getJSONObject(i)

                    val id_cine = jsonObjectCine.getInt("id_cine")
                    val direccion = jsonObjectCine.getString("direccion")
                    val nombre = jsonObjectCine.getString("nombre_cine")
                    val id_usuario = jsonObjectCine.getInt("id_usuario")
                    val nombre_usuario = jsonObjectCine.getString("nombre_usuario")
                    val email = jsonObjectCine.getString("email_usuario")
                    val documento = jsonObjectCine.getString("documento_usuario")
                    val telefono = jsonObjectCine.getString("telefono")

                    val nuevoCine = Cine(id_cine, direccion, nombre, id_usuario, nombre_usuario, email, documento, telefono)

                    cinesEncontrados.add(nuevoCine)
                }
                CineAdapter(cinesEncontrados)
                recyclerViewCines.adapter = CineAdapter(cinesEncontrados)
                recyclerViewCines.layoutManager= LinearLayoutManager(this)

                Log.d("CinesAdmin", "Respuesta del servidor: $response")
            },
            { error ->
                Log.e("CinesAdmin", "Error de Volley: ", error)
            }
        )

        // Añadir la petición a la cola de Volley para que se ejecute
        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }
}