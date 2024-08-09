package com.flyngener.hometutor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.flyngener.hometutor.databinding.ActivityLoginMobileNoHomeBinding
import com.flyngener.hometutor.databinding.ActivityLoginSignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginMobileNoHome : AppCompatActivity() {

    private lateinit var binding: ActivityLoginMobileNoHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginMobileNoHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetOtp.setOnClickListener{
            val etMobileNumber = binding.etMobileNumber.text.toString()


            if (etMobileNumber.isBlank() || etMobileNumber.length < 10) {
                Toast.makeText(
                    this,
                    "Please enter a valid mobile number",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                loginCheckMobileNumber(etMobileNumber)
            }
        }

        binding.tvUserIdPass.setOnClickListener{
            val intent = Intent(this, LogInHomeTutor::class.java)
            startActivity(intent)
        }

        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, RegisterNowActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginCheckMobileNumber(mobile_number: String) {
        val addUserDetails = LoginWithMobileModel(mobile_number)
        Log.i("TAG", "addCustomer: $addUserDetails")
        val call = RetrofitClient.api.loginCheckMobileNumber(addUserDetails)
        call.enqueue(object : Callback<LoginMobileResponse> {
            override fun onResponse(call: Call<LoginMobileResponse>, response: Response<LoginMobileResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            Constant.INSERT_ID = updateProfileResponse.user_id
                            Toast.makeText(applicationContext, "OTP sent successfully...", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, HomeTutorOtpActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(applicationContext, it.otp, Toast.LENGTH_SHORT).show()

                        } else {
                            Log.i("TAG", "onResponse: " + it.message)
//                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginMobileResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}