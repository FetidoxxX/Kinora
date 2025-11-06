package com.example.kinora

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Recuperar2CodigoFragment : Fragment() {

    private val urlVerificar = "http://10.0.2.2/kinora_php/recuperar_2_verificar.php"
    private val urlSolicitar = "http://10.0.2.2/kinora_php/recuperar_1_solicitar.php"
    private var email: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recuperar_2_codigo, container, false)

        email = (activity as? recuperar_clave)?.emailVerificado
        if (email == null) {
            (activity as? recuperar_clave)?.navegarAFragmento(Recuperar1EmailFragment(), false)
            return null
        }

        val etCodigo = view.findViewById<EditText>(R.id.et_recuperar_codigo)
        val btnVerificar = view.findViewById<Button>(R.id.btn_verificar_codigo)
        val tvReenviar = view.findViewById<TextView>(R.id.tv_reenviar_codigo)

        btnVerificar.setOnClickListener {
            val codigo = etCodigo.text.toString().trim()
            if (codigo.length != 6) {
                Toast.makeText(requireContext(), "El código debe ser de 6 dígitos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            btnVerificar.isEnabled = false
            verificarCodigo(codigo)
        }

        tvReenviar.setOnClickListener {
            tvReenviar.isEnabled = false
            Toast.makeText(requireContext(), "Reenviando código...", Toast.LENGTH_SHORT).show()
            reenviarCodigo(email!!)
        }

        return view
    }

    private fun verificarCodigo(codigo: String) {
        val stringRequest = object : StringRequest(
            Request.Method.POST, urlVerificar,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    if (success) {
                        (activity as? recuperar_clave)?.codigoVerificado = codigo
                        (activity as? recuperar_clave)?.navegarAFragmento(Recuperar3ClaveFragment())
                    } else {
                        view?.findViewById<Button>(R.id.btn_verificar_codigo)?.isEnabled = true
                    }
                } catch (e: Exception) {

                    view?.findViewById<Button>(R.id.btn_verificar_codigo)?.isEnabled = true
                }
            },
            Response.ErrorListener { error ->
                view?.findViewById<Button>(R.id.btn_verificar_codigo)?.isEnabled = true
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email!!
                params["codigo"] = codigo
                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }

    private fun reenviarCodigo(email: String) {
        val stringRequest = object : StringRequest(
            Request.Method.POST, urlSolicitar,
            Response.Listener { response ->
                try {
                    val message = JSONObject(response).getString("message")
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error procesando respuesta de reenvío.", Toast.LENGTH_LONG).show()
                }
                view?.findViewById<TextView>(R.id.tv_reenviar_codigo)?.isEnabled = true
            },
            Response.ErrorListener {
                Toast.makeText(requireContext(), "Error de red al reenviar.", Toast.LENGTH_SHORT).show()
                view?.findViewById<TextView>(R.id.tv_reenviar_codigo)?.isEnabled = true
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