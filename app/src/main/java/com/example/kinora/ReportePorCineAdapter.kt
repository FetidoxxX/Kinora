package com.example.kinora

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

interface OnReportePorCineUpdateListener {
    fun onUpdateSuccess()
}

class ReportePorCineAdapter(private val listaCines: List<Cine>) : RecyclerView.Adapter<ReportePorCineAdapter.ViewHolder>() {


    private var listener: OnReportePorCineUpdateListener? = null

    private var selectedPosition = RecyclerView.NO_POSITION


    fun setOnReportePorCineUpdateListener(listener: OnReportePorCineUpdateListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportePorCineAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_reporte_item_cine, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ReportePorCineAdapter.ViewHolder, position: Int) {
        val cineActual = listaCines[position]
        holder.nombreCine.text = cineActual.nombre
        holder.direccionCine.text = cineActual.direccion

        Log.d("ReportePorCineAdapter", "bind position $position -> ${cineActual.nombre}")
        Log.d("Adapter", "Bind: ${cineActual.direccion}")

        // Obtener el layout raíz para cambiar color
        val rootLayout = holder.itemView.findViewById<View>(R.id.layout_root)

        // Si este item es el seleccionado → aplicar morado claro
        if (position == selectedPosition) {
            rootLayout.setBackgroundColor(Color.parseColor("#5C4D9B")) // morado más claro
        } else {
            rootLayout.setBackgroundColor(Color.parseColor("#332D6C")) // morado normal
        }

        holder.itemView.setOnClickListener {

            // Guardamos la posición anterior
            val previousPosition = selectedPosition

            // Actualizamos la selección
            selectedPosition = holder.adapterPosition

            // Refrescar solo los afectados
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            // Llamar tu evento existente
            onItemClick?.invoke(cineActual)
        }

    }

    override fun getItemCount(): Int {
        return listaCines.size
    }

    private var onItemClick: ((Cine) -> Unit)? = null

    fun setOnItemClickListener(listener: (Cine) -> Unit) {
        onItemClick = listener
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreCine: TextView = itemView.findViewById(R.id.tv_nombre_cine)
        val direccionCine: TextView = itemView.findViewById(R.id.tv_direccion_cine)
    }

}

