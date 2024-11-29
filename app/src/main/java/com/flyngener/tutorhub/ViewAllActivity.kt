package com.flyngener.tutorhub

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.tutorhub.databinding.ActivityViewAllBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewAllActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewAllBinding
    private lateinit var teacherBookingList: List<BookingItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        getBookingList()
    }

    private fun getBookingList() {
        showProgressBar()
        val call = RetrofitClient.api.getBookingList(1)
        call.enqueue(object : Callback<TeacherBookingResponse> {
            override fun onResponse(
                call: Call<TeacherBookingResponse>,
                response: Response<TeacherBookingResponse>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    teacherBookingList = response.body()?.result ?: emptyList()
                    val viewAllAdapter = ViewAllAdapter(this@ViewAllActivity, teacherBookingList)
                    binding.rvFeatured.layoutManager = LinearLayoutManager(this@ViewAllActivity, LinearLayoutManager.VERTICAL, false)
                    binding.rvFeatured.adapter = viewAllAdapter
                    if (teacherBookingList.isEmpty()){
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TeacherBookingResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
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