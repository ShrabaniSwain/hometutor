package com.flyngener.tutorhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.flyngener.tutorhub.Constant.GUARDIAN_CARD
import com.flyngener.tutorhub.databinding.ActivityLoginSignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.homeTutorCard.setOnClickListener {
            binding.tickHomeTutor.visibility = View.VISIBLE
            binding.tickGuardian.visibility = View.GONE

            val intent = Intent(this, QualificationActivity::class.java)
            startActivity(intent)
        }

        binding.guardiansCard.setOnClickListener {
            binding.tickHomeTutor.visibility = View.GONE
            binding.tickGuardian.visibility = View.VISIBLE

            clickOnGuardian(Constant.INSERT_ID.toInt())
        }
    }

    private fun clickOnGuardian(insertId: Int) {
        showProgressBar()
        val addUserDetails = Guardian(insertId)
        Log.i("TAG", "addCustomer: $addUserDetails")
        val call = RetrofitClient.api.clickOnGuardian(addUserDetails)
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(call: Call<HomeTutorModel>, response: Response<HomeTutorModel>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            hideProgressBar()
                            val intent = Intent(applicationContext, GuardianPackageActivity::class.java)
                            intent.putExtra("CARD_TYPE", GUARDIAN_CARD)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    hideProgressBar()
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                hideProgressBar()
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
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