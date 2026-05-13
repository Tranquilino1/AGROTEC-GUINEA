/**
 * PROYECTO: AGROTEC GUINEA v4.0 (REVERSIÓN PRE-INDABAX)
 * EQUIPO: Tranquilino Mba Ncogo, Josefa Adela Mikue, María Milagrosa Mbile
 * 
 * DESCRIPCIÓN: Restauración de la interfaz Dark Mode con Glassmorphism.
 * Incluye el motor Dual Core (Online/Offline) original.
 */

package com.agrotec.guinea.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
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
import com.agrotec.guinea.ia.MotorIaDualCore
import com.agrotec.guinea.ia.ResultadoDiagnostico
import com.agrotec.guinea.data.CampusManager
import com.agrotec.guinea.databinding.FragmentScannerBinding
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.Locale

class ScannerFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var lastResultText = ""
    
    private lateinit var motorIa: MotorIaDualCore
    private var currentBitmap: Bitmap? = null

    private val permLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            startCamera()
            startLocationUpdates()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        motorIa = MotorIaDualCore(requireContext())
        tts = TextToSpeech(requireContext(), this)

        checkPermissions()
        startScanLineAnimation()

        // Botón principal de escaneo (FAB)
        binding.scanFab.setOnClickListener {
            ejecutarDiagnostico()
        }

        // Botón de acción en la tarjeta flotante
        binding.btnAction.setOnClickListener {
            binding.resultsContainer.visibility = View.VISIBLE
        }

        // Botón para cerrar el detalle
        binding.btnCloseResults.setOnClickListener {
            binding.resultsContainer.visibility = View.GONE
        }

        binding.btnReadAloud.setOnClickListener {
            speak(lastResultText)
        }
    }

    private fun checkPermissions() {
        val camera = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        if (camera == PackageManager.PERMISSION_GRANTED) {
            startCamera()
            startLocationUpdates()
        } else {
            permLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                        currentBitmap = binding.viewFinder.bitmap
                        imageProxy.close()
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer)
            } catch (e: Exception) {
                Log.e("Scanner", "Error cámara", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun startLocationUpdates() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                val atAAUCA = CampusManager.isAtAAUCA(location)
                activity?.runOnUiThread {
                    binding.tvWeather.text = if (atAAUCA) "🌤 AAUCA Campus · 28°C" else "🌤 Guinea Ecuatorial · 30°C"
                }
            }
        } catch (e: SecurityException) {}
    }

    private fun ejecutarDiagnostico() {
        val bitmap = currentBitmap ?: return
        binding.particleView.setScanning(true)
        
        lifecycleScope.launch {
            // Restauración del modo Dual (Online con Fallback Offline)
            val resultado = motorIa.diagnosticar(bitmap, online = true)
            binding.particleView.setScanning(false)
            mostrarResultado(resultado)
        }
    }

    private fun mostrarResultado(res: ResultadoDiagnostico) {
        with(binding) {
            // Mostrar tarjeta flotante Glassmorphism
            resultCard.visibility = View.VISIBLE
            resultCard.alpha = 0f
            resultCard.animate().alpha(1f).setDuration(500).start()

            tvPest.text = if (res.esOnline) "MODO NUBE: ${res.plaga}" else "MODO CAMPO: ${res.plaga}"
            tvTreatment.text = res.tratamiento
            
            // Cargar datos en el detalle
            tvPestDetailed.text = res.plaga
            tvTreatmentDetailed.text = res.tratamiento

            lastResultText = "Diagnóstico finalizado. Se ha detectado ${res.plaga}. " +
                             "Tratamiento: ${res.tratamiento}"
            
            speak(lastResultText)
        }
    }

    private fun startScanLineAnimation() {
        val anim = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, -0.4f,
            Animation.RELATIVE_TO_PARENT, 0.4f
        ).apply {
            duration = 2000
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.scanLine.startAnimation(anim)
    }

    private fun speak(text: String) {
        if (ttsReady) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "agrotec")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("es", "ES")
            tts?.setPitch(1.0f)
            tts?.setSpeechRate(1.0f)
            ttsReady = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.shutdown()
        _binding = null
    }
}
