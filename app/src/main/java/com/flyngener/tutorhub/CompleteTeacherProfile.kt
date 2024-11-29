package com.flyngener.tutorhub

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.databinding.ActivityCompleteTeacherProfileBinding
import com.google.gson.Gson
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
import java.util.Locale

class CompleteTeacherProfile : AppCompatActivity() {

    private lateinit var binding: ActivityCompleteTeacherProfileBinding
    private val calendar: Calendar = Calendar.getInstance()
    private val unit1Map = mutableMapOf<String, SelectUnitResult>()
    private val unit2Map = mutableMapOf<String, SelectUnitResult>()
    private val selectedUnit1List = mutableListOf<String>()
    private val selectedUnit2List = mutableListOf<String>()
    private var unitId1 = ""
    private var unitName1 = ""
    private var unitId2 = ""
    private var unitName2 = ""
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    private val FILE_REQUEST_CODE = 100
    private var imageUri: Uri ? = null
    private lateinit var selectUnit: List<SelectUnitResult>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteTeacherProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSelectUnitList()
        getCompleteTeacherProfile(Constant.INSERT_ID.toInt())
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.etSelectGender.setOnClickListener {
            showGenderOptionsDialog()
        }
        binding.tvCalender.setOnClickListener {
            openDatePickerDialog()
        }
        binding.etSelectStartDate.setOnClickListener {
            openStartDatePickerDialog()
        }
        binding.etSelectYourImage.setOnClickListener {
            openGallery()
        }

