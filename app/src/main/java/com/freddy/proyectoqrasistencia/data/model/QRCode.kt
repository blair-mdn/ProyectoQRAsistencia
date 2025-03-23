package com.freddy.proyectoqrasistencia.data.model

import com.google.gson.annotations.SerializedName

data class QRCode(
    @SerializedName("qr_id")
    val qrId: Int? = null,
    @SerializedName("alumno_id")
    val alumnoId: Int,
    @SerializedName("dni_alumno")
    val dniAlumno: String,
    @SerializedName("qr_generado")
    val qrGenerado: String, // QR en Base64
    @SerializedName("fecha_generacion")
    val fechaGeneracion: String,
    @SerializedName("fecha_expiracion")
    val fechaExpiracion: String,
    @SerializedName("estado")
    val estado: String, // 'activo' o 'vencido'
    @SerializedName("ultima_actualizacion")
    val ultimaActualizacion: String
) 