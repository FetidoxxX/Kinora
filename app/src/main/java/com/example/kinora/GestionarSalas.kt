package com.example.kinora

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class GestionarSalas : nav_bar(), SalaCallback {

    private lateinit var rvSalas: RecyclerView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var cargarCosasAdmin: cargar_cosas_admin
    private val crearCosasAdmin = object : crear_cosas_admin {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_salas)

        configurarNavBar()

        rvSalas = findViewById(R.id.rvSalas)
        rvSalas.layoutManager = LinearLayoutManager(this)
        fabAgregar = findViewById(R.id.fabAgregarSala)
        cargarCosasAdmin = cargar_cosas_admin(this)

        cargarSalas()

        fabAgregar.setOnClickListener {
            mostrarDialogoSala()
        }
    }

    private fun cargarSalas() {
        cargarCosasAdmin.cargarSalas(this)
    }

    override fun onSuccess(listaSalas: List<Sala>) {
        val adapter = SalaAdapter(listaSalas) { sala ->
            mostrarDialogoSala(sala)
        }
        rvSalas.adapter = adapter
    }

    override fun onError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarDialogoSala(sala: Sala? = null) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_crear_editar_sala, null)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val edtNumero = view.findViewById<TextInputEditText>(R.id.edtNumeroSala)
        val edtFilas = view.findViewById<TextInputEditText>(R.id.edtFilas)
        val edtColumnas = view.findViewById<TextInputEditText>(R.id.edtColumnas)
        val tilFilas = view.findViewById<TextInputLayout>(R.id.tilFilas)
        val tilColumnas = view.findViewById<TextInputLayout>(R.id.tilColumnas)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarSala)
        val tvTitulo = view.findViewById<android.widget.TextView>(R.id.tvTituloDialogoSala)

        if (sala != null) {
            tvTitulo.text = "Editar Sala"
            edtNumero.setText(sala.numero_sala)
            // En edición no permitimos cambiar filas/columnas porque implicaría regenerar sillas y facturas
            tilFilas.visibility = View.GONE
            tilColumnas.visibility = View.GONE
        } else {
            tvTitulo.text = "Crear Sala"
        }

        btnGuardar.setOnClickListener {
            val numero = edtNumero.text.toString()
            val filas = edtFilas.text.toString()
            val columnas = edtColumnas.text.toString()
            val baseUrl = "http://192.168.1.6/kinora_php/"

            if (numero.isEmpty()) {
                edtNumero.error = "Campo requerido"
                return@setOnClickListener
            }

            if (sala == null) {
                if (filas.isEmpty() || columnas.isEmpty()) {
                    Toast.makeText(this, "Filas y Columnas son requeridas", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                crearCosasAdmin.crearSala(this, numero, filas, columnas, baseUrl) {
                    dialog.dismiss()
                    cargarSalas()
                }
            } else {
                crearCosasAdmin.actualizarSala(this, sala.id_sala, numero, baseUrl) {
                    dialog.dismiss()
                    cargarSalas()
                }
            }
        }

        dialog.show()
    }
}
