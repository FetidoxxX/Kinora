package com.example.kinora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup    import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PeliculasAdapter(private val listaPeliculas: List<Pelicula>) : RecyclerView.Adapter<PeliculasAdapter.PeliculaViewHolder>() {

    class PeliculaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombrePelicula: TextView = itemView.findViewById(R.id.tvNombrePelicula)
        val btnDetalles: Button = itemView.findViewById(R.id.btnDetalles)
        val btnBorrar: Button = itemView.findViewById(R.id.btnBorrar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeliculaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_pelicula, parent, false)
        return PeliculaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeliculaViewHolder, position: Int) {
        val peliculaActual = listaPeliculas[position]
        holder.nombrePelicula.text = peliculaActual.nombre

        holder.btnDetalles.setOnClickListener {

        }
        holder.btnBorrar.setOnClickListener {

        }
    }

    override fun getItemCount() = listaPeliculas.size
}
