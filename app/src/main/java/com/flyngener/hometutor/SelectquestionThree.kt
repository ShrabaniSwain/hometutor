package com.flyngener.hometutor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.flyngener.hometutor.databinding.ActivitySelectquestionThreeBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SelectquestionThree : AppCompatActivity(), ChoosefeeAdapter.OnFeeItemSelectedListener,
    PaymentResultListener {

    private lateinit var binding: ActivitySelectquestionThreeBinding
    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var jobPost: BookingDataSubmit
    private lateinit var freesList: List<Fee>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectquestionThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateDateField()
        updateTimeField()
        getPaymentAmount(Constant.PROFILEID)
        binding.bookFreeDemo.isChecked = true
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvCalender.setOnClickListener {
            openDatePickerDialog()
        }

        binding.tvTime.setOnClickListener {
            openTimePickerDialog()
        }

        binding.btnPayBook.setOnClickListener {
            if (Constant.BUDGET.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please select the price",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val amount = Constant.BUDGET.replace(Regex("[^\\d]"), "")
                val pricePay = Math.round(amount.toFloat() * 100)

                jobPost = BookingDataSubmit(
                    Constant.PROFILEID.toString(),
                    Constant.RANDOM_NO,
                    amount,
                    "",
                    Constant.UNIT_TYPE,
                    "",
                    "",
                    "",
                    "1",
                    "0",
                    Constant.questionnaireList,Constant.selectUnitList, Constant.INSERT_ID
                )

                Checkout.preload(applicationContext)
                val co = Checkout()
                co.setKeyID("rzp_test_1taCjc4CNenr7q")
                val obj = JSONObject()
                try {
                    obj.put("description", "Test payment")
                    obj.put("theme.color", "")
                    obj.put("currency", "INR")
                    obj.put("amount", pricePay)
//                    obj.put("name", Constant.)
//                    obj.put("prefill.contact", Constant.MOBILE_NO)
//                    obj.put("prefill.email", Constant.EMAIL)
                    co.open(this@SelectquestionThree, obj)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.i("TAG", "onCreate: " + jobPost)
            }

        }

        binding.btnBookFreeDemo.setOnClickListener {
            jobPost = BookingDataSubmit(
                Constant.PROFILEID.toString(), Constant.RANDOM_NO, "", "", "", "", "", "",
                "0", "1", Constant.questionnaireList,Constant.selectUnitList, Constant.INSERT_ID
            )

            customerJobPost(jobPost)
            Log.i("TAG", "onCreate: " + jobPost)

        }
    }

    private fun updateDateField() {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        binding.tvCalender.text = sdf.format(calendar.time)
    }

    private fun updateTimeField() {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        binding.tvTime.text = sdf.format(calendar.time)
    }

    private fun openDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this, R.style.DialogTheme,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                updateDateField()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun openTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this, R.style.DialogTheme,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateTimeField()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun customerJobPost(bookingDataSubmit: BookingDataSubmit) {
        showProgressBar()
        val call = RetrofitClient.api.guardianQuestionSubmit(bookingDataSubmit)
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                hideProgressBar()
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: $updateProfileResponse")

//                        val transaction = supportFragmentManager.beginTransaction()
//                        transaction.replace(R.id.request, CustomerRequestsFragment())
//                        supportFragmentManager.popBackStack()
//                        transaction.commit()
                    val intent = Intent(applicationContext, GuardianMainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(
                        applicationContext,
                        "You have successfully submitted your booking..",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "appUserServicesUpdate onResponse error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "appUserServicesUpdate onFailure: ${t.message}")
            }
        })
    }

    override fun onItemSelected(isSelected: Boolean) {
        binding.btnBookFreeDemo.visibility = if (isSelected) View.GONE else View.VISIBLE
        binding.leftShadow.visibility = if (isSelected) View.GONE else View.VISIBLE
        binding.rightShadow.visibility = if (isSelected) View.GONE else View.VISIBLE
        binding.centeredText.visibility = if (isSelected) View.GONE else View.VISIBLE
        binding.bookFreeDemo.visibility = if (isSelected) View.GONE else View.VISIBLE
        if (isSelected) {
            binding.checkFreeDemo.isChecked = true
            binding.bookFreeDemo.isChecked = false
        } else {
            binding.checkFreeDemo.isChecked = false
            binding.bookFreeDemo.isChecked = true
        }
    }

    private fun getPaymentAmount(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getPaymentAmount(id)
        call.enqueue(object : Callback<FeesResponse> {
            override fun onResponse(call: Call<FeesResponse>, response: Response<FeesResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    freesList = response.body()?.fees_array ?: emptyList()

                    val chooseFeeAdapter =
                        ChoosefeeAdapter(freesList, applicationContext, this@SelectquestionThree)
                    binding.rvChooseFee.adapter = chooseFeeAdapter
                    binding.rvChooseFee.layoutManager =
                        GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)

                    if (freesList.isEmpty()) {
                        binding.checkFreeDemo.visibility = View.GONE
                        binding.chooseFee.visibility = View.GONE
                        binding.btnPayBook.visibility = View.GONE
                        binding.leftShadow.visibility = View.GONE
                        binding.rightShadow.visibility = View.GONE
                        binding.centeredText.visibility = View.GONE
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

            override fun onFailure(call: Call<FeesResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    override fun onPaymentSuccess(p0: String?) {
        if (p0 != null) {
            jobPost.transaction_id = p0
        }
        Log.i("TAG", "onPaymentSuccess: " + jobPost)
        customerJobPost(jobPost)
        Toast.makeText(this, "Payment is successful", Toast.LENGTH_SHORT).show();
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Failed due to error", Toast.LENGTH_SHORT).show();
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