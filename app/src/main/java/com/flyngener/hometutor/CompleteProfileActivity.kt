package com.flyngener.hometutor

import android.Manifest
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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.hometutor.databinding.ActivityCompleteProfileBinding
import com.flyngener.hometutor.databinding.ActivityTeacherHomeBinding
import com.google.gson.annotations.SerializedName
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
import java.util.Locale

class CompleteProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompleteProfileBinding
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
    private lateinit var imageUri: Uri

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompleteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val unit1Names = mutableListOf<String>()
        val unit1adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, unit1Names)
        binding.etSelectUnit.setAdapter(unit1adapter)

        val unit2Names = mutableListOf<String>()
        val unit2adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, unit2Names)
        binding.etSelectUnit1.setAdapter(unit2adapter)


        val unit1SelectedAdapter = Unit1SelectedAdapter(selectedUnit1List)
        binding.rvUnit1.adapter = unit1SelectedAdapter
        binding.rvUnit1.layoutManager =
            GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)

        val unit2SelectedAdapter = Unit2SelectedAdapter(selectedUnit2List)
        binding.rvUnit2.adapter = unit2SelectedAdapter
        binding.rvUnit2.layoutManager =
            GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)

        binding.etSelectUnit.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = unit1adapter.getItem(position)
            selectedItem?.let {
                unit1SelectedAdapter.addService(it)
            }
        }

        binding.etSelectUnit1.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = unit2adapter.getItem(position)
            selectedItem?.let {
                unit2SelectedAdapter.addService(it)
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

        binding.btnCompleteProfile.setOnClickListener {
            if (binding.rvUnit1.isNotEmpty()) {
                unitId1 = "1"
                unitName1 = "Unit1"
            }

            if (binding.rvUnit2.isNotEmpty()) {
                unitId2 = "2"
                unitName2 = "Unit2"
            }
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

            if (etNameSelect.isEmpty() || etSelectGender.isEmpty() || tvCalender.isEmpty() || etAgeNo.isEmpty() ||
                address.isEmpty() || etAppointmentLocation.isEmpty() || amount.isEmpty() || etSelectFeesDate.isEmpty() ||
                etSelectStartDate.isEmpty()) {

                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
            else {
                teacherAddStudent(
                    Constant.INSERT_ID,
                    Constant.BOOKING_ID,
                    Constant.GUARDIAN_ID,
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
                    unitIds,
                    unitNames,
                    subUnits
                )

                Log.d("FieldValues", "INSERT_ID: ${Constant.INSERT_ID}")
                Log.d("FieldValues", "BOOKING_ID: ${Constant.BOOKING_ID}")
                Log.d("FieldValues", "GUARDIAN_ID: ${Constant.GUARDIAN_ID}")
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

    private fun teacherAddStudent( teacher_id: String, booking_id: String, guardian_id: String, student_name: String, gender: String, dob: String, age: String, address: String, appointment_location: String,
                                   fees_amount: String, fees_c_date: String, student_image: Uri?, start_date: String, unitIds: List<String>, unitName: List<String>, subUnit: List<String>
    ) {
        showProgressBar()
        val teacherIdMultiPart: RequestBody = teacher_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val bookingIdMultipartBody: RequestBody = booking_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val guardianIdMultipartBody: RequestBody = guardian_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val nameMultipartBody: RequestBody = student_name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val genderMultipartBody: RequestBody = gender.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val dobMultipartBody: RequestBody = dob.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val ageMultipartBody: RequestBody = age.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val addressMultipartBody: RequestBody = address.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val locationMultipartBody: RequestBody = appointment_location.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val amountMultipartBody: RequestBody = fees_amount.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val feesDateMultipartBody: RequestBody = fees_c_date.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val startDateMultipartBody: RequestBody = start_date.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val unitIdsMultipartBody: RequestBody = unitIds.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val unitNameMultipartBody: RequestBody = unitName.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val subUnitMultipartBody: RequestBody = subUnit.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val imagePart = createImagePart(student_image, "student_image")

        val apiService = RetrofitClient.retrofit.create(HomeTutorApi::class.java)

        if (imagePart == null) {
            hideProgressBar()
            Toast.makeText(this, "Please select all required images.", Toast.LENGTH_SHORT).show()
            return
        }

        val call = apiService.teacherAddStudent(teacherIdMultiPart, bookingIdMultipartBody, guardianIdMultipartBody, nameMultipartBody, genderMultipartBody, dobMultipartBody,
            ageMultipartBody, addressMultipartBody, locationMultipartBody, amountMultipartBody, feesDateMultipartBody, imagePart, startDateMultipartBody, unitIdsMultipartBody, unitNameMultipartBody, subUnitMultipartBody)

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
                                this@CompleteProfileActivity,
                                "Student details added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(applicationContext, TeacherMainActivity::class.java)
                            startActivity(intent)
                        } else {
                            hideProgressBar()
                            Toast.makeText(
                                this@CompleteProfileActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(
                        this@CompleteProfileActivity,
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
                    this@CompleteProfileActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("KycActivity", "API call failure", t)
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