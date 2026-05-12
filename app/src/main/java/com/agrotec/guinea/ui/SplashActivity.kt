package com.agrotec.guinea.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.agrotec.guinea.R
import com.agrotec.guinea.data.DatabaseSeeder
import com.agrotec.guinea.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animate logo in
        binding.logo.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in))
        binding.tagline.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left))

        lifecycleScope.launch {
            DatabaseSeeder.seedIfEmpty(this@SplashActivity)
            delay(2500)
            startActivity(Intent(this@SplashActivity, com.agrotec.guinea.ui.onboarding.OnboardingActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
