package com.example.kinora

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class GenerosAdapter(private val listaGeneros: List<Genero>) :
    RecyclerView.Adapter<GenerosAdapter.GeneroViewHolder>() {
    class GeneroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreItem: TextView = itemView.findViewById(R.id.tvNombreItem)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cosas_pelicula, parent, false)

        return GeneroViewHolder(view)
    }

    override fun onBindViewHolder(holder: GeneroViewHolder, position: Int) {
        val generoActual = listaGeneros[position]
        holder.tvNombreItem.text = generoActual.genero

        holder.btnEditar.setOnClickListener {
            val context = holder.itemView.context

            val builder = android.app.AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)

            val dialogView = inflater.inflate(R.layout.editar_tipo, null)

            val edtNombre = dialogView.findViewById<EditText>(R.id.edtTipo)
            val btnGuardar = dialogView.findViewById<LinearLayout>(R.id.btnGuardarTipo)

            edtNombre.setText(generoActual.genero)

            builder.setView(dialogView)
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            btnGuardar.setOnClickListener {
                val nuevoNombre = edtNombre.text.toString().trim()

                if (nuevoNombre.isEmpty()) {
                    Toast.makeText(context, "El nombre del género no puede estar vacío.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                actualizarGeneroEnServidor(context, generoActual.id_genero, nuevoNombre, dialog)
            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return listaGeneros.size
    }

    private fun actualizarGeneroEnServidor(context: Context, idGenero: String, nuevoNombre: String, dialog: android.app.AlertDialog) {
        val url = "http://192.168.80.25/kinora_php/actualizar_genero.php"

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            com.android.volley.Response.Listener<String> { response ->
                android.util.Log.e("KINORA_PHP_RESPONSE", "Respuesta del Servidor (Género): $response")
                Toast.makeText(context, "Género actualizado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            },
            com.android.volley.Response.ErrorListener { error ->
                Toast.makeText(context, "Error al actualizar: ${error.message}", Toast.LENGTH_LONG).show()
                android.util.Log.e("GenerosAdapter", "Error al actualizar género", error)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_genero"] = idGenero
                params["nuevo_nombre"] = nuevoNombre
                return params
            }
        }

        Volley.newRequestQueue(context).add(stringRequest)
    }
}