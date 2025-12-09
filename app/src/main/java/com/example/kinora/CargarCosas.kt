package com.example.kinora

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.kinora.Actor
import com.example.kinora.ActorCallback
import com.example.kinora.ClasiCallback
import com.example.kinora.Clasificacion
import com.example.kinora.Dia
import com.example.kinora.DiaCallback
import com.example.kinora.Director
import com.example.kinora.DirectorCallback
import com.example.kinora.Genero
import com.example.kinora.GeneroCallback
import org.json.JSONException
import com.example.kinora.Tipo
import com.example.kinora.TipoCallback

class CargarCosas(private val context: Context) {
    fun cargarTipos(callback: TipoCallback) {
        //val urlTipos = "http://192.168.80.25/Kinora/kinora_php/obtener_tipos.php"//Breyner
        val urlTipos = "http://10.0.2.2/Kinora/kinora_php/obtener_tipos.php"//Breyner


        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlTipos, null,
            { response ->
                try {
                    val listaTipos = mutableListOf<Tipo>()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val id = jsonObject.getString("id_tipo")
                        val nombre = jsonObject.getString("tipo")

                        listaTipos.add(Tipo(id, nombre))
                    }
                    callback.onSuccess(listaTipos)

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

    fun cargarGeneros(callback: GeneroCallback) {

        //val urlTipos = "http://192.168.80.25/kinora_php/obtener_generos.php"
        val urlTipos = "http://10.0.2.2/kinora_php/obtener_generos.php"


        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlTipos, null,
            { response ->
                try {
                    val listaGeneros = mutableListOf<Genero>()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)

                        val id = jsonObject.getString("id_genero")
                        val nombre = jsonObject.getString("genero")

                        listaGeneros.add(Genero(id, nombre))
                    }
                    callback.onSuccess(listaGeneros)

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

    fun cargarClasi(callback: ClasiCallback) {
        //val urlTipos = "http://192.168.80.25/kinora_php/obtener_clasificaciones.php"
        val urlTipos = "http://10.0.2.2/kinora_php/obtener_clasificaciones.php"


        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlTipos, null,
            { response ->
                try {
                    val listaClasificacion = mutableListOf<Clasificacion>()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)

                        val id = jsonObject.getString("id_clasificacion")
                        val nombre = jsonObject.getString("clasificacion")

                        listaClasificacion.add(Clasificacion(id, nombre))
                    }
                    callback.onSuccess(listaClasificacion)

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

    fun cargarActores(callback: ActorCallback) {
        //val urlTipos = "http://192.168.80.25/kinora_php/obtener_actores.php"
        val urlTipos = "http://10.0.2.2/kinora_php/obtener_actores.php"


        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlTipos, null,
            { response ->
                try {
                    val listaActores = mutableListOf<Actor>()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)

                        val id = jsonObject.getString("id_actor")
                        val nombre = jsonObject.getString("nombre")
                        val apellido = jsonObject.getString("apellido")

                        listaActores.add(Actor(id, nombre, apellido))
                    }
                    callback.onSuccess(listaActores)
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

    fun cargarDirectores(callback: DirectorCallback) {
        //val urlTipos = "http://192.168.80.25/kinora_php/obtener_directores.php"
        val urlTipos = "http://10.0.2.2/kinora_php/obtener_directores.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlTipos, null,
            { response ->
                try {
                    val listaDirectores = mutableListOf<Director>()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)

                        val id = jsonObject.getString("id_director")
                        val nombre = jsonObject.getString("nombre")
                        val apellido = jsonObject.getString("apellido")

                        listaDirectores.add(Director(id, nombre, apellido))
                    }
                    callback.onSuccess(listaDirectores)
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