/**
 * PROYECTO: AGROTEC GUINEA v4.1
 * EQUIPO: Tranquilino Mba Ncogo, Josefa Adela Mikue, María Milagrosa Mbile
 * 
 * DESCRIPCIÓN: Lógica para el cálculo de proyecciones económicas basadas en
 * los precios de mercado proyectados para mayo de 2026 en Guinea Ecuatorial.
 */

package com.agrotec.guinea.data

object CalculadoraEconomica {
    
    // Precios proyectados por kg (XAF) para Mayo 2026
    private const val PRECIO_MAIZ = 1196.0
    private const val PRECIO_PLATANO = 603.0
    private const val PRECIO_CACAO = 2450.0 // Precio premium IndabaX
    private const val PRECIO_YUCA = 450.0

    /**
     * Calcula el beneficio bruto estimado basado en la cantidad y el cultivo.
     */
    fun calcularBeneficioEstimado(kilos: Double, cultivo: String): Double {
        val precio = when (cultivo.uppercase()) {
            "MAIZ" -> PRECIO_MAIZ
            "PLATANO" -> PRECIO_PLATANO
            "BANANA" -> PRECIO_PLATANO
            "CACAO" -> PRECIO_CACAO
            "YUCA" -> PRECIO_YUCA
            else -> 500.0
        }
        return kilos * precio
    }

    /**
     * Retorna el precio unitario formateado para mostrar en la UI.
     */
    fun getPrecioUnitario(cultivo: String): String {
        val precio = calcularBeneficioEstimado(1.0, cultivo)
        return String.format("%,.0f XAF/kg", precio)
    }
}
