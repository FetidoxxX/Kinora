package com.example.kinora

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kinora.databinding.ItemSillaBinding

class sillasAdapter(private val listaSillas: List<Silla>,
                    private val onSillaClickListener: (Silla)-> Unit)
    : RecyclerView.Adapter<sillasAdapter.ViewHolder>() {

    inner class ViewHolder(val binding : ItemSillaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(silla: Silla){
            binding.btnSilla.text="${silla.fila}${silla.columna}"

            when(silla.estado){
                EstadoSilla.OCUPADA->{
                    binding.btnSilla.isEnabled=false
                    binding.btnSilla.isChecked=false
                }
                EstadoSilla.DISPONIBLE -> {
                    binding.btnSilla.isChecked = false
                    binding.btnSilla.isEnabled = true
                }
                EstadoSilla.SELECCIONADA -> {
                    binding.btnSilla.isChecked = true
                    binding.btnSilla.isEnabled = true
                }
            }

            binding.btnSilla.setOnClickListener{
                onSillaClickListener(silla)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        /*val inflater = LayoutInflater.from(parent.context)
        val bind = inflater.inflate(R.layout.item_silla,parent,false)
        return ViewHolder(bind)
        */
        val binding = ItemSillaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val sillaActual = listaSillas[position]
        holder.bind(sillaActual)
    }

    override fun getItemCount(): Int {
        return listaSillas.size
    }
}






