package com.example.kinora

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class detalles_pelicula : DialogFragment() {

    private val baseUrl = "http://192.168.1.12/kinora_php/obtener_detalles_pelicula.php"

    private lateinit var txtTitulo: TextView
    private lateinit var txtDirector: TextView
    private lateinit var txtGenero: TextView
    private lateinit var txtTipo: TextView
    private lateinit var txtClasificacion: TextView
    private lateinit var txtActores: TextView

    companion object {
        private const val ARG_PELI_ID = "PELICULA_ID"

        fun newInstance(peliculaId: String): detalles_pelicula {
            val f = detalles_pelicula()
            val b = Bundle()
            b.putString(ARG_PELI_ID, peliculaId)
            f.arguments = b
            return f
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_detalles_pelicula, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtTitulo = view.findViewById(R.id.txt_titulo)
        txtDirector = view.findViewById(R.id.txt_director)
        txtGenero = view.findViewById(R.id.txt_genero)
        txtTipo = view.findViewById(R.id.txt_tipo)
        txtActores = view.findViewById(R.id.txt_actores)
        txtClasificacion = view.findViewById(R.id.txt_clasificacion)

        val btnAceptar: Button = view.findViewById(R.id.btn_aceptar)
        val btnEditar: Button? = view.findViewById(R.id.btn_editar)

        btnAceptar.setOnClickListener { dismiss() }
        btnEditar?.setOnClickListener {
            dismiss()
        }

        val peliculaId = arguments?.getString(ARG_PELI_ID)
        if (peliculaId == null) {
            Toast.makeText(requireContext(), "Error: No se recibió el ID de la película", Toast.LENGTH_LONG).show()
            dismiss()
            return
        }

        cargarDetalles(peliculaId)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun cargarDetalles(peliculaId: String) {
        val urlConId = "$baseUrl?id_pelicula=$peliculaId"
        Log.d("DetallesPelicula", "Cargando datos desde: $urlConId")

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, urlConId, null,
            { response ->
                Log.d("DetallesPelicula", "Respuesta recibida: $response")
                try {
                    val detalles = PeliculaDetalles(
                        nombre = response.getString("nombre"),
                        director = response.getString("director"),
                        genero = response.getString("genero"),
                        tipo = response.getString("tipo"),
                        clasificacion = response.optString("clasificacion", "N/A"),
                        actores = response.getString("actores")
                    )

                    txtTitulo.text = detalles.nombre
                    txtDirector.text = detalles.director
                    txtGenero.text = detalles.genero
                    txtTipo.text = detalles.tipo
                    txtActores.text = detalles.actores
                    txtClasificacion.text = detalles.clasificacion

                } catch (e: JSONException) {
                    Log.e("DetallesPelicula", "Error al parsear el JSON de detalles", e)
                    Toast.makeText(requireContext(), "Error al procesar los datos de la película", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("DetallesPelicula", "Error de Volley al cargar detalles", error)
                Toast.makeText(requireContext(), "No se pudieron cargar los detalles. Revisa la conexión.", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)
    }
}
