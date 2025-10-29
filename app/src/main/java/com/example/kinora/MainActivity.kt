package com.example.kinora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {

    private var edtusuario: EditText? = null
    private var edtcontraseña: EditText? = null
    private var btnrecuperar: TextView? = null
    private var btniniciar: Button? = null
    private var btncrear: TextView? = null

    private val url: String = "http://192.168.1.5/kinora_PHP/login.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        edtusuario = findViewById(R.id.edtusuario)
        edtcontraseña = findViewById(R.id.edtcontraseña)
        btnrecuperar = findViewById(R.id.btnrecuperar)
        btniniciar = findViewById(R.id.btniniciar)
        btncrear = findViewById(R.id.btncrear)

        btniniciar?.setOnClickListener {
            login()
        }

        btncrear?.setOnClickListener {
            val intent = Intent(this, Registrar_usuario::class.java)
            startActivity(intent)
        }

        btnrecuperar?.setOnClickListener {
            val intent = Intent(this, recuperar_clave::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                when (response.trim()) {
                    "ERROR 1" -> {
                        Toast.makeText(this, "Se deben llenar todos los campos", Toast.LENGTH_SHORT).show()
                    }
                    "ERROR 2" -> {
                        Toast.makeText(this, "No existe el Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show()
                    }
                    "ERROR 3" -> {
                        Toast.makeText(this, "Error en el servidor", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // Login exitoso
                        Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                        finish() // Cerrar MainActivity para que no pueda volver atrás
                    }
                }
            },
            Response.ErrorListener { volleyError ->
                Toast.makeText(this, "ERROR AL CONECTAR CON EL SERVIDOR: ${volleyError.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["usuario"] = edtusuario?.text.toString()
                params["contraseña"] = edtcontraseña?.text.toString()
                return params
            }
        }

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}
