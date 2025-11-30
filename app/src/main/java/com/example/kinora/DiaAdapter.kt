package com.example.kinora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiaAdapter(
    private val listaPromociones: List<Dia>,
    private val onEditClick: (Dia) -> Unit
) : RecyclerView.Adapter<DiaAdapter.DiaViewHolder>() {

    class DiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombrePromocion)
        val tvDescuento: TextView = itemView.findViewById(R.id.tvDescuentoPromocion)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFechaPromocion)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarPromocion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promocion, parent, false)
        return DiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
        val promocion = listaPromociones[position]
        holder.tvNombre.text = promocion.nombre
        holder.tvDescuento.text = "${promocion.descuento}%"
        holder.tvFecha.text = "${promocion.fecha}"

        holder.btnEditar.setOnClickListener {
            onEditClick(promocion)
        }
    }

    override fun getItemCount(): Int = listaPromociones.size
}
