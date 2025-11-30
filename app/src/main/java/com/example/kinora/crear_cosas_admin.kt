package com.example.kinora

import android.content.Context
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

interface crear_cosas_admin {

    fun crearSala(context: Context, numeroSala: String, filas: String, columnas: String, baseUrl: String, onSuccess: () -> Unit) {
        val url = "${baseUrl}crear_sala.php"
        val queue = Volley.newRequestQueue(context)
        val idUsuario = AdministradorSesiones(context).obtenerIdUsuario().toString()

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                try {
                    val json = org.json.JSONObject(response)
                    if (json.optString("status") == "success" || json.optString("status") == "warning") {
                        Toast.makeText(context, json.optString("message"), Toast.LENGTH_SHORT).show()
                        onSuccess()
                    } else {
                        Toast.makeText(context, "Error: ${json.optString("message")}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error parseando respuesta: $response", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(context, "Error de red: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val p = HashMap<String, String>()
                p["id_usuario"] = idUsuario
                p["numero_sala"] = numeroSala
                p["filas"] = filas
                p["columnas"] = columnas
                return p
            }
        }
        queue.add(request)
    }

    fun actualizarSala(context: Context, idSala: String, numeroSala: String, baseUrl: String, onSuccess: () -> Unit) {
        val url = "${baseUrl}actualizar_sala.php"
        val queue = Volley.newRequestQueue(context)

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                try {
                    val json = org.json.JSONObject(response)
                    if (json.optString("status") == "success") {
                        Toast.makeText(context, "Sala actualizada correctamente", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    } else {
                        Toast.makeText(context, "Error: ${json.optString("message")}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error parseando respuesta", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(context, "Error de red: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val p = HashMap<String, String>()
                p["id_sala"] = idSala
                p["numero_sala"] = numeroSala
                return p
            }
        }
        queue.add(request)
    }
}
