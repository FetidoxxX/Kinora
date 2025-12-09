package com.example.kinora

import android.app.AlertDialog
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

class DirectorAdapter(private val listaDirectores: List<Director>) :
    RecyclerView.Adapter<DirectorAdapter.DirectorViewHolder>() {

    class DirectorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreItem: TextView = itemView.findViewById(R.id.tvNombreItem)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cosas_pelicula, parent, false)

        return DirectorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DirectorViewHolder, position: Int) {
        val directorActual = listaDirectores[position]
        val nombreCompleto = "${directorActual.nombre} ${directorActual.apellido}"
        holder.tvNombreItem.text = nombreCompleto

        holder.btnEditar.setOnClickListener {
            val context = holder.itemView.context

            val builder = android.app.AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)

            val dialogView = inflater.inflate(R.layout.editar_actor, null)

            val edtNombre = dialogView.findViewById<EditText>(R.id.edtNombre)
            val edtApellido = dialogView.findViewById<EditText>(R.id.edtApellido)
            val btnGuardar = dialogView.findViewById<LinearLayout>(R.id.btnGuardar)

            edtNombre.setText(directorActual.nombre)
            edtApellido.setText(directorActual.apellido)

            builder.setView(dialogView)
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            btnGuardar.setOnClickListener {
                val nuevoNombre = edtNombre.text.toString().trim()
                val nuevoApellido = edtApellido.text.toString().trim()

                if (nuevoNombre.isEmpty() || nuevoApellido.isEmpty()) {
                    Toast.makeText(context, "El nombre y apellido no pueden estar vacíos.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                actualizarDirectorEnServidor(context, directorActual.id_director, nuevoNombre, nuevoApellido, dialog)
            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return listaDirectores.size
    }

    private fun actualizarDirectorEnServidor(context: Context, idDirector: String, nuevoNombre: String, nuevoApellido: String, dialog: android.app.AlertDialog) {

        //val url = "http://192.168.80.25/kinora_php/actualizar_director.php"
        val url = "http://10.0.2.2/kinora_php/actualizar_director.php"


        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            com.android.volley.Response.Listener<String> { response ->
                android.util.Log.e("KINORA_PHP_RESPONSE", "Respuesta del Servidor (Director): $response")
                Toast.makeText(context, "Director actualizado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            },
            com.android.volley.Response.ErrorListener { error ->
                Toast.makeText(context, "Error al actualizar: ${error.message}", Toast.LENGTH_LONG).show()
                android.util.Log.e("DirectorAdapter", "Error al actualizar director", error)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_director"] = idDirector
                params["nuevo_nombre"] = nuevoNombre
                params["nuevo_apellido"] = nuevoApellido
                return params
            }
        }

        Volley.newRequestQueue(context).add(stringRequest)
    }
}