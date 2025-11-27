package com.example.kinora

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import java.util.Calendar

class Reporte_por_cine : nav_bar(), OnReportePorCineUpdateListener {

    private lateinit var adminSesiones: AdministradorSesiones
    private lateinit var rvCines: RecyclerView
    private lateinit var etBuscarCines: EditText
    private lateinit var etFechaInicio: EditText
    private lateinit var etFechaFinal: EditText
    private var cineSeleccionado: Cine? = null



    private val url = "http://192.168.1.34/kinora_php/buscar_cines.php"

    override fun onUpdateSuccess() {
        etBuscarCines.setText("")
        rvCines.adapter = ReportePorCineAdapter(mutableListOf())
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        adminSesiones = AdministradorSesiones(this)
        adminSesiones.verificarAcceso(this, listOf(Roles.ADMINISTRADOR))

        setContentView(R.layout.activity_reporte_por_cine)
        configurarNavBar()

        rvCines = findViewById(R.id.rv_cines)
        rvCines.layoutManager = LinearLayoutManager(this)
        etBuscarCines=findViewById(R.id.et_buscar_cine_RepCli)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        buscarCinesEnDB("")

        etBuscarCines.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val textoEscrito = s?.toString() ?: ""
                if (textoEscrito.length > 3) {
                    buscarCinesEnDB(textoEscrito)
                } else if (textoEscrito.isEmpty()) {
                    buscarCinesEnDB("")
                }
            }

        })

        etFechaInicio = findViewById(R.id.etFechaInicio)
        etFechaFinal = findViewById(R.id.etFechaFinal)


        //_-------------------------------------

        val btnGenerar = findViewById<Button>(R.id.btn_generar_RepPorCine)
        btnGenerar.setOnClickListener {
            val inicio = etFechaInicio.text.toString()
            val fin = etFechaFinal.text.toString()

            if (inicio.isEmpty() || fin.isEmpty()) {
                Toast.makeText(this, "Debe seleccionar ambas fechas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cineId = cineSeleccionado?.id_cine
            if (cineId != null) {
                abrirPDFRemoto(inicio, fin, cineId)
            } else {
                Toast.makeText(this, "Debe seleccionar un cine primero", Toast.LENGTH_SHORT).show()
            }
        }
        //_-------------------------------------


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

    private fun buscarCinesEnDB(query: String) {


        val urlConQuery = "$url?nombre=${Uri.encode(query)}"
        Log.d("ReportePorCineActivity", "URL del cine: $urlConQuery")

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlConQuery, null,
            { response ->

                val listaCines = mutableListOf<Cine>()

                for (i in 0 until response.length()) {

                    val json = response.getJSONObject(i)

                    val id_cine = json.optInt("id_cine")
                    val direccion = json.optString("direccion")
                    val nombre = json.optString("nombre_cine")
                    val id_usuario = json.optInt("id_usuario")
                    val nombre_usuario = json.optString("nombre_usuario")
                    val email = json.optString("email_usuario")
                    val documento = json.optString("documento_usuario")
                    val telefono = json.optString("telefono")
                    val usuario = json.optString("usuario")
                    val id_estado_cine = json.optInt("id_estado_cine")

                    val cine = Cine(id_cine, direccion, nombre, id_usuario, nombre_usuario, email, documento, telefono, usuario, id_estado_cine)

                    listaCines.add(cine)
                }


                val adapter = ReportePorCineAdapter(listaCines)
                adapter.setOnReportePorCineUpdateListener(this)

                rvCines.adapter = adapter

                adapter.setOnItemClickListener { cine ->
                    cineSeleccionado = cine
                    Toast.makeText(this, "Cine seleccionado: ${cine.nombre}", Toast.LENGTH_SHORT).show()

                    val inicio = etFechaInicio.text.toString()
                    val fin = etFechaFinal.text.toString()

                    if (inicio.isNotEmpty() && fin.isNotEmpty()) {
                        abrirPDFRemoto(inicio, fin, cine.id_cine)
                    } else {
                        Toast.makeText(this, "Seleccione fechas primero", Toast.LENGTH_SHORT).show()
                    }
                }


                    Log.d("ReportePorCineActivity", "Cines obtenidos: $listaCines")
            },
            { error ->
                Log.e("ReportePorCineActivity", "Error de Volley: ", error)
            }
        )

        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

    private fun abrirPDFRemoto(inicio: String, fin: String, cineId: Int) {
        // Construir URL con parámetros
        val urlDelReportePorCine = "http://192.168.1.34/kinora_php/cine_admin_tabla.php?inicio=${inicio}&fin=${fin}&cine_id=${cineId}"

        val intent = Intent(this, Reportes_PDF::class.java)
        intent.putExtra("pdf_url", urlDelReportePorCine)
        startActivity(intent)
    }
}