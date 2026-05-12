# 📋 DOCUMENTACIÓN TÉCNICA — AGROTEC GUINEA
## Sistema de Diagnóstico Agrícola con Inteligencia Artificial
### II Foro Nacional de IA — AAUCA, Guinea Ecuatorial 2026

---

## 1. RESUMEN EJECUTIVO

**AGROTEC GUINEA** es una aplicación Android de diagnóstico agrícola en tiempo real diseñada específicamente para el contexto socioeconómico de Guinea Ecuatorial. Combina el motor de inferencia ultrarrápido **Groq LPU** con visión por computadora para identificar plagas en cultivos locales (yuca, maíz, plátano, tomate, cacao) en menos de 2 segundos, ofreciendo recomendaciones de tratamiento tanto en modo online como completamente offline.

**Tecnologías clave:** Groq LPU · Google Gemini 1.5 Flash · Android (Kotlin) · Room Database · CameraX · TTS

---

## 2. ARQUITECTURA TÉCNICA DETALLADA

### 2.1 Diagrama de Flujo del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                     AGROTEC GUINEA APP                          │
├──────────────┬──────────────────┬───────────────┬──────────────┤
│   Splash     │   Onboarding     │   Scanner     │   Historia   │
│  (2.5s load) │  (T&C 1ª vez)    │  (Principal)  │   Mercado    │
│              │                  │               │   Guías      │
└──────────────┴──────────────────┴───────┬───────┴──────────────┘
                                          │
                          ┌───────────────┼───────────────┐
                          ▼               ▼               ▼
                    ┌──────────┐  ┌─────────────┐  ┌──────────┐
                    │  Groq    │  │   Gemini    │  │  Room DB │
                    │  LPU API │  │  1.5 Flash  │  │ (Local)  │
                    │  Vision  │  │  Consejos   │  │ Offline  │
                    └──────────┘  └─────────────┘  └──────────┘
                          │               │               │
                          └───────────────┴───────────────┘
                                          │
                                  ┌───────────────┐
                                  │  TTS Engine   │
                                  │  (Voz ES)     │
                                  └───────────────┘
