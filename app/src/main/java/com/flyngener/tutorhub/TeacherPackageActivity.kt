package com.flyngener.tutorhub

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.Constant.PACKAGE_ID
import com.flyngener.tutorhub.databinding.ActivityTeacherPackageActivtyBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class TeacherPackageActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityTeacherPackageActivtyBinding
    var isSelected = false
    var price = "0"
    private lateinit var teacherPackageData: TeacherPackageData
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
            if (!isSelected){
                Toast.makeText(applicationContext, "Please select a package.", Toast.LENGTH_SHORT).show()
            }
            else {
//                val amount = price.replace(Regex("[^\\d]"), "")
                teacherPackageData = TeacherPackageData(Constant.INSERT_ID, PACKAGE_ID, "")

                if (price.isEmpty() || price == "0"){
                    clickOnGuardian(teacherPackageData)
                    Log.i("TAG", "onPaymentSuccess:  " + teacherPackageData)
                }
                else {
                    val pricePay = (price.toFloat() * 100).roundToInt()
                    Checkout.preload(applicationContext)
                    val co = Checkout()
                    co.setKeyID(Constant.RAZOR_PAY_KEY)
                    val obj = JSONObject()
                    try {
                        obj.put("description", "Test payment")
                        obj.put("theme.color", "")
                        obj.put("currency", "INR")
                        obj.put("amount", pricePay)
                        obj.put("prefill.contact", Constant.MOBILE_NO)
                        co.open(this@TeacherPackageActivity, obj)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    private fun clickOnGuardian(teacherPackageData: TeacherPackageData) {
        val call = RetrofitClient.api.teacherPackageBooking(teacherPackageData)
        call.enqueue(object : Callback<PackageBookingResponse> {
            override fun onResponse(
                call: Call<PackageBookingResponse>,
                response: Response<PackageBookingResponse>
            ) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            hideProgressBar()
                            Constant.INSERT_ID = updateProfileResponse.user_id
                            val sharedPrefHelper = SharedPreferenceHelper(applicationContext)
                            sharedPrefHelper.setLoggedIn(true)

                            sharedPrefHelper.saveLoginData(
                                it.user_id,
                                it.is_guardian
                            )

                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
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

            override fun onFailure(call: Call<PackageBookingResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getTeacherPackageList() {
        showProgressBar()
        val call = RetrofitClient.api.getTeacherPackageList()
        call.enqueue(object : Callback<TeacherPackageResponse> {
            @SuppressLint("SetTextI18n")
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
                    Glide.with(applicationContext).load(response.body()?.result?.get(0)?.package_icon ?: "").apply(RequestOptions.placeholderOf(R.drawable.noimageavailbale)).into(binding.ivStarter)

                    binding.tvUnlimited.text = cleanText1
                    binding.tvStarterMonthly.text =
                        (response.body()?.result?.get(0)?.package_validity + " Days") ?: ""

                    if (response.body()?.result?.get(0)?.package_price.isNullOrEmpty()){
                        binding.tvStarterAmount.text = "₹ 0"
                    }
                    else {
                        binding.tvStarterAmount.text =
                            "₹" + response.body()?.result?.get(0)?.package_price
                    }

                    val resultList = response.body()?.result
                    if (resultList != null && resultList.size > 1) {
                        binding.tvPremium.text =
                            response.body()?.result?.get(1)?.teacher_package_name ?: ""
                        val htmlText = response.body()?.result?.get(1)?.package_description ?: ""
                        val cleanText = Html.fromHtml(htmlText).toString()
                        binding.tvUnlimitedRequest.text = cleanText
                        Glide.with(applicationContext)
                            .load(response.body()?.result?.get(1)?.package_icon ?: "")
                            .apply(RequestOptions.placeholderOf(R.drawable.noimageavailbale))
                            .into(binding.ivPremium)
                        binding.tvPremiumYearly.text =
                            (response.body()?.result?.get(1)?.package_validity + " Days") ?: ""

                        if (response.body()?.result?.get(1)?.package_price.isNullOrEmpty()) {
                            binding.tvPremiumAmount.text = "₹ 0"
                        } else {
                            binding.tvPremiumAmount.text =
                                "₹" + response.body()?.result?.get(1)?.package_price
                        }
                        binding.premiumCatdView.setOnClickListener {
                            binding.tickStarter.visibility = View.GONE
                            binding.tickPremium.visibility = View.VISIBLE
                            PACKAGE_ID = response.body()?.result?.get(1)?.id ?: ""
                            price = response.body()?.result?.get(1)?.package_price ?: "0"
                            isSelected = true

                        }
                    }

                    else {
                        binding.premiumCatdView.visibility = View.GONE
                    }


                    binding.starterCatdView.setOnClickListener {
                        binding.tickStarter.visibility = View.VISIBLE
                        binding.tickPremium.visibility = View.GONE
                        PACKAGE_ID = response.body()?.result?.get(0)?.id ?: ""
                        price = response.body()?.result?.get(0)?.package_price ?: "0"
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

    override fun onPaymentSuccess(p0: String?) {

        if (p0 != null) {
            teacherPackageData.transaction_id = p0
        }
        Log.i("TAG", "onPaymentSuccess: $teacherPackageData")
        clickOnGuardian(teacherPackageData)

        Toast.makeText(this, "Payment is successful", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Failed due to error", Toast.LENGTH_SHORT).show()

    }

}