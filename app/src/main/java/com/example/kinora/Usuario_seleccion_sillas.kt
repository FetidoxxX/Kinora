package com.example.kinora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import android.widget.Spinner
import android.widget.Toast
import com.android.volley.Request.Method
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.kinora.databinding.ActivityUsuarioSeleccionSillasBinding

class Usuario_seleccion_sillas : nav_bar() {

    private lateinit var recyclerViewSillas: RecyclerView

    private lateinit var binding: ActivityUsuarioSeleccionSillasBinding

    private lateinit var sillasAdapter: sillasAdapter
    //private lateinit var btnConfirmar: Button
    //private lateinit var btnCancelar: Button
    private var idFuncionRecibida: Int = 0
    private var idFuncionPrueba = 1
    private val url = "http://10.0.2.2/kinora_php/consultar_sillas_por_funcion.php"


    private val listaSillas = mutableListOf<Silla>()
    private val sillasOcupadas= mutableListOf<Silla>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityUsuarioSeleccionSillasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarNavBar()

        idFuncionRecibida = intent.getIntExtra("ID_FUNCION_SELECCIONADA", 0)
        val adminSesiones = AdministradorSesiones(this)
        val idUsuario = adminSesiones.obtenerIdUsuario()
        val datosUsuario = adminSesiones.obtenerDatosUsuario()
        val emailUsuario = datosUsuario["email"]
        setupRecyclerView()
        generarDatos()       

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            insets
        }

        binding.btnConfirmar.setOnClickListener {
            val sillasSeleccionadas = listaSillas.filter { it.estado == EstadoSilla.SELECCIONADA }

            if (emailUsuario == null) {
                Toast.makeText(
                    this,
                    "Error: no se pudo obtener el email del usuario.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val queue = Volley.newRequestQueue(this)

            for (silla in sillasSeleccionadas) {
                val urlCrearSilla = "http://10.0.2.2/kinora_php/crear_factura_silla.php"
                val uri = android.net.Uri.parse(urlCrearSilla).buildUpon()
                    .appendQueryParameter("id_silla", silla.id.toString())
                    .appendQueryParameter("id_funcion", idFuncionRecibida.toString())
                    .appendQueryParameter("id_usuario", idUsuario.toString())
                    .build()
                val urlConDatos = uri.toString()

                val jsonObjectRequest = JsonObjectRequest(
                    Method.GET, urlConDatos, null,
                    { response ->
                        try {
                            val success = response.getBoolean("success")
                            if (!success) {
                                val msg = response.optString("message", "Error al crear factura")
                                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                                return@JsonObjectRequest
                            }

                            val idFacturaGenerada = response.getInt("id_factura")

                            val infoFactura =
                                "Silla: ${silla.fila}${silla.columna}"

                            enviarCorreoDeConfirmacion(
                                emailUsuario,
                                infoFactura,
                                idFacturaGenerada.toString()
                            )

                        } catch (e: Exception) {
                            Log.e("UsuarioSeleccionSillas", "Error parseando respuesta", e)
                            Toast.makeText(this, "Error procesando la factura", Toast.LENGTH_SHORT).show()
                        }
                    },
                    { error ->
                        Log.e("UsuarioSeleccionSillas", "Error de Volley al crear factura", error)
                        Toast.makeText(this, "Error al crear facturas", Toast.LENGTH_SHORT).show()
                    }
                )

                queue.add(jsonObjectRequest)
            }

            Toast.makeText(
                this,
                "Boletas creadas, revisa tu correo para verlas.",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(this, Cartelera_Cliente::class.java)
            startActivity(intent)
        }
        binding.btnCancelar.setOnClickListener {
            adminSesiones.cerrarSesion()
        }
    }

    private fun enviarCorreoDeConfirmacion(email: String, info: String, idFactura: String) { val urlCorreo = "http://10.0.2.2/kinora_php/enviar_correo_factura.php"
        val stringRequest = object : StringRequest(
        Method.POST, urlCorreo,
        Response.Listener { response ->
            Log.d("EnvioCorreo", "Respuesta del servidor: $response")
        },
        Response.ErrorListener { error ->
            Log.e("EnvioCorreo", "Error de Volley: ${error.message}")
        }) {

        override fun getParams(): MutableMap<String, String> {
            val params = HashMap<String, String>()
            params["email_usuario"] = email
            params["info_factura"] = info
            params["id_factura"] = idFactura
            return params
        }
    }

        Volley.newRequestQueue(this).add(stringRequest)}


    private fun generarDatos(){
        //val filas= 'A'..'J'
        //val columnas = 15

        val urlconQuery = "$url?idFuncion=$idFuncionRecibida"
        Log.d("UsuarioSeleccionSillas", "URL de la petición: $urlconQuery")

        val jsonObjectRequest = JsonObjectRequest(
            Method.GET, urlconQuery, null,
            { response ->
                val sillasArray = response.getJSONArray("sillas")
                val sillasOcupadasArray = response.getJSONArray("sillasOcupadas")
                val idsSillasOcupadas = mutableSetOf<Int>()
                for (i in 0 until sillasOcupadasArray.length()){
                    val sillaOcupadaObj = sillasOcupadasArray.getJSONObject(i)
                    idsSillasOcupadas.add(sillaOcupadaObj.getInt("id_silla"))
                }
                listaSillas.clear()

                for(i in 0 until sillasArray.length()){
                    val sillaObj = sillasArray.getJSONObject(i)
                    val id = sillaObj.getInt("id_silla")
                    val estado = if(id in idsSillasOcupadas){
                        EstadoSilla.OCUPADA
                    }else{
                        EstadoSilla.DISPONIBLE
                    }

                    val silla = Silla(
                        id = id,
                        fila = sillaObj.getString("fila_silla"),
                        columna = sillaObj.getInt("columna_silla"),
                        estado = estado
                    )
                    listaSillas.add(silla)
                }
                sillasAdapter.notifyDataSetChanged()

            },
            { error ->
                Log.e("UsuarioSeleccionSillas", "Error de Volley: ", error)
            }

        )
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)


    }

    private fun setupRecyclerView(){

        sillasAdapter = sillasAdapter(listaSillas) { sillaPulsada ->
            if (sillaPulsada.estado == EstadoSilla.DISPONIBLE) {
                sillaPulsada.estado = EstadoSilla.SELECCIONADA
            } else if (sillaPulsada.estado == EstadoSilla.SELECCIONADA) {
                sillaPulsada.estado = EstadoSilla.DISPONIBLE
            }

            val posicion = listaSillas.indexOf(sillaPulsada)
            sillasAdapter.notifyItemChanged(posicion)
        }

        binding.rvSillas.apply {
            adapter = sillasAdapter
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@Usuario_seleccion_sillas, 15, RecyclerView.VERTICAL, false)
        }


    }


}