```

### 2.2 Stack Tecnológico Completo

| Capa | Tecnología | Versión | Función |
|------|-----------|---------|---------|
| **Lenguaje** | Kotlin | 1.9.0 | Desarrollo principal |
| **Build** | Gradle | 8.2.0 | Gestión de dependencias |
| **UI Framework** | Material3 | 1.11.0 | Componentes premium |
| **Navegación** | Navigation Component | 2.7.6 | Routing entre pantallas |
| **Cámara** | CameraX | 1.3.1 | Captura y análisis de imagen |
| **Red** | Retrofit2 + OkHttp | 2.9.0 / 4.12.0 | Llamadas a APIs REST |
| **Base de Datos** | Room | 2.6.1 | Persistencia local |
| **Async** | Kotlin Coroutines + Flow | 1.7.3 | Programación asíncrona |
| **Carga de Imágenes** | Glide | 4.16.0 | Optimización de imágenes |
| **Síntesis de Voz** | Android TTS (nativo) | — | Asistente de voz en español |
| **IA Principal** | Groq API (LPU) | — | Diagnóstico visual ultrarrápido |
| **IA Secundaria** | Google Gemini 1.5 Flash | — | Consejos agronómicos |
| **Clima** | OpenWeatherMap | — | Datos climáticos en tiempo real |

### 2.3 Módulos de la Aplicación

#### 2.3.1 Módulo de Escáner (ScannerFragment)
- **Cámara**: CameraX con `ImageAnalysis` en modo `STRATEGY_KEEP_ONLY_LATEST`
- **Optimización**: Frame skipping cada 30 frames para ahorro de batería
- **Procesamiento de imagen**: Redimensionado a 512×512px, compresión JPEG al 65%, codificación Base64
- **UI**: Marco de escáner con esquinas verdes, línea animada, vignette oscura
- **Pipeline IA**:
  1. Imagen → Groq LPU (visión multimodal) → JSON estructurado
  2. JSON → Gemini 1.5 Flash → Consejo agronómico personalizado
  3. Resultado → Room DB (persistencia) + TTS (lectura en voz)

#### 2.3.2 Módulo de Base de Datos (Room)

**Entidades:**
```kotlin
DiagnosisEntity   // Historial de diagnósticos (id, cultivo, plaga, confianza, tratamiento, consejo, timestamp)
TreatmentEntity   // Guías offline (cultivo, plaga, síntomas, tratamiento, prevención, gravedad)
MarketPriceEntity // Precios 2026 (cultivo, precio_XAF, unidad, tendencia, fecha)
```

**Pre-carga automática:**
- 10 guías de tratamiento de plagas locales
- 9 precios de mercado actualizados para Mayo 2026

#### 2.3.3 Módulo de Voz (TTS)
- Motor: `android.speech.tts.TextToSpeech` (nativo Android, sin dependencias externas)
- Idioma: Español (Locale `es_ES`)
- Velocidad: 0.9x para mayor claridad
- Activación: Automática al completar el análisis + botón manual

---

## 3. INTEGRACIÓN DE APIs

### 3.1 Groq API (Motor Principal)

```
Endpoint:    https://api.groq.com/openai/v1/chat/completions
Modelo:      meta-llama/llama-4-scout-17b-16e-instruct
Tipo:        Multimodal (texto + imagen)
Temperatura: 0.1 (alta precisión)
Max tokens:  1,024
Latencia:    < 2 segundos (Groq LPU)
```

**Formato de respuesta esperado:**
```json
{
  "nombre_plaga": "Sigatoka Negra",
  "confianza": "87%",
  "cultivo_detectado": "Plátano",
  "gravedad": "Alta",
  "tratamiento_local": "Aplicar fungicida mancozeb..."
}
```

### 3.2 Google Gemini 1.5 Flash

```
Endpoint:    https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent
Función:     Generación de consejos agronómicos contextualizados
Contexto:    Clima de Guinea Ecuatorial, cultivos locales
Idioma:      Español
```

### 3.3 OpenWeatherMap

```
Endpoint:    https://api.openweathermap.org/data/2.5/weather
Ciudad:      Bata, GQ
Parámetros:  temp, humidity, feels_like, wind.speed
Unidades:    Metric (Celsius)
Idioma:      Español
```

---

## 4. SEGURIDAD Y PRIVACIDAD

### 4.1 Manejo de Datos
- **Imágenes**: Procesadas en memoria RAM, nunca guardadas en almacenamiento externo
- **Transmisión**: HTTPS en todas las comunicaciones API
- **Historial**: Almacenado únicamente en la base de datos local del dispositivo
- **API Keys**: Integradas en el binario (APK), no expuestas en código fuente público

### 4.2 Permisos Solicitados
| Permiso | Justificación |
|---------|--------------|
| `CAMERA` | Captura de imágenes de cultivos para análisis |
| `INTERNET` | Consulta a APIs de IA y clima |
| `ACCESS_NETWORK_STATE` | Detección de conectividad para modo offline |
| `ACCESS_FINE_LOCATION` | Geolocalización de parcelas (funcionalidad futura) |

---

## 5. RENDIMIENTO Y OPTIMIZACIÓN

### 5.1 Métricas de Rendimiento Objetivo

| Métrica | Valor Objetivo |
|---------|--------------|
| Tiempo de diagnóstico (online) | < 3 segundos |
| Latencia Groq LPU | < 2 segundos |
| Tamaño APK | < 12 MB |
| Tiempo de arranque (cold start) | < 3 segundos |
| Consumo de batería (análisis) | Mínimo (frame skipping) |

### 5.2 Optimizaciones Implementadas

1. **Frame Skipping**: El analizador procesa solo 1 de cada 30 frames
2. **Compresión adaptativa**: JPEG al 65% con redimensionado a 512px
3. **Coroutines**: Todo el trabajo pesado en `Dispatchers.IO` para no bloquear el hilo principal
4. **Database Seeding lazy**: Solo se puebla la BD si está vacía (verificación al inicio)
5. **TTS reutilización**: Motor TTS inicializado una sola vez por sesión

---

## 6. MODO OFFLINE

Cuando no hay conexión a Internet, la app proporciona:
- ✅ Guías completas de 10 plagas locales (síntomas, tratamiento, prevención)
- ✅ Precios de mercado actualizados (Mayo 2026)
- ✅ Historial completo de diagnósticos anteriores
- ✅ Asistente de voz (TTS nativo del dispositivo)
- ❌ Diagnóstico con IA por visión (requiere API Groq)
- ❌ Clima en tiempo real (requiere OpenWeatherMap)

---

## 7. PANTALLAS DE LA APLICACIÓN

### Flujo de Usuario

```
Instalación → Splash (2.5s) → Términos y Condiciones (1ª vez)
                                         │
                               ACEPTO Y CONTINUAR
                                         │
                               ┌─────────────────┐
                               │   MAIN APP      │
                               ├────────┬────────┤
                               │Scanner │Historial│
                               ├────────┼────────┤
                               │Mercado │ Guías  │
                               └────────┴────────┘
```

---

## 8. COMPILACIÓN Y DESPLIEGUE

### Requisitos del Entorno
- **JDK**: 17+
- **Android Studio**: Hedgehog (2023.1.1) o superior
- **Android SDK**: API 34 (compileSdk)
- **minSdk**: API 26 (Android 8.0)
- **Gradle**: 8.2.0

### Comando de Compilación
```bash
./gradlew assembleDebug      # APK de desarrollo
./gradlew assembleRelease    # APK de producción (requiere keystore)
```

### APK de Producción
- **Archivo**: `AGROTEC_GUINEA_V4_FINAL.apk`
- **Tamaño**: 8.5 MB
- **Target**: Android 8.0+ (arm64-v8a / armeabi-v7a)

---

## 9. ROADMAP FUTURO

| Fase | Funcionalidad | Estado |
|------|--------------|--------|
| V5 | Soporte offline con TFLite (modelo local) | 🔜 Planificado |
| V5 | Geolocalización de parcelas con Mapbox | 🔜 Planificado |
| V5 | Sincronización en la nube con Firebase | 🔜 Planificado |
| V6 | Soporte multilingüe (Fang, Bubi, Español) | 🔜 Planificado |
| V6 | Dashboard de analítica para técnicos | 🔜 Planificado |

---

*Documentación generada para el II Foro Nacional de IA — AAUCA 2026*  
*© 2026 Equipo AGROTEC GUINEA. Todos los derechos reservados.*
