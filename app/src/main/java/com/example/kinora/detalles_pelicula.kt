package com.example.kinora

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class detalles_pelicula : DialogFragment() {

    //private val baseUrl = "http://192.168.1.11/Kinora/kinora_php/"
    private val baseUrl = "http://10.0.2.2/kinora_php/"


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
            val peliculaId = arguments?.getString(ARG_PELI_ID)
            if (peliculaId != null) showEditDialog(peliculaId)
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
        val urlConId = "${baseUrl}obtener_detalles_pelicula.php?id_pelicula=$peliculaId"
        Log.d("DetallesPelicula", "Cargando datos desde: $urlConId")

        val jsonObjectRequest = JsonObjectRequest(
            com.android.volley.Request.Method.GET, urlConId, null,
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

    //Edicion

    private fun showEditDialog(peliculaId: String) {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 12, 24, 12)
        }

        val etTitulo = EditText(requireContext()).apply {
            hint = "Título"
            setText(txtTitulo.text.toString())
            setSingleLine(true)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 12 }
        }

        fun makeLabel(text: String): TextView {
            return TextView(requireContext()).apply {
                this.text = text
                textSize = 14f
                setTextColor(0xFF737373.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = 6 }
            }
        }

        val spinnerDirector = Spinner(requireContext())
        val spinnerGenero = Spinner(requireContext())
        val spinnerTipo = Spinner(requireContext())
        val spinnerClasificacion = Spinner(requireContext())
        val btnActores = Button(requireContext()).apply { text = "Seleccionar Actores" }

        container.addView(etTitulo)
        container.addView(makeLabel("Director"))
        container.addView(spinnerDirector)
        container.addView(makeLabel("Género"))
        container.addView(spinnerGenero)
        container.addView(makeLabel("Tipo"))
        container.addView(spinnerTipo)
        container.addView(makeLabel("Clasificación"))
        container.addView(spinnerClasificacion)
        container.addView(makeLabel("Actores"))
        container.addView(btnActores)

        val listaDirectores = ArrayList<String>()
        val listaGeneros = ArrayList<String>()
        val listaTipos = ArrayList<String>()
        val listaClasificaciones = ArrayList<String>()
        val listaActores = ArrayList<String>()
        val seleccionadosActores = mutableListOf<String>()

        fun attachAdapter(spinner: Spinner, list: ArrayList<String>) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        attachAdapter(spinnerDirector, listaDirectores)
        attachAdapter(spinnerGenero, listaGeneros)
        attachAdapter(spinnerTipo, listaTipos)
        attachAdapter(spinnerClasificacion, listaClasificaciones)

        fetchList("${baseUrl}gestionar_peliculas.php?action=directores", listaDirectores, spinnerDirector, txtDirector.text.toString())
        fetchList("${baseUrl}gestionar_peliculas.php?action=generos", listaGeneros, spinnerGenero, txtGenero.text.toString())
        fetchList("${baseUrl}gestionar_peliculas.php?action=tipos", listaTipos, spinnerTipo, txtTipo.text.toString())
        fetchList("${baseUrl}gestionar_peliculas.php?action=clasificaciones", listaClasificaciones, spinnerClasificacion, txtClasificacion.text.toString())

        val queue = Volley.newRequestQueue(requireContext())
        val actoresRequest = JsonArrayRequest(
            "${baseUrl}gestionar_peliculas.php?action=actores",
            { response ->
                listaActores.clear()
                for (i in 0 until response.length()) {
                    val actor = response.getJSONObject(i).optString("nombre", "")
                    if (actor.isNotEmpty()) listaActores.add(actor)
                }
                val actuales = txtActores.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
                seleccionadosActores.clear()
                seleccionadosActores.addAll(actuales.filter { listaActores.contains(it) })
            },
            { error ->
                Toast.makeText(requireContext(), "Error al cargar actores", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(actoresRequest)

        btnActores.setOnClickListener {
            if (listaActores.isEmpty()) {
                Toast.makeText(requireContext(), "Cargando actores, inténtalo de nuevo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val checked = BooleanArray(listaActores.size) { i -> seleccionadosActores.contains(listaActores[i]) }

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Seleccionar actores")
            builder.setMultiChoiceItems(listaActores.toTypedArray(), checked) { _, which, isChecked ->
                val actorName = listaActores[which]
                if (isChecked) {
                    if (!seleccionadosActores.contains(actorName)) seleccionadosActores.add(actorName)
                } else {
                    seleccionadosActores.remove(actorName)
                }
            }
            builder.setPositiveButton("Aceptar", null)
            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }

        val editDialog = AlertDialog.Builder(requireContext())
            .setTitle("Editar película")
            .setView(container)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar") { dlg, _ -> dlg.dismiss() }
            .create()

        editDialog.setOnShowListener {
            val btnGuardar = editDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnGuardar.setOnClickListener {
                val nuevoTitulo = etTitulo.text.toString().trim()
                val selectedDirector = spinnerDirector.selectedItem?.toString() ?: ""
                val selectedGenero = spinnerGenero.selectedItem?.toString() ?: ""
                val selectedTipo = spinnerTipo.selectedItem?.toString() ?: ""
                val selectedClasif = spinnerClasificacion.selectedItem?.toString() ?: ""
                val actores = seleccionadosActores.joinToString(", ")

                if (nuevoTitulo.isEmpty()) {
                    etTitulo.error = "Título requerido"
                    etTitulo.requestFocus()
                    return@setOnClickListener
                }

                enviarActualizacion(
                    peliculaId = peliculaId,
                    nombre = nuevoTitulo,
                    director = selectedDirector,
                    genero = selectedGenero,
                    tipo = selectedTipo,
                    clasificacion = selectedClasif,
                    actores = actores,
                    onSuccess = {
                        txtTitulo.text = nuevoTitulo
                        txtDirector.text = selectedDirector
                        txtGenero.text = selectedGenero
                        txtTipo.text = selectedTipo
                        txtClasificacion.text = selectedClasif
                        txtActores.text = actores
                        Toast.makeText(requireContext(), "Actualizado correctamente", Toast.LENGTH_SHORT).show()
                        editDialog.dismiss()
                    },
                    onError = { errMsg ->
                        Toast.makeText(requireContext(), "Error al actualizar: $errMsg", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }

        editDialog.show()
        editDialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.92).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun fetchList(url: String, targetList: ArrayList<String>, spinner: Spinner, preselectValue: String?) {
        val queue = Volley.newRequestQueue(requireContext())
        val jsonArrayRequest = JsonArrayRequest(
            url,
            { array ->
                targetList.clear()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val name = obj.optString("nombre", obj.optString("genero", obj.optString("tipo", obj.optString("clasificacion", ""))))
                    if (name.isNotEmpty()) targetList.add(name)
                }
                if (targetList.isEmpty()) targetList.add("Sin opciones")
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, targetList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                preselectValue?.let { value ->
                    val pos = targetList.indexOfFirst { it.equals(value, ignoreCase = true) }
                    if (pos >= 0) spinner.setSelection(pos)
                }
            },
            { error ->
                targetList.clear()
                targetList.add("Error al cargar")
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, targetList)
                spinner.adapter = adapter
            }
        )
        queue.add(jsonArrayRequest)
    }

    private fun enviarActualizacion(
        peliculaId: String,
        nombre: String,
        director: String,
        genero: String,
        tipo: String,
        clasificacion: String,
        actores: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "${baseUrl}gestionar_peliculas.php?action=actualizar"
        val queue = Volley.newRequestQueue(requireContext())

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            { response ->
                Log.d("DetallesPelicula", "Respuesta update: $response")
                try {
                    val json = org.json.JSONObject(response)
                    val status = json.optString("status", "")
                    if (status.equals("success", ignoreCase = true)) {
                        onSuccess()
                    } else {
                        onError(json.optString("error", response))
                    }
                } catch (e: Exception) {
                    if (response.trim().equals("OK", ignoreCase = true) || response.contains("success", true)) {
                        onSuccess()
                    } else {
                        onError(response)
                    }
                }
            },
            { error ->
                Log.e("DetallesPelicula", "Error volley update", error)
                val net = error.networkResponse
                val body = try {
                    net?.data?.let { String(it) } ?: error.message
                } catch (ex: Exception) {
                    error.message ?: "Error desconocido"
                }
                val statusCode = net?.statusCode ?: -1
                val errMsg = "HTTP $statusCode — ${body ?: "sin cuerpo"}"
                Log.e("DetallesPelicula", "Detalle error: $errMsg")
                onError(errMsg)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = peliculaId
                params["titulo"] = nombre
                params["director"] = director
                params["genero"] = genero
                params["tipo"] = tipo
                params["clasificacion"] = clasificacion
                params["actores"] = actores
                return params
            }
        }

        queue.add(stringRequest)
    }
}
