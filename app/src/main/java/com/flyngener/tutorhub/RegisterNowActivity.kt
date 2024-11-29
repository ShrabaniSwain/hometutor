package com.flyngener.tutorhub

import android.R
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import com.flyngener.tutorhub.databinding.ActivityRegisterNowBinding
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
    private var tick = false
    private var isGuardian = "0"

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

        binding.cardViewHomeImage.setOnClickListener {
            binding.tickTeacher.visibility = View.VISIBLE
            binding.tickGuardian.visibility = View.GONE
            tick = true
            isGuardian = "0"
        }

        binding.cardViewGuardian.setOnClickListener {
            binding.tickGuardian.visibility = View.VISIBLE
            binding.tickTeacher.visibility = View.GONE
            tick = true
            isGuardian = "1"
        }
        binding.ivTermsOfService.setOnClickListener {
            val termsOfServiceUrl = "https://flyngener.com/home_tutors/mobile/terms_for_guardian.php"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(termsOfServiceUrl)
            }
            startActivity(intent)
        }

        binding.btnNext.setOnClickListener {
            val etName = binding.etName.text.toString()
            val etMobileNumber = binding.etPhone.text.toString()
            val etEmail = binding.etEmail.text.toString()
            val etCity = binding.etCity.text.toString()
            val etState = binding.etState.text.toString()
            val etPinCode = binding.etPinCode.text.toString()
            val etReferralCode = binding.etReferralCode.text.toString()
            val etUsername = binding.etUsername.text.toString()
            val etPassword = binding.etPassword.text.toString()
            val isTermsChecked = binding.checkTerms.isChecked

            if (etName.isEmpty() || etMobileNumber.isEmpty() || etCity.isEmpty() || etEmail.isEmpty() || etState.isEmpty() || etPinCode.isEmpty() || etUsername.isEmpty() || etPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (gender.isEmpty()) {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (!tick){
                Toast.makeText(this, "Please select either the guardian or teacher profile.", Toast.LENGTH_SHORT).show()
            }
            else if (!isTermsChecked){
                Toast.makeText(this, "Please accept the Terms and Conditions", Toast.LENGTH_SHORT).show()
            }
            else {
                addRegistrationsDetails(
                    etName,
                    etEmail,
                    etMobileNumber,
                    etReferralCode,
                    etCity,
                    etState,
                    etPinCode,
                    gender,
                    isGuardian,
                    etUsername,etPassword
                )

                Log.d("UserDetails", "Name: ${etName}, Email: ${etEmail}, Mobile: ${etMobileNumber}, " +
                        "Referral Code: ${etReferralCode}, City: ${etCity}, State: ${etState}, " +
                        "Pin Code: ${etPinCode}, Gender: $gender, Is Guardian: $isGuardian, " +
                        "Username: ${etUsername}, Password: ${etPassword}")

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

    private fun addRegistrationsDetails(full_name: String, email_id: String, mobile_number: String, refferal_code: String, city: String, state: String, pincode: String, gender: String,
                                        isGuardian: String, username: String, password: String) {
        val addCustomer = RegistrationModel(full_name,email_id,mobile_number,refferal_code,city,state, pincode, gender, isGuardian, username, password)
        Log.i("TAG", "addRegistrationsDetails: $addCustomer")
        val call = RetrofitClient.api.addRegistrationsDetails(addCustomer)
        showProgressBar()
        call.enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            if (it.insert_id == 0){
                                Toast.makeText(applicationContext, "This phone number already exist", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Constant.INSERT_ID = response.body()!!.insert_id.toString()
                                Constant.MOBILE_NO = response.body()!!.mobile_number.toString()
                                val sharedPrefHelper = SharedPreferenceHelper(applicationContext)
                                sharedPrefHelper.saveMobileNo(it.mobile_number)
                                if (isGuardian == "0"){
                                    val intent = Intent(applicationContext, QualificationActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    Toast.makeText(
                                        applicationContext,
                                        "Registrations details added successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else{
                                    clickOnGuardian(Constant.INSERT_ID.toInt())
                                    Toast.makeText(
                                        applicationContext,
                                        "Registrations details added successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        } else {
                            hideProgressBar()
                            Toast.makeText(applicationContext, "Failed to save registrations details. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    hideProgressBar()
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                hideProgressBar()

                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, "Failed to save registrations details details. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clickOnGuardian(insertId: Int) {
        showProgressBar()
        val addUserDetails = Guardian(insertId)
        Log.i("TAG", "addCustomer: $addUserDetails")
        val call = RetrofitClient.api.clickOnGuardian(addUserDetails)
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(call: Call<HomeTutorModel>, response: Response<HomeTutorModel>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            hideProgressBar()
                            val intent = Intent(applicationContext, GuardianPackageActivity::class.java)
                            intent.putExtra("CARD_TYPE", Constant.GUARDIAN_CARD)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
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