package com.flyngener.tutorhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.tutorhub.databinding.ActivityDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var activeTeacherList: List<StudentDetails>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getActiveListForTeacher(Constant.BOOKING_ID.toInt())
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun getActiveListForTeacher(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getDetailsActiveListForTeacher(id)
        call.enqueue(object : Callback<DetailsActiveResponse> {
            override fun onResponse(call: Call<DetailsActiveResponse>, response: Response<DetailsActiveResponse>) {
                Log.i("TAG", "onResponse: " + response)
                if (response.isSuccessful) {
                    hideProgressBar()

                    activeTeacherList = response.body()?.result  ?: emptyList()

                    val appointedAdapter = DetailsAdapter(applicationContext, activeTeacherList)
                    binding.rvDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                    binding.rvDetails.adapter = appointedAdapter

                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<DetailsActiveResponse>, t: Throwable) {
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