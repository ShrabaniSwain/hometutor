package com.flyngener.hometutor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyngener.hometutor.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageResId = intent.getIntExtra("IMAGE_RES_ID", 0)

        binding.ivFullScreenImage.setImageResource(imageResId)

        binding.ivFullScreenImage.setOnClickListener {
            finish()
        }
    }
}