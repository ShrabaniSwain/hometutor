package com.flyngener.hometutor

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.GridLayoutManager
import com.flyngener.hometutor.databinding.ActivityServicesBinding
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

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, LogInHomeTutor::class.java)
            startActivity(intent)
        }

        val servicesSelectedAdapter = ServicesSelectedAdapter(selectedServicesList)
        binding.rvServices.adapter = servicesSelectedAdapter
        binding.rvServices.layoutManager =
            GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)

        val servicesNames = mutableListOf<String>() // Use String list
        val servicesAdapter = ArrayAdapter(this, R.layout.simple_list_item_1, servicesNames)
        binding.etServices.setAdapter(servicesAdapter)

        val unit1Names = mutableListOf<String>()
        val unit1adapter = ArrayAdapter(this, R.layout.simple_list_item_1, unit1Names)
        binding.etUnit1.setAdapter(unit1adapter)

        val unit2Names = mutableListOf<String>()
        val unit2adapter = ArrayAdapter(this, R.layout.simple_list_item_1, unit2Names)
        binding.etUnit2.setAdapter(unit2adapter)


        val unit1SelectedAdapter = Unit1SelectedAdapter(selectedUnit1List)
        binding.rvUnit1.adapter = unit1SelectedAdapter
        binding.rvUnit1.layoutManager =
            GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)

        val unit2SelectedAdapter = Unit2SelectedAdapter(selectedUnit2List)
        binding.rvUnit2.adapter = unit2SelectedAdapter
        binding.rvUnit2.layoutManager =
            GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)


        binding.btnNext.setOnClickListener {
            val etCaptureArea = binding.etCaptureArea.text.toString()
            if (binding.rvUnit1.isNotEmpty()) {
                unitId1 = "1"
                unitName1 = "Unit1"
            }

            if (binding.rvUnit2.isNotEmpty()) {
                unitId2 = "2"
                unitName2 = "Unit2"
            }

            if (selectedServicesList.isEmpty() || etCaptureArea.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {

                val serviceProfileIds = selectedServicesList.map { it.id }
                val serviceProfileNames = selectedServicesList.map { it.serviceProfileName }

                val unitIds = listOf(unitId1, unitId2).filter { it.isNotEmpty() }
                val unitNames = listOf(unitName1, unitName2).filter { it.isNotEmpty() }
                val subUnits1 = selectedUnit1List.map { it }
                val subUnits2 = selectedUnit2List.map { it }
                val subUnits = subUnits1 + subUnits2

                teacherServiceRegistration(
                    Constant.INSERT_ID,
                    serviceProfileIds,
                    unitIds,
                    serviceProfileNames,
                    unitNames,
                    subUnits,
                    etCaptureArea
                )
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

        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getUnit1List()
            }

            if (response.isSuccessful) {
                val customerDetailsResponse = response.body()?.result
                customerDetailsResponse?.let {
                    handleUnitListResponse(it, unit1Names, unit2Names, unit1adapter, unit2adapter)
                }
            } else {
                Log.e("TAG", "API Call failed with error code: ${response.code()}")
            }
        }


        binding.etServices.setOnItemClickListener { parent, _, position, _ ->
            val selectedServiceName = parent.getItemAtPosition(position) as String
            val selectedService = servicesMap[selectedServiceName]
            selectedService?.let {
                servicesSelectedAdapter.addService(it)
            }
        }


        binding.etUnit1.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = unit1adapter.getItem(position)
            selectedItem?.let {
                unit1SelectedAdapter.addService(it)
            }
        }

        binding.etUnit2.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = unit2adapter.getItem(position)
            selectedItem?.let {
                unit2SelectedAdapter.addService(it)
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
        unit_ids: List<String>,
        service_profile_name: List<String>,
        unit_name: List<String>, subUnit: List<String>,
        capture_area: String
    ) {

        val serviceIdsString = service_profile_ids.joinToString(separator = ",")
        val serviceNamesString = service_profile_name.joinToString(separator = ",")
        val unitIdsString = unit_ids.joinToString(separator = ",")
        val unitNamesString = unit_name.joinToString(separator = ",")
        val subNamesString = subUnit.joinToString(separator = ",")

        val addUserDetails = ServiceRegistrationModel(
            insert_id,
            serviceIdsString,
            unitIdsString,
            serviceNamesString,
            unitNamesString,
            subNamesString,
            capture_area
        )
        Log.i("TAG", "addCustomer: " + addUserDetails)
        val call = RetrofitClient.api.teacherServiceRegistration(addUserDetails)
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

            override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    applicationContext,
                    "Failed to send registrations details details. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}