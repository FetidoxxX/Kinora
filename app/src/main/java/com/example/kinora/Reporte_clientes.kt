package com.example.kinora

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class Reporte_clientes : nav_bar(), OnReporteClientesUpdateListener {

    private lateinit var adminSesiones: AdministradorSesiones
    private lateinit var rvCines: RecyclerView
    private lateinit var etBuscarCines: EditText

    private val url = "http://192.168.2.103/kinora_php/buscar_cines.php"

    override fun onUpdateSuccess() {
        etBuscarCines.setText("")
        rvCines.adapter = PeticionesAdapter(mutableListOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        adminSesiones = AdministradorSesiones(this)
        adminSesiones.verificarAcceso(this, listOf(Roles.ADMINISTRADOR))

        setContentView(R.layout.activity_reporte_clientes)
        configurarNavBar()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etFechaInicio = findViewById<EditText>(R.id.etFechaInicio)

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



    }

}