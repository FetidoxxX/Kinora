package com.example.kinora

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.kinora.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import android.Manifest

class Reporte_clientes : nav_bar() {

    private lateinit var adminSesiones: AdministradorSesiones

    private val url = "http://192.168.1.34/kinora_php/buscar_cines.php"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        adminSesiones = AdministradorSesiones(this)
        adminSesiones.verificarAcceso(this, listOf(Roles.ADMINISTRADOR))

        setContentView(R.layout.activity_reporte_clientes)
        configurarNavBar()

        val etFechaInicio = findViewById<EditText>(R.id.etFechaInicio)
        val etFechaFinal = findViewById<EditText>(R.id.etFechaFinal)

        //_-------------------------------------

        val btnGenerar = findViewById<Button>(R.id.btn_generar_RepClientes)
        btnGenerar.setOnClickListener {
            val inicio = etFechaInicio.text.toString()
            val fin = etFechaFinal.text.toString()

            if (inicio.isEmpty() || fin.isEmpty()) {
                Toast.makeText(this, "Debe seleccionar ambas fechas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            abrirPDFRemoto(inicio, fin)
        }
        //_-------------------------------------


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

    private fun abrirPDFRemoto(inicio: String, fin: String) {
        // Construir URL con parámetros
        val urlPDF = "http://192.168.1.34/kinora_php/cliente_tabla.php?inicio=${inicio}&fin=${fin}"

        val intent = Intent(this, Reporte_clientes_PDF::class.java)
        intent.putExtra("pdf_url", urlPDF)
        startActivity(intent)
    }

    //_-------------------------------------


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 200){
            if(grantResults.isNotEmpty()){
                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if(writeStorage && readStorage){
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    //_-------------------------------------


}