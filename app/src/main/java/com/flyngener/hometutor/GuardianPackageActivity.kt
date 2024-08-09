package com.flyngener.hometutor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.flyngener.hometutor.Constant.GUARDIAN_CARD
import com.flyngener.hometutor.Constant.TEACHER_CARD

import com.flyngener.hometutor.databinding.ActivityGuardianPackageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GuardianPackageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuardianPackageBinding
    var isSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuardianPackageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val cardType = intent.getStringExtra("CARD_TYPE")

        binding.btnSubmit.setOnClickListener {
            val guardianPackageData = GuardianPackageData(Constant.INSERT_ID, Constant.PACKAGE_ID)
            if (!isSelected){
                Toast.makeText(applicationContext, "Please select a package.", Toast.LENGTH_SHORT).show()
            }
            else {
                Log.i("TAG", "onCreate: $guardianPackageData")
                clickOnTeacher(guardianPackageData)
            }
        }

        getGuardianPackageList()

    }

    private fun clickOnTeacher(teacherPackageData: GuardianPackageData) {
        val call = RetrofitClient.api.guardianPackageBooking(teacherPackageData)
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
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT)
                                .show()
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

    private fun getGuardianPackageList() {
        showProgressBar()
        val call = RetrofitClient.api.getGaurdianPackageList()
        call.enqueue(object : Callback<PackageResponse> {
            override fun onResponse(call: Call<PackageResponse>, response: Response<PackageResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    binding.tvStarter.text = response.body()?.result?.get(0)?.guardians_package_name ?: ""
                    val htmlText1 = response.body()?.result?.get(0)?.package_description ?: ""
                    val cleanText1 = Html.fromHtml(htmlText1).toString()
                    binding.tvUnlimited.text = cleanText1

                    binding.tvStarterMonthly.text =
                        (response.body()?.result?.get(0)?.package_validity + " Days") ?: ""
                    binding.tvStarterAmount.text =
                        ("₹" + response.body()?.result?.get(0)?.package_price) ?: ""

                    val result = response.body()?.result
                    if (result != null && result.size > 1) {
                        binding.tvPremium.text = result[1].guardians_package_name ?: ""
                        val htmlText = response.body()?.result?.get(1)?.package_description ?: ""
                        val cleanText = Html.fromHtml(htmlText).toString()
                        binding.tvUnlimitedRequest.text = cleanText

                        binding.tvPremiumYearly.text =
                            (response.body()?.result?.get(1)?.package_validity + " Days") ?: ""
                        binding.tvPremiumAmount.text =
                            ("₹" + response.body()?.result?.get(1)?.package_price) ?: ""
                    } else {
                        binding.tvPremium.text = ""
                    }



                    binding.starterCatdView.setOnClickListener {
                        binding.tickStarter.visibility = View.VISIBLE
                        binding.tickPremium.visibility = View.GONE
                        Constant.PACKAGE_ID = response.body()?.result?.get(0)?.id ?: ""
                        isSelected = true
                    }

                    binding.premiumCatdView.setOnClickListener {
                        binding.tickStarter.visibility = View.GONE
                        binding.tickPremium.visibility = View.VISIBLE
                        Constant.PACKAGE_ID = response.body()?.result?.get(1)?.id ?: ""
                        isSelected = true
                    }

                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<PackageResponse>, t: Throwable) {
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