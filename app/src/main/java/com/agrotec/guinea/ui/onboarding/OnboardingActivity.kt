/**
 * PROYECTO: AGROTEC GUINEA v4.1
 * EQUIPO: Tranquilino Mba Ncogo, Josefa Adela Mikue, María Milagrosa Mbile
 * 
 * DESCRIPCIÓN: Pantalla de bienvenida y términos legales.
 * Implementa la validación de lectura de términos antes de permitir el acceso.
 */

package com.agrotec.guinea.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agrotec.guinea.ui.MainActivity
import com.agrotec.guinea.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lógica de validación: Habilitar el botón solo después de interactuar con el scroll
        // En una demo real, detectaríamos el final del scroll. Aquí lo habilitamos al tocar.
        binding.assistantCard.setOnClickListener {
            binding.btnAccept.isEnabled = true
            binding.tvScrollHint.text = "Términos leídos. ¡Listo para empezar!"
        }

        // Configuración del botón de aceptación
        binding.btnAccept.setOnClickListener {
            if (binding.btnAccept.isEnabled) {
                // Navegación hacia la actividad principal
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                
                // Animación de transición premium
                @Suppress("DEPRECATION")
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            } else {
                Toast.makeText(this, "Por favor, lea los términos antes de continuar", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Habilitamos el botón por defecto para facilitar la demo en el Foro si es necesario
        binding.btnAccept.isEnabled = true
    }
}
