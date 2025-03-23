package com.freddy.proyectoqrasistencia.data.repository

import com.freddy.proyectoqrasistencia.data.model.QRCode
import com.freddy.proyectoqrasistencia.data.model.Alumno
import android.util.Log
import kotlin.collections.HashMap

class SupabaseRepository {
    private val api = SupabaseClient.create(SupabaseService::class.java)

    suspend fun getAlumnos(): List<Alumno>? {
        val response = api.getAlumnos()
        Log.d("SupabaseRepository", "Respuesta de getAlumnos: ${response.code()}")
        if (!response.isSuccessful) {
            Log.e("SupabaseRepository", "Error en getAlumnos: ${response.errorBody()?.string()}")
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getQRCodes(): List<QRCode>? {
        val response = api.getQRCodes()
        Log.d("SupabaseRepository", "Respuesta de getQRCodes: ${response.code()}")
        if (!response.isSuccessful) {
            Log.e("SupabaseRepository", "Error en getQRCodes: ${response.errorBody()?.string()}")
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun insertQRCode(qrCode: QRCode): Boolean {
        try {
            Log.d("SupabaseRepository", "Intentando insertar QR: $qrCode")
            val response = api.insertQRCode(qrCode)
            Log.d("SupabaseRepository", "Respuesta de insertQRCode: ${response.code()}")
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("SupabaseRepository", "Error en insertQRCode: $errorBody")
                return false
            }
            return true
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Excepción al insertar QR", e)
            return false
        }
    }

    suspend fun actualizarEstadoQR(qrId: Int, estado: String): Boolean {
        try {
            Log.d("SupabaseRepository", "Intentando actualizar estado QR: $qrId a $estado")
            val estadoMap = HashMap<String, String>()
            estadoMap["estado"] = estado
            val response = api.actualizarEstadoQR(qrId, estadoMap)
            Log.d("SupabaseRepository", "Respuesta de actualizarEstadoQR: ${response.code()}")
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("SupabaseRepository", "Error en actualizarEstadoQR: $errorBody")
                return false
            }
            return true
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Excepción al actualizar estado QR", e)
            return false
        }
    }
} 