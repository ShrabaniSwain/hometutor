package com.flyngener.hometutor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.hometutor.databinding.ActivityTeacherNotificationBinding

class TeacherNotificationActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityTeacherNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val adapter = GuardianNotificationAdapter()
        binding.rvNotification.adapter = adapter
        binding.rvNotification.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}