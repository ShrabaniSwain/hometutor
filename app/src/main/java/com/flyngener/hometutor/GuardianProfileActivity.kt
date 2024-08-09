package com.flyngener.hometutor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyngener.hometutor.databinding.ActivityGuardianProfileBinding

class GuardianProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGuardianProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuardianProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}