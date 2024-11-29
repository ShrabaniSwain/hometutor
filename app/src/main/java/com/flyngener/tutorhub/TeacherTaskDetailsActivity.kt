package com.flyngener.tutorhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.tutorhub.databinding.ActivityTeacherTaskDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherTaskDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherTaskDetailsBinding
    private lateinit var taskTeacherList: List<TaskDetails>

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

        getTaskListInTeacher(Constant.INSERT_ID.toInt(), Constant.BOOKING_ID.toInt())

    }

    private fun getTaskListInTeacher(teacher_id: Int, booking_id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getTaskListInTeacher(teacher_id, booking_id)
        call.enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                Log.i("TAG", "onResponse: $response")
                if (response.isSuccessful) {
                    hideProgressBar()

                    taskTeacherList = response.body()?.result  ?: emptyList()
                    if (taskTeacherList.isEmpty()){
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                    else {
                        val taskDeatilsAdapter =
                            TeacherTaskdetailsAdapter(applicationContext, taskTeacherList)
                        binding.rvTaskDeatils.layoutManager = LinearLayoutManager(
                            applicationContext,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        binding.rvTaskDeatils.adapter = taskDeatilsAdapter
                    }

                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}