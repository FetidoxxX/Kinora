package com.example.kinora

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Recuperar3ClaveFragment : Fragment() {

    private val url = "http://10.0.2.2/kinora_php/recuperar_3_restablecer.php"
    //private val url = "http://192.168.1.6/kinora_php/recuperar_3_restablecer.php"//michael
    private var email: String? = null
    private var codigo: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recuperar_3_clave, container, false)

        val act = (activity as? recuperar_clave)
        email = act?.emailVerificado
        codigo = act?.codigoVerificado

        if (email == null || codigo == null) {
            act?.navegarAFragmento(Recuperar1EmailFragment(), false)
            return null
        }

        val etNuevaClave = view.findViewById<EditText>(R.id.et_nueva_clave)
        val etConfirmarClave = view.findViewById<EditText>(R.id.et_confirmar_clave)
        val btnGuardar = view.findViewById<Button>(R.id.btn_guardar_clave)

        btnGuardar.setOnClickListener {
            val nuevaClave = etNuevaClave.text.toString()
            val confirmarClave = etConfirmarClave.text.toString()

            if (nuevaClave.isEmpty() || confirmarClave.isEmpty()) {
                Toast.makeText(requireContext(), "Complete ambos campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (nuevaClave.length < 8) {
                Toast.makeText(requireContext(), "La contraseña debe tener al menos 8 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (nuevaClave != confirmarClave) {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnGuardar.isEnabled = false
            restablecerClave(nuevaClave)
        }

        return view
    }

    private fun restablecerClave(nuevaClave: String) {
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

                    if (success) {
                        activity?.finish()
                    } else {
                        view?.findViewById<Button>(R.id.btn_guardar_clave)?.isEnabled = true
                    }
                } catch (e: Exception) {
                    view?.findViewById<Button>(R.id.btn_guardar_clave)?.isEnabled = true
                }
            },
            Response.ErrorListener { error ->

                view?.findViewById<Button>(R.id.btn_guardar_clave)?.isEnabled = true
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email!!
                params["codigo"] = codigo!!
                params["nueva_clave"] = nuevaClave
                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }
}