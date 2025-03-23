package com.freddy.proyectoqrasistencia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freddy.proyectoqrasistencia.data.model.QRCode
import com.freddy.proyectoqrasistencia.data.repository.SupabaseRepository
import com.freddy.proyectoqrasistencia.utils.QRGenerator
import com.freddy.proyectoqrasistencia.utils.BitmapUtils
import android.graphics.Bitmap
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

class QRViewModel : ViewModel() {
    private val repository = SupabaseRepository()

    private val _qrBitmap = MutableLiveData<Bitmap?>()
    val qrBitmap: LiveData<Bitmap?> get() = _qrBitmap

    private val _qrList = MutableLiveData<List<QRCode>?>()
    val qrList: LiveData<List<QRCode>?> get() = _qrList

    fun generateQR(dni: String) {
        _qrBitmap.value = QRGenerator.generateQRCode(dni)
    }

    fun saveQRCode(dni: String, alumnoId: Int) {
        val qrBitmap = QRGenerator.generateQRCode(dni) ?: return
        val qrCodeBase64 = encodeBitmapToBase64(qrBitmap)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fechaActual = sdf.format(Date())
        val fechaExpiracion = sdf.format(Date(System.currentTimeMillis() + 3 * 60 * 1000)) // 3 minutos desde ahora

        val newQR = QRCode(
            alumnoId = alumnoId,
            dniAlumno = dni,
            qrGenerado = qrCodeBase64,
            fechaGeneracion = fechaActual,
            fechaExpiracion = fechaExpiracion,
            estado = "activo",
            ultimaActualizacion = fechaActual
        )

        viewModelScope.launch {
            try {
                val resultado = repository.insertQRCode(newQR)
                if (!resultado) {
                    Log.e("QRViewModel", "Error al guardar el QR en Supabase")
                }
            } catch (e: Exception) {
                Log.e("QRViewModel", "Error al guardar el QR", e)
            }
        }
    }

    fun getQRCodes() {
        viewModelScope.launch {
            _qrList.value = repository.getQRCodes()
        }
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        return android.util.Base64.encodeToString(
            BitmapUtils.bitmapToByteArray(bitmap),
            android.util.Base64.DEFAULT
        )
    }
} 