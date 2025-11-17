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
    private lateinit var adminSesiones: AdministradorSesiones

    //private val url: String = "http://172.20.10.3/kinora_php/login.php"
    //private val url: String = "http://192.168.0.149/kinora_php/login.php" // breyner
    //private val url: String = "http://192.168.1.6/kinora_php/login.php" //michael
    //private val url: String = "http://192.168.1.11/Kinora/kinora_php/login.php" //Cristhian
    private val url: String = "http://10.0.2.2/kinora_php/login.php" //michael


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        adminSesiones = AdministradorSesiones(this)

        if (adminSesiones.sesionIniciada()) {
            // Si es así, ir directamente al Home y cerrar esta actividad
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
            return // Importante para no seguir ejecutando el onCreate del Login
        }

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
            val intent = Intent(this,Registrar_usuario::class.java)
            startActivity(intent)
        }

        btnrecuperar?.setOnClickListener {
            val intent = Intent(this,recuperar_clave::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                val cleanResponse = response.trim()

                when (cleanResponse) {
                    "ERROR 1" -> {
                        Toast.makeText(this, "Se deben llenar todos los campos", Toast.LENGTH_SHORT).show()
                    }
                    "ERROR 2" -> {
                        Toast.makeText(this, "Usuario o contraseña no validos", Toast.LENGTH_SHORT).show()
                    }
                    "ERROR 3" -> {
                        Toast.makeText(this, "Usuario inactivo. Contacte al administrador para su activación.", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        if (cleanResponse.startsWith("[{") && cleanResponse.endsWith("}]")) {

                            adminSesiones.crearSesionDesdeJson(cleanResponse)
                            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, Home::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error: Respuesta inválida del servidor", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            Response.ErrorListener { volleyError ->
                // Error de red (el teléfono no puede conectarse al servidor web/URL)
                Toast.makeText(this, "ERROR DE CONEXIÓN: Revise su red o la URL: ${volleyError.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["usuario"] = edtusuario?.text.toString()
                params["clave"] = edtcontraseña?.text.toString()
                return params
            }
        }

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}
