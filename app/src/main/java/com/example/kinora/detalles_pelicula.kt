package com.example.kinora

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONException
import java.io.InputStream

class detalles_pelicula : DialogFragment() {

    private val baseUrl = "http://192.168.1.4/Kinora/kinora_php/"

    private lateinit var txtTitulo: TextView
    private lateinit var txtDirector: TextView
    private lateinit var txtGenero: TextView
    private lateinit var txtTipo: TextView
    private lateinit var txtClasificacion: TextView
    private lateinit var txtActores: TextView
    private lateinit var txtSinopsis: TextView
    private lateinit var posterView: ImageView

    // selector de imagen y estado de edición
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var editedPosterBase64: String? = null
    private var editedPosterName: String? = null
    private var isEditingEnabled = false

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val bytes = readBytesFromUri(uri)
                if (bytes != null) {
                    editedPosterBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    editedPosterName = "poster_${System.currentTimeMillis()}.jpg"
                    Toast.makeText(requireContext(), "Portada seleccionada (listo para guardar)", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No se pudo leer la imagen seleccionada", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No se seleccionó imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        txtSinopsis = view.findViewById(R.id.txt_sinopsis)
        posterView = view.findViewById(R.id.poster)

        val btnAceptar: Button = view.findViewById(R.id.btn_aceptar)
        val btnEditar: Button? = view.findViewById(R.id.btn_editar)

        btnAceptar.setOnClickListener { dismiss() }
        btnEditar?.setOnClickListener {
            val peliculaId = arguments?.getString(ARG_PELI_ID)
            if (peliculaId != null) {
                isEditingEnabled = true
                showEditDialog(peliculaId)
            }
        }

        posterView.setOnClickListener {
            if (isEditingEnabled) pickImageLauncher.launch("image/*")
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
        dialog?.window?.setLayout((resources.displayMetrics.widthPixels * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun readBytesFromUri(uri: Uri): ByteArray? {
        return try {
            val input: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val bytes = input?.readBytes()
            input?.close()
            bytes
        } catch (e: Exception) {
            Log.e("DetallesPelicula", "readBytesFromUri error: ${e.message}", e)
            null
        }
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

    private fun showEditDialog(peliculaId: String) {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 12, 24, 12)
        }

        val etTitulo = EditText(requireContext()).apply {
            hint = "Título"
            setText(txtTitulo.text.toString())
            setSingleLine(true)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 12 }
        }

        val etSinopsis = EditText(requireContext()).apply {
            hint = "Sinopsis"
            setText(txtSinopsis.text.toString())
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 12 }
        }

        val ivPosterPreview = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(300, 400).apply { bottomMargin = 12 }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        try { posterView.drawable?.let { ivPosterPreview.setImageDrawable(it) } } catch (_: Exception) {}

        ivPosterPreview.setOnClickListener { pickImageLauncher.launch("image/*") }

        fun makeLabel(text: String): TextView {
            return TextView(requireContext()).apply {
                this.text = text
                textSize = 14f
                setTextColor(0xFF737373.toInt())
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = 6 }
            }
        }

        val spinnerDirector = Spinner(requireContext())
        val spinnerGenero = Spinner(requireContext())
        val spinnerTipo = Spinner(requireContext())
        val spinnerClasificacion = Spinner(requireContext())
        val btnActores = Button(requireContext()).apply { text = "Seleccionar Actores" }

        container.addView(etTitulo)
        container.addView(makeLabel("Sinopsis"))
        container.addView(etSinopsis)
        container.addView(ivPosterPreview)
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
        val actoresRequest = com.android.volley.toolbox.JsonArrayRequest("${baseUrl}gestionar_peliculas.php?action=actores",
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
            { error -> Toast.makeText(requireContext(), "Error al cargar actores", Toast.LENGTH_SHORT).show() }
        )
        queue.add(actoresRequest)

        btnActores.setOnClickListener {
            if (listaActores.isEmpty()) { Toast.makeText(requireContext(), "Cargando actores, inténtalo de nuevo", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val checked = BooleanArray(listaActores.size) { i -> seleccionadosActores.contains(listaActores[i]) }
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Seleccionar actores")
            builder.setMultiChoiceItems(listaActores.toTypedArray(), checked) { _, which, isChecked ->
                val actorName = listaActores[which]
                if (isChecked) { if (!seleccionadosActores.contains(actorName)) seleccionadosActores.add(actorName) }
                else { seleccionadosActores.remove(actorName) }
            }
            builder.setPositiveButton("Aceptar", null)
            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }

        val editDialog = AlertDialog.Builder(requireContext()).setTitle("Editar película").setView(container)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar") { dlg, _ -> isEditingEnabled = false; dlg.dismiss() }
            .create()

        editDialog.setOnShowListener {
            val btnGuardar = editDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnGuardar.setOnClickListener {
                val nuevoTitulo = etTitulo.text.toString().trim()
                val nuevaSinopsis = etSinopsis.text.toString().trim()
                val selectedDirector = spinnerDirector.selectedItem?.toString() ?: ""
                val selectedGenero = spinnerGenero.selectedItem?.toString() ?: ""
                val selectedTipo = spinnerTipo.selectedItem?.toString() ?: ""
                val selectedClasif = spinnerClasificacion.selectedItem?.toString() ?: ""
                val actores = seleccionadosActores.joinToString(", ")

                if (nuevoTitulo.isEmpty()) { etTitulo.error = "Título requerido"; etTitulo.requestFocus(); return@setOnClickListener }

                enviarActualizacion(
                    peliculaId = peliculaId,
                    nombre = nuevoTitulo,
                    sinopsis = nuevaSinopsis,
                    director = selectedDirector,
                    genero = selectedGenero,
                    tipo = selectedTipo,
                    clasificacion = selectedClasif,
                    actores = actores,
                    posterBase64 = editedPosterBase64,
                    posterName = editedPosterName,
                    onSuccess = {
                        txtTitulo.text = nuevoTitulo
                        txtSinopsis.text = nuevaSinopsis
                        txtDirector.text = selectedDirector
                        txtGenero.text = selectedGenero
                        txtTipo.text = selectedTipo
                        txtClasificacion.text = selectedClasif
                        txtActores.text = actores

                        editedPosterBase64?.let {
                            val bytes = android.util.Base64.decode(it, Base64.NO_WRAP)
                            try {
                                val bmp = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                posterView.setImageBitmap(bmp)
                                ivPosterPreview.setImageBitmap(bmp)
                            } catch (_: Exception) {}
                        }

                        Toast.makeText(requireContext(), "Actualizado correctamente", Toast.LENGTH_SHORT).show()
                        isEditingEnabled = false
                        editedPosterBase64 = null
                        editedPosterName = null
                        editDialog.dismiss()
                    },
                    onError = { errMsg -> Toast.makeText(requireContext(), "Error al actualizar: $errMsg", Toast.LENGTH_LONG).show() }
                )
            }
        }

        editDialog.show()
        editDialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.92).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun fetchList(url: String, targetList: ArrayList<String>, spinner: Spinner, preselectValue: String?) {
        val queue = Volley.newRequestQueue(requireContext())
        val jsonArrayRequest = com.android.volley.toolbox.JsonArrayRequest(url,
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
        sinopsis: String,
        director: String,
        genero: String,
        tipo: String,
        clasificacion: String,
        actores: String,
        posterBase64: String?,
        posterName: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "${baseUrl}gestionar_peliculas.php?action=actualizar"
        val queue = Volley.newRequestQueue(requireContext())

        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                Log.d("DetallesPelicula", "Respuesta update: $response")
                try {
                    val json = org.json.JSONObject(response)
                    val status = json.optString("status", "")
                    if (status.equals("success", ignoreCase = true)) onSuccess() else onError(json.optString("error", response))
                } catch (e: Exception) {
                    if (response.trim().equals("OK", ignoreCase = true) || response.contains("success", true)) onSuccess() else onError(response)
                }
            },
            { error ->
                Log.e("DetallesPelicula", "Error volley update", error)
                val net = error.networkResponse
                val body = try { net?.data?.let { String(it) } ?: error.message } catch (ex: Exception) { error.message ?: "Error desconocido" }
                val statusCode = net?.statusCode ?: -1
                val errMsg = "HTTP $statusCode — ${body ?: "sin cuerpo"}"
                Log.e("DetallesPelicula", "Detalle error: $errMsg")
                onError(errMsg)
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = peliculaId
                params["titulo"] = nombre
                params["sinopsis"] = sinopsis
                params["director"] = director
                params["genero"] = genero
                params["tipo"] = tipo
                params["clasificacion"] = clasificacion
                params["actores"] = actores
                posterBase64?.let { params["poster"] = it; params["poster_name"] = posterName ?: "poster_${System.currentTimeMillis()}.jpg" }
                return params
            }
        }

        queue.add(stringRequest)
    }
}
