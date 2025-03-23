package com.freddy.proyectoqrasistencia.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object BitmapUtils {
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
} 