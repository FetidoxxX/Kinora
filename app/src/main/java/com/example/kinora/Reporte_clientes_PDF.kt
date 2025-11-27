package com.example.kinora

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kinora.databinding.ActivityReporteClientesPdfBinding
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class Reporte_clientes_PDF : AppCompatActivity() {

    private lateinit var binding: ActivityReporteClientesPdfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReporteClientesPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val urlPDF = intent.getStringExtra("pdf_url")

        if (urlPDF == null) {
            Toast.makeText(this, "URL del PDF no recibida", Toast.LENGTH_LONG).show()
            return
        }

        descargarYMostrarPDF(urlPDF)
    }


    private fun descargarYMostrarPDF(urlPDF: String) {
        Thread {
            try {
                val url = URL(urlPDF)
                val conexion = url.openConnection() as HttpURLConnection
                conexion.connectTimeout = 15_000
                conexion.readTimeout = 15_000
                // opcional: setUserAgent si tu servidor lo requiere
                // conexion.setRequestProperty("User-Agent", "KinoraApp")

                val responseCode = conexion.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    runOnUiThread {
                        Toast.makeText(this, "Error al conectar: código $responseCode", Toast.LENGTH_LONG).show()
                    }
                    return@Thread
                }

                val input = conexion.inputStream

                // Archivo temporal local
                val archivoPDF = File(cacheDir, "reporte_clientes.pdf")

                // Escribe el archivo (esto cierra el stream al usar 'use')
                archivoPDF.outputStream().use { output ->
                    input.copyTo(output)
                }

                // Comprobaciones básicas
                val exists = archivoPDF.exists()
                val length = if (exists) archivoPDF.length() else 0L

                // Leer encabezado PDF sin usar readNBytes (API 24 compatible)
                val header = if (exists && length >= 4) {
                    archivoPDF.inputStream().use {
                        val headerBytes = ByteArray(4)
                        it.read(headerBytes, 0, 4)   // Lee los primeros 4 bytes
                        String(headerBytes, Charsets.US_ASCII)
                    }
                } else {
                    ""
                }

                runOnUiThread {
                    Toast.makeText(this, "Archivo guardado: ${archivoPDF.absolutePath}\nTamaño: $length bytes\nEncabezado: '$header'", Toast.LENGTH_LONG).show()
                }

                // Si el archivo es muy pequeño o no tiene la cabecera PDF, avisar
                if (!exists || length < 100 || !header.startsWith("%PDF")) {
                    runOnUiThread {
                        Toast.makeText(this, "Archivo PDF inválido o vacío (tamaño=$length, header='$header')", Toast.LENGTH_LONG).show()
                        // Opcional: abrir con app externa para comprobar
                        try {
                            val uri = androidx.core.content.FileProvider.getUriForFile(
                                this,
                                "${applicationContext.packageName}.provider",
                                archivoPDF
                            )
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/pdf")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            startActivity(intent)
                        } catch (ex: Exception) {
                            // no hay app para abrir o FileProvider no configurado
                            Toast.makeText(this, "No se pudo abrir con app externa: ${ex.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    return@Thread
                }

                // Finalmente, mostrar en el PDFView de tu librería simple
                runOnUiThread {
                    try {
                        // Aseguramos visibilidad por si hay algo ocultando la vista
                        binding.vistaPdf.visibility = android.view.View.VISIBLE

                        // Llamada simple requerida por la librería que usas
                        binding.vistaPdf.fromFile(archivoPDF).show()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        Toast.makeText(this, "Error al renderizar en PDFView: ${ex.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error descargando PDF: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

}
