package com.example.kinora


import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import kotlin.random.Random
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.kinora.PeliculasAdapter.PeliculaViewHolder

interface OnPeticionUpdateListener {
    fun onUpdateSuccess()
}

class PeticionesAdapter(private val listaPeticiones: List<Peticion>) : RecyclerView.Adapter<PeticionesAdapter.ViewHolder>() {


    private var listener: OnPeticionUpdateListener? = null


    fun setOnPeticionUpdateListener(listener: OnPeticionUpdateListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeticionesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_peticion_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: PeticionesAdapter.ViewHolder, position: Int) {
        val peticionActual = listaPeticiones[position]
        holder.nombreCine.text = peticionActual.nombre_cine
        holder.nombrePelicula.text = peticionActual.nombre_pelicula

        Log.d("PeticionesAdapter", "bind position $position -> ${peticionActual.nombre_pelicula}")


        Log.d("Adapter", "Bind: ${peticionActual.nombre_pelicula}")
        holder.btnDetalles.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)

            val inflater=LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.activity_peticion_detalles, null)

            val tvNombreCine = dialogView.findViewById<TextView>(R.id.txt_cine)
            val tvNombreEncargado = dialogView.findViewById<TextView>(R.id.txt_encargado)
            val tvPelicula = dialogView.findViewById<TextView>(R.id.txt_pelicula)
            val tvDirector = dialogView.findViewById<TextView>(R.id.txt_director)
            val tvGenero = dialogView.findViewById<TextView>(R.id.txt_genero)
            val tvClasificacion = dialogView.findViewById<TextView>(R.id.txt_clasif)
            val tvTipo = dialogView.findViewById<TextView>(R.id.txt_tipop)
            val tvActores = dialogView.findViewById<TextView>(R.id.txt_actores)
            val btnAceptar = dialogView.findViewById<Button>(R.id.btn_aceptar)
            val btnRechazar = dialogView.findViewById<Button>(R.id.btn_rechazar)

            tvNombreCine.text = peticionActual.nombre_cine
            tvNombreEncargado.text = peticionActual.nombre_encargado
            tvPelicula.text = peticionActual.nombre_pelicula
            tvDirector.text = peticionActual.director
            tvGenero.text = peticionActual.genero
            tvClasificacion.text = peticionActual.clasificacion
            tvTipo.text = peticionActual.tipo
            tvActores.text = peticionActual.actores

            builder.setView(dialogView)
            val dialog = builder.create()

            //val url = "http://192.168.2.103/kinora_php/actualizar_estado_pelicula.php"
            val url = "http://10.0.2.2/kinora_php/actualizar_estado_pelicula.php"


            btnAceptar.setOnClickListener {
                val stringRequest = object : StringRequest(
                    Method.POST,
                    url,
                    Response.Listener<String> { response ->
                        Toast.makeText(context, "Película Aceptada", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        listener?.onUpdateSuccess()
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(context, "Error al aceptar: ${error.message}", Toast.LENGTH_SHORT).show()
                        Log.e("PeticionesAdapter", "Error al aceptar película", error)
                    }
                ) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["id_pelicula"] = peticionActual.id_pelicula.toString()
                        params["id_estado_pelicula"] = "1" // 1 = Aceptada
                        return params
                    }
                }
                Volley.newRequestQueue(context).add(stringRequest)
            }

            btnRechazar.setOnClickListener {
                val stringRequest = object : StringRequest(
                    Method.POST,
                    url,
                    Response.Listener<String> { response ->
                        Toast.makeText(context, "Película rechazada", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        listener?.onUpdateSuccess()
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(context, "Error al rechazar: ${error.message}", Toast.LENGTH_SHORT).show()
                        Log.e("PeticionesAdapter", "Error al rechazar película", error)
                    }
                ) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["id_pelicula"] = peticionActual.id_pelicula.toString()
                        params["id_estado_pelicula"] = "2" // 2 = Rechazada
                        return params
                    }
                }
                Volley.newRequestQueue(context).add(stringRequest)
            }

            dialog.show()

        }

    }

    override fun getItemCount(): Int {
        return listaPeticiones.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nombreCine: TextView = itemView.findViewById(R.id.pt_nombre_cine)
        val nombrePelicula: TextView = itemView.findViewById(R.id.pt_pelicula)
        val btnDetalles: Button = itemView.findViewById(R.id.btn_detalles)
    }

}

