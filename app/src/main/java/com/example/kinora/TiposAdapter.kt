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

class TiposAdapter(private val listaTipos: List<Tipo>) :
    RecyclerView.Adapter<TiposAdapter.TipoViewHolder>() {
    class TipoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreItem: TextView = itemView.findViewById(R.id.tvNombreItem)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cosas_pelicula, parent, false)
        return TipoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipoViewHolder, position: Int) {
        val tipoActual = listaTipos[position]
        holder.tvNombreItem.text = tipoActual.tipo

        holder.btnEditar.setOnClickListener {
            val context = holder.itemView.context

            val builder = android.app.AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.editar_tipo, null)

            val edtTipo = dialogView.findViewById<EditText>(R.id.edtTipo)
            val btnGuardar = dialogView.findViewById<LinearLayout>(R.id.btnGuardarTipo)

            edtTipo.setText(tipoActual.tipo)

            builder.setView(dialogView)
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            btnGuardar.setOnClickListener {
                val nuevoNombre = edtTipo.text.toString().trim()

                if (nuevoNombre.isEmpty()) {
                    Toast.makeText(context, "El nombre del tipo no puede estar vacío.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                actualizarTipoEnServidor(context, tipoActual.id_tipo, nuevoNombre, dialog)
            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return listaTipos.size
    }

    private fun actualizarTipoEnServidor(context: Context, idTipo: String, nuevoNombre: String, dialog: android.app.AlertDialog) {

        //val url = "http://192.168.80.25/kinora_php/actualizar_tipo.php"
        val url = "http://10.0.2.2/kinora_php/actualizar_tipo.php"


        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            com.android.volley.Response.Listener<String> { response ->
                android.util.Log.e("KINORA_PHP_RESPONSE", "Respuesta del Servidor (Tipo): $response")
                Toast.makeText(context, "Tipo actualizado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            },
            com.android.volley.Response.ErrorListener { error ->
                Toast.makeText(context, "Error al actualizar: ${error.message}", Toast.LENGTH_LONG).show()
                android.util.Log.e("TiposAdapter", "Error al actualizar tipo", error)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_tipo"] = idTipo
                params["nuevo_nombre"] = nuevoNombre
                return params
            }
        }

        Volley.newRequestQueue(context).add(stringRequest)
    }
}