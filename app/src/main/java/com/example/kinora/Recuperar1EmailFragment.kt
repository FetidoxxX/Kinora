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

class Recuperar1EmailFragment : Fragment() {

    //private val url = "http://10.0.2.2/kinora_php/recuperar_1_solicitar.php"
    private val url = "http://192.168.1.6/kinora_php/recuperar_1_solicitar.php"//michael

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recuperar_1_email, container, false)

        val etEmail = view.findViewById<EditText>(R.id.et_recuperar_email)
        val btnEnviar = view.findViewById<Button>(R.id.btn_enviar_codigo)

        btnEnviar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, ingrese su email.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnEnviar.isEnabled = false
            solicitarCodigo(email)
        }

        return view
    }

    private fun solicitarCodigo(email: String) {
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

                    if (success) {
                        (activity as? recuperar_clave)?.emailVerificado = email
                        (activity as? recuperar_clave)?.navegarAFragmento(Recuperar2CodigoFragment())
                    } else {
                        view?.findViewById<Button>(R.id.btn_enviar_codigo)?.isEnabled = true
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error al procesar la respuesta.", Toast.LENGTH_SHORT).show()
                    view?.findViewById<Button>(R.id.btn_enviar_codigo)?.isEnabled = true
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(requireContext(), "Error de red: ${error.message}", Toast.LENGTH_LONG).show()
                view?.findViewById<Button>(R.id.btn_enviar_codigo)?.isEnabled = true
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }
}