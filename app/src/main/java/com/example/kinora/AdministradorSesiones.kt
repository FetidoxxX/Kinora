package com.example.kinora

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

/**
 * Objeto Singleton para definir los roles de usuario
 * basados en la base de datos kinora_db(5).sql.
 */
object Roles {
    const val ADMINISTRADOR = 1
    const val ENCARGADO = 2
    const val CLIENTE = 3
}

/**
 * Clase para gestionar la sesión del usuario en toda la aplicación Kinora.
 * Utiliza SharedPreferences para almacenar los datos de la sesión.
 *
 * @param contexto El contexto de la aplicación o actividad.
 */
class AdministradorSesiones(private val contexto: Context) {

    private val preferencias: SharedPreferences
    private val editor: SharedPreferences.Editor

    companion object {
        // Nombre del archivo de preferencias
        private const val NOMBRE_PREFERENCIAS = "KinoraSessionPrefs"

        // Claves (keys) para los datos de sesión.
        private const val CLAVE_SESION_INICIADA = "isLoggedIn"
        private const val CLAVE_ID_USUARIO = "userId"
        private const val CLAVE_ROL_USUARIO = "userRole"
        private const val CLAVE_NOMBRE_USUARIO = "userName"
        private const val CLAVE_EMAIL_USUARIO = "userEmail"
        private const val CLAVE_USUARIO_LOGIN = "userUsuario" // Para el nombre de usuario de login
    }

    init {
        // Inicializa SharedPreferences en modo privado
        preferencias = contexto.getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE)
        editor = preferencias.edit()
    }

    /**
     * Crea la sesión del usuario después de un login exitoso.
     *
     * @param id_u El ID del usuario.
     * @param rol_id El ID del rol (1=Admin, 2=Encargado, 3=Cliente).
     * @param nombre El nombre completo del usuario.
     * @param email El email del usuario.
     * @param usuario El nombre de usuario (login).
     */
    fun crearSesion(id_u: Int, rol_id: Int, nombre: String, email: String, usuario: String) {
        editor.putBoolean(CLAVE_SESION_INICIADA, true)
        editor.putInt(CLAVE_ID_USUARIO, id_u)
        editor.putInt(CLAVE_ROL_USUARIO, rol_id)
        editor.putString(CLAVE_NOMBRE_USUARIO, nombre)
        editor.putString(CLAVE_EMAIL_USUARIO, email)
        editor.putString(CLAVE_USUARIO_LOGIN, usuario)
        editor.apply() // Guarda los cambios
    }

    /**
     * Alternativa para crear sesión directamente desde la respuesta JSON de login.php.
     * @param respuestaJson Respuesta JSON cruda (que es un array con un objeto de usuario).
     */
    fun crearSesionDesdeJson(respuestaJson: String) {
        try {
            val jsonArray = JSONArray(respuestaJson)
            if (jsonArray.length() > 0) {
                val objetoUsuario = jsonArray.getJSONObject(0) // Variable local
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

    /**
     * Verifica si el usuario tiene una sesión activa.
     * @return true si el usuario está logueado, false en caso contrario.
     */
    fun sesionIniciada(): Boolean {
        return preferencias.getBoolean(CLAVE_SESION_INICIADA, false)
    }

    /**
     * Obtiene el ID del rol del usuario logueado.
     * @return El ID del rol (1, 2, o 3), o -1 si no está logueado.
     */
    fun obtenerIdRol(): Int {
        return preferencias.getInt(CLAVE_ROL_USUARIO, -1)
    }

    /**
     * Obtiene el ID del usuario logueado.
     * @return El ID del usuario, o -1 si no está logueado.
     */
    fun obtenerIdUsuario(): Int {
        return preferencias.getInt(CLAVE_ID_USUARIO, -1)
    }

    /**
     * Obtiene los detalles del usuario almacenados en la sesión.
     * @return Un Mapa con los datos del usuario.
     */
    fun obtenerDatosUsuario(): Map<String, String?> {
        return mapOf(
            "id" to preferencias.getInt(CLAVE_ID_USUARIO, -1).toString(),
            "rol" to preferencias.getInt(CLAVE_ROL_USUARIO, -1).toString(),
            "nombre" to preferencias.getString(CLAVE_NOMBRE_USUARIO, null),
            "email" to preferencias.getString(CLAVE_EMAIL_USUARIO, null),
            "usuario" to preferencias.getString(CLAVE_USUARIO_LOGIN, null)
        )
    }

    /**
     * Cierra la sesión del usuario, borra todos los datos de SharedPreferences
     * y redirige al Login (MainActivity).
     */
    fun cerrarSesion() {
        editor.clear()
        editor.apply()

        // Redirigir a MainActivity (Login)
        val intent = Intent(contexto, MainActivity::class.java)
        // Flags para limpiar el stack de actividades y empezar de nuevo
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        contexto.startActivity(intent)
        Toast.makeText(contexto, "Sesión cerrada", Toast.LENGTH_SHORT).show()
    }

    /**
     * Método principal para proteger una Activity.
     * Se debe llamar en el `onCreate` de las actividades protegidas (ej. Home, cines_admin).
     *
     * @param actividad La Activity que se está verificando (se usa para finalizarla si es necesario).
     * @param rolesPermitidos Lista de IDs de roles permitidos (ej. listOf(Roles.ADMINISTRADOR)).
     */
    fun verificarAcceso(actividad: Activity, rolesPermitidos: List<Int>) {
        // 1. ¿No está logueado?
        if (!sesionIniciada()) {
            Toast.makeText(contexto, "Debes iniciar sesión para continuar", Toast.LENGTH_LONG).show()
            redirigirALoginYFinalizar(actividad)
            return
        }

        // 2. ¿Tiene el rol permitido?
        val rolUsuario = obtenerIdRol()
        if (rolUsuario !in rolesPermitidos) {
            Toast.makeText(contexto, "No tienes permisos para acceder a esta sección", Toast.LENGTH_LONG).show()
            // Redirige al login y cierra la activity actual
            redirigirALoginYFinalizar(actividad)
        }
        // Si pasa ambas pruebas, no hace nada y la Activity continúa.
    }

    /**
     * Redirige al usuario a la pantalla de Login (MainActivity) y
     * finaliza la actividad actual para que no quede en el stack.
     */
    private fun redirigirALoginYFinalizar(actividad: Activity) {
        // Aquí está la redirección a MainActivity que mencionaste
        val intent = Intent(contexto, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        contexto.startActivity(intent)
        actividad.finish() // Cierra la actividad protegida
    }
}