package com.flyngener.tutorhub

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.Constant.SERVICE_NAME
import com.flyngener.tutorhub.databinding.ActivityEditProflieBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditProflieActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProflieBinding
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    private val FILE_REQUEST_CODE = 100
    private lateinit var imageUri: Uri
    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var selectUnit: List<SelectUnitResult>
    private lateinit var selectServiceProfile: List<ServiceProfile>
    private val selectedServicesList = mutableListOf<ServiceProfile>()
    private val servicesMap = mutableMapOf<String, ServiceProfile>()
    var pincode = ""
    var state = ""
    var city = ""
    var fullAddress = ""

    private val requestLocationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProflieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getSelectUnitList()
        getTeacherProfile(Constant.INSERT_ID.toInt())
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setupConstraintToggle(binding.tvPersonalDetailsClose, binding.personalDetailsConstraint)
        setupConstraintToggle(binding.tvAddressDetailsClose, binding.addressDetailsConstraint)
        setupConstraintToggle(binding.tvServiceDetailsClose, binding.servicesDetailsConstraint)
        setupConstraintToggle(binding.tvKycClose, binding.kycDetailsConstraint)
        setupConstraintToggle(binding.tvBankDetailsClose, binding.bankDetailsConstraint)

        binding.personalDetailsConstraint.setOnClickListener {
            hideAndShow(binding.personalDetailsConstraint, binding.tvPersonalDetailsClose)
        }
        binding.addressDetailsConstraint.setOnClickListener {
            hideAndShow(binding.addressDetailsConstraint, binding.tvAddressDetailsClose)
        }
        binding.servicesDetailsConstraint.setOnClickListener {
            hideAndShow(binding.servicesDetailsConstraint, binding.tvServiceDetailsClose)
        }
        binding.kycDetailsConstraint.setOnClickListener {
            hideAndShow(binding.kycDetailsConstraint, binding.tvKycClose)
        }
        binding.bankDetailsConstraint.setOnClickListener {
            hideAndShow(binding.bankDetailsConstraint, binding.tvBankDetailsClose)
        }
        binding.imageView.setOnClickListener {
            openGallery()
        }
        binding.etDObSelect.setOnClickListener {
            openStartDatePickerDialog()
        }
        val servicesSelectedAdapter = ServicesSelectedAdapter(selectedServicesList)
        binding.rvServices.adapter = servicesSelectedAdapter
        binding.rvServices.layoutManager =
            GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)

        val servicesNames = mutableListOf<String>()
        val servicesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, servicesNames)
        binding.tvServicesNameSelect.setText(SERVICE_NAME)

        binding.tvServicesNameSelect.setAdapter(servicesAdapter)

        binding.tvServicesNameSelect.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tvServicesNameSelect.showDropDown()
            }
        }
        binding.tvServicesNameSelect.setOnClickListener {
            binding.tvServicesNameSelect.showDropDown()
        }
        binding.tvServicesNameSelect.setOnItemClickListener { parent, _, position, _ ->
            val selectedServiceName = parent.getItemAtPosition(position) as String
            val selectedService = servicesMap[selectedServiceName]
            selectedService?.let {
                servicesSelectedAdapter.addService(it)
            }
        }

        binding.btnSaveService.setOnClickListener {
            val serviceResponse = ServiceResponse(Constant.INSERT_ID,selectedServicesList, Constant.unitDetailsList)
            editTeacherProfileService(serviceResponse)
            Log.i("TAG", "serviceData: $serviceResponse")
        }
        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etNameSelect.text.toString()
            val email = binding.etEmailIDSelect.text.toString()
            val phoneNumber = binding.etPhoneNumberSelect.text.toString()
            val gender = binding.etGenderSelect.text.toString()
            val qualification = binding.etQualificationSelect.text.toString()
            val experience = binding.etExperienceSelect.text.toString()
            val dob = binding.etDObSelect.text.toString()
            val age = binding.etAgeSelect.text.toString()
            val about = binding.etAboutSelect.text.toString()
            if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || gender.isEmpty() || qualification.isEmpty() ||
                experience.isEmpty() || dob.isEmpty() || age.isEmpty() || about.isEmpty()) {
                Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_SHORT).show()
            }
            if (::imageUri.isInitialized) {
                changeTeacherProfileImage(Constant.INSERT_ID, imageUri)
            }
            val profileResponse = ProfileResponse(Constant.INSERT_ID, name, email, phoneNumber, gender, qualification, experience, dob, age, about)
            editTeacherProfile(profileResponse)
            Log.i("TAG", "onCreate: $profileResponse")
            Log.d("ProfileData", "Name: $name, Email: $email, phoneNumber Number: $phoneNumber, Gender: $gender, Qualification: $qualification, Experience: $experience, DOB: $dob, Age: $age, About: $about")
        }

        binding.tvAddressNameSelect.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        binding.btnSaveAddress.setOnClickListener {
            if (binding.tvAddressNameSelect.text.toString().isEmpty()){
                Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_SHORT).show()
            }
            else {
                val addressResponse =
                    AddressResponse(Constant.INSERT_ID, city, state, pincode, binding.tvAddressNameSelect.text.toString())
                editTeacherProfileAddress(addressResponse)
                Log.i("AddressData", "onCreate: $addressResponse")
            }
        }

        binding.btnSaveKyc.setOnClickListener {
            val etVotarIdNumber = binding.etVotarIdNumber.text.toString()
            val etAadharIdNo = binding.etAadharIdNo.text.toString()
            val etPanNumberNo = binding.etPanNumberNo.text.toString()
            val etDrivingLicenseNo = binding.etDrivingLicenseNo.text.toString()

            if (etVotarIdNumber.isEmpty() || etAadharIdNo.isEmpty() || etPanNumberNo.isEmpty() || etDrivingLicenseNo.isEmpty()) {
                Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_SHORT).show()
            }
            else {
                val documentDetails = DocumentDetails(
                    Constant.INSERT_ID,
                    etVotarIdNumber,
                    "",
                    etAadharIdNo,
                    "",
                    etPanNumberNo,
                    "",
                    "",
                    etDrivingLicenseNo,
                    ""
                )
                editTeacherProfileKyc(documentDetails)
                Log.i("TAG", "KycData: $documentDetails")
            }
        }

        binding.btnSaveBank.setOnClickListener {
            val etBankName = binding.etBankName.text.toString()
            val etAccountHolderName = binding.etAccountHolderName.text.toString()
            val etBankAccountNumber = binding.etBankAccountNumber.text.toString()
            val etIFSCCode = binding.etIFSCCode.text.toString()
            val etBranchName = binding.etBranchName.text.toString()

            if (etBankName.isEmpty() && etAccountHolderName.isEmpty() && etBankAccountNumber.isEmpty() && etIFSCCode.isEmpty() && etBranchName.isEmpty()) {
                Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_SHORT).show()
            }
            else{
                val bankDetails = BankDetails(Constant.INSERT_ID, etBankName, etBranchName, etBankAccountNumber,etAccountHolderName,etIFSCCode,"")
                editTeacherProfileBank(bankDetails)
                Log.i("TAG", "bankdata: " + bankDetails)
                Log.d("BankDetails", "Bank Name: $etBankName, Account Holder Name: $etAccountHolderName, Bank Account Number: $etBankAccountNumber, IFSC Code: $etIFSCCode, Branch Name: $etBranchName")
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
                    this@EditProflieActivity,
                    "Failed to fetch services",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getTeacherProfile(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getTeacherProfile(id)
        call.enqueue(object : Callback<TeacherProfileDetails> {
            override fun onResponse(call: Call< TeacherProfileDetails>, response: Response<TeacherProfileDetails>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    binding.etNameSelect.setText(response.body()?.result?.get(0)?.full_name ?: "")
                    SERVICE_NAME = response.body()?.result?.get(0)?.service_profile_name ?: ""
                    binding.etPhoneNumberSelect.setText(response.body()?.result?.get(0)?.mobile_number ?: "")
                    binding.etEmailIDSelect.setText(response.body()?.result?.get(0)?.email_id ?: "")
                    binding.etGenderSelect.setText(response.body()?.result?.get(0)?.gender ?: "")
                    binding.etQualificationSelect.setText(response.body()?.result?.get(0)?.qualification ?: "")
                    binding.etExperienceSelect.setText(response.body()?.result?.get(0)?.experience ?: "")
                    binding.etAgeSelect.setText(response.body()?.result?.get(0)?.age ?: "")
                    binding.etAboutSelect.setText(response.body()?.result?.get(0)?.about ?: "")
                    val fullAddress = response.body()?.result?.get(0)?.full_address
                    binding.tvAddressNameSelect.setText(fullAddress)
                    binding.etVotarIdNumber.setText(response.body()?.result?.get(0)?.voter_id ?: "")
                    binding.etAadharIdNo.setText(response.body()?.result?.get(0)?.aadhar_id ?: "")
                    binding.etPanNumberNo.setText(response.body()?.result?.get(0)?.pan_no ?: "")
                    binding.etDrivingLicenseNo.setText(response.body()?.result?.get(0)?.driving_license_no ?: "")
                    binding.etBankName.setText(response.body()?.result?.get(0)?.bank_name ?: "")
                    binding.etAccountHolderName.setText(response.body()?.result?.get(0)?.account_name ?: "")
                    binding.etBankAccountNumber.setText(response.body()?.result?.get(0)?.account_number ?: "")
                    binding.etIFSCCode.setText(response.body()?.result?.get(0)?.ifsc_code ?: "")
                    binding.etBranchName.setText(response.body()?.result?.get(0)?.branch_name ?: "")
                    binding.etDObSelect.setText(response.body()?.result?.get(0)?.dob ?: "")
                    Glide.with(applicationContext)
                        .load(response.body()?.result?.get(0)?.profile_image)
                        .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                        .into(binding.imageView)
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TeacherProfileDetails>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                fetchAddressFromLocation(location.latitude, location.longitude)
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                pincode = address.postalCode ?: ""
                state = address.adminArea ?: ""
                city = address.locality ?: ""
                fullAddress = address.getAddressLine(0)
                val fullAddressInput = buildString {
                    append(address.getAddressLine(0))
                }
                binding.tvAddressNameSelect.setText(fullAddressInput)
            } else {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
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


    private fun setupConstraintToggle(textView: TextView, constraintLayout: ConstraintLayout) {
        textView.setOnClickListener {
            val isVisible = constraintLayout.visibility == View.VISIBLE
            hideAllConstraintsExcept(constraintLayout)
            if (!isVisible) {
                textView.visibility = View.GONE
                constraintLayout.visibility = View.VISIBLE
            } else {
                textView.visibility = View.VISIBLE
                constraintLayout.visibility = View.GONE
            }
        }
    }

    private fun hideAllConstraintsExcept(exceptConstraint: ConstraintLayout) {
        val constraints = listOf(
            binding.personalDetailsConstraint,
            binding.addressDetailsConstraint,
            binding.servicesDetailsConstraint,
            binding.kycDetailsConstraint,
            binding.bankDetailsConstraint
        )

        for (constraint in constraints) {
            if (constraint != exceptConstraint && constraint.visibility == View.VISIBLE) {
                constraint.visibility = View.GONE
                when (constraint) {
                    binding.personalDetailsConstraint -> binding.tvPersonalDetailsClose.visibility = View.VISIBLE
                    binding.addressDetailsConstraint -> binding.tvAddressDetailsClose.visibility = View.VISIBLE
                    binding.servicesDetailsConstraint -> binding.tvServiceDetailsClose.visibility = View.VISIBLE
                    binding.kycDetailsConstraint -> binding.tvKycClose.visibility = View.VISIBLE
                    binding.bankDetailsConstraint -> binding.tvBankDetailsClose.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun openStartDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,R.style.DialogTheme,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                updateStartDateField()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    private fun updateStartDateField() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDate = sdf.format(calendar.time)
        binding.etDObSelect.text = selectedDate
        calculateAndSetAge(calendar.time)
    }

    private fun calculateAndSetAge(dateOfBirth: Date) {
        val dobCalendar = Calendar.getInstance().apply { time = dateOfBirth }
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        binding.etAgeSelect.setText(age.toString())
    }
    private fun hideAndShow(constraintLayout: ConstraintLayout, textView: TextView) {
        constraintLayout.visibility = View.GONE
        textView.visibility = View.VISIBLE
    }

    private fun openGallery() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 ){
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                )
            }
            else {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, FILE_REQUEST_CODE)
            }
        }
        else{
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                )
            }

            else {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, FILE_REQUEST_CODE)
            }

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri? = data?.data
            selectedImage?.let {
                imageUri = it
                Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                    .into(binding.imageView)

            }
        }
    }
    private fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            cursor.moveToFirst()
            val imagePath = cursor.getString(column_index)
            cursor.close()
            return imagePath
        }
        return null
    }

    private fun changeTeacherProfileImage(teacher_id: String, profileImage: Uri?
    ) {
        showProgressBar()
        val teacherIdMultiPart: RequestBody = teacher_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val imagePart = createImagePart(profileImage, "profile_image")

        val apiService = RetrofitClient.retrofit.create(HomeTutorApi::class.java)

        if (imagePart == null) {
            hideProgressBar()
            Toast.makeText(this, "Please select all required images.", Toast.LENGTH_SHORT).show()
            return
        }

        val call = apiService.changeTeacherProfileImage(teacherIdMultiPart, imagePart)

        Log.i("KycActivity", "kycRegistration: $call")
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(call: Call<HomeTutorModel>, response: Response<HomeTutorModel>) {
                Log.i("KycActivity", "onResponse called")
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.i("KycActivity", "onResponse: $apiResponse")
                    apiResponse?.let {
                        if (it.isSuccess) {
                            hideProgressBar()
                            Toast.makeText(
                                this@EditProflieActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@EditProflieActivity, EditProflieActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            hideProgressBar()
                            Toast.makeText(
                                this@EditProflieActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(
                        this@EditProflieActivity,
                        "Response not successful: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("KycActivity", "Response error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                Log.i("KycActivity", "onFailure called")
                hideProgressBar()
                Toast.makeText(
                    this@EditProflieActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("KycActivity", "API call failure", t)
            }
        })

    }

    private fun createImagePart(uri: Uri?, partName: String): MultipartBody.Part? {
        uri?.let {
            val file = File(getPath(uri).toString())
            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(partName, file.name, requestFile)
        }
        return null
    }

    private fun editTeacherProfile(profileResponse: ProfileResponse) {
        showProgressBar()
        val call = RetrofitClient.api.editTeacherProfile(profileResponse)
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            val intent = Intent(this@EditProflieActivity, EditProflieActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
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
    private fun editTeacherProfileAddress(addressResponse: AddressResponse) {
        showProgressBar()
        val call = RetrofitClient.api.editTeacherProfileAddress(addressResponse)
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            val intent = Intent(this@EditProflieActivity, EditProflieActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
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
    private fun editTeacherProfileKyc(documentDetails: DocumentDetails) {
        showProgressBar()
        val call = RetrofitClient.api.editTeacherProfileKyc(documentDetails)
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            val intent = Intent(this@EditProflieActivity, EditProflieActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
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
    private fun editTeacherProfileBank(bankDetails: BankDetails) {
        showProgressBar()
        val call = RetrofitClient.api.editTeacherProfileBank(bankDetails)
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            val intent = Intent(this@EditProflieActivity, EditProflieActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
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

                    val selectBoardAdapter = UnitAdapter(selectUnit, applicationContext)
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

    private fun editTeacherProfileService(serviceResponse: ServiceResponse) {
        showProgressBar()
        val call = RetrofitClient.api.editTeacherProfileService(serviceResponse)
        val gson = Gson()
        Log.i("Request Body", gson.toJson(serviceResponse))

        call.enqueue(object : Callback<TeacherServiceResponse> {
            override fun onResponse(
                call: Call<TeacherServiceResponse>,
                response: Response<TeacherServiceResponse>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            val intent = Intent(this@EditProflieActivity, EditProflieActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TeacherServiceResponse>, t: Throwable) {
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