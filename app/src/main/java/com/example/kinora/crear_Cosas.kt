package com.example.kinora

import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.util.Log
import android.widget.Toast
import android.widget.EditText
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import android.content.Context
import com.android.volley.toolbox.StringRequest
import com.android.volley.Request

interface crear_Cosas {
    fun inicializarCreacionTipo(
        context: Context,
        vistaCreacionTipo: View,
        baseUrl: String
    ) {
        val btnFondoOscuroTipo = vistaCreacionTipo.findViewById<ImageView>(R.id.btnfondoOscuroTipo)
        val btnCrearTipo = vistaCreacionTipo.findViewById<LinearLayout>(R.id.btnCrearTipo)
        val edtTipo = vistaCreacionTipo.findViewById<EditText>(R.id.edtTipo)

        btnFondoOscuroTipo?.setOnClickListener {
            // Se asume que performHapticFeedback y View.GONE están disponibles en el Context o View
            it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionTipo.visibility = View.GONE
        }

        btnCrearTipo.setOnClickListener {
            val nuevoTipo = edtTipo.text.toString().trim()
            if (nuevoTipo.isNotEmpty()) {
                // Lógica de Volley para crear el Tipo, similar a guardarPelicula()
                // Aquí necesitarías llamar a otra función que ejecute Volley.
                crearNuevoTipo(context, nuevoTipo, baseUrl)
            } else {
                // Mostrar error si el campo está vacío
                edtTipo.error = "Escribe el nuevo tipo"
                edtTipo.requestFocus()
            }
        }
    }
    fun crearNuevoTipo(context: Context, nuevoTipo: String, baseUrl: String) {
        val url = "${baseUrl}gestionar_tipo.php?action=crear_tipo"
        val queue = Volley.newRequestQueue(context)

        Log.d("PeliculaManager", "POST Tipo -> $url")
        Log.d("PeliculaManager", "params Tipo -> tipo:$nuevoTipo")

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                Log.d("PeliculaManager", "RESP Tipo: $response")
                try {
                    // Parsear la respuesta JSON
                    val json = org.json.JSONObject(response)
                    if (json.optString("status") == "success") {
                        Toast.makeText(context, "Tipo '$nuevoTipo'creado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        val err = json.optString("error", "Error desconocido del servidor al crear tipo")
                        Toast.makeText(context, "Error servidor Tipo: $err", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("PeliculaManager", "Error parseando respuesta Tipo: ${e.message}", e)
                    Toast.makeText(context, "Respuesta inesperada del servidor Tipo. Revisa logs.", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val nr = error.networkResponse
                if (nr != null) {
                    val body = nr.data?.let { String(it) }
                    Log.e("PeliculaManager", "Volley error Tipo HTTP ${nr.statusCode} - body: $body", error)
                    Toast.makeText(context, "Error crear Tipo: HTTP ${nr.statusCode} — ${body ?: error.message}", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("PeliculaManager", "Volley error Tipo sin respuesta: ${error.message}", error)
                    Toast.makeText(context, "Error crear Tipo (sin respuesta): ${error.message}", Toast.LENGTH_LONG).show()
                }
            }) {

            override fun getParams(): MutableMap<String, String> {
                val p = HashMap<String, String>()
                p["tipo"] = nuevoTipo
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
    fun inicializarCreacionGenero(
        context: Context,
        vistaCreacionGenero: View,
        baseUrl: String
    ) {
        val btnFondoOscuroGenero = vistaCreacionGenero.findViewById<ImageView>(R.id.btnfondoOscuroGenero)
        val btnCrearGenero = vistaCreacionGenero.findViewById<LinearLayout>(R.id.btnCrearGenero)
        val edtGenero = vistaCreacionGenero.findViewById<EditText>(R.id.edtGenero)

        btnFondoOscuroGenero?.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionGenero.visibility = View.GONE
        }

        btnCrearGenero.setOnClickListener {
            val nuevoGenero = edtGenero.text.toString().trim()
            if (nuevoGenero.isNotEmpty()) {
                crearNuevoGenero(context, nuevoGenero, baseUrl)
            } else {
                edtGenero.error = "Escribe el nuevo Genero"
                edtGenero.requestFocus()
            }
        }
    }
    fun crearNuevoGenero(context: Context, nuevoGenero: String, baseUrl: String) {
        val url = "${baseUrl}gestionar_genero.php?action=crear_genero"

        val queue = Volley.newRequestQueue(context)

        Log.d("PeliculaManager", "POST Genero -> $url")
        Log.d("PeliculaManager", "params Genero -> genero:$nuevoGenero")

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                Log.d("PeliculaManager", "RESP Genero: $response")
                try {
                    val json = org.json.JSONObject(response)
                    if (json.optString("status") == "success") {
                        Toast.makeText(context, "Genero '$nuevoGenero' creado correctamente", Toast.LENGTH_SHORT).show()

                    } else {
                        val err = json.optString("error", "Error desconocido del servidor al crear tipo")
                        Toast.makeText(context, "Error servidor Tipo: $err", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("PeliculaManager", "Error parseando respuesta Genero: ${e.message}", e)
                    Toast.makeText(context, "Respuesta inesperada del servidor Genero. Revisa logs.", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val nr = error.networkResponse
                if (nr != null) {
                    val body = nr.data?.let { String(it) }
                    Log.e("PeliculaManager", "Volley error Tipo HTTP ${nr.statusCode} - body: $body", error)
                    Toast.makeText(context, "Error crear Genero: HTTP ${nr.statusCode} — ${body ?: error.message}", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("PeliculaManager", "Volley error Genero sin respuesta: ${error.message}", error)
                    Toast.makeText(context, "Error crear Genero (sin respuesta): ${error.message}", Toast.LENGTH_LONG).show()
                }
            }) {

            override fun getParams(): MutableMap<String, String> {
                val p = HashMap<String, String>()
                p["genero"] = nuevoGenero
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

    fun inicializarCreacionClasificacion(
        context: Context,
        vistaCreacionClasificacion: View,
        baseUrl: String
    ) {
        val btnFondoOscuroClasificacion = vistaCreacionClasificacion.findViewById<ImageView>(R.id.btnfondoOscuroClasificacion)
        val btnCrearClasificacion = vistaCreacionClasificacion.findViewById<LinearLayout>(R.id.btnCrearClasi)
        val edtClasificacion = vistaCreacionClasificacion.findViewById<EditText>(R.id.edtClasi)

        btnFondoOscuroClasificacion?.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionClasificacion.visibility = View.GONE
        }

        btnCrearClasificacion.setOnClickListener {
            val nuevoClasificacion = edtClasificacion.text.toString().trim()
            if (nuevoClasificacion.isNotEmpty()) {
                crearNuevoClasificacion(context, nuevoClasificacion, baseUrl)
            } else {
                edtClasificacion.error = "Escribe la nueva Clasificacion"
                edtClasificacion.requestFocus()
            }
        }
    }
    fun crearNuevoClasificacion(context: Context, nuevoClasificacion: String, baseUrl: String) {
        val url = "${baseUrl}gestionar_clasificacion.php?action=crear_clasificacion"

        val queue = Volley.newRequestQueue(context)

        Log.d("PeliculaManager", "POST Clasificacion -> $url")
        Log.d("PeliculaManager", "params Clasificacion -> clasificacion:$nuevoClasificacion")

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                Log.d("PeliculaManager", "RESP Clasificacion: $response")
                try {
                    val json = org.json.JSONObject(response)
                    if (json.optString("status") == "success") {
                        Toast.makeText(context, "Clasificacion '$nuevoClasificacion' creado correctamente", Toast.LENGTH_SHORT).show()

                    } else {
                        val err = json.optString("error", "Error desconocido del servidor al crear tipo")
                        Toast.makeText(context, "Error servidor Tipo: $err", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("PeliculaManager", "Error parseando respuesta Clasificacion: ${e.message}", e)
                    Toast.makeText(context, "Respuesta inesperada del servidor Clasificacion. Revisa logs.", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val nr = error.networkResponse
                if (nr != null) {
                    val body = nr.data?.let { String(it) }
                    Log.e("PeliculaManager", "Volley error Tipo HTTP ${nr.statusCode} - body: $body", error)
                    Toast.makeText(context, "Error crear Clasificacion: HTTP ${nr.statusCode} — ${body ?: error.message}", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("PeliculaManager", "Volley error Clasificacion sin respuesta: ${error.message}", error)
                    Toast.makeText(context, "Error crear Clasificacion (sin respuesta): ${error.message}", Toast.LENGTH_LONG).show()
                }
            }) {

            override fun getParams(): MutableMap<String, String> {
                val p = HashMap<String, String>()
                p["clasificacion"] = nuevoClasificacion
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
    fun inicializarCreacionActor(
        context: Context,
        vistaCreacionActor: View,
        baseUrl: String
    ) {
        val btnFondoOscuroActores = vistaCreacionActor.findViewById<ImageView>(R.id.btnfondoOscuroActores)
        val btnCrearActor = vistaCreacionActor.findViewById<LinearLayout>(R.id.btnCrearActor)

        val edtNombreActor = vistaCreacionActor.findViewById<EditText>(R.id.edtNombreActor)
        val edtApellidoActor = vistaCreacionActor.findViewById<EditText>(R.id.edtApellidoActor)

        btnFondoOscuroActores?.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionActor.visibility = View.GONE
        }

        btnCrearActor.setOnClickListener {
            val nombreActor = edtNombreActor.text.toString().trim()
            val apellidoActor = edtApellidoActor.text.toString().trim()

            if (nombreActor.isEmpty()) {
                edtNombreActor.error = "Nombre requerido"
                edtNombreActor.requestFocus()
                return@setOnClickListener
            }
            if (apellidoActor.isEmpty()) {
                edtApellidoActor.error = "Apellido requerido"
                edtApellidoActor.requestFocus()
                return@setOnClickListener
            }
            crearNuevoActor(context, nombreActor, apellidoActor, baseUrl)
        }
    }
    fun crearNuevoActor(context: Context, nombre: String, apellido: String, baseUrl: String) {
        val url = "${baseUrl}gestionar_actores.php?action=crear_actor"
        val queue = Volley.newRequestQueue(context)

        Log.d("PeliculaManager", "POST Actor -> $url")
        Log.d("PeliculaManager", "params Actor -> nombre:$nombre apellido:$apellido")

        // 3. Definir el StringRequest
        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                Log.d("PeliculaManager", "RESP Actor: $response")
                try {
                    val json = org.json.JSONObject(response)
                    if (json.optString("status") == "success") {
                        Toast.makeText(context, "Actor '$nombre $apellido' creado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        val err = json.optString("error", "Error desconocido del servidor al crear actor")
                        Toast.makeText(context, "Error servidor Actor: $err", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("PeliculaManager", "Error parseando respuesta Actor: ${e.message}", e)
                    Toast.makeText(context, "Respuesta inesperada del servidor Actor. Revisa logs.", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val nr = error.networkResponse
                if (nr != null) {
                    val body = nr.data?.let { String(it) }
                    Log.e("PeliculaManager", "Volley error Actor HTTP ${nr.statusCode} - body: $body", error)
                    Toast.makeText(context, "Error crear Actor: HTTP ${nr.statusCode} — ${body ?: error.message}", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("PeliculaManager", "Volley error Actor sin respuesta: ${error.message}", error)
                    Toast.makeText(context, "Error crear Actor (sin respuesta): ${error.message}", Toast.LENGTH_LONG).show()
                }
            }) {

            override fun getParams(): MutableMap<String, String> {
                val p = HashMap<String, String>()
                p["nombre"] = nombre
                p["apellido"] = apellido
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

    fun inicializarCreacionDirector(
        context: Context,
        vistaCreacionDirector: View,
        baseUrl: String
    ) {
        val btnFondoOscuroDirector = vistaCreacionDirector.findViewById<ImageView>(R.id.btnfondoOscuroDirector)

        val btnCrearDirector = vistaCreacionDirector.findViewById<LinearLayout>(R.id.btnCrearDirector)
        val edtNombreDirector = vistaCreacionDirector.findViewById<EditText>(R.id.edtNombreDirector)
        val edtApellidoDirector = vistaCreacionDirector.findViewById<EditText>(R.id.edtApellidoDirector)

        btnFondoOscuroDirector?.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            vistaCreacionDirector.visibility = View.GONE
        }

        btnCrearDirector.setOnClickListener {
            val nombreDirector = edtNombreDirector.text.toString().trim()
            val apellidoDirector = edtApellidoDirector.text.toString().trim()

            if (nombreDirector.isEmpty()) {
                edtNombreDirector.error = "Nombre requerido"
                edtNombreDirector.requestFocus()
                return@setOnClickListener
            }
            if (apellidoDirector.isEmpty()) {
                edtApellidoDirector.error = "Apellido requerido"
                edtApellidoDirector.requestFocus()
                return@setOnClickListener
            }

            crearNuevoDirector(context, nombreDirector, apellidoDirector, baseUrl)
        }
    }
    fun crearNuevoDirector(context: Context, nombre: String, apellido: String, baseUrl: String) {
        val url = "${baseUrl}gestionar_directores.php?action=crear_director"
        val queue = Volley.newRequestQueue(context)

        Log.d("PeliculaManager", "POST Director -> $url")
        Log.d("PeliculaManager", "params Director -> nombre:$nombre apellido:$apellido")

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                Log.d("PeliculaManager", "RESP Director: $response")
                try {
                    val json = org.json.JSONObject(response)
                    if (json.optString("status") == "success") {
                        Toast.makeText(context, "Director '$nombre $apellido' creado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        val err = json.optString("error", "Error desconocido del servidor al crear director")
                        Toast.makeText(context, "Error servidor Director: $err", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("PeliculaManager", "Error parseando respuesta Director: ${e.message}", e)
                    Toast.makeText(context, "Respuesta inesperada del servidor Director. Revisa logs.", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val nr = error.networkResponse
                if (nr != null) {
                    val body = nr.data?.let { String(it) }
                    Log.e("PeliculaManager", "Volley error Director HTTP ${nr.statusCode} - body: $body", error)
                    Toast.makeText(context, "Error crear Director: HTTP ${nr.statusCode} — ${body ?: error.message}", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("PeliculaManager", "Volley error Director sin respuesta: ${error.message}", error)
                    Toast.makeText(context, "Error crear Director (sin respuesta): ${error.message}", Toast.LENGTH_LONG).show()
                }
            }) {

            override fun getParams(): MutableMap<String, String> {
                val p = HashMap<String, String>()
                p["nombre"] = nombre
                p["apellido"] = apellido
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
