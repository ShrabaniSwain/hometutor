package com.flyngener.tutorhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("IMAGE_URL")

        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions.placeholderOf(R.drawable.noimageavailbale))
            .into(binding.ivFullScreenImage)

        binding.ivFullScreenImage.setOnClickListener {
            finish()
        }
    }
}