package com.freddy.proyectoqrasistencia.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.freddy.proyectoqrasistencia.data.model.QRCode
import com.freddy.proyectoqrasistencia.data.repository.SupabaseRepository
import com.freddy.proyectoqrasistencia.databinding.ActivityQrBinding
import com.freddy.proyectoqrasistencia.utils.BitmapUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class QRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrBinding
    private val repository = SupabaseRepository()
    private var modoVisualizacion = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alumnoId = intent.getStringExtra("alumno_id")
        val nombre = intent.getStringExtra("nombre")
        val dni = intent.getStringExtra("dni")
        modoVisualizacion = intent.getBooleanExtra("modo_visualizacion", false)

        Log.d("QRActivity", "Datos recibidos - alumnoId: $alumnoId, nombre: $nombre, dni: $dni")

        if (alumnoId != null && nombre != null && dni != null) {
            setupUI(alumnoId, nombre, dni)
        } else {
            Log.e("QRActivity", "Datos incompletos - alumnoId: $alumnoId, nombre: $nombre, dni: $dni")
            Toast.makeText(this, "Error: Datos del alumno incompletos", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupUI(alumnoId: String, nombre: String, dni: String) {
        binding.tvNombre.text = "Nombre: $nombre"
        binding.tvDni.text = "DNI: $dni"

        if (modoVisualizacion) {
            binding.btnGuardarQR.visibility = View.GONE
            cargarQRExistente(alumnoId)
        } else {
            generarQR(alumnoId, dni)
        }
    }

    private fun generarQR(alumnoId: String, dni: String) {
        try {
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix: BitMatrix = multiFormatWriter.encode(
                dni,
                BarcodeFormat.QR_CODE,
                500,
                500
            )
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            binding.ivQR.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.e("QRActivity", "Error al generar QR", e)
            Toast.makeText(this, "Error al generar el código QR", Toast.LENGTH_LONG).show()
        }
    }

    private fun cargarQRExistente(alumnoId: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val qrCodes = repository.getQRCodes()
                val qrAlumno = qrCodes?.find { it.alumnoId.toString() == alumnoId }
                
                if (qrAlumno != null) {
                    generarQR(alumnoId, qrAlumno.dniAlumno)
                } else {
                    Toast.makeText(this@QRActivity, "No se encontró el código QR", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("QRActivity", "Error al cargar QR", e)
                Toast.makeText(this@QRActivity, "Error al cargar el código QR", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun guardarQR(alumnoId: String, dni: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                Log.d("QRActivity", "Iniciando guardado de QR para alumnoId: $alumnoId, dni: $dni")
                val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val fechaExpiracion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Date(System.currentTimeMillis() + 3 * 60 * 1000)) // 3 minutos desde ahora
                
                val drawable = binding.ivQR.drawable
                val qrBitmap = if (drawable is BitmapDrawable) {
                    drawable.bitmap
                } else {
                    val width = drawable.intrinsicWidth
                    val height = drawable.intrinsicHeight
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                    bitmap
                }
                val qrBase64 = android.util.Base64.encodeToString(
                    BitmapUtils.bitmapToByteArray(qrBitmap),
                    android.util.Base64.DEFAULT
                )

                val qrCode = QRCode(
                    alumnoId = alumnoId.toInt(),
                    dniAlumno = dni,
                    qrGenerado = qrBase64,
                    fechaGeneracion = fechaActual,
                    fechaExpiracion = fechaExpiracion,
                    estado = "activo",
                    ultimaActualizacion = fechaActual
                )

                Log.d("QRActivity", "QR generado, intentando guardar en Supabase")
                val resultado = repository.insertQRCode(qrCode)
                if (resultado) {
                    Log.d("QRActivity", "QR guardado exitosamente")
                    Toast.makeText(this@QRActivity, "Código QR guardado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e("QRActivity", "Error al guardar QR en Supabase")
                    Toast.makeText(this@QRActivity, "Error al guardar el código QR", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("QRActivity", "Error al guardar QR", e)
                Toast.makeText(this@QRActivity, "Error al guardar el código QR: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    fun onGuardarClick(view: View) {
        val alumnoId = intent.getStringExtra("alumno_id")
        val dni = intent.getStringExtra("dni")
        if (alumnoId != null && dni != null) {
            guardarQR(alumnoId, dni)
        }
    }
} 