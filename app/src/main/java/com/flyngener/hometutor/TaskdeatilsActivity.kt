package com.flyngener.hometutor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.TaskDeatilsAdapter
import com.flyngener.hometutor.databinding.ActivitySupportBinding
import com.flyngener.hometutor.databinding.ActivityTaskdeatilsBinding

class TaskdeatilsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskdeatilsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskdeatilsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val taskDeatilsAdapter = TaskDeatilsAdapter(applicationContext)
        binding.rvTaskDeatils.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.rvTaskDeatils.adapter = taskDeatilsAdapter
    }
}