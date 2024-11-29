package com.flyngener.tutorhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.flyngener.tutorhub.databinding.ActivityTeacherTermsCondition2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherTermsCondition : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherTermsCondition2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherTermsCondition2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        getTermsList()

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getTermsList() {
        showProgressBar()
        val call = RetrofitClient.api.getTermsList()
        call.enqueue(object : Callback<SupportResponse> {
            override fun onResponse(call: Call<SupportResponse>, response: Response<SupportResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    binding.tvSubject.text = response.body()?.result?.get(0)?.page_details
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<SupportResponse>, t: Throwable) {
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