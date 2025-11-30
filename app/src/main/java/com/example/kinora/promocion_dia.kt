package com.example.kinora

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class promocion_dia : nav_bar() {

    private lateinit var rvPromociones: RecyclerView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var adapter: DiaAdapter
    private val cargarCosas = CargarCosas(this)
    private val crearCosas = object : crear_Cosas {} // Interface implementation
    private val baseUrl = "http://192.168.1.6/kinora_php/" // Adjust if needed

    private lateinit var adminSesiones: AdministradorSesiones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        adminSesiones = AdministradorSesiones(this)
        adminSesiones.verificarAcceso(this, listOf(Roles.ADMINISTRADOR))

        setContentView(R.layout.activity_promocion_dia)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configurarNavBar()

        rvPromociones = findViewById(R.id.rvPromociones)
        fabAgregar = findViewById(R.id.fabAgregarPromocion)

        rvPromociones.layoutManager = LinearLayoutManager(this)

        cargarPromociones()

        fabAgregar.setOnClickListener {
            mostrarDialogo()
        }
    }

    private fun cargarPromociones() {
        cargarCosas.cargarPromociones(object : DiaCallback {
            override fun onSuccess(listaPromociones: List<Dia>) {
                adapter = DiaAdapter(listaPromociones) { promocion ->
                    mostrarDialogo(promocion)
                }
                rvPromociones.adapter = adapter
            }

            override fun onError(mensaje: String) {
                Toast.makeText(this@promocion_dia, mensaje, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogo(promocion: Dia? = null) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_crear_editar_promocion, null)
        builder.setView(view)

        val tvTitulo = view.findViewById<TextView>(R.id.tvTituloDialogo)
        val edtNombre = view.findViewById<EditText>(R.id.edtNombrePromocion)
        val edtDescuento = view.findViewById<EditText>(R.id.edtDescuentoPromocion)
        val edtFecha = view.findViewById<EditText>(R.id.edtFechaPromocion)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarPromocion)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        if (promocion != null) {
            tvTitulo.text = "Editar Promoción"
            edtNombre.setText(promocion.nombre)
            edtDescuento.setText(promocion.descuento)
            edtFecha.setText(promocion.fecha)
        } else {
            tvTitulo.text = "Crear Promoción"
        }

        edtFecha.setOnClickListener {
            val datePicker = com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona la fecha")
                .setSelection(com.google.android.material.datepicker.MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                val fechaFormateada = sdf.format(java.util.Date(selection))
                edtFecha.setText(fechaFormateada)
            }

            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        btnGuardar.setOnClickListener {
            val nombre = edtNombre.text.toString()
            val descuento = edtDescuento.text.toString()
            val fecha = edtFecha.text.toString()

            if (nombre.isNotEmpty() && descuento.isNotEmpty() && fecha.isNotEmpty()) {
                if (promocion != null) {
                    crearCosas.actualizarPromocion(this, promocion.id_dia, nombre, descuento, fecha, baseUrl) {
                        cargarPromociones()
                        dialog.dismiss()
                    }
                } else {
                    crearCosas.crearPromocion(this, nombre, descuento, fecha, baseUrl) {
                        cargarPromociones()
                        dialog.dismiss()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}