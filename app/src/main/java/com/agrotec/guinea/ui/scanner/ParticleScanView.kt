package com.agrotec.guinea.ui.scanner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.Random

/**
 * Vista de Escaneo de Partículas 3D para AGROTEC GUINEA.
 * Proyecta puntos brillantes que simulan un escaneo volumétrico.
 */
class ParticleScanView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val particles = mutableListOf<Particle>()
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val random = Random()
    private var isScanning = false

    data class Particle(
        var x: Float, var y: Float, var size: Float, var alpha: Int, var speed: Float
    )

    fun setScanning(active: Boolean) {
        isScanning = active
        if (active) {
            createParticles()
            invalidate()
        } else {
            particles.clear()
            invalidate()
        }
    }

    private fun createParticles() {
        particles.clear()
        for (i in 0..100) {
            particles.add(createRandomParticle())
        }
    }

    private fun createRandomParticle(): Particle {
        return Particle(
            x = random.nextFloat() * width,
            y = random.nextFloat() * height,
            size = random.nextFloat() * 8f + 2f,
            alpha = random.nextInt(150) + 50,
            speed = random.nextFloat() * 5f + 2f
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isScanning) return

        particles.forEach { p ->
            paint.color = 0x00F2FE // Cyan/Aqua Accent
            paint.alpha = p.alpha
            canvas.drawCircle(p.x, p.y, p.size, paint)

            // Animation logic
            p.y -= p.speed
            p.alpha -= 2
            if (p.alpha <= 0 || p.y < 0) {
                // Reset particle
                p.x = random.nextFloat() * width
                p.y = height.toFloat()
                p.alpha = random.nextInt(150) + 50
            }
        }

        invalidate()
    }
}