        binding.btnCompleteProfile.setOnClickListener {

            val etNameSelect = binding.etNameSelect.text.toString()
            val etSelectGender = binding.etSelectGender.text.toString()
            val tvCalender = binding.tvCalender.text.toString()
            val etAgeNo = binding.etAgeNo.text.toString()
            val address = binding.etRequirements.text.toString()
            val etAppointmentLocation = binding.etAppointmentLocation.text.toString()
            val amount = binding.etEnterAmount.text.toString()
            val etSelectFeesDate = binding.etSelectFeesDate.text.toString()
            val etSelectStartDate = binding.etSelectStartDate.text.toString()

            val unitIds = listOf(unitId1, unitId2).filter { it.isNotEmpty() }
            val unitNames = listOf(unitName1, unitName2).filter { it.isNotEmpty() }
            val subUnits1 = selectedUnit1List.map { it }
            val subUnits2 = selectedUnit2List.map { it }
            val subUnits = subUnits1 + subUnits2

            val gson = Gson()
            var unit1DetailJson = ""
            unit1DetailJson = gson.toJson(Constant.unitDetailsList)

            if (etNameSelect.isEmpty() || etSelectGender.isEmpty() || tvCalender.isEmpty() || etAgeNo.isEmpty() ||
                address.isEmpty() || etAppointmentLocation.isEmpty() || amount.isEmpty() || etSelectFeesDate.isEmpty() ||
                etSelectStartDate.isEmpty()) {

                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
            else {
                teacherCompleteProfile(
                    Constant.INSERT_ID,
                    etNameSelect,
                    etSelectGender,
                    tvCalender,
                    etAgeNo,
                    address,
                    etAppointmentLocation,
                    amount,
                    etSelectFeesDate,
                    imageUri,
                    etSelectStartDate,
                    unit1DetailJson)

                Log.d("FieldValues", "INSERT_ID: ${Constant.INSERT_ID}")
                Log.d("FieldValues", "Name: $etNameSelect")
                Log.d("FieldValues", "Gender: $etSelectGender")
                Log.d("FieldValues", "DOB: $tvCalender")
                Log.d("FieldValues", "Age: $etAgeNo")
                Log.d("FieldValues", "Address: $address")
                Log.d("FieldValues", "Appointment Location: $etAppointmentLocation")
                Log.d("FieldValues", "Amount: $amount")
                Log.d("FieldValues", "Fees Date: $etSelectFeesDate")
                Log.d("FieldValues", "Image URI: $imageUri")
                Log.d("FieldValues", "Start Date: $etSelectStartDate")
                Log.d("FieldValues", "Unit IDs: $unitIds")
                Log.d("FieldValues", "Unit Names: $unitNames")
                Log.d("FieldValues", "Sub Units: $subUnits")
                Log.d("FieldValues", "unitDetailsList: $unit1DetailJson")
            }


        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed to open the gallery
                    openGallery()
                } else {
                    // Permission denied, show a message or take appropriate action
                    Toast.makeText(this, "Permission denied. To use this feature, please grant the permission in App Settings.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openGallery() {
        // Check if permission is not granted
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
                // Permission is already granted, open the gallery
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
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                )
            }

            else {
                // Permission is already granted, open the gallery
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
                    .into(binding.image)

                binding.image.visibility = View.VISIBLE
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

    private fun showGenderOptionsDialog() {
        val genderOptions = arrayOf("Male", "Female", "Others")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Gender")
        builder.setItems(genderOptions) { _, which ->
            val selectedGender = genderOptions[which]
            binding.etSelectGender.text = selectedGender
        }

        val dialog = builder.create()
        dialog.show()
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

    private fun updateDateField() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.tvCalender.text = sdf.format(calendar.time)

        val age = calculateAge(calendar)
        binding.etAgeNo.text = Editable.Factory.getInstance().newEditable(age.toString())
    }

    private fun calculateAge(dateOfBirth: Calendar): Int {
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dateOfBirth.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
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
                    binding.rvUnit1.adapter = selectBoardAdapter
                    binding.rvUnit1.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
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

    private fun createImagePart(uri: Uri?, partName: String): MultipartBody.Part? {
        uri?.let {
            val file = File(getPath(uri).toString())
            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(partName, file.name, requestFile)
        }
        return null
    }

    private fun updateStartDateField() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.etSelectStartDate.text = sdf.format(calendar.time)
    }
    private fun teacherCompleteProfile(teacher_id: String, full_name: String, gender: String, dob: String, age: String, address: String, appointment_date: String,
                                  fees_amount: String, fees_c_date: String, upload_image: Uri?, start_date: String, unitIds: String
    ) {
        showProgressBar()
        val teacherIdMultiPart: RequestBody = teacher_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val nameMultipartBody: RequestBody = full_name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val genderMultipartBody: RequestBody = gender.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val dobMultipartBody: RequestBody = dob.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val ageMultipartBody: RequestBody = age.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val addressMultipartBody: RequestBody = address.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val locationMultipartBody: RequestBody = appointment_date.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val amountMultipartBody: RequestBody = fees_amount.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val feesDateMultipartBody: RequestBody = fees_c_date.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val startDateMultipartBody: RequestBody = start_date.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val unitIdsMultipartBody: RequestBody = unitIds.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val imagePart = createImagePart(upload_image, "upload_image")

        val apiService = RetrofitClient.retrofit.create(HomeTutorApi::class.java)

//        if (imagePart == null) {
//            hideProgressBar()
//            Toast.makeText(this, "Please select all required images.", Toast.LENGTH_SHORT).show()
//            return
//        }

        val call = apiService.teacherCompleteProfile(teacherIdMultiPart, nameMultipartBody, genderMultipartBody, dobMultipartBody,
            ageMultipartBody, addressMultipartBody, locationMultipartBody, amountMultipartBody, feesDateMultipartBody, imagePart, startDateMultipartBody, unitIdsMultipartBody)

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
                                this@CompleteTeacherProfile,
                                "Profile details added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(applicationContext, TeacherMainActivity::class.java)
                            startActivity(intent)
                        } else {
                            hideProgressBar()
                            Toast.makeText(
                                this@CompleteTeacherProfile,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(
                        this@CompleteTeacherProfile,
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
                    this@CompleteTeacherProfile,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("KycActivity", "API call failure", t)
            }
        })

    }

    private fun getCompleteTeacherProfile(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getCompleteTeacherProfile(id)
        call.enqueue(object : Callback<CompleteProfileDetailsResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<CompleteProfileDetailsResponse>, response: Response<CompleteProfileDetailsResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    binding.etNameSelect.setText(response.body()?.result?.get(0)?.full_name ?: "")
                    binding.etSelectGender.text = response.body()?.result?.get(0)?.gender ?: ""
                    binding.tvCalender.text = response.body()?.result?.get(0)?.dob ?: ""
                    binding.etAgeNo.setText(response.body()?.result?.get(0)?.age ?: "")
                    binding.etRequirements.setText(response.body()?.result?.get(0)?.address ?: "")
                    binding.etAppointmentLocation.setText(response.body()?.result?.get(0)?.appointment_date ?: "")
                    binding.etEnterAmount.setText(response.body()?.result?.get(0)?.fees_amount ?: "")
                    binding.etSelectFeesDate.setText(response.body()?.result?.get(0)?.fees_c_date ?: "")
                    binding.etSelectStartDate.text = response.body()?.result?.get(0)?.start_date ?: ""

                    binding.image.visibility = View.VISIBLE
                    Glide.with(applicationContext)
                        .load(response.body()?.result?.get(0)?.upload_image)
                        .into(binding.image)

                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<CompleteProfileDetailsResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
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
}