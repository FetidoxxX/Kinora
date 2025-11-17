package com.example.kinora

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class Crear_Peliculas : nav_bar(), DeplegableCreacion, crear_Cosas {

    private val baseUrl = "http://192.168.1.11/Kinora/kinora_php/"

    private lateinit var edtTitulo: EditText
    private lateinit var edtDirector: AutoCompleteTextView
    private lateinit var edtGenero: Spinner
    private lateinit var edtTipo: AutoCompleteTextView
    private lateinit var edtClasificacion: AutoCompleteTextView
    private lateinit var btnAgregar: LinearLayout
    private lateinit var actorsContainer: LinearLayout
    private lateinit var btnCreacionTop: ImageButton
    private lateinit var vistaCreacion: View
    private lateinit var btnCrear: LinearLayout

    private val directorsList = ArrayList<String>()
    private lateinit var directorsAdapter: ArrayAdapter<String>

    private val tiposList = ArrayList<String>()
    private lateinit var tiposAdapter: ArrayAdapter<String>

    private val clasifList = ArrayList<String>()
    private lateinit var clasifAdapter: ArrayAdapter<String>

    private val generosList = ArrayList<String>()
    private lateinit var generosAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_peliculas)

        configurarNavBar()
        despliegue(this)
        tipo(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            insets
        }

        btnCreacionTop = findViewById(R.id.btnCreacion)
        vistaCreacion = findViewById(R.id.includeCreacion)
        edtTitulo = findViewById(R.id.edtTitulo)
        edtDirector = findViewById(R.id.edtDirector)
        edtGenero = findViewById(R.id.edtGenero)
        edtTipo = findViewById(R.id.edtTipo)
        edtClasificacion = findViewById(R.id.edtClasificacion)
        btnAgregar = findViewById(R.id.btnAgregar)
        actorsContainer = findViewById(R.id.actorsContainer)
        btnCrear = findViewById(R.id.btnCrear)

        directorsAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, directorsList)
        edtDirector.setAdapter(directorsAdapter)

        tiposAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tiposList)
        edtTipo.setAdapter(tiposAdapter)

        clasifAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, clasifList)
        edtClasificacion.setAdapter(clasifAdapter)

        generosAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, generosList)
        generosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        edtGenero.adapter = generosAdapter

        edtDirector.threshold = 1
        edtTipo.threshold = 1
        edtClasificacion.threshold = 1

        btnCreacionTop.setOnClickListener {
            vistaCreacion.visibility = View.VISIBLE
        }

        cargarListasIniciales()

        mostrarAlFoco(edtDirector, "directores")
        mostrarAlFoco(edtTipo, "tipos")
        mostrarAlFoco(edtClasificacion, "clasificaciones")

        setupBuscador(edtDirector, "search_directores", directorsList, directorsAdapter)
        setupBuscador(edtTipo, "search_tipos", tiposList, tiposAdapter)
        setupBuscador(edtClasificacion, "search_clasificaciones", clasifList, clasifAdapter)

        btnAgregar.setOnClickListener { addActorField() }
        addActorField()

        btnCrear.setOnClickListener { guardarPelicula() }
    }

    private fun cargarListasIniciales() {
        fetchAuto("${baseUrl}gestionar_peliculas.php?action=directores", directorsList, directorsAdapter)
        fetchSpinner("${baseUrl}gestionar_peliculas.php?action=generos", generosList, generosAdapter)
        fetchAuto("${baseUrl}gestionar_peliculas.php?action=tipos", tiposList, tiposAdapter)
        fetchAuto("${baseUrl}gestionar_peliculas.php?action=clasificaciones", clasifList, clasifAdapter)
    }

    private fun mostrarAlFoco(view: AutoCompleteTextView, action: String) {
        view.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                when (action) {
                    "directores" -> if (directorsList.isEmpty()) fetchAuto("${baseUrl}gestionar_peliculas.php?action=directores", directorsList, directorsAdapter)
                    "tipos" -> if (tiposList.isEmpty()) fetchAuto("${baseUrl}gestionar_peliculas.php?action=tipos", tiposList, tiposAdapter)
                    "clasificaciones" -> if (clasifList.isEmpty()) fetchAuto("${baseUrl}gestionar_peliculas.php?action=clasificaciones", clasifList, clasifAdapter)
                }
                view.showDropDown()
            }
        }
    }

    private fun setupBuscador(
        view: AutoCompleteTextView,
        action: String,
        lista: ArrayList<String>,
        adapter: ArrayAdapter<String>
    ) {
        view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s?.toString()?.trim() ?: ""
                val url = "${baseUrl}gestionar_peliculas.php?action=$action&q=${java.net.URLEncoder.encode(q, "utf-8")}"
                fetchAuto(url, lista, adapter, showDropdown = true)
            }
        })
    }

    private fun fetchAuto(url: String, targetList: ArrayList<String>, adapter: ArrayAdapter<String>, showDropdown: Boolean = false) {
        val queue = Volley.newRequestQueue(this)
        val req = JsonArrayRequest(url,
            { array ->
                targetList.clear()
                for (i in 0 until array.length()) {
                    val name = array.getJSONObject(i).optString("nombre", "")
                    if (name.isNotEmpty()) targetList.add(name)
                }
                if (targetList.isEmpty()) targetList.add("Sin opciones")
                adapter.notifyDataSetChanged()
                if (showDropdown) {
                    try {
                        if (adapter === directorsAdapter) edtDirector.showDropDown()
                        if (adapter === tiposAdapter) edtTipo.showDropDown()
                        if (adapter === clasifAdapter) edtClasificacion.showDropDown()
                    } catch (ex: Exception) { /* ignore */ }
                }
            },
            { error ->
                Log.e("Crear_Peliculas", "fetchAuto error: $error")
            })
        queue.add(req)
    }

    private fun fetchSpinner(url: String, list: ArrayList<String>, adapter: ArrayAdapter<String>) {
        val queue = Volley.newRequestQueue(this)
        val req = JsonArrayRequest(url,
            { array ->
                list.clear()
                for (i in 0 until array.length()) {
                    val name = array.getJSONObject(i).optString("nombre", "")
                    if (name.isNotEmpty()) list.add(name)
                }
                if (list.isEmpty()) list.add("Sin opciones")
                adapter.notifyDataSetChanged()
            },
            { error -> Log.e("Crear_Peliculas", "fetchSpinner error: $error") })
        queue.add(req)
    }

    private fun addActorField() {
        val index = actorsContainer.childCount + 1
        val et = EditText(this).apply {
            hint = "Actor $index"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { lp ->
                lp.topMargin = 8
                lp.leftMargin = 0
                lp.rightMargin = 0
            }
            id = View.generateViewId()
        }
        actorsContainer.addView(et)
    }

    private fun guardarPelicula() {
        val titulo = edtTitulo.text.toString().trim()
        if (titulo.isEmpty()) {
            edtTitulo.error = "Título requerido"
            edtTitulo.requestFocus()
            return
        }

        val director = edtDirector.text.toString().trim()
        val genero = if (edtGenero.selectedItem != null) edtGenero.selectedItem.toString() else ""
        val tipo = edtTipo.text.toString().trim()
        val clasificacion = edtClasificacion.text.toString().trim()

        val actores = mutableListOf<String>()
        for (i in 0 until actorsContainer.childCount) {
            val v = actorsContainer.getChildAt(i)
            if (v is EditText) {
                val txt = v.text.toString().trim()
                if (txt.isNotEmpty()) actores.add(txt)
            }
        }
        val actoresCsv = actores.joinToString(",")

        val url = "${baseUrl}gestionar_peliculas.php?action=crear_pelicula"
        val queue = Volley.newRequestQueue(this)

        Log.d("Crear_Peliculas", "POST -> $url")
        Log.d("Crear_Peliculas", "params -> titulo:$titulo director:$director genero:$genero tipo:$tipo clasif:$clasificacion actores:$actoresCsv")

        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                Log.d("Crear_Peliculas", "RESP: $response")
                try {
                    val json = org.json.JSONObject(response)
                    if (json.optString("status") == "success") {
                        Toast.makeText(this, "Película creada correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val err = json.optString("error", "Error desconocido del servidor")
                        Toast.makeText(this, "Error servidor: $err", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("Crear_Peliculas", "Error parseando respuesta: ${e.message}", e)
                    Toast.makeText(this, "Respuesta inesperada del servidor. Revisa logs.", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val nr = error.networkResponse
                if (nr != null) {
                    val body = nr.data?.let { String(it) }
                    Log.e("Crear_Peliculas", "Volley error HTTP ${nr.statusCode} - body: $body", error)
                    Toast.makeText(this, "Error crear: HTTP ${nr.statusCode} — ${body ?: error.message}", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("Crear_Peliculas", "Volley error sin respuesta: ${error.message}", error)
                    Toast.makeText(this, "Error crear (sin respuesta): ${error.message}", Toast.LENGTH_LONG).show()
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                val p = HashMap<String, String>()
                p["titulo"] = titulo
                p["director"] = director
                p["genero"] = genero
                p["tipo"] = tipo
                p["clasificacion"] = clasificacion
                p["actores"] = actoresCsv
                return p
            }

            override fun getRetryPolicy() = com.android.volley.DefaultRetryPolicy(
                15000,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }

        queue.add(request)
    }
}
