package com.agrotec.guinea.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seedIfEmpty(context: Context) = withContext(Dispatchers.IO) {
        val db = AppDatabase.getInstance(context)

        // Seed Treatments
        if (db.treatmentDao().count() == 0) {
            val treatments = listOf(
                TreatmentEntity(cropName = "Plátano", pestName = "Sigatoka Negra", symptoms = "Manchas negras en hojas, hojas amarillas", treatment = "Fungicidas a base de mancozeb o clorotalonil. Eliminar hojas afectadas.", prevention = "Buena ventilación del cultivo, riego controlado. Rotar fungicidas.", severityLevel = 3),
                TreatmentEntity(cropName = "Maíz", pestName = "Gusano Cogollero", symptoms = "Hojas perforadas, excremento en cogollo, plantas débiles", treatment = "Aplicar Lambda-cihalotrina o Spinosad al cogollo. Trampas con feromonas.", prevention = "Monitoreo semanal. Siembra de cultivos trampa. Uso de Bt (Bacillus thuringiensis).", severityLevel = 3),
                TreatmentEntity(cropName = "Yuca", pestName = "Mosca Blanca", symptoms = "Hojas amarillas con puntos blancos, pegajosidad en hojas", treatment = "Imidacloprid o Thiametoxam. Jabón insecticida potásico.", prevention = "Plantas aromáticas repelentes. Eliminar malezas. Trampas amarillas.", severityLevel = 2),
                TreatmentEntity(cropName = "Yuca", pestName = "Ácaros Rojos", symptoms = "Hojas con manchas bronceadas, telaraña en envés", treatment = "Abamectina o aceite de neem. Acaricidas específicos en caso grave.", prevention = "Evitar estrés hídrico. Plantas saludables son más resistentes.", severityLevel = 2),
                TreatmentEntity(cropName = "Tomate", pestName = "Tizón Tardío", symptoms = "Manchas marrón oscuro en hojas y frutos, micelio blanco", treatment = "Cobre metálico, Metalaxil+Mancozeb. Eliminar plantas afectadas.", prevention = "Semillas certificadas. Buen drenaje. No mojar el follaje al regar.", severityLevel = 3),
                TreatmentEntity(cropName = "Tomate", pestName = "Mosca Minadora", symptoms = "Galerías blancas en hojas, defoliación", treatment = "Cyromazine o Abamectina. Trampas amarillas adhesivas.", prevention = "Control de malezas. Cubierta de malla anti-insectos en semilleros.", severityLevel = 1),
                TreatmentEntity(cropName = "Cacao", pestName = "Moniliasis", symptoms = "Frutos con manchas acuosas, cubierta blanquecina", treatment = "Poda sanitaria. Fungicidas cúpricos. Eliminar frutos enfermos.", prevention = "Poda de mantenimiento. Cosecha frecuente. Sombra controlada.", severityLevel = 3),
                TreatmentEntity(cropName = "Aguacate", pestName = "Trips", symptoms = "Frutos con cicatrices corchosas, hojas enrolladas", treatment = "Spinosad o Imidacloprid. Aplicar al atardecer para no afectar abejas.", prevention = "Monitoreo con trampas azules. Poda para mejorar ventilación.", severityLevel = 1),
                TreatmentEntity(cropName = "Caña de Azúcar", pestName = "Barrenador", symptoms = "Tallos con galerías internas, muerte del brote central", treatment = "Trichogramma (control biológico). Insecticidas sistémicos en casos graves.", prevention = "Variedades resistentes. Rotación de cultivos. Destruir residuos.", severityLevel = 2),
                TreatmentEntity(cropName = "Cacahuete", pestName = "Aflatoxina (Aspergillus)", symptoms = "Semillas con moho verde-amarillento, olor a humedad", treatment = "Eliminar lotes afectados. No consumir. Secar bien antes de almacenar.", prevention = "Cosechar a tiempo. Almacenamiento seco y ventilado. Control de humedad.", severityLevel = 3)
            )
            treatments.forEach { db.treatmentDao().insert(it) }
        }

        // Seed Market Prices 2026
        if (db.marketPriceDao().count() == 0) {
            val prices = listOf(
                MarketPriceEntity(cropName = "Maíz", priceXAF = 1196.0, unit = "kg", trend = "up", lastUpdated = "Mayo 2026"),
                MarketPriceEntity(cropName = "Plátano", priceXAF = 603.0, unit = "racimo", trend = "stable", lastUpdated = "Mayo 2026"),
                MarketPriceEntity(cropName = "Yuca", priceXAF = 450.0, unit = "kg", trend = "up", lastUpdated = "Mayo 2026"),
                MarketPriceEntity(cropName = "Tomate", priceXAF = 1800.0, unit = "kg", trend = "up", lastUpdated = "Mayo 2026"),
                MarketPriceEntity(cropName = "Cacao", priceXAF = 4500.0, unit = "kg", trend = "up", lastUpdated = "Mayo 2026"),
                MarketPriceEntity(cropName = "Café", priceXAF = 3200.0, unit = "kg", trend = "stable", lastUpdated = "Mayo 2026"),
                MarketPriceEntity(cropName = "Aguacate", priceXAF = 800.0, unit = "unidad", trend = "down", lastUpdated = "Mayo 2026"),
                MarketPriceEntity(cropName = "Cacahuete", priceXAF = 950.0, unit = "kg", trend = "stable", lastUpdated = "Mayo 2026"),
                MarketPriceEntity(cropName = "Caña de Azúcar", priceXAF = 350.0, unit = "kg", trend = "stable", lastUpdated = "Mayo 2026")
            )
            prices.forEach { db.marketPriceDao().insert(it) }
        }
    }
}
