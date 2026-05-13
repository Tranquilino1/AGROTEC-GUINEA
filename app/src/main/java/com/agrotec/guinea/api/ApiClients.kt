/**
 * PROYECTO: AGROTEC GUINEA v4.1
 * EQUIPO: Tranquilino Mba Ncogo, Josefa Adela Mikue, María Milagrosa Mbile
 * 
 * DESCRIPCIÓN: Configuración de clientes API para Groq Vision, Gemini 1.5 Flash
 * y OpenWeatherMap utilizando Retrofit y OkHttp.
 */

package com.agrotec.guinea.api

import com.agrotec.guinea.BuildConfig
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// ─── CLIENTE GROQ API (DIAGNÓSTICO ULTRA-RÁPIDO) ──────────────────────────────

interface GroqService {
    @POST("chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") token: String,
        @Body request: GroqRequest
    ): GroqResponse
}

data class GroqRequest(
    val model: String = "meta-llama/llama-4-scout-17b-16e-instruct",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.1,
    @SerializedName("max_tokens") val maxTokens: Int = 1024
)

data class GroqMessage(
    val role: String,
    val content: Any // Puede ser String o una lista de partes (Texto + Imagen)
)

data class TextContent(val type: String = "text", val text: String)
data class ImageContent(val type: String = "image_url", @SerializedName("image_url") val imageUrl: GroqImageUrl)
data class GroqImageUrl(val url: String, val detail: String = "low")

data class GroqResponse(val choices: List<GroqChoice>)
data class GroqChoice(val message: GroqMsg)
data class GroqMsg(val content: String)

object GroqApiClient {
    private const val BASE_URL = "https://api.groq.com/openai/v1/"
    // Llave de API obtenida desde BuildConfig para seguridad
    private val API_KEY = BuildConfig.GROQ_API_KEY

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    val service: GroqService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqService::class.java)
    }

    fun getAuthToken() = "Bearer $API_KEY"
}

// ─── CLIENTE OPENWEATHER (DATOS CLIMÁTICOS LOCALES) ───────────────────────────

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") location: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): WeatherResponse
}

data class WeatherResponse(
    val main: WeatherMain,
    val weather: List<WeatherDesc>,
    val name: String,
    val wind: Wind
)
data class WeatherMain(val temp: Double, val humidity: Int, val feels_like: Double)
data class WeatherDesc(val description: String, val icon: String)
data class Wind(val speed: Double)

object WeatherApiClient {
    private const val BASE_URL = "https://api.openweathermap.org/"
    private val API_KEY = BuildConfig.WEATHER_API_KEY

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    val service: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

    fun getApiKey() = API_KEY
}

// ─── CLIENTE GEMINI (CONSEJOS AGRONÓMICOS AVANZADOS) ──────────────────────────

interface GeminiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

data class GeminiRequest(val contents: List<GeminiContent>)
data class GeminiContent(val parts: List<Part>)
data class Part(val text: String)
data class GeminiResponse(val candidates: List<GeminiCandidate>)
data class GeminiCandidate(val content: GeminiContent)

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"
    private val API_KEY = BuildConfig.GEMINI_API_KEY

    val service: GeminiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiService::class.java)
    }

    fun getApiKey() = API_KEY
}
