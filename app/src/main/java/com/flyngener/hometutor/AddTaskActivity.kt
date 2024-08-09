package com.flyngener.hometutor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyngener.hometutor.databinding.ActivityAddTaskBinding
import com.flyngener.hometutor.databinding.ActivityTeacherTaskDetailsBinding

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}