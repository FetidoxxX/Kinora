package com.example.kinora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

interface OnItemClickListener {
    fun onItemClick(pelicula: PeliculaCartelera)
}

class PeliculaCarteleraAdapter(
    private val listaPeliculas: List<PeliculaCartelera>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<PeliculaCarteleraAdapter.PeliculaViewHolder>() {

    inner class PeliculaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val ivPoster: ImageView = itemView.findViewById(R.id.ivPoster)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(listaPeliculas[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeliculaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pelicula_cartelera, parent, false)
        return PeliculaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeliculaViewHolder, position: Int) {
        val peliculaActual = listaPeliculas[position]
        val urlImagen = peliculaActual.urlPoster
        Glide.with(holder.itemView.context)
            .load(urlImagen)
            .centerCrop()
            .into(holder.ivPoster)
    }

    override fun getItemCount(): Int {
        return listaPeliculas.size
    }
}