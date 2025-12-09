package com.example.kinora

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlin.jvm.java

class FuncionHoraAdapter(private val listaHoras: List<FuncionHora>) :
    RecyclerView.Adapter<FuncionHoraAdapter.HoraViewHolder>() {

    class HoraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHora: TextView = itemView.findViewById(R.id.textView15)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoraViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hora, parent, false)
        return HoraViewHolder(view)
    }

    override fun onBindViewHolder(holder: HoraViewHolder, position: Int) {
        val funcionActual = listaHoras[position]
        holder.tvHora.text = funcionActual.hora

        holder.itemView.setOnClickListener {
            val idFuncionSeleccionada = funcionActual.id_funcion
            val intent = Intent(holder.itemView.context, Usuario_seleccion_sillas::class.java)
            intent.putExtra("ID_FUNCION_SELECCIONADA", idFuncionSeleccionada)
            holder.itemView.context.startActivity(intent)
            Toast.makeText(
                holder.itemView.context,
                "Función ID: $idFuncionSeleccionada",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    override fun getItemCount() = listaHoras.size
}