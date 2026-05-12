package com.agrotec.guinea.ui.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.agrotec.guinea.databinding.ActivityOnboardingBinding
import com.agrotec.guinea.ui.MainActivity

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("agrotec_prefs", MODE_PRIVATE)

        // If user already accepted terms, skip to main
        if (prefs.getBoolean("terms_accepted", false)) {
            goToMain()
            return
        }

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animate elements
        binding.logoContainer.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in))

        binding.btnAccept.setOnClickListener {
            prefs.edit().putBoolean("terms_accepted", true).apply()
            binding.btnAccept.isEnabled = false
            goToMain()
        }

        binding.btnDecline.setOnClickListener {
            finish()
        }

        // Enable accept button only after scrolling to bottom
        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val child = binding.scrollView.getChildAt(0)
            if (child != null) {
                val childHeight = child.height
                val scrollViewHeight = binding.scrollView.height
                if (scrollY >= childHeight - scrollViewHeight - 50) {
                    binding.btnAccept.isEnabled = true
                    binding.tvScrollHint.visibility = View.GONE
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
