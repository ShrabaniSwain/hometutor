package com.flyngener.hometutor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.hometutor.databinding.ActivitySelectBoardOptionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectBoardOption : AppCompatActivity() {

    private lateinit var binding: ActivitySelectBoardOptionBinding
    private lateinit var selectUnit: List<SelectUnitResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBoardOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSelectUnitList()
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            if (Constant.isQuestion) {
                val intent = Intent(applicationContext, SelectQuestionOne::class.java)
                startActivity(intent)
            }
            else{
                val intent = Intent(applicationContext, SelectquestionThree::class.java)
                startActivity(intent)
            }
        }

    }

    private fun getSelectUnitList() {
        showProgressBar()
        val call = RetrofitClient.api.getSelectUnitList()
        call.enqueue(object : Callback<SelectUnitResponse> {
            override fun onResponse(
                call: Call<SelectUnitResponse>,
                response: Response<SelectUnitResponse>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    selectUnit = response.body()?.result ?: emptyList()

                    val selectBoardAdapter = SelectBoardAdapter(selectUnit, applicationContext)
                    binding.rvBoard.adapter = selectBoardAdapter
                    binding.rvBoard.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<SelectUnitResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
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