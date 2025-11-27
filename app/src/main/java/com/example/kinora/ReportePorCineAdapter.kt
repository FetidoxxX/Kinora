package com.example.kinora

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


    fun setOnReportePorCineUpdateListener(listener: OnReportePorCineUpdateListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportePorCineAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_reporte_clientes_item_cine, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ReportePorCineAdapter.ViewHolder, position: Int) {
        val cineActual = listaCines[position]
        holder.nombreCine.text = cineActual.nombre
        holder.direccionCine.text = cineActual.direccion

        Log.d("ReportePorCineAdapter", "bind position $position -> ${cineActual.nombre}")
        Log.d("Adapter", "Bind: ${cineActual.direccion}")
    }

    override fun getItemCount(): Int {
        return listaCines.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreCine: TextView = itemView.findViewById(R.id.tv_nombre_cine)
        val direccionCine: TextView = itemView.findViewById(R.id.tv_direccion_cine)
        val switchCines: SwitchCompat = itemView.findViewById(R.id.switch_cines)
    }

}

