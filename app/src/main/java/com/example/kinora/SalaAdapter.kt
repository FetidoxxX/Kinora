package com.example.kinora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SalaAdapter(
    private val listaSalas: List<Sala>,
    private val onEditClick: (Sala) -> Unit
) : RecyclerView.Adapter<SalaAdapter.SalaViewHolder>() {

    class SalaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumero: TextView = itemView.findViewById(R.id.tvNumeroSala)
        val tvCapacidad: TextView = itemView.findViewById(R.id.tvCapacidadSala)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarSala)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sala, parent, false)
        return SalaViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalaViewHolder, position: Int) {
        val sala = listaSalas[position]
        holder.tvNumero.text = "Sala ${sala.numero_sala}"
        holder.tvCapacidad.text = "Capacidad: ${sala.capacidad}"

        holder.btnEditar.setOnClickListener {
            onEditClick(sala)
        }
    }

    override fun getItemCount(): Int = listaSalas.size
}
