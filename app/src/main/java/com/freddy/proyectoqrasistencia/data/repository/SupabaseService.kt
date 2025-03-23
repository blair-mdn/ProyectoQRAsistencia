package com.freddy.proyectoqrasistencia.data.repository

import com.freddy.proyectoqrasistencia.data.model.QRCode
import com.freddy.proyectoqrasistencia.data.model.Alumno
import retrofit2.Response
import retrofit2.http.*
import android.util.Log

interface SupabaseService {
    @GET("tbl_alumnos") // Nombre correcto de la tabla en Supabase
    suspend fun getAlumnos(): Response<List<Alumno>> {
        val response = getAlumnos()
        Log.d("SupabaseService", "Respuesta de getAlumnos: ${response.code()}")
        if (!response.isSuccessful) {
            Log.e("SupabaseService", "Error en getAlumnos: ${response.errorBody()?.string()}")
        }
        return response
    }

    @GET("tbl_qr") // Nombre correcto de la tabla en Supabase
    suspend fun getQRCodes(): Response<List<QRCode>>

    @POST("tbl_qr")
    suspend fun insertQRCode(
        @Body qrCode: QRCode
    ): Response<Void>

    @PATCH("tbl_qr/{qr_id}")
    suspend fun actualizarEstadoQR(
        @Path("qr_id") qrId: Int,
        @Body estado: Map<String, String>
    ): Response<QRCode>
} 