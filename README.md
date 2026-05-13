# AGROTEC GUINEA — Sistema de Diagnóstico Agrícola con IA

<div align="center">
  <img src="app/src/main/res/drawable/logo_agrotec.png" alt="AGROTEC GUINEA Logo" width="180"/>
  
  **Motor de IA: Groq LPU · Gemini AI · Offline-First**

  ![Android](https://img.shields.io/badge/Android-26%2B-green?style=for-the-badge&logo=android)
  ![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blue?style=for-the-badge&logo=kotlin)
  ![Motor](https://img.shields.io/badge/Motor-Groq_LPU-orange?style=for-the-badge)
  ![License](https://img.shields.io/badge/License-AAUCA_2026-brightgreen?style=for-the-badge)

  🌐 **[Visita la Landing Page Oficial](https://tranquilino1.github.io/AGROTEC-GUINEA)**
</div>

---

## 🏆 Contexto Académico

- **Proyecto:** II Foro Nacional de Inteligencia Artificial (IndabaX GE 2026)
- **Institución:** Universidad Afroamericana de África Central (AAUCA)
- **Facultad:** Facultad de Ingenierías y Arquitectura

---

**AGROTEC GUINEA** es una aplicación Android de diagnóstico agrícola en tiempo real que utiliza modelos de visión por computadora alojados en la infraestructura **LPU (Language Processing Unit)** de Groq para identificar plagas en cultivos locales de Guinea Ecuatorial con una latencia inferior a **2 segundos**.

## 🎯 Problema que Resuelve

Los agricultores de Guinea Ecuatorial pierden entre el **20% y el 40%** de sus cosechas anuales por plagas no detectadas a tiempo. La escasez de técnicos agrónomos en zonas rurales y la falta de acceso a internet estable generan una brecha de conocimiento crítica. **AGROTEC GUINEA** democratiza el acceso al diagnóstico agronómico de calidad industrial.

---

## 🧠 Arquitectura de IA

### Pipeline de Diagnóstico Dual
```text
📷 CameraX (Captura)
      └── CameraAnalyzer (Resize & Compress)
            └── Groq LPU API (Llama-3-Vision)
                  └── JSON: { plaga, tratamiento, confianza }
```

### Modelos Utilizados
| Motor | Modelo | Función |
| :--- | :--- | :--- |
| **Groq LPU** | `llama-3.2-11b-vision-preview` | Visión: Identificación visual de plagas |
| **Google Gemini** | `gemini-1.5-flash` | NLP: Consejos agronómicos contextuales |
| **OpenWeatherMap** | `REST API` | Datos climáticos en tiempo real (Bata/Oyala) |

---

## 🌿 Cultivos y Plagas Soportadas

| Cultivo | Plagas Monitoreadas | Severidad |
| :--- | :--- | :--- |
| **Plátano** | Sigatoka Negra | 🔴 Alta |
| **Maíz** | Gusano Cogollero | 🔴 Alta |
| **Yuca** | Mosca Blanca, Ácaros Rojos | 🟡 Media |
| **Tomate** | Tizón Tardío, Mosca Minadora | 🔴 Alta |
| **Cacao** | Moniliasis | 🔴 Alta |
| **Aguacate** | Trips | 🟢 Baja |
| **Caña de Azúcar** | Barrenador del Tallo | 🟡 Media |
| **Cacahuete** | Aflatoxina (Aspergillus) | 🔴 Alta |

---

## 🏗️ Arquitectura del Sistema

```text
com.agrotec.guinea/
├── api/
│   └── ApiClients.kt (Groq, Gemini, Weather)
├── camera/
│   └── CameraAnalyzer.kt
├── data/
│   ├── AppDatabase.kt (Room)
│   └── DatabaseSeeder.kt
└── ui/
    ├── SplashActivity.kt
    ├── onboarding/
    │   └── OnboardingActivity.kt
    ├── MainActivity.kt (Navigation Host)
    ├── scanner/
    │   └── ScannerFragment.kt
    ├── history/
    │   └── HistoryFragment.kt
    ├── market/
    │   └── MarketFragment.kt
    └── guides/
        └── GuidesFragment.kt
```

### Patrón Arquitectónico: MVVM + Repository
- **UI Layer:** Fragments con ViewBinding + BottomNavigation (Navigation Component).
- **Domain Layer:** Coroutines + Kotlin Flow para asincronía.
- **Data Layer:** Retrofit para servicios en la nube y Room para persistencia local.

---

## 👥 Equipo Fundador (AAUCA)

- **Tranquilino Mba Ncogo**: Arquitecto de Software & Lead AI Engineer
- **Josefa Adela Mikue**: Especialista en Backend & API Integration
- **María Milagrosa Mbile**: Líder de UX/UI & Frontend Architect

---

## 📂 Documentación del Hackathon
Consulte los detalles técnicos en la carpeta `/docs`:
- 📄 **[Manual de Arquitectura Técnica](docs/manual_arquitectura_tecnica.md)**
- 📄 **[Resumen Ejecutivo para el Jurado](docs/resumen_jurado_agrotec.md)**
- 📑 **[Resumen Ejecutivo Oficial (PDF)](docs/Resumen_Agrotec_Guinea_AAUCA.pdf)**
- 📊 **[Presentación de 5 Minutos (PowerPoint)](docs/Presentacion_Agrotec_Guinea_5Min.pptx)**
- 🎨 **[Pitch Deck Visual Ilustrado (PDF)](docs/Agrotec_Guinea_Pitch_Visual.pdf)**

---

## 🚀 Instalación
1. Clona el repositorio: `git clone https://github.com/Tranquilino1/AGROTEC-GUINEA.git`
2. Configura tu `GROQ_API_KEY` en `local.properties`.
3. Compila el proyecto en Android Studio (Giraffe+).

**© 2026 Equipo AGROTEC GUINEA — AAUCA.**  
*Programado en Ciudad de la Paz, para el futuro de Guinea Ecuatorial.*
