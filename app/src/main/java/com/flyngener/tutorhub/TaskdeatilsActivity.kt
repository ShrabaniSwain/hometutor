package com.flyngener.tutorhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.TaskDeatilsAdapter
import com.flyngener.tutorhub.databinding.ActivityTaskdeatilsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskdeatilsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskdeatilsBinding
    private lateinit var taskGuardianList: List<GuardianTask>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskdeatilsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTaskListInGuardian(Constant.INSERT_ID.toInt(), Constant.BOOKING_ID.toInt())

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getTaskListInGuardian(id: Int, bookingId: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getTaskListInGuardian(id, bookingId)
        call.enqueue(object : Callback<GuardianTaskResponse> {
            override fun onResponse(call: Call<GuardianTaskResponse>, response: Response<GuardianTaskResponse>) {
                Log.i("TAG", "onResponse: " + response)
                if (response.isSuccessful) {
                    hideProgressBar()

                    taskGuardianList = response.body()?.result  ?: emptyList()

                    if (taskGuardianList.isEmpty()){
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                    else {
                        val taskDeatilsAdapter =
                            TaskDeatilsAdapter(applicationContext, taskGuardianList)
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
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<GuardianTaskResponse>, t: Throwable) {
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