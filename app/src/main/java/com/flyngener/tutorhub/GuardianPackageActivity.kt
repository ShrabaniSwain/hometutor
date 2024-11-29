package com.flyngener.tutorhub

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.flyngener.tutorhub.databinding.ActivityGuardianPackageBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class GuardianPackageActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityGuardianPackageBinding
    var isSelected = false
    var price = "0"
    private lateinit var guardianPackageData: GuardianPackageData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuardianPackageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val cardType = intent.getStringExtra("CARD_TYPE")

        binding.btnSubmit.setOnClickListener {
            if (!isSelected){
                Toast.makeText(applicationContext, "Please select a package.", Toast.LENGTH_SHORT).show()
            }
            else {
                guardianPackageData = GuardianPackageData(Constant.INSERT_ID, Constant.PACKAGE_ID, "")

                if (price.isEmpty() || price == "0"){
                    clickOnTeacher(guardianPackageData)
                    Log.i("TAG", "onPaymentSuccess:  " + guardianPackageData)
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

                        co.open(this@GuardianPackageActivity, obj)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        getGuardianPackageList()

    }

    private fun clickOnTeacher(teacherPackageData: GuardianPackageData) {
        val call = RetrofitClient.api.guardianPackageBooking(teacherPackageData)
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

            override fun onFailure(call: Call<PackageBookingResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getGuardianPackageList() {
        showProgressBar()
        val call = RetrofitClient.api.getGaurdianPackageList()
        call.enqueue(object : Callback<PackageResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<PackageResponse>, response: Response<PackageResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    Glide.with(applicationContext).load(response.body()?.result?.get(0)?.package_icon ?: "").apply(
                        RequestOptions.placeholderOf(R.drawable.noimageavailbale)).into(binding.ivStarter)

                    binding.tvStarter.text = response.body()?.result?.get(0)?.guardians_package_name ?: ""
                    val htmlText1 = response.body()?.result?.get(0)?.package_description ?: ""
                    val cleanText1 = Html.fromHtml(htmlText1).toString()
                    binding.tvUnlimited.text = cleanText1

                    if (response.body()?.result?.get(0)?.package_price.isNullOrEmpty()){
                        binding.tvStarterAmount.text = "₹ 0"
                    }
                    else {
                        binding.tvStarterAmount.text =
                            "₹" + response.body()?.result?.get(0)?.package_price
                    }

                    binding.tvStarterMonthly.text =
                        (response.body()?.result?.get(0)?.package_validity + " Days") ?: ""

                    val result = response.body()?.result
                    if (result != null && result.size > 1) {
                        Glide.with(applicationContext).load(response.body()?.result?.get(1)?.package_icon ?: "").apply(RequestOptions.placeholderOf(R.drawable.noimageavailbale)).into(binding.ivPremium)

                        binding.tvPremium.text = result[1].guardians_package_name ?: ""
                        val htmlText = response.body()?.result?.get(1)?.package_description ?: ""
                        val cleanText = Html.fromHtml(htmlText).toString()
                        binding.tvUnlimitedRequest.text = cleanText

                        binding.tvPremiumYearly.text =
                            (response.body()?.result?.get(1)?.package_validity + " Days") ?: ""

                        if (response.body()?.result?.get(1)?.package_price.isNullOrEmpty()){
                            binding.tvPremiumAmount.text = "₹ 0"
                        }
                        else {
                            binding.tvPremiumAmount.text =
                                "₹" + response.body()?.result?.get(1)?.package_price
                        }

                    } else {
                        binding.premiumCatdView.visibility = View.GONE
                    }



                    binding.starterCatdView.setOnClickListener {
                        binding.tickStarter.visibility = View.VISIBLE
                        binding.tickPremium.visibility = View.GONE
                        Constant.PACKAGE_ID = response.body()?.result?.get(0)?.id ?: ""
                        price = response.body()?.result?.get(0)?.package_price ?: "0"
                        isSelected = true
                    }

                    binding.premiumCatdView.setOnClickListener {
                        binding.tickStarter.visibility = View.GONE
                        binding.tickPremium.visibility = View.VISIBLE
                        Constant.PACKAGE_ID = response.body()?.result?.get(1)?.id ?: ""
                        price = response.body()?.result?.get(1)?.package_price ?: "0"
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

    override fun onPaymentSuccess(p0: String?) {
        if (p0 != null) {
            guardianPackageData.transaction_id = p0
        }
        clickOnTeacher(guardianPackageData)
        Log.i("TAG", "onPaymentSuccess: $guardianPackageData")

        Toast.makeText(this, "Payment is successful", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Failed due to error", Toast.LENGTH_SHORT).show()
    }

}