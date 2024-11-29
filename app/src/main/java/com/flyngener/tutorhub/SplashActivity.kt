package com.flyngener.tutorhub

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.flyngener.tutorhub.databinding.ActivitySplashBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefHelper = SharedPreferenceHelper(this)
        if (sharedPrefHelper.isLoggedIn()) {

            Constant.INSERT_ID = sharedPrefHelper.getCustomerId(this)
            Constant.MOBILE_NO = sharedPrefHelper.getMobileNo(this)
            Constant.is_guardian = sharedPrefHelper.getCustomerName(this)

            Log.i("TAG", "insertid: " + Constant.INSERT_ID)
            Log.i("TAG", "MOBILE_NO: " + Constant.MOBILE_NO)
            loadMainActivityWithDelay()
        }
        else {
            loadSignUpActivityWithDelay()
        }
    }

    private fun loadSignUpActivityWithDelay() {
        Handler().postDelayed({
            val intent = Intent(this, LoginMobileNoHome::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun loadMainActivityWithDelay() {
        Handler().postDelayed({

            if (Constant.is_guardian == "1"){
                val userTypeGuardian = UserTypeSwitch("teacher", Constant.INSERT_ID, "teacher")
                appSaveTodoStatus(userTypeGuardian)
//                val intent = Intent(applicationContext, GuardianMainActivity::class.java)
//                startActivity(intent)
//                finish()
            }
            else{
                val userTypeTeacher = UserTypeSwitch("guardian", Constant.INSERT_ID, "guardian")

                appSaveTodoStatus(userTypeTeacher)
//                val intent = Intent(applicationContext, TeacherMainActivity::class.java)
//                startActivity(intent)
//                finish()
            }
        }, 2000)
    }

    private fun appSaveTodoStatus(userTypeSwitch: UserTypeSwitch) {
        val call = RetrofitClient.api.switchAccount(userTypeSwitch)
        call.enqueue(object : Callback<SwitchResponse> {
            override fun onResponse(call: Call<SwitchResponse>, response: Response<SwitchResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Constant.INSERT_ID = updateProfileResponse?.user_id.toString()
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            Log.i("TAG", "onResponse: " + response.body())
                            if (Constant.is_guardian == "1"){
                                checkGuardianPackage()
                            }
                            else{
                                if (updateProfileResponse.step == "1"){
                                    val intent = Intent(applicationContext, QualificationActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else if(updateProfileResponse.step == "2"){
                                    val intent = Intent(applicationContext, ServicesActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else if(updateProfileResponse.step == "3"){
                                    val intent = Intent(applicationContext, KycActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else if(updateProfileResponse.step == "4"){
                                    val intent = Intent(applicationContext, BankDetailsActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else if(updateProfileResponse.step == "5"){
                                    checkTeacherPackage()
                                }
                            }

                        } else {
                            Toast.makeText(
                                applicationContext,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }else {
                    val message = response.body()?.message ?: "Something went wrong, please try again."

                    Toast.makeText(
                        applicationContext,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.e("API", "API call failed with code ${response.code()} and message: $message")
                }

            }

            override fun onFailure(call: Call<SwitchResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    applicationContext,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
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