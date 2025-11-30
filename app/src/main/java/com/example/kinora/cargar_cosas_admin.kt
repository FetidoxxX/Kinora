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
    fun cargarFunciones(callback: FuncionCallback) {
        val urlFunciones = "http://192.168.1.6/kinora_php/obtener_funciones.php"
        val idUsuario = AdministradorSesiones(context).obtenerIdUsuario()
        val urlConParametros = "$urlFunciones?id_usuario=$idUsuario"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlConParametros, null,
            { response ->
                val listaFunciones = mutableListOf<Funcion>()
                try {
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val funcion = Funcion(
                            id_funcion = jsonObject.getString("id_funcion"),
                            precio_base = jsonObject.getString("precio_base"),
                            fecha_hora = jsonObject.getString("fecha_hora"),
                            nombre_pelicula = jsonObject.getString("nombre_pelicula"),
                            id_pelicula = jsonObject.getString("id_pelicula"),
                            numero_sala = jsonObject.getString("numero_sala"),
                            id_sala = jsonObject.getString("id_sala"),
                            capacidad = jsonObject.getString("capacidad"),
                            nombre_dia = if (jsonObject.has("nombre_dia") && !jsonObject.isNull("nombre_dia")) jsonObject.getString("nombre_dia") else null,
                            descuento = if (jsonObject.has("descuento") && !jsonObject.isNull("descuento")) jsonObject.getString("descuento") else null,
                            id_dia = if (jsonObject.has("id_dia") && !jsonObject.isNull("id_dia")) jsonObject.getString("id_dia") else null,
                            precio_final = jsonObject.getDouble("precio_final")
                        )
                        listaFunciones.add(funcion)
                    }
                    callback.onSuccess(listaFunciones)
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

    fun buscarPeliculas(query: String, callback: PeliculaSearchCallback) {
        val urlBuscar = "http://192.168.1.6/kinora_php/buscar_peliculas.php?query=$query"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlBuscar, null,
            { response ->
                val listaPeliculas = mutableListOf<PeliculaSimple>()
                try {
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val pelicula = PeliculaSimple(
                            id_pelicula = jsonObject.getString("id_pelicula"),
                            nombre = jsonObject.getString("nombre")
                        )
                        listaPeliculas.add(pelicula)
                    }
                    callback.onSuccess(listaPeliculas)
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
    fun cargarPromociones(callback: DiaCallback) {
        val urlPromociones = "http://192.168.1.6/kinora_php/obtener_promociones.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlPromociones, null,
            { response ->
                try {
                    val listaPromociones = mutableListOf<Dia>()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)

                        val id = jsonObject.getString("id_dia")
                        val nombre = jsonObject.getString("nombre")
                        val descuento = jsonObject.getString("descuento")
                        val fecha = jsonObject.getString("fecha")

                        listaPromociones.add(Dia(id, nombre, descuento, fecha))
                    }
                    callback.onSuccess(listaPromociones)
                } catch (e: JSONException) {
                    callback.onError("Error al procesar la respuesta del servidor.")
                }
            },
            { error ->
                callback.onError("Error de conexión o de red: ${error.message}")
            }
        )

        Volley.newRequestQueue(context).add(jsonArrayRequest)
    }
}
