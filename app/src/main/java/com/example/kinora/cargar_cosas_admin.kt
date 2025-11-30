package com.example.kinora

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class cargar_cosas_admin(private val context: Context) {

    fun cargarSalas(callback: SalaCallback) {
        val urlSalas = "http://192.168.1.6/kinora_php/obtener_salas.php"
        val idUsuario = AdministradorSesiones(context).obtenerIdUsuario()
        val urlConParametros = "$urlSalas?id_usuario=$idUsuario"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlConParametros, null,
            { response ->
                val listaSalas = mutableListOf<Sala>()
                try {
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val sala = Sala(
                            id_sala = jsonObject.getString("id_sala"),
                            numero_sala = jsonObject.getString("numero_sala"),
                            capacidad = jsonObject.getString("capacidad")
                        )
                        listaSalas.add(sala)
                    }
                    callback.onSuccess(listaSalas)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback.onError("Error al procesar los datos: ${e.message}")
                }
            },
            { error ->
                error.printStackTrace()
                callback.onError("Error de conexión: ${error.message}")
            }
        )
        Volley.newRequestQueue(context).add(jsonArrayRequest)
    }
}
