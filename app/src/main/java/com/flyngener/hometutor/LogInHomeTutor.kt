package com.flyngener.hometutor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyngener.hometutor.databinding.ActivityLogInHomeTutorBinding

class LogInHomeTutor : AppCompatActivity() {

    private lateinit var binding: ActivityLogInHomeTutorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInHomeTutorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, TeacherMainActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, RegisterNowActivity::class.java)
            startActivity(intent)
        }

        binding.tvUserIdPass.setOnClickListener {
            val intent = Intent(this, LoginMobileNoHome::class.java)
            startActivity(intent)
        }


    }
}