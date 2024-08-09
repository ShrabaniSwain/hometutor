package com.flyngener.hometutor

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.flyngener.hometutor.databinding.ActivityRegisterNowBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterNowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterNowBinding
    private val cityNames = mutableListOf<String>()
    private val stateNames = mutableListOf<String>()
    private var selectedCityId: String? = null
    private var gender: String = ""

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterNowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.genderMen.setOnClickListener {
            gender = "Men"
            binding.tickMen.visibility = View.VISIBLE
            binding.tickWomen.visibility = View.GONE
        }

        binding.genderWomen.setOnClickListener {
            gender = "Women"
            binding.tickMen.visibility = View.GONE
            binding.tickWomen.visibility = View.VISIBLE
        }

        binding.referralCodeCheck.setOnCheckedChangeListener { _, isChecked ->
            binding.etReferralCode.isEnabled = isChecked
        }

        binding.btnNext.setOnClickListener {
            val etName = binding.etName.text.toString()
            val etMobileNumber = binding.etPhone.text.toString()
            val etEmail = binding.etEmail.text.toString()
            val etCity = binding.etCity.text.toString()
            val etState = binding.etState.text.toString()
            val etPinCode = binding.etPinCode.text.toString()
            val etReferralCode = binding.etReferralCode.text.toString()
            if (etName.isEmpty() || etMobileNumber.isEmpty() || etCity.isEmpty() || etEmail.isEmpty() || etState.isEmpty() || etPinCode.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (gender.isEmpty()) {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                addRegistrationsDetails(
                    etName,
                    etEmail,
                    etMobileNumber,
                    etReferralCode,
                    etCity,
                    etState,
                    etPinCode,
                    gender
                )
            }
        }

        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(applicationContext, LogInHomeTutor::class.java)
            startActivity(intent)
        }

        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getCityList()
            }

            Log.i("TAG", "onCreate: " + response)
            if (response.isSuccessful) {
                val customerDetailsResponse = response.body()
                customerDetailsResponse?.let { handleCityListResponse(it.result) }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }

    }

    private fun handleCityListResponse(result: List<City>) {

        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, cityNames)
        binding.etCity.setAdapter(adapter)

        binding.etCity.setOnItemClickListener { parent, _, position, _ ->
            val selectedProductName = parent.getItemAtPosition(position) as String
            val selectedProduct = result.find { it.city_name == selectedProductName }

            selectedProduct?.let {
                selectedCityId = it.id
                binding.etState.text = Editable.Factory.getInstance().newEditable(it.state_name)
            }
        }
        cityNames.clear()
        cityNames.addAll(result.map { it.city_name })

        stateNames.clear()
        stateNames.addAll(result.map { it.state_name })
    }

    private fun addRegistrationsDetails(full_name: String, email_id: String, mobile_number: String, refferal_code: String, city: String, state: String, pincode: String, gender: String) {
        val addCustomer = RegistrationModel(full_name,email_id,mobile_number,refferal_code,city,state, pincode, gender)
        val call = RetrofitClient.api.addRegistrationsDetails(addCustomer)
        call.enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            if (it.insert_id == 0){
                                Toast.makeText(applicationContext, "This phone number already exist", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Constant.INSERT_ID = response.body()!!.insert_id.toString()
                                Toast.makeText(
                                    applicationContext,
                                    "Registrations details added successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent =
                                    Intent(applicationContext, LoginSignUpActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Toast.makeText(applicationContext, "Failed to save registrations details. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, "Failed to save registrations details details. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

}