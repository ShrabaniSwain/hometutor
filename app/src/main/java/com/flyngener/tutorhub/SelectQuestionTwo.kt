package com.flyngener.tutorhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyngener.tutorhub.databinding.ActivitySelectQuestionTwoBinding

class SelectQuestionTwo : AppCompatActivity() {

    private lateinit var binding: ActivitySelectQuestionTwoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectQuestionTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(applicationContext, SelectquestionThree::class.java)
            startActivity(intent)
        }
    }
}