package com.flyngener.tutorhub

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.tutorhub.databinding.ActivityTeacherNotificationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherNotificationActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityTeacherNotificationBinding
    private lateinit var notification: List<PaymentReminder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTeacherNotification(Constant.INSERT_ID.toInt())
        binding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun getTeacherNotification(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getTeacherNotification(id)
        call.enqueue(object : Callback<PaymentReminderResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<PaymentReminderResponse>, response: Response<PaymentReminderResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    notification = response.body()?.result?.get(0)?.result ?: emptyList()

                    val adapter = GuardianNotificationAdapter(applicationContext, notification)
                    binding.rvNotification.adapter = adapter
                    binding.rvNotification.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                    Log.i("TAG", "responseList: ${response.body()}")

                    if (notification.isEmpty()){
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<PaymentReminderResponse>, t: Throwable) {
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