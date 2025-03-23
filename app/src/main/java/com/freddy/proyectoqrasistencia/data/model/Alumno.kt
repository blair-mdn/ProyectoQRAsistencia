package com.freddy.proyectoqrasistencia.data.model

import com.google.gson.annotations.SerializedName

data class Alumno(
    @SerializedName("alumno_id")
    val alumnoId: Int,
    @SerializedName("dni")
    val dni: String,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("programa_estudio")
    val programaEstudio: String,
    @SerializedName("estado")
    val estado: String, // 'A' para activo, 'D' para desactivado
    @SerializedName("fecha_registro")
    val fechaRegistro: String,
    @SerializedName("observaciones")
    val observaciones: String?
) 