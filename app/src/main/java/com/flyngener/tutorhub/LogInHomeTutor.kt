package com.flyngener.tutorhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.flyngener.tutorhub.databinding.ActivityLogInHomeTutorBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogInHomeTutor : AppCompatActivity() {

    private lateinit var binding: ActivityLogInHomeTutorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInHomeTutorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, RegisterNowActivity::class.java)
            startActivity(intent)
        }

        binding.tvUserIdPass.setOnClickListener {
            val intent = Intent(this, LoginMobileNoHome::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val userId = binding.etUserId.text.toString()
            val password = binding.etPassword.text.toString()
            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_SHORT).show()
            } else {
                val username = Username(userId, password)
                loginWithUsername(username)
            }
        }

    }

    private fun loginWithUsername(username: Username) {
        showProgressBar()
        val call = RetrofitClient.api.loginWithUsername(username)
        call.enqueue(object : Callback<UsernameResponse> {
            override fun onResponse(call: Call<UsernameResponse>, response: Response<UsernameResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            hideProgressBar()
                            Constant.INSERT_ID = updateProfileResponse.user_id
                            Constant.MOBILE_NO = updateProfileResponse.mobile_number
                            val sharedPrefHelper = SharedPreferenceHelper(applicationContext)
                            sharedPrefHelper.setLoggedIn(true)

                            sharedPrefHelper.saveLoginData(
                                it.user_id,
                                it.is_guardian
                            )

                            sharedPrefHelper.saveMobileNo(it.mobile_number)

                            Toast.makeText(applicationContext, "Login successfully...", Toast.LENGTH_SHORT).show()
                            if (updateProfileResponse.is_guardian == "1"){
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
                            hideProgressBar()
                            Log.i("TAG", "onResponse: " + it.message)
                            Toast.makeText(applicationContext, "Mobile number did not match..", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    hideProgressBar()
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UsernameResponse>, t: Throwable) {
                hideProgressBar()
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkTeacherPackage() {
        showProgressBar()
        val call = RetrofitClient.api.checkTeacherPackage(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()
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

    private fun checkGuardianPackage() {
        showProgressBar()
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
                            hideProgressBar()
                            val intent = Intent(applicationContext, GuardianMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            hideProgressBar()
                            val intent = Intent(applicationContext, GuardianPackageActivity::class.java)
                            intent.putExtra("CARD_TYPE", Constant.GUARDIAN_CARD)
                            startActivity(intent)
                            finish()
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