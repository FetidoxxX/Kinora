package com.example.kinora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class FuncionAdapter(
    private val listaFunciones: List<Funcion>,
    private val onVerClick: (Funcion) -> Unit,
    private val onEditarClick: (Funcion) -> Unit
) : RecyclerView.Adapter<FuncionAdapter.FuncionViewHolder>() {

    class FuncionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombrePelicula: TextView = itemView.findViewById(R.id.tvNombrePeliculaFuncion)
        val tvFechaHora: TextView = itemView.findViewById(R.id.tvFechaHoraFuncion)
        val tvSala: TextView = itemView.findViewById(R.id.tvSalaFuncion)
        val btnVer: ImageButton = itemView.findViewById(R.id.btnVerFuncion)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarFuncion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuncionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_funcion_admin, parent, false)
        return FuncionViewHolder(view)
    }

    override fun onBindViewHolder(holder: FuncionViewHolder, position: Int) {
        val funcion = listaFunciones[position]
        holder.tvNombrePelicula.text = funcion.nombre_pelicula
        
        // Formatear hora
        try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = inputFormat.parse(funcion.fecha_hora)
            if (date != null) {
                holder.tvFechaHora.text = outputFormat.format(date)
            } else {
                holder.tvFechaHora.text = funcion.fecha_hora
            }
        } catch (e: Exception) {
            holder.tvFechaHora.text = funcion.fecha_hora
        }

        holder.tvSala.text = "Sala: ${funcion.numero_sala}"

        holder.btnVer.setOnClickListener { onVerClick(funcion) }
        holder.btnEditar.setOnClickListener { onEditarClick(funcion) }
    }

    override fun getItemCount(): Int = listaFunciones.size
}
