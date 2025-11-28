package com.example.kinora

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONException


class detalles_enc_peliculas : DialogFragment(){
    private val baseUrl = "http://192.168.1.4/Kinora/kinora_php/"

    private lateinit var txtTitulo: TextView
    private lateinit var txtDirector: TextView
    private lateinit var txtGenero: TextView
    private lateinit var txtTipo: TextView
    private lateinit var txtClasificacion: TextView
    private lateinit var txtActores: TextView
    private lateinit var txtSinopsis: TextView
    private lateinit var posterView: ImageView

    companion object {
        private const val ARG_PELI_ID = "PELICULA_ID"

        fun newInstance(peliculaId: String): detalles_enc_peliculas {
            val f = detalles_enc_peliculas()
            val b = Bundle()
            b.putString(ARG_PELI_ID, peliculaId)
            f.arguments = b
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_detalles_enc_peliculas, container, false)
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
        txtSinopsis = view.findViewById(R.id.txt_sinopsis)
        posterView = view.findViewById(R.id.poster)

        val btnAceptar: Button = view.findViewById(R.id.btn_aceptar)

        btnAceptar.setOnClickListener { dismiss() }


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
        dialog?.window?.setLayout((resources.displayMetrics.widthPixels * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
    }


    private fun cargarDetalles(peliculaId: String) {
        val urlConId = "${baseUrl}obtener_detalles_pelicula.php?id_pelicula=$peliculaId"
        Log.d("DetallesPelicula", "Cargando datos desde: $urlConId")

        val jsonObjectRequest = JsonObjectRequest(com.android.volley.Request.Method.GET, urlConId, null,
            { response ->
                Log.d("DetallesPelicula", "Respuesta recibida: $response")
                try {
                    val detalles = PeliculaDetalles(
                        nombre = response.getString("nombre"),
                        sinopsis = response.optString("sinopsis", ""),
                        director = response.getString("director"),
                        genero = response.getString("genero"),
                        tipo = response.getString("tipo"),
                        clasificacion = response.optString("clasificacion", "N/A"),
                        actores = response.getString("actores"),
                        poster = response.optString("poster", "1")
                    )

                    txtTitulo.text = detalles.nombre
                    txtDirector.text = detalles.director
                    txtGenero.text = detalles.genero
                    txtTipo.text = detalles.tipo
                    txtActores.text = detalles.actores
                    txtClasificacion.text = detalles.clasificacion
                    txtSinopsis.text = detalles.sinopsis

                    val posterPath = detalles.poster
                    if (!posterPath.isNullOrBlank() && posterPath != "1") {
                        val fullUrl = if (posterPath.startsWith("http")) posterPath else baseUrl + posterPath
                        Glide.with(requireContext()).load(fullUrl).centerCrop().into(posterView)
                    } else {
                        posterView.setImageResource(android.R.drawable.ic_menu_report_image)
                    }

                } catch (e: JSONException) {
                    Log.e("DetallesPelicula", "Error parse JSON", e)
                    Toast.makeText(requireContext(), "Error al procesar los datos de la película", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("DetallesPelicula", "Error Volley", error)
                Toast.makeText(requireContext(), "No se pudieron cargar los detalles. Revisa la conexión.", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)
    }
}