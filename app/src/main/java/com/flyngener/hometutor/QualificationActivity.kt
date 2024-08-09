package com.flyngener.hometutor

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.flyngener.hometutor.databinding.ActivityQualificationAddressBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class QualificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQualificationAddressBinding
    private val calendar: Calendar = Calendar.getInstance()
    private val cityNames = mutableListOf<String>()
    private val stateNames = mutableListOf<String>()
    private var selectedCityId: String? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQualificationAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etGender.setOnClickListener {
            showGenderOptionsDialog()
        }
        binding.btnNext.setOnClickListener {
            val etQualification = binding.etQualification.text.toString()
            val etExperience = binding.etExperience.text.toString()
            val etDob = binding.etDob.text.toString()
            val etAge = binding.etAge.text.toString()
            val etGender = binding.etGender.text.toString()
            val etAbout = binding.etAbout.text.toString()
            val etState = binding.etState.text.toString()
            val etCity = binding.etCity.text.toString()
            val etPincode = binding.etPincode.text.toString()
            val etFullAddress = binding.etFullAddress.text.toString()
            if (etQualification.isEmpty() || etExperience.isEmpty() || etDob.isEmpty() || etAge.isEmpty() || etGender.isEmpty() || etAbout.isEmpty() || etState.isEmpty() || etCity.isEmpty() ||
                etPincode.isEmpty() || etFullAddress.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                teacherRegistrationStep2(
                    Constant.INSERT_ID,
                    etQualification,
                    etExperience,
                    etDob,
                    etAge,
                    etGender,
                    etAbout,
                    etState,
                    etCity,
                    etPincode,
                    etFullAddress
                )
            }
        }

        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, LogInHomeTutor::class.java)
            startActivity(intent)
        }

        binding.etDob.setOnClickListener {
            openDatePickerDialog()
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

    private fun showGenderOptionsDialog() {
        val genderOptions = arrayOf("Male", "Female", "Others")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Gender")
        builder.setItems(genderOptions) { _, which ->
            val selectedGender = genderOptions[which]
            binding.etGender.text = selectedGender
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun teacherRegistrationStep2(
        insert_id: String,
        qualification: String,
        experience: String,
        dob: String,
        age: String,
        gender: String,
        about: String,
        state: String,
        city: String,
        pincode: String,
        full_address: String
    ) {
        val addUserDetails = UserDetails(
            insert_id,
            qualification,
            experience,
            dob,
            age,
            gender,
            about,
            state,
            city,
            pincode,
            full_address
        )
        Log.i("TAG", "addCustomer: "+ addUserDetails)
        val call = RetrofitClient.api.teacherRegistrationStep2(addUserDetails)
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(call: Call<HomeTutorModel>, response: Response<HomeTutorModel>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            Toast.makeText(applicationContext, "Registrations details added successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, ServicesActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "Failed to save registrations details. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, "Failed to save registrations details details. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateDateField() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.etDob.text = sdf.format(calendar.time)

        val age = calculateAge(calendar)
        binding.etAge.text = Editable.Factory.getInstance().newEditable(age.toString())
    }

    private fun calculateAge(dateOfBirth: Calendar): Int {
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dateOfBirth.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    private fun openDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,R.style.DialogTheme,
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

    private fun handleCityListResponse(result: List<City>) {

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cityNames)
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
}