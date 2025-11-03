package com.example.kinora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CineAdapter(private val listaCines: List<Cine>) : RecyclerView.Adapter<CineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CineAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val view = inflater.inflate(R.layout.item_cine, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: CineAdapter.ViewHolder, position: Int) {
        val cineActual = listaCines[position]
        holder.nombreCine.text = cineActual.nombre
        holder.direccionDine.text = cineActual.direccion


    }

    override fun getItemCount(): Int {
        return listaCines.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nombreCine: TextView = itemView.findViewById(R.id.tv_nombre_cine)
        val direccionDine: TextView = itemView.findViewById(R.id.tv_direccion_cine)
        val btnDetalles: Button = itemView.findViewById(R.id.btn_detalles)
        val btnActualizar: Button = itemView.findViewById(R.id.btn_actualizar)
    }
}

