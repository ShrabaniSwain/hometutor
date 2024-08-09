package com.flyngener.hometutor

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.flyngener.hometutor.databinding.ActivityHomeTutorOtpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeTutorOtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeTutorOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeTutorOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val otpFields = arrayOf(
            binding.otpBox1,
            binding.otpBox2,
            binding.otpBox3,
            binding.otpBox4
        )

        otpFields.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Move focus to the next EditText when this EditText is filled
                    if (s?.length == 1 && index < otpFields.size - 1) {
                        otpFields[index + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editText.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (index > 0) {
                        otpFields[index - 1].requestFocus()
                        otpFields[index - 1].setText("")
                    }
                    return@setOnKeyListener true
                }
                false
            }
        }

        binding.btnLogin.setOnClickListener {
            val otp = otpFields.joinToString("") { it.text.toString() }

            if (otp.length < otpFields.size) {
                Toast.makeText(this, "Please enter a valid OTP", Toast.LENGTH_SHORT).show()
            } else {
                binding.progressBar.visibility = View.VISIBLE
                checkOtpForLogin(otp)
            }
        }
    }

    private fun checkOtpForLogin(otp: String) {
        val addUserDetails = OtpModel(otp)
        Log.i("TAG", "addCustomer: "+ addUserDetails)
        val call = RetrofitClient.api.checkOtpForLogin(addUserDetails)
        call.enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            Constant.INSERT_ID = updateProfileResponse.userId
                            binding.progressBar.visibility = View.GONE
                            val sharedPrefHelper = SharedPreferenceHelper(applicationContext)
                            sharedPrefHelper.setLoggedIn(true)

                            sharedPrefHelper.saveLoginData(
                                it.userId,
                                it.isGuardian
                            )

                            Toast.makeText(applicationContext, "OTP verify successfully...", Toast.LENGTH_SHORT).show()

                            if (updateProfileResponse.isGuardian == "1"){
                                checkGuardianPackage()
                            }
                            else{
                                if (updateProfileResponse.step_complete == "1"){
                                    val intent = Intent(applicationContext, QualificationActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else if(updateProfileResponse.step_complete == "2"){
                                    val intent = Intent(applicationContext, ServicesActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else if(updateProfileResponse.step_complete == "3"){
                                    val intent = Intent(applicationContext, KycActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else if(updateProfileResponse.step_complete == "4"){
                                    val intent = Intent(applicationContext, BankDetailsActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else if(updateProfileResponse.step_complete == "5"){
                                    checkTeacherPackage()
                                }
                            }

                        } else {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkTeacherPackage() {
        val call = RetrofitClient.api.checkTeacherPackage(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            val intent = Intent(applicationContext, TeacherMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(applicationContext, TeacherPackageActivity::class.java)
                            intent.putExtra("CARD_TYPE", Constant.GUARDIAN_CARD)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkGuardianPackage() {
        val call = RetrofitClient.api.checkGuardianPackage(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            val intent = Intent(applicationContext, GuardianMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(applicationContext, GuardianPackageActivity::class.java)
                            intent.putExtra("CARD_TYPE", Constant.GUARDIAN_CARD)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}