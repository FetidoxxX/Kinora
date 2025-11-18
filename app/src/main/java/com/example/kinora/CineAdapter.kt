package com.example.kinora

import android.app.AlertDialog
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

interface OnCineUpdateListener {
    fun onUpdateSuccess()
}
class CineAdapter(private val listaCines: List<Cine>) : RecyclerView.Adapter<CineAdapter.ViewHolder>() {


    private var listener: OnCineUpdateListener? = null


    fun setOnCineUpdateListener(listener: OnCineUpdateListener) {
        this.listener = listener
    }

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

        holder.btnDetalles.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)

            val inflater=LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.detalles_cine, null)

            val tvNombreCine = dialogView.findViewById<TextView>(R.id.tv_detalle_nombre_cine)
            val tvDireccionCine = dialogView.findViewById<TextView>(R.id.tv_detalle_direccion_cine)
            val tvTelefonoCine = dialogView.findViewById<TextView>(R.id.tv_detalle_telefono_cine)
            val tvNombreUsuario = dialogView.findViewById<TextView>(R.id.tv_detalle_nombre_usuario)
            val tvEmailUsuario = dialogView.findViewById<TextView>(R.id.tv_detalle_email_usuario)
            val btnAceptar = dialogView.findViewById<Button>(R.id.btn_detalle_aceptar)

            tvNombreCine.text = cineActual.nombre
            tvDireccionCine.text = cineActual.direccion
            tvTelefonoCine.text = cineActual.telefono
            tvNombreUsuario.text = cineActual.nombre_usuario
            tvEmailUsuario.text = cineActual.email

            builder.setView(dialogView)
            val dialog = builder.create()
            btnAceptar.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()



        }
        holder.btnActualizar.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)

            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.actualizar_cine, null)

            val etNombreCine = dialogView.findViewById<EditText>(R.id.et_actualizar_nombre_cine)
            val etDireccionCine = dialogView.findViewById<EditText>(R.id.et_actualizar_direccion)
            val etTelefonoCine = dialogView.findViewById<EditText>(R.id.et_actualizar_telefono)
            val etNombreUsuario = dialogView.findViewById<EditText>(R.id.et_actualizar_nombre_usuario)
            val etEmailUsuario = dialogView.findViewById<EditText>(R.id.et_actualizar_email)
            //val etDocumentoUsuario = dialogView.findViewById<EditText>(R.id.et_actualizar_documento)
            val etUsuario=dialogView.findViewById<EditText>(R.id.et_actualizar_usuario)
            val btnActualizar = dialogView.findViewById<Button>(R.id.btn_actualizar_guardar)
            val spinnerEstado= dialogView.findViewById<Spinner>(R.id.spinner_actualizar_estado)


            etNombreCine.setText(cineActual.nombre)
            etDireccionCine.setText(cineActual.direccion)
            etTelefonoCine.setText(cineActual.telefono)
            etNombreUsuario.setText(cineActual.nombre_usuario)
            etEmailUsuario.setText(cineActual.email)
            //etDocumentoUsuario.setText(cineActual.documento)
            etUsuario.setText(cineActual.usuario)
            if(cineActual.id_estado_cine==2){
                spinnerEstado.setSelection(1)
            }else{
                spinnerEstado.setSelection(0)
            }

            builder.setView(dialogView)
            val dialog = builder.create()
            dialog.show()

            btnActualizar.setOnClickListener {
                val idCine = cineActual.id_cine
                val idUsuario = cineActual.id_usuario
                val idEstadoCine=spinnerEstado.selectedItemPosition+1
                val nuevoNombre= etNombreCine.text.toString()
                val nuevaDireccion = etDireccionCine.text.toString()
                val nuevoTelefono = etTelefonoCine.text.toString()
                val nuevoNombreUsuario = etNombreUsuario.text.toString()
                val nuevoEmail = etEmailUsuario.text.toString()
                //val nuevoDocumento = etDocumentoUsuario.text.toString()

                val url = "http://10.0.2.2/kinora_php/actualizar_cine.php"
                //val url = "http://192.168.1.4/kinora_php/actualizar_cine.php" //michael

                val stringRequest = object: StringRequest(Method.POST,
                    url,
                    Response.Listener<String> { response ->
                        Toast.makeText(
                            context,
                            "Cine actualizado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                        listener?.onUpdateSuccess()

                    },
                    Response.ErrorListener{
                        Toast.makeText(
                            context,
                            "Error al actualizar el cine",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }
                ){
                    override fun getParams(): MutableMap<String, String>{
                        val params = HashMap<String, String>()
                        params["id_cine"] = idCine.toString()
                        params["id_usuario"] = idUsuario.toString()
                        params["id_estado_cine"] = idEstadoCine.toString()
                        params["nombre_cine"] = nuevoNombre
                        params["direccion"] = nuevaDireccion
                        params["telefono"] = nuevoTelefono
                        params["nombre_usuario"] = nuevoNombreUsuario
                        params["email"] = nuevoEmail
                        //params["documento"] = nuevoDocumento
                        return params
                    }

                }
                Volley.newRequestQueue(context).add(stringRequest)
            }


        }


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

