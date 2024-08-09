package com.flyngener.hometutor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.hometutor.databinding.ActivityViewAllBinding

class ViewAllActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewAllBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val viewAllAdapter = ViewAllAdapter(applicationContext)
        binding.rvFeatured.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.rvFeatured.adapter = viewAllAdapter

    }
}