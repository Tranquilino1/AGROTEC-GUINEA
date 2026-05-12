package com.agrotec.guinea.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Base64
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

class CameraAnalyzer(private val onImageAnalyzed: (String) -> Unit) : ImageAnalysis.Analyzer {
    private var frameCount = 0

    override fun analyze(image: ImageProxy) {
        frameCount++
        // Process only every 30th frame to save battery
        if (frameCount % 30 == 0) {
            val bitmap = image.toBitmap().rotate(image.imageInfo.rotationDegrees.toFloat())
            val compressedBase64 = bitmap.toBase64()
            onImageAnalyzed(compressedBase64)
        }
        image.close()
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        if (degrees == 0f) return this
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun Bitmap.toBase64(): String {
        val out = ByteArrayOutputStream()
        val scaled = Bitmap.createScaledBitmap(this, 512, 512, true)
        scaled.compress(Bitmap.CompressFormat.JPEG, 65, out)
        return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
    }
}
