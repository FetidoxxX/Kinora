package com.example.kinora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FuncionCineAdapter(private val listaFuncionesCine: List<FuncionCine>) :
    RecyclerView.Adapter<FuncionCineAdapter.FuncionCineViewHolder>() {

    class FuncionCineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCineNombre: TextView = itemView.findViewById(R.id.CINETxT)
        val rvHoras: RecyclerView = itemView.findViewById(R.id.rvHoras)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuncionCineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_funcion, parent, false)
        return FuncionCineViewHolder(view)
    }

    override fun onBindViewHolder(holder: FuncionCineViewHolder, position: Int) {
        val funcionCine = listaFuncionesCine[position]

        holder.tvCineNombre.text = funcionCine.nombreCine

        holder.rvHoras.apply {
            layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = FuncionHoraAdapter(funcionCine.horas)
            setHasFixedSize(true)
        }
    }

    override fun getItemCount() = listaFuncionesCine.size
}