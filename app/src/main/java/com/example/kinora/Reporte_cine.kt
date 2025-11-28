package com.example.kinora

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class Reporte_cine : nav_bar() {


    private lateinit var adminSesiones: AdministradorSesiones



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        adminSesiones = AdministradorSesiones(this)
        adminSesiones.verificarAcceso(this, listOf(Roles.ENCARGADO))

        setContentView(R.layout.activity_reporte_cine)
        configurarNavBar()

        val etFechaInicio = findViewById<EditText>(R.id.etFechaInicio)
        val etFechaFinal = findViewById<EditText>(R.id.etFechaFinal)

        val encargadoId = administradorSesiones.obtenerIdUsuario()
        //_-------------------------------------

        val btnGenerar = findViewById<Button>(R.id.btn_generar_RepCine)
        btnGenerar.setOnClickListener {
            val inicio = etFechaInicio.text.toString()
            val fin = etFechaFinal.text.toString()

            if (inicio.isEmpty() || fin.isEmpty()) {
                Toast.makeText(this, "Debe seleccionar ambas fechas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            abrirPDFRemoto(inicio, fin, encargadoId)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etFechaInicio.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    val fecha = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
                    etFechaInicio.setText(fecha)
                }, year, month, day)

            datePickerDialog.show()
        }

        etFechaFinal.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    val fecha = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
                    etFechaFinal.setText(fecha)
                }, year, month, day)

            datePickerDialog.show()
        }
    }
    private fun abrirPDFRemoto(inicio: String, fin: String, encargadoId: Int) {
        // Construir URL con parámetros
        val urlDelReporteCine = "http://192.168.1.34/kinora_php/cine_encargado_tabla.php?inicio=${inicio}&fin=${fin}&encargado_id=${encargadoId}"

        val intent = Intent(this, Reportes_PDF::class.java)
        intent.putExtra("pdf_url", urlDelReporteCine)
        startActivity(intent)
    }
}