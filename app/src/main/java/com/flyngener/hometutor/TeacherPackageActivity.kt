package com.flyngener.hometutor

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.flyngener.hometutor.Constant.PACKAGE_ID
import com.flyngener.hometutor.databinding.ActivityGuardianPackageBinding
import com.flyngener.hometutor.databinding.ActivityTeacherPackageActivtyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherPackageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherPackageActivtyBinding
    var isSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherPackageActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        getTeacherPackageList()

        val cardType = intent.getStringExtra("CARD_TYPE")

        binding.btnSubmit.setOnClickListener {
            val teacherPackageData = TeacherPackageData(Constant.INSERT_ID, PACKAGE_ID)
            if (!isSelected){
                Toast.makeText(applicationContext, "Please select a package.", Toast.LENGTH_SHORT).show()
            }
            else {
                Log.i("TAG", "onCreate: $teacherPackageData")
                clickOnGuardian(teacherPackageData)
            }
        }

    }

    private fun clickOnGuardian(teacherPackageData: TeacherPackageData) {
        val call = RetrofitClient.api.teacherPackageBooking(teacherPackageData)
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

    private fun getTeacherPackageList() {
        showProgressBar()
        val call = RetrofitClient.api.getTeacherPackageList()
        call.enqueue(object : Callback<TeacherPackageResponse> {
            override fun onResponse(
                call: Call<TeacherPackageResponse>,
                response: Response<TeacherPackageResponse>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    binding.tvStarter.text =
                        response.body()?.result?.get(0)?.teacher_package_name ?: ""

                    val htmlText1 = response.body()?.result?.get(0)?.package_description ?: ""
                    val cleanText1 = Html.fromHtml(htmlText1).toString()
                    binding.tvUnlimited.text = cleanText1
                    binding.tvStarterMonthly.text =
                        (response.body()?.result?.get(0)?.package_validity + " Days") ?: ""
                    binding.tvStarterAmount.text =
                        ("₹" + response.body()?.result?.get(0)?.package_price) ?: ""

                    binding.tvPremium.text =
                        response.body()?.result?.get(1)?.teacher_package_name ?: ""
                    val htmlText = response.body()?.result?.get(1)?.package_description ?: ""
                    val cleanText = Html.fromHtml(htmlText).toString()
                    binding.tvUnlimitedRequest.text = cleanText
                    binding.tvPremiumYearly.text =
                        (response.body()?.result?.get(1)?.package_validity + " Days") ?: ""
                    binding.tvPremiumAmount.text =
                        ("₹" + response.body()?.result?.get(1)?.package_price) ?: ""

                    binding.starterCatdView.setOnClickListener {
                        binding.tickStarter.visibility = View.VISIBLE
                        binding.tickPremium.visibility = View.GONE
                        PACKAGE_ID = response.body()?.result?.get(0)?.id ?: ""
                        isSelected = true
                    }
                    binding.premiumCatdView.setOnClickListener {
                        binding.tickStarter.visibility = View.GONE
                        binding.tickPremium.visibility = View.VISIBLE
                        PACKAGE_ID = response.body()?.result?.get(1)?.id ?: ""
                        isSelected = true

                    }

                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TeacherPackageResponse>, t: Throwable) {
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