package com.flyngener.hometutor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.TaskDeatilsAdapter
import com.flyngener.hometutor.databinding.ActivityTaskdeatilsBinding
import com.flyngener.hometutor.databinding.ActivityTeacherTaskDetailsBinding

class TeacherTaskDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherTaskDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnAddTask.setOnClickListener {
            val intent = Intent(applicationContext, AddTaskActivity::class.java)
            startActivity(intent)
        }

        val taskDeatilsAdapter = TeacherTaskdetailsAdapter(applicationContext)
        binding.rvTaskDeatils.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.rvTaskDeatils.adapter = taskDeatilsAdapter


    }
}