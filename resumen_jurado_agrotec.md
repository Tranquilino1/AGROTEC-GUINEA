# AGROTEC GUINEA: Diagnóstico Agrícola en Tiempo Real con Groq LPU

**Presentado por:** Equipo AAUCA (Tranquilino, Josefa y María)
**Evento:** II Foro Nacional de IA - Campus AAUCA

## El Problema: Latencia en el Campo
En Guinea Ecuatorial, los agricultores de yuca, plátano y maíz enfrentan plagas devastadoras como la Sigatoka o el Gusano Cogollero. Las soluciones de IA tradicionales suelen ser lentas debido al procesamiento en GPU convencionales o la alta latencia de red, lo que dificulta el uso en tiempo real en entornos rurales.

## La Solución: El "Efecto Groq"
AGROTEC GUINEA utiliza el motor de inferencia de **Groq**, impulsado por su revolucionaria arquitectura **LPU (Language Processing Unit)**. 

### Ventajas Técnicas:
1. **Velocidad Ultra-Rápida**: Groq permite procesar modelos de visión (Llama-3-Vision) con una velocidad de tokens por segundo significativamente superior a las GPUs, entregando diagnósticos en menos de 2 segundos.
2. **Eficiencia en Ancho de Banda**: Nuestra app implementa un pre-procesamiento local que redimensiona y comprime las imágenes (CameraAnalyzer) antes del envío, asegurando que incluso con conexiones 3G/4G inestables, la inferencia sea exitosa.
3. **Precisión Localizada**: El motor ha sido instruido específicamente para actuar como un agrónomo experto en el ecosistema de Guinea Ecuatorial, reconociendo variedades locales de tubérculos y frutales.

## Impacto Social y Económico
- **Seguridad Alimentaria**: Respuesta inmediata para salvar cosechas críticas.
- **Modo Offline**: Integración con una base de datos **Room** que contiene precios de mercado proyectados a 2026 y tratamientos locales validados.
- **Escalabilidad**: La infraestructura de Groq permite manejar miles de consultas simultáneas sin degradación del servicio, posicionando a AGROTEC GUINEA como la herramienta líder para la soberanía tecnológica agrícola nacional.
