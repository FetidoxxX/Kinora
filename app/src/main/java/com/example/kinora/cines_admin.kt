package com.example.kinora

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import android.widget.Spinner
import android.widget.Toast
import com.android.volley.Request.Method
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest


class cines_admin : nav_bar(), OnCineUpdateListener  {

    private lateinit var recyclerViewCines: RecyclerView
    private lateinit var btnCrearCine: Button

    private lateinit var etBuscar: EditText
    private val url = "http://10.0.2.2/kinora_php/buscar_cines.php"

    override fun onUpdateSuccess() {
        etBuscar.setText("") // o también etBuscar.text.clear()
        recyclerViewCines.adapter = CineAdapter(mutableListOf())
    }

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


        btnCrearCine.setOnClickListener {
            mostrarDialogoCrearCine()
        }
    }
    private fun mostrarDialogoCrearCine(){
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.crear_cine, null)


        val btnCancelar = dialogView.findViewById<Button>(R.id.btn_cancelar)
        val btnCrear = dialogView.findViewById<Button>(R.id.btn_crear)
        val etNombreCine = dialogView.findViewById<EditText>(R.id.et_crear_nombre_cine)
        val etDireccionCine = dialogView.findViewById<EditText>(R.id.et_crear_direccion)
        val etTelefonoCine = dialogView.findViewById<EditText>(R.id.et_crear_telefono)
        val etNombreUsuario = dialogView.findViewById<EditText>(R.id.et_crear_nombre_usuario)
        val etEmailUsuario = dialogView.findViewById<EditText>(R.id.et_crear_email)
        val etDocumentoUsuario = dialogView.findViewById<EditText>(R.id.et_crear_documento)
        val etUsuario=dialogView.findViewById<EditText>(R.id.et_crear_usuario)
        val spinnerEstado= dialogView.findViewById<Spinner>(R.id.spinner_crear_estado)
        val spinnerTipoDocumento= dialogView.findViewById<Spinner>(R.id.spinner_tipo_documento)



        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnCrear.setOnClickListener {

            val nuevoNombre= etNombreCine.text.toString()
            val nuevaDireccion = etDireccionCine.text.toString()
            val nuevoTelefono = etTelefonoCine.text.toString()
            val nuevoNombreUsuario = etNombreUsuario.text.toString()
            val nuevoEmail = etEmailUsuario.text.toString()
            val nuevoDocumento = etDocumentoUsuario.text.toString()
            val nuevoUsuario=etUsuario.text.toString()
            val idEstadoCine=spinnerEstado.selectedItemPosition+1
            val idTipoDocumento=spinnerTipoDocumento.selectedItemPosition+1



            val url = "http://10.0.2.2/kinora_php/crear_cine.php"


            val stringRequest = object: StringRequest(Method.POST,
                url,
                Response.Listener<String> { response ->
                    Toast.makeText(
                        this,
                        "Cine creado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()

                },
                Response.ErrorListener{
                    Toast.makeText(
                        this,
                        "Error al crear el cine",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                }
            ){
                override fun getParams(): MutableMap<String, String>{
                    val params = HashMap<String, String>()
                    params["id_estado_cine"] = idEstadoCine.toString()
                    params["nombre_cine"] = nuevoNombre
                    params["direccion"] = nuevaDireccion
                    params["telefono"] = nuevoTelefono
                    params["nombre_usuario"] = nuevoNombreUsuario
                    params["email"] = nuevoEmail
                    params["id_tipo_documento"] = idTipoDocumento.toString()
                    params["documento"] = nuevoDocumento
                    params["usuario"]= nuevoUsuario
                    return params
                }

            }
            Volley.newRequestQueue(this).add(stringRequest)
        }

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
                    val usuario = jsonObjectCine.getString("usuario")
                    val id_estado_cine = jsonObjectCine.getInt("id_estado_cine")

                    val nuevoCine = Cine(id_cine, direccion, nombre, id_usuario, nombre_usuario, email, documento, telefono, usuario, id_estado_cine)

                    cinesEncontrados.add(nuevoCine)
                }
                val adapter = CineAdapter(cinesEncontrados)
                adapter.setOnCineUpdateListener(this)
                recyclerViewCines.adapter = adapter
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