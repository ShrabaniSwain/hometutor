package com.flyngener.hometutor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.flyngener.hometutor.databinding.ActivitySelectTeacherBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectTeacher : AppCompatActivity() {

    private lateinit var binding: ActivitySelectTeacherBinding
    private lateinit var profileGuardian: List<ServiceProfileGuardian>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Constant.CLICKTYPE == Constant.MORECLICK) {
            getServiceProfileList()
        }
        else if (Constant.CLICKTYPE == Constant.POPULARTEACHERCLICK){
            getSubPopularProfile(Constant.PROFILEID)
        }
        else{
            getSubcategoryProfile(Constant.PROFILEID)
        }
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun getServiceProfileList() {
        showProgressBar()
        val call = RetrofitClient.api.getServiceProfileList()
        call.enqueue(object : Callback<ServiceProfileGuardianResult> {
            override fun onResponse(call: Call< ServiceProfileGuardianResult>, response: Response<ServiceProfileGuardianResult>) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    profileGuardian = response.body()?.result ?: emptyList()

                    response.body()?.result?.let { featuredServices ->

                        val isHome = featuredServices.filter { it.is_show_in_home == "Yes" }
                        val selectTeacherAdapter = SelectTeacherAdapter(isHome,applicationContext)
                        binding.rvServices.adapter = selectTeacherAdapter
                        binding.rvServices.layoutManager = GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
                    }
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ServiceProfileGuardianResult>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    private fun getSubcategoryProfile(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getSubcategoryProfile(id)
        call.enqueue(object : Callback<ServiceProfileGuardianResult> {
            override fun onResponse(call: Call< ServiceProfileGuardianResult>, response: Response<ServiceProfileGuardianResult>) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    profileGuardian = response.body()?.result ?: emptyList()

                    response.body()?.result?.let { featuredServices ->

                        val isHome = featuredServices.filter { it.is_show_in_home == "Yes" }
                        val selectTeacherAdapter = SelectTeacherAdapter(isHome,applicationContext)
                        binding.rvServices.adapter = selectTeacherAdapter
                        binding.rvServices.layoutManager = GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
                    }
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ServiceProfileGuardianResult>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    private fun getSubPopularProfile(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getSubPopularProfile(id)
        call.enqueue(object : Callback<ServiceProfileGuardianResult> {
            override fun onResponse(call: Call< ServiceProfileGuardianResult>, response: Response<ServiceProfileGuardianResult>) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    profileGuardian = response.body()?.result ?: emptyList()

                    response.body()?.result?.let { featuredServices ->

                        val isHome = featuredServices.filter { it.is_show_in_home == "Yes" }
                        val selectTeacherAdapter = SelectTeacherAdapter(isHome,applicationContext)
                        binding.rvServices.adapter = selectTeacherAdapter
                        binding.rvServices.layoutManager = GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
                    }
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ServiceProfileGuardianResult>, t: Throwable) {
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