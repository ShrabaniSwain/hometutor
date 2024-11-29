package com.flyngener.tutorhub

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.tutorhub.databinding.ActivitySupportBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class SupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportBinding
    private lateinit var result: List<SupportPageResult>
    val servicesNames = mutableListOf<String>()
    private val servicesMap = mutableMapOf<String, Appointment>()
    var appointmentId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSupport()
        getTeacherProfile(Constant.INSERT_ID.toInt())
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val servicesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, servicesNames)
        binding.etSelectAppointmentId.setAdapter(servicesAdapter)

        binding.etSelectAppointmentId.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etSelectAppointmentId.showDropDown()
            }
        }

        binding.etSelectAppointmentId.setOnClickListener {
            binding.etSelectAppointmentId.showDropDown()
        }

        binding.etSelectAppointmentId.setOnItemClickListener { parent, _, position, _ ->
            val selectedServiceName = parent.getItemAtPosition(position) as String
            val selectedService = servicesMap[selectedServiceName]
            binding.etSelectAppointmentId.setText(selectedServiceName)
        }

        binding.btnSendEnquiry.setOnClickListener {
            val subject = binding.etSubjectSelect.text.toString().trim()
            val requirements = binding.etRequirements.text.toString().trim()
            appointmentId = binding.etSelectAppointmentId.text.toString()

            if (subject.isEmpty() || requirements.isEmpty()) {
                Toast.makeText(this, "Please fill in both the subject and requirements.", Toast.LENGTH_SHORT).show()
            } else {
                val guardianSubject = GuardianSubject(Constant.INSERT_ID, subject, requirements, appointmentId)
                guardianSupportPackageForm(guardianSubject)
                Log.i("TAG", "onCreate: $guardianSubject")
            }
        }


    }

    private fun getTeacherProfile(id: Int) {
        val call = RetrofitClient.api.getAppointmentList(id)
        call.enqueue(object : Callback<AppointmentResponse> {
            override fun onResponse(call: Call<AppointmentResponse>, response: Response<AppointmentResponse>) {
                Log.i("TAG", "onResponse: " + call + "response : " + response)
                if (response.isSuccessful) {
                    val resultList = response.body()?.result
                    resultList?.let { appointments ->
                        servicesNames.clear()
                        servicesMap.clear()
                        appointments.forEach { appointment ->
                            servicesNames.add(appointment.appointment_id)
                            servicesMap[appointment.appointment_id] = appointment
                        }
                    }
                } else {
                    Toast.makeText(this@SupportActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AppointmentResponse>, t: Throwable) {
                Toast.makeText(this@SupportActivity, t.message.orEmpty(), Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun getSupport() {
        showProgressBar()
        val call = RetrofitClient.api.getGuardianSupport(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<SupportPageResponse> {
            override fun onResponse(call: Call<SupportPageResponse>, response: Response<SupportPageResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    result = response.body()?.result ?: emptyList()

                        val supportDeatilsAdapter =
                            SupportDeatilsAdapter(applicationContext, result)
                        binding.rvSupport.layoutManager = LinearLayoutManager(
                            applicationContext,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        binding.rvSupport.adapter = supportDeatilsAdapter

                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<SupportPageResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    private fun guardianSupportPackageForm(guardianSubject: GuardianSubject) {
        showProgressBar()
        val call = RetrofitClient.api.guardianSupportPackageForm(guardianSubject)
        call.enqueue(object : Callback<SupportRequestResponse> {
            override fun onResponse(call: Call<SupportRequestResponse>, response: Response<SupportRequestResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    Log.i("TAG", "onResponse: "+response.body())
                        hideProgressBar()
                    Toast.makeText(applicationContext, "Support details added successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, SupportActivity::class.java)
                        startActivity(intent)
                        finish()
                } else {
                    hideProgressBar()
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SupportRequestResponse>, t: Throwable) {
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