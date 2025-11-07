package com.example.kinora

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import org.json.JSONArray


object Roles {
    const val ADMINISTRADOR = 1
    const val ENCARGADO = 2
    const val CLIENTE = 3
}

class AdministradorSesiones(private val contexto: Context) {

    private val preferencias: SharedPreferences
    private val editor: SharedPreferences.Editor

    companion object {
        private const val NOMBRE_PREFERENCIAS = "KinoraSessionPrefs"

        private const val CLAVE_SESION_INICIADA = "isLoggedIn"
        private const val CLAVE_ID_USUARIO = "userId"
        private const val CLAVE_ROL_USUARIO = "userRole"
        private const val CLAVE_NOMBRE_USUARIO = "userName"
        private const val CLAVE_EMAIL_USUARIO = "userEmail"
        private const val CLAVE_USUARIO_LOGIN = "userUsuario"
    }

    init {
        preferencias = contexto.getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE)
        editor = preferencias.edit()
    }

    fun crearSesion(id_u: Int, rol_id: Int, nombre: String, email: String, usuario: String) {
        editor.putBoolean(CLAVE_SESION_INICIADA, true)
        editor.putInt(CLAVE_ID_USUARIO, id_u)
        editor.putInt(CLAVE_ROL_USUARIO, rol_id)
        editor.putString(CLAVE_NOMBRE_USUARIO, nombre)
        editor.putString(CLAVE_EMAIL_USUARIO, email)
        editor.putString(CLAVE_USUARIO_LOGIN, usuario)
        editor.apply()
    }

    fun crearSesionDesdeJson(respuestaJson: String) {
        try {
            val jsonArray = JSONArray(respuestaJson)
            if (jsonArray.length() > 0) {
                val objetoUsuario = jsonArray.getJSONObject(0)
                crearSesion(
                    objetoUsuario.getInt("id_u"),
                    objetoUsuario.getInt("rol_id"),
                    objetoUsuario.getString("nombre"),
                    objetoUsuario.getString("email"),
                    objetoUsuario.getString("usuario")
                )
            }
        } catch (e: Exception) {
            Toast.makeText(contexto, "Error al procesar datos de sesión", Toast.LENGTH_SHORT).show()
        }
    }

    fun sesionIniciada(): Boolean {
        return preferencias.getBoolean(CLAVE_SESION_INICIADA, false)
    }

    fun obtenerIdRol(): Int {
        return preferencias.getInt(CLAVE_ROL_USUARIO, -1)
    }

    fun obtenerIdUsuario(): Int {
        return preferencias.getInt(CLAVE_ID_USUARIO, -1)
    }

    fun obtenerDatosUsuario(): Map<String, String?> {
        return mapOf(
            "id" to preferencias.getInt(CLAVE_ID_USUARIO, -1).toString(),
            "rol" to preferencias.getInt(CLAVE_ROL_USUARIO, -1).toString(),
            "nombre" to preferencias.getString(CLAVE_NOMBRE_USUARIO, null),
            "email" to preferencias.getString(CLAVE_EMAIL_USUARIO, null),
            "usuario" to preferencias.getString(CLAVE_USUARIO_LOGIN, null)
        )
    }


    fun cerrarSesion() {
        editor.clear()
        editor.apply()

        val intent = Intent(contexto, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        contexto.startActivity(intent)
        Toast.makeText(contexto, "Sesión cerrada", Toast.LENGTH_SHORT).show()
    }


    fun verificarAcceso(actividad: Activity, rolesPermitidos: List<Int>) {
        if (!sesionIniciada()) {
            Toast.makeText(contexto, "Debes iniciar sesión para continuar", Toast.LENGTH_LONG).show()
            redirigirALoginYFinalizar(actividad)
            return
        }

        val rolUsuario = obtenerIdRol()
        if (rolUsuario !in rolesPermitidos) {
            Toast.makeText(contexto, "No tienes permisos para acceder a esta sección", Toast.LENGTH_LONG).show()
            redirigirALoginYFinalizar(actividad)
        }
    }

    private fun redirigirALoginYFinalizar(actividad: Activity) {
        val intent = Intent(contexto, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        contexto.startActivity(intent)
        actividad.finish()
    }
}