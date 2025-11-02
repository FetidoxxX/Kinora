package com.example.kinora


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ArrayAdapter
import android.widget.Toast
import android.view.View
import android.widget.AdapterView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject



class Registrar_usuario : AppCompatActivity() {


    private lateinit var spntipo_doc: Spinner
    private lateinit var requestQueue: RequestQueue


    private lateinit var edtnum_doc: EditText
    private lateinit var edtnombre_u: EditText
    private lateinit var edtemail_u: EditText
    private lateinit var edtuser_u: EditText
    private lateinit var edtpass_u: EditText
    private lateinit var edtpass_conf: EditText
    private lateinit var btnregistrar_u: Button
    private lateinit var btnvolver: Button
    private var idTipoSeleccionado: Int? = null




    private var url: String = "http://10.0.2.2/kinora_PHP/registrar_u.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_usuario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spntipo_doc = findViewById(R.id.spntipo_doc)
        edtnum_doc = findViewById(R.id.edtnum_doc)
        edtnombre_u = findViewById(R.id.edtnombre_u)
        edtemail_u = findViewById(R.id.edtemail_u)
        edtuser_u = findViewById(R.id.edtuser_u)
        edtpass_u = findViewById(R.id.edtpass_u)
        edtpass_conf = findViewById(R.id.edtpass_conf)
        btnregistrar_u = findViewById(R.id.btnregistrar_u)
        requestQueue = Volley.newRequestQueue(this)

        cargarTiposDocumento()


        btnregistrar_u.setOnClickListener {
            if (idTipoSeleccionado != null) {
                val idTipo = idTipoSeleccionado!!
                // 🔹 Aquí luego llamarías tu función para registrar el usuario:

                Toast.makeText(this, "Tipo de documento seleccionado: $idTipo", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Selecciona un tipo de documento", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun cargarTiposDocumento() {
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                val listaTipos = ArrayList<String>()
                val listaIds = ArrayList<Int>()

                for (i in 0 until response.length()) {
                    val tipo: JSONObject = response.getJSONObject(i)
                    listaTipos.add(tipo.getString("nombre_tipo"))
                    listaIds.add(tipo.getInt("id_tipo_doc"))
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaTipos)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spntipo_doc.adapter = adapter

                spntipo_doc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long ) {
                        idTipoSeleccionado = listaIds[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        idTipoSeleccionado = null
                    }
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                error.printStackTrace()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }


}

