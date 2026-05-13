/**
 * PROYECTO: AGROTEC GUINEA v4.1
 * EQUIPO: Tranquilino Mba Ncogo, Josefa Adela Mikue, María Milagrosa Mbile
 * 
 * DESCRIPCIÓN: Gestor de geolocalización inteligente para detectar la presencia
 * del usuario en el Campus AAUCA (Djibloho) y ajustar riesgos climáticos.
 */

package com.agrotec.guinea.data

import android.location.Location

object CampusManager {
    
    // Coordenadas aproximadas del Campus AAUCA en Djibloho, Ciudad de la Paz
    private const val AAUCA_LAT = 1.5833
    private const val AAUCA_LNG = 10.8333
    private const val RADIO_CAMPUS = 2000.0 // Radio de 2km alrededor del campus

    /**
     * Determina si el usuario se encuentra dentro del rango del campus universitario.
     * Esto permite activar alertas específicas para el microclima de Djibloho.
     */
    fun isAtAAUCA(location: Location?): Boolean {
        if (location == null) return false
        
        val results = FloatArray(1)
        Location.distanceBetween(
            location.latitude, location.longitude,
            AAUCA_LAT, AAUCA_LNG,
            results
        )
        
        return results[0] <= RADIO_CAMPUS
    }

    /**
     * Retorna el factor de riesgo de enfermedades fúngicas basado en la ubicación.
     * En Djibloho (AAUCA), la humedad es más alta, por lo que el riesgo aumenta.
     */
    fun getLocalRiskFactor(location: Location?): Double {
        return if (isAtAAUCA(location)) 1.5 else 1.0
    }
}
