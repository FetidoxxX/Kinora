package com.example.kinora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast


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
            val context = holder.itemView.context

            val fm = (context as? androidx.fragment.app.FragmentActivity)?.supportFragmentManager
            if (fm != null) {
                val dialog = detalles_pelicula.newInstance(peliculaActual.id_pelicula)
                dialog.show(fm, "detalles_pelicula")
            } else {
                Toast.makeText(context, "No se puede abrir el diálogo desde este contexto", Toast.LENGTH_SHORT).show()
            }
        }

        holder.btnBorrar.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Borrando: ${peliculaActual.nombre}", Toast.LENGTH_SHORT).show()

        }
    }

    override fun getItemCount() = listaPeliculas.size
}
