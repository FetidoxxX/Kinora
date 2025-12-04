package com.example.kinora

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class PeliculaDetalleCartelera : nav_bar_cliente() {

    private lateinit var ivPoster: ImageView
    private lateinit var tvDirector: TextView
    private lateinit var tvReparto: TextView
    private lateinit var tvClasificacion: TextView
    private lateinit var tvGenero: TextView
    private lateinit var tvTipo: TextView
    private lateinit var tvSinopsis: TextView
    private lateinit var tvFecha1: TextView
    private lateinit var tvFecha2: TextView
    private lateinit var tvFecha3: TextView
    private lateinit var tvFecha4: TextView
    private val URL_DETALLE = "http://192.168.1.4/Kinora/kinora_php/obtener_detalle_pelicula_cartelera.php?id_pelicula="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pelicula_detalle_cartelera)
        configurarNavBar()

        ivPoster = findViewById(R.id.imageView4)
        tvDirector = findViewById(R.id.DirectorTxT)
        tvReparto = findViewById(R.id.RepartoTxt)
        tvClasificacion = findViewById(R.id.ClasificacionTxT)
        tvGenero = findViewById(R.id.GeneroTXT)
        tvTipo = findViewById(R.id.TipoTxT)
        tvSinopsis = findViewById(R.id.SinopsisTxT)
        tvFecha1 = findViewById(R.id.Fecha1)
        tvFecha2 = findViewById(R.id.Fecha2)
        tvFecha3 = findViewById(R.id.Fecha3)
        tvFecha4 = findViewById(R.id.Fecha4)

        val fechas = getNextFourDays()
        val dateTextViews = listOf(tvFecha1, tvFecha2, tvFecha3, tvFecha4)

        dateTextViews.forEachIndexed { index, textView ->
            textView.text = fechas[index].display
            textView.tag = fechas[index].filter
        }

        dateTextViews.forEach { textView ->
            textView.setOnClickListener {
                resetDateSelection(dateTextViews)
                setSelectedDate(textView)

                val filterDate = textView.tag as String
                peliculaId?.let { id ->
                    cargarFunciones(id, filterDate)
                }
            }
        }

        val peliculaCartelera = intent.getSerializableExtra("PELICULA_SELECCIONADA") as? PeliculaCartelera
        rvFunciones = findViewById(R.id.rvFunciones)

        if (peliculaCartelera != null) {
            peliculaId = peliculaCartelera.id_pelicula

            cargarPoster(peliculaCartelera.urlPoster)
            cargarDetalles(peliculaCartelera.id_pelicula)

            setupFuncionesRecyclerView()

            val fechas = getNextFourDays()
            val initialFilterDate = fechas[0].filter
            setSelectedDate(tvFecha1)
            cargarFunciones(peliculaCartelera.id_pelicula, initialFilterDate)
        }else {
            Toast.makeText(this, "Error: No se recibió la película.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    private fun cargarPoster(url: String) {
        Glide.with(this)
            .load(url)
            .into(ivPoster)
    }

    private fun cargarDetalles(id: String) {
        val url = URL_DETALLE + id

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val director = response.getString("director")
                    val reparto = response.getString("reparto")
                    val clasificacion = response.getString("clasificacion")
                    val genero = response.getString("genero")
                    val tipo = response.getString("tipo")
                    val sinopsis = response.getString("sinopsis")

                    tvDirector.text = "Director: $director"
                    tvReparto.text = "Reparto: $reparto"
                    tvClasificacion.text = "Clasificación: $clasificacion"
                    tvGenero.text = "Género: $genero"
                    tvTipo.text = "Tipo: $tipo"
                    tvSinopsis.text = "Sinopsis: $sinopsis"

                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar datos: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión o servidor: ${error.message}", Toast.LENGTH_LONG).show()
                Log.d("CARTELERA", "Error de conexión o servidor: ${error.message}")
            }
        )

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private lateinit var rvFunciones: RecyclerView
    private val URL_FUNCIONES = "http://192.168.1.4/Kinora/kinora_php/obtener_funciones_pelicula.php?id_pelicula="
    private var peliculaId: String? = null

    private fun setupFuncionesRecyclerView() {
        rvFunciones.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun cargarFunciones(id: String, fecha: String) {
        val url = "$URL_FUNCIONES$id&fecha=$fecha"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val listaFuncionesCine = mutableListOf<FuncionCine>()

                for (i in 0 until response.length()) {
                    val cineJson = response.getJSONObject(i)
                    val nombreCine = cineJson.getString("nombreCine")
                    val idCine = cineJson.getInt("id_cine")

                    val horasArray = cineJson.getJSONArray("horas")
                    val listaHoras = mutableListOf<FuncionHora>()

                    for (j in 0 until horasArray.length()) {
                        val horaJson = horasArray.getJSONObject(j)
                        val funcionHora = FuncionHora(
                            id_funcion = horaJson.getInt("id_funcion"),
                            hora = horaJson.getString("hora"),
                            precio = horaJson.getDouble("precio")
                        )
                        listaHoras.add(funcionHora)
                    }

                    val funcionCine = FuncionCine(idCine, nombreCine, listaHoras)
                    listaFuncionesCine.add(funcionCine)
                }

                rvFunciones.adapter = FuncionCineAdapter(listaFuncionesCine)

            },
            { error ->
                Toast.makeText(
                    this,
                    "Error al cargar funciones: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("CARTELERA", "Error al cargar funciones: ${error.message}")
            }
        )

        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

    private fun getNextFourDays(): List<FechaData> {
        val calendar = Calendar.getInstance()
        val fechas = mutableListOf<FechaData>()

        val filterFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val dayOfWeekFormat = SimpleDateFormat("EEE.", Locale("es", "ES"))
        val dayMonthYearFormat = SimpleDateFormat("dd MMM. yyyy", Locale("es", "ES"))

        for (i in 0 until 4) {
            val dayOfWeek = dayOfWeekFormat.format(calendar.time).uppercase(Locale.getDefault())
            val dayMonthYear = dayMonthYearFormat.format(calendar.time).uppercase(Locale.getDefault())

            val displayString = "$dayOfWeek\n$dayMonthYear"

            val filterString = filterFormat.format(calendar.time)

            fechas.add(FechaData(displayString, filterString))

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return fechas
    }

    private fun resetDateSelection(views: List<TextView>) {
        views.forEach { textView ->
            textView.setTextColor(Color.parseColor("#FDFDFD"))
        }
    }
    private fun setSelectedDate(textView: TextView) {
        textView.setTextColor(Color.parseColor("#FFD700"))
    }
}