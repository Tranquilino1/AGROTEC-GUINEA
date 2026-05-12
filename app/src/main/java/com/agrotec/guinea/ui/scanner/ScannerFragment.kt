package com.agrotec.guinea.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.agrotec.guinea.api.*
import com.agrotec.guinea.camera.CameraAnalyzer
import com.agrotec.guinea.data.AppDatabase
import com.agrotec.guinea.data.DiagnosisEntity
import com.agrotec.guinea.databinding.FragmentScannerBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale

class ScannerFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private var lastBase64: String? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var lastAnalysisText = ""
    private val TAG = "ScannerFragment"

    private val permLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startCamera()
        else Toast.makeText(requireContext(), "Permiso de cámara requerido", Toast.LENGTH_LONG).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init Text-To-Speech
        tts = TextToSpeech(requireContext(), this)

        // Camera permission check
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            permLauncher.launch(Manifest.permission.CAMERA)
        }

        // Start scan line animation
        startScanLineAnimation()
        fetchWeather()

        // Main scan button
        binding.scanFab.setOnClickListener {
            if (lastBase64 != null) analyzeWithGroq()
            else Toast.makeText(requireContext(), "Esperando imagen de cámara...", Toast.LENGTH_SHORT).show()
        }

        // Voice button: read last result
        binding.btnVoice.setOnClickListener {
            if (lastAnalysisText.isNotBlank()) speakResult(lastAnalysisText)
            else Toast.makeText(requireContext(), "Realiza un análisis primero", Toast.LENGTH_SHORT).show()
        }

        // Read-aloud inside result card
        binding.btnReadAloud.setOnClickListener {
            speakResult(lastAnalysisText)
        }
    }

    private fun startScanLineAnimation() {
        val anim = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, -0.5f,
            Animation.RELATIVE_TO_PARENT, 0.5f
        ).apply {
            duration = 2000
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.scanLine.startAnimation(anim)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), CameraAnalyzer { base64 ->
                        lastBase64 = base64
                        activity?.runOnUiThread {
                            binding.captureIndicator.visibility = View.VISIBLE
                        }
                    })
                }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
            } catch (e: Exception) {
                Log.e(TAG, "Camera error", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun analyzeWithGroq() {
        val base64 = lastBase64 ?: return
        setLoading(true)
        binding.resultCard.visibility = View.GONE

        lifecycleScope.launch {
            try {
                // Step 1: Groq LPU Vision
                val groqMsg = GroqMessage(
                    role = "user",
                    content = listOf(
                        TextContent(text = """Eres un agrónomo experto en Guinea Ecuatorial. Analiza la imagen.
Responde SOLO con JSON válido sin markdown:
{"nombre_plaga":"nombre exacto o Sin Plaga Detectada","confianza":"XX%","cultivo_detectado":"nombre del cultivo","gravedad":"Baja o Media o Alta","tratamiento_local":"descripción del tratamiento en 2 oraciones"}"""),
                        ImageContent(imageUrl = GroqImageUrl("data:image/jpeg;base64,$base64", "low"))
                    )
                )
                val groqResp = GroqApiClient.service.getChatCompletion(
                    GroqApiClient.getAuthToken(),
                    GroqRequest(messages = listOf(groqMsg))
                )
                val raw = groqResp.choices.firstOrNull()?.message?.content?.trim() ?: "{}"
                val json = try {
                    val s = raw.indexOf("{"); val e = raw.lastIndexOf("}") + 1
                    JSONObject(if (s >= 0 && e > s) raw.substring(s, e) else raw)
                } catch (ex: Exception) { JSONObject() }

                val pest = json.optString("nombre_plaga", "Sin plaga detectada")
                val confidence = json.optString("confianza", "N/A")
                val crop = json.optString("cultivo_detectado", "No identificado")
                val severity = json.optString("gravedad", "Baja")
                val treatment = json.optString("tratamiento_local", "Consultar técnico agronómico local.")

                // Step 2: Gemini AI advanced advice
                var advice = ""
                try {
                    val gemResp = GeminiApiClient.service.generateContent(
                        GeminiApiClient.getApiKey(),
                        GeminiRequest(contents = listOf(GeminiContent(parts = listOf(
                            Part("Sé un agrónomo experto en Guinea Ecuatorial. La plaga detectada es '$pest' en cultivo de '$crop'. Da consejos en español: 1) Acción inmediata. 2) Tratamiento recomendado. 3) Impacto en rendimiento si no se trata. Máximo 3 oraciones, claro y directo.")
                        ))))
                    )
                    advice = gemResp.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                } catch (e: Exception) {
                    val dbT = AppDatabase.getInstance(requireContext()).treatmentDao().getForPest(pest)
                    advice = dbT?.prevention ?: "Consulte las guías de tratamiento disponibles sin conexión."
                }

                // Step 3: Save to Room DB
                AppDatabase.getInstance(requireContext()).diagnosisDao().insert(
                    DiagnosisEntity(cropName = crop, pestName = pest, confidence = confidence, treatment = treatment, aiAdvice = advice)
                )

                // Step 4: Build voice text
                lastAnalysisText = "Cultivo analizado: $crop. " +
                    "Plaga detectada: $pest. " +
                    "Nivel de confianza: $confidence. " +
                    "Gravedad: $severity. " +
                    "Tratamiento recomendado: $treatment. " +
                    "Consejo de inteligencia artificial: $advice"

                // Step 5: Show results
                activity?.runOnUiThread {
                    setLoading(false)
                    with(binding) {
                        resultCard.visibility = View.VISIBLE
                        tvCrop.text = "🌱 $crop"
                        tvPest.text = pest
                        tvConfidence.text = confidence
                        tvSeverity.text = "Gravedad: $severity"
                        tvTreatment.text = treatment
                        tvAdvice.text = advice

                        val (color, badgeText) = when (severity) {
                            "Alta" -> Pair(ContextCompat.getColor(requireContext(), com.agrotec.guinea.R.color.accent_red), "● ALTA")
                            "Media" -> Pair(ContextCompat.getColor(requireContext(), com.agrotec.guinea.R.color.accent_yellow), "● MEDIA")
                            else -> Pair(ContextCompat.getColor(requireContext(), com.agrotec.guinea.R.color.accent_green), "● BAJA")
                        }
                        severityBadge.background.setTint(color)
                        severityBadge.text = badgeText

                        resultCard.alpha = 0f
                        resultCard.animate().alpha(1f).translationY(0f).setDuration(400).start()
                    }
                    // Auto-speak result
                    speakResult(lastAnalysisText)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Analysis failed", e)
                activity?.runOnUiThread {
                    setLoading(false)
                    lastAnalysisText = "Error de conexión. Verifique su acceso a internet e intente nuevamente."
                    with(binding) {
                        resultCard.visibility = View.VISIBLE
                        tvCrop.text = "⚠️ Error"
                        tvPest.text = "Sin conexión"
                        tvConfidence.text = "—"
                        tvSeverity.text = ""
                        tvTreatment.text = "Verifique su conexión a Internet."
                        tvAdvice.text = "Use las Guías offline disponibles en la pestaña 'Guías'."
                    }
                    speakResult(lastAnalysisText)
                }
            }
        }
    }

    private fun fetchWeather() {
        lifecycleScope.launch {
            try {
                val w = WeatherApiClient.service.getCurrentWeather("Bata,GQ", WeatherApiClient.getApiKey())
                binding.tvWeather.text = "🌤 ${w.name}: ${w.main.temp.toInt()}°C | 💧${w.main.humidity}% | Sensación: ${w.main.feels_like.toInt()}°C"
            } catch (e: Exception) {
                binding.tvWeather.text = "🌤 Guinea Ecuatorial · Clima tropical húmedo · ~26°C"
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        with(binding) {
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            scanFab.isEnabled = !loading
            tvStatus.text = if (loading) "⚡ Procesando con Groq LPU..." else "📷 Apunta al cultivo y pulsa ANALIZAR"
        }
    }

    private fun speakResult(text: String) {
        if (!ttsReady || tts == null) {
            Toast.makeText(requireContext(), "Asistente de voz iniciando...", Toast.LENGTH_SHORT).show()
            return
        }
        tts?.stop()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "agrotec_tts_${System.currentTimeMillis()}")
    }

    // TextToSpeech callback
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("es", "ES"))
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            tts?.setSpeechRate(0.9f)
            tts?.setPitch(1.0f)
        }
    }

    override fun onDestroyView() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroyView()
        _binding = null
    }
}
