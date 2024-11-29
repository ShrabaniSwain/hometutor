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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.tutorhub.databinding.ActivityServicesBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServicesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServicesBinding
    private val selectedServicesList = mutableListOf<ServiceProfile>()
    private val servicesMap = mutableMapOf<String, ServiceProfile>()
    private val selectedUnit1List = mutableListOf<String>()
    private val selectedUnit2List = mutableListOf<String>()
    private val unit1Map = mutableMapOf<String, SelectUnitResult>()
    private val unit2Map = mutableMapOf<String, SelectUnitResult>()
    private var unitId1 = ""
    private var unitName1 = ""
    private var unitId2 = ""
    private var unitName2 = ""
    private lateinit var selectUnit: List<SelectUnitResult>

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPrefHelper = SharedPreferenceHelper(this)
        if (sharedPrefHelper.isLoggedIn()) {
            binding.alreadySignIn.visibility = View.INVISIBLE
        }
        else{
            binding.alreadySignIn.visibility = View.VISIBLE
        }

        getSelectUnitList()
        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, LogInHomeTutor::class.java)
            startActivity(intent)
        }

        val servicesSelectedAdapter = ServicesSelectedAdapter(selectedServicesList)
        binding.rvServices.adapter = servicesSelectedAdapter
        binding.rvServices.layoutManager =
            GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)

        val servicesNames = mutableListOf<String>() // Use String list
        val servicesAdapter = ArrayAdapter(this, R.layout.simple_list_item_1, servicesNames)
        binding.etServices.setAdapter(servicesAdapter)

        binding.etServices.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etServices.showDropDown() // Show dropdown on focus
            }
        }
        binding.etServices.setOnClickListener {
            binding.etServices.showDropDown()
        }


        binding.btnNext.setOnClickListener {
            val etCaptureArea = binding.etCaptureArea.text.toString()
            if (selectedServicesList.isEmpty() || etCaptureArea.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {

                val serviceProfileIds = selectedServicesList.map { it.id}
                val serviceProfileNames = selectedServicesList.map { it.serviceProfileName }

                val unitIds = listOf(unitId1, unitId2).filter { it.isNotEmpty() }
                val unitNames = listOf(unitName1, unitName2).filter { it.isNotEmpty() }
                val subUnits1 = selectedUnit1List.map { it }
                val subUnits2 = selectedUnit2List.map { it }
                val subUnits = subUnits1 + subUnits2

                teacherServiceRegistration(
                    Constant.INSERT_ID,
                    serviceProfileIds,
                    serviceProfileNames,
                    Constant.unitDetailsList,
                    etCaptureArea
                )

                Log.d("TeacherServiceRegistration", "INSERT_ID: ${Constant.INSERT_ID}")
                Log.d("TeacherServiceRegistration", "Service Profile IDs: $serviceProfileIds")
                Log.d("TeacherServiceRegistration", "Service Profile Names: $serviceProfileNames")
                Log.d("TeacherServiceRegistration", "Unit Details List: ${Constant.unitDetailsList}")
                Log.d("TeacherServiceRegistration", "Capture Area: $etCaptureArea")
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getServiceList()
            }

            if (response.isSuccessful) {
                val serviceList = response.body()?.result
                serviceList?.let { handleServiceListResponse(it, servicesNames, servicesAdapter) }
            } else {
                Toast.makeText(
                    this@ServicesActivity,
                    "Failed to fetch services",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        binding.etServices.setOnItemClickListener { parent, _, position, _ ->
            val selectedServiceName = parent.getItemAtPosition(position) as String
            val selectedService = servicesMap[selectedServiceName]
            selectedService?.let {
                servicesSelectedAdapter.addService(it)
            }
        }
    }

    private fun handleServiceListResponse(
        result: List<ServiceProfile>,
        serviceNames: MutableList<String>,
        adapter: ArrayAdapter<String>
    ) {
        serviceNames.clear()
        servicesMap.clear()
        result.forEach { serviceProfile ->
            val serviceName = serviceProfile.serviceProfileName
            servicesMap[serviceName] = serviceProfile
            serviceNames.add(serviceName)
        }
        adapter.notifyDataSetChanged()
    }

    private fun getSelectUnitList() {
        showProgressBar()
        val call = RetrofitClient.api.getSelectUnitList()
        call.enqueue(object : Callback<SelectUnitResponse> {
            override fun onResponse(
                call: Call<SelectUnitResponse>,
                response: Response<SelectUnitResponse>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    selectUnit = response.body()?.result ?: emptyList()

                    val selectBoardAdapter = ServiceUnitAdapter(selectUnit, applicationContext)
                    binding.rvUnitList.adapter = selectBoardAdapter
                    binding.rvUnitList.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<SelectUnitResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }
    private fun handleUnitListResponse(
        result: List<SelectUnitResult>,
        unit1Names: MutableList<String>,
        unit2Names: MutableList<String>,
        adapter1: ArrayAdapter<String>,
        adapter2: ArrayAdapter<String>
    ) {
        unit1Names.clear()
        unit2Names.clear()

        result.forEach { unit ->
            when (unit.unit_name) {
                "Unit1" -> {
                    unit.sub_unit_name.forEach { subUnit ->
                        unit1Names.add(subUnit.value)
                        unit1Map[subUnit.value] = unit
                    }
                }

                "Unit2" -> {
                    unit.sub_unit_name.forEach { subUnit ->
                        unit2Names.add(subUnit.value)
                        unit2Map[subUnit.value] = unit
                    }
                }
            }
        }

        adapter1.notifyDataSetChanged()
        adapter2.notifyDataSetChanged()
    }


    private fun teacherServiceRegistration(
        insert_id: String,
        service_profile_ids: List<String>,
        service_profile_name: List<String>,
        unit_name: List<SelectUnitResult>,
        capture_area: String
    ) {

        val serviceIdsString = service_profile_ids.joinToString(separator = ",")
        val serviceNamesString = service_profile_name.joinToString(separator = ",")

        val addUserDetails = ServiceRegistrationModel(
            insert_id,
            service_profile_ids,
            service_profile_name,
            unit_name,
            capture_area
        )
        Log.i("TAG", "addCustomer: " + addUserDetails)
        val call = RetrofitClient.api.teacherServiceRegistration(addUserDetails)
        call.enqueue(object : Callback<ServiceRegistrationResponse> {
            override fun onResponse(
                call: Call<ServiceRegistrationResponse>,
                response: Response<ServiceRegistrationResponse>
            ) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            Toast.makeText(
                                applicationContext,
                                "Registrations details send successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(applicationContext, KycActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Failed to send registrations details. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ServiceRegistrationResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    applicationContext,
                    "Failed to send registrations details details. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
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