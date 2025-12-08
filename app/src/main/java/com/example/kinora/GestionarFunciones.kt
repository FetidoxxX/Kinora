package com.example.kinora

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class GestionarFunciones : nav_bar() {

    private lateinit var rvFunciones: RecyclerView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var cargarCosasAdmin: cargar_cosas_admin
    private val crearCosasAdmin = object : crear_cosas_admin {}
    private var listaSalas: List<Sala> = emptyList()
    private var listaPromociones: List<Dia> = emptyList()
    private var selectedTime24h: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_gestionar_funciones)

        val navBar = findViewById<View>(R.id.navBarInclude)
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(navBar) { v, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

        configurarNavBar()

        rvFunciones = findViewById(R.id.rvFunciones)
        rvFunciones.layoutManager = LinearLayoutManager(this)
        fabAgregar = findViewById(R.id.fabAgregarFuncion)
        cargarCosasAdmin = cargar_cosas_admin(this)

        cargarSalas()
        cargarPromociones()
        cargarFunciones()

        fabAgregar.setOnClickListener {
            mostrarDialogoFuncion()
        }
    }

    private fun cargarFunciones() {
        cargarCosasAdmin.cargarFunciones(object : FuncionCallback {
            override fun onSuccess(listaFunciones: List<Funcion>) {
                val adapter = FuncionAdapter(listaFunciones, 
                    onVerClick = { funcion -> mostrarDialogoDetalle(funcion) },
                    onEditarClick = { funcion -> mostrarDialogoFuncion(funcion) }
                )
                rvFunciones.adapter = adapter
            }

            override fun onError(mensaje: String) {
                Toast.makeText(this@GestionarFunciones, mensaje, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarSalas() {
        cargarCosasAdmin.cargarSalas(object : SalaCallback {
            override fun onSuccess(listaSalas: List<Sala>) {
                this@GestionarFunciones.listaSalas = listaSalas
            }

            override fun onError(mensaje: String) {
                Toast.makeText(this@GestionarFunciones, mensaje, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarPromociones() {
        cargarCosasAdmin.cargarPromociones(object : DiaCallback {
            override fun onSuccess(listaDias: List<Dia>) {
                this@GestionarFunciones.listaPromociones = listaDias
            }

            override fun onError(mensaje: String) {
                Toast.makeText(this@GestionarFunciones, mensaje, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogoDetalle(funcion: Funcion) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_detalle_funcion, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvPelicula = view.findViewById<TextView>(R.id.tvDetallePelicula)
        val tvSala = view.findViewById<TextView>(R.id.tvDetalleSala)
        val tvCapacidad = view.findViewById<TextView>(R.id.tvDetalleCapacidad)
        val tvFecha = view.findViewById<TextView>(R.id.tvDetalleFecha)
        val tvHora = view.findViewById<TextView>(R.id.tvDetalleHora)
        val tvPrecioBase = view.findViewById<TextView>(R.id.tvDetallePrecioBase)
        val tvDescuento = view.findViewById<TextView>(R.id.tvDetalleDescuento)
        val tvPrecioFinal = view.findViewById<TextView>(R.id.tvDetallePrecioFinal)
        val btnCerrar = view.findViewById<Button>(R.id.btnCerrarDetalle)

        val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

        tvPelicula.text = funcion.nombre_pelicula
        tvSala.text = funcion.numero_sala
        tvCapacidad.text = "${funcion.capacidad} personas"
        
        // Split Date and Time
        val parts = funcion.fecha_hora.split(" ")
        if (parts.size >= 2) {
            tvFecha.text = parts[0]
            try {
                val inputFormat = java.text.SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val outputFormat = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
                val time = inputFormat.parse(parts[1])
                tvHora.text = if (time != null) outputFormat.format(time) else parts[1]
            } catch (e: Exception) {
                tvHora.text = parts[1]
            }
        } else {
            tvFecha.text = funcion.fecha_hora
            tvHora.text = ""
        }

        tvPrecioBase.text = formatoMoneda.format(funcion.precio_base.toDouble())
        
        if (funcion.nombre_dia != null) {
            tvDescuento.text = "${funcion.nombre_dia} (${funcion.descuento}%)"
        } else {
            tvDescuento.text = "N/A"
        }
        
        tvPrecioFinal.text = formatoMoneda.format(funcion.precio_final)

        btnCerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun mostrarDialogoFuncion(funcion: Funcion? = null) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_crear_editar_funcion, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val actvPelicula = view.findViewById<AutoCompleteTextView>(R.id.actvPelicula)
        val edtFecha = view.findViewById<TextInputEditText>(R.id.edtFechaFuncion)
        val edtHora = view.findViewById<TextInputEditText>(R.id.edtHoraFuncion)
        val actvSala = view.findViewById<AutoCompleteTextView>(R.id.actvSalaFuncion)
        val edtPrecio = view.findViewById<TextInputEditText>(R.id.edtPrecioBase)
        val edtPromocionNombre = view.findViewById<TextInputEditText>(R.id.edtPromocionNombre)
        val edtPromocionDescuento = view.findViewById<TextInputEditText>(R.id.edtPromocionDescuento)
        val tilPromocionDescuento = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilPromocionDescuento)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarFuncion)
        val tvTitulo = view.findViewById<TextView>(R.id.tvTituloDialogoFuncion)

        // Configurar Dropdown Salas
        val salasAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listaSalas.map { it.numero_sala })
        actvSala.setAdapter(salasAdapter)

        // Configurar AutoComplete Película
        val peliculasAdapter = ArrayAdapter<PeliculaSimple>(this, android.R.layout.simple_dropdown_item_1line)
        actvPelicula.setAdapter(peliculasAdapter)
        var selectedPeliculaId: String? = null
        var selectedDiaId: String? = null

        actvPelicula.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length >= 2) {
                    cargarCosasAdmin.buscarPeliculas(s.toString(), object : PeliculaSearchCallback {
                        override fun onSuccess(listaPeliculas: List<PeliculaSimple>) {
                            peliculasAdapter.clear()
                            peliculasAdapter.addAll(listaPeliculas)
                            peliculasAdapter.notifyDataSetChanged()
                        }
                        override fun onError(mensaje: String) {}
                    })
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        actvPelicula.setOnItemClickListener { parent, _, position, _ ->
            val pelicula = parent.getItemAtPosition(position) as PeliculaSimple
            selectedPeliculaId = pelicula.id_pelicula
        }

        // Lógica de Promoción
        fun verificarPromocion(fecha: String) {
            
            val promocion = listaPromociones.find { it.fecha == fecha }
            if (promocion != null) {
                edtPromocionNombre.setText(promocion.nombre)
                edtPromocionDescuento.setText(promocion.descuento)
                tilPromocionDescuento.visibility = View.VISIBLE
                selectedDiaId = promocion.id_dia
            } else {
                edtPromocionNombre.setText("No aplica")
                edtPromocionDescuento.setText("")
                tilPromocionDescuento.visibility = View.GONE
                selectedDiaId = null
            }
        }

        // Configurar DatePicker
        edtFecha.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, { _, y, m, d ->
                val mes = if (m + 1 < 10) "0${m + 1}" else "${m + 1}"
                val dia = if (d < 10) "0$d" else "$d"
                val fechaSeleccionada = "$y-$mes-$dia"
                edtFecha.setText(fechaSeleccionada)
                verificarPromocion(fechaSeleccionada)
            }, year, month, day).show()
        }

        // Configurar TimePicker
        edtHora.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            TimePickerDialog(this, { _, h, m ->
                val hora24 = if (h < 10) "0$h" else "$h"
                val min = if (m < 10) "0$m" else "$m"
                selectedTime24h = "$hora24:$min:00"

                // Display in 12h format
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, m)
                val format12 = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
                edtHora.setText(format12.format(calendar.time))
            }, hour, minute, false).show() // false for 12h view
        }

        if (funcion != null) {
            tvTitulo.text = "Editar Función"
            actvPelicula.setText(funcion.nombre_pelicula)
            selectedPeliculaId = funcion.id_pelicula
            
            val parts = funcion.fecha_hora.split(" ")
            if (parts.size >= 2) {
                edtFecha.setText(parts[0])
                selectedTime24h = parts[1]
                
                // Format display time
                try {
                    val inputFormat = java.text.SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    val outputFormat = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val time = inputFormat.parse(parts[1])
                    edtHora.setText(if (time != null) outputFormat.format(time) else parts[1])
                } catch (e: Exception) {
                    edtHora.setText(parts[1])
                }
                
                verificarPromocion(parts[0]) // Verificar promo al cargar
            }
            edtPrecio.setText(funcion.precio_base)
            
            // Set Sala
            val sala = listaSalas.find { it.id_sala == funcion.id_sala }
            if (sala != null) {
                actvSala.setText(sala.numero_sala, false)
            }
        } else {
            tvTitulo.text = "Crear Función"
        }

        btnGuardar.setOnClickListener {
            val fecha = edtFecha.text.toString()
            val hora = edtHora.text.toString()
            val precio = edtPrecio.text.toString()
            val salaNumero = actvSala.text.toString()
            val sala = listaSalas.find { it.numero_sala == salaNumero }
            
            if (selectedPeliculaId == null) {
                actvPelicula.error = "Seleccione una película válida"
                return@setOnClickListener
            }
            if (fecha.isEmpty() || selectedTime24h == null || precio.isEmpty() || sala == null) {
                Toast.makeText(this, "Complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fechaHora = "$fecha $selectedTime24h"
            val idSala = sala.id_sala
            val baseUrl = "http://192.168.1.4/kinora_php/"

            if (funcion == null) {
                crearCosasAdmin.crearFuncion(this, selectedPeliculaId!!, idSala, precio, fechaHora, selectedDiaId, baseUrl) {
                    dialog.dismiss()
                    cargarFunciones()
                }
            } else {
                crearCosasAdmin.actualizarFuncion(this, funcion.id_funcion, selectedPeliculaId!!, idSala, precio, fechaHora, selectedDiaId, baseUrl) {
                    dialog.dismiss()
                    cargarFunciones()
                }
            }
        }

        dialog.show()
    }
}
