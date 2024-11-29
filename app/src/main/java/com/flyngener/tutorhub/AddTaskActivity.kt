package com.flyngener.tutorhub

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.databinding.ActivityAddTaskBinding
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

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private val calendar: Calendar = Calendar.getInstance()
    private var lastClickedField: Int? = null
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    private val FILE_REQUEST_CODE = 100
    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.etTimeSelect.setOnClickListener {
            openTimePickerDialog()
        }
        binding.etSelectDate.setOnClickListener {
            lastClickedField = R.id.etSelectDate
            openStartDatePickerDialog()
        }
        binding.etSelectScheduleDate.setOnClickListener {
            lastClickedField = R.id.etSelectScheduleDate
            openStartDatePickerDialog()
        }
        binding.etSelectImage.setOnClickListener {
            openGallery()
        }

        binding.btnCompleteProfile.setOnClickListener {
            val etTask = binding.etTask.text.toString()
            val etSelectDate = binding.etSelectDate.text.toString()
            val etTimeSelect = binding.etTimeSelect.text.toString()
            val etSelectScheduleDate = binding.etSelectScheduleDate.text.toString()
            if (etTask.isEmpty() || etSelectDate.isEmpty() || etTimeSelect.isEmpty() || etSelectScheduleDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
            else{
                teacherAddTask(Constant.INSERT_ID, Constant.BOOKING_ID, Constant.GUARDIAN_ID, Constant.STUDENT_ID, Constant.STUDENT_NAME, etTask, etSelectDate, etTimeSelect, etSelectScheduleDate, imageUri)
                Log.d(
                    "TaskDetailsLog",
                    "Insert ID: ${Constant.INSERT_ID}, " +
                            "Booking ID: ${Constant.BOOKING_ID}, " +
                            "Guardian ID: ${Constant.GUARDIAN_ID}, " +
                            "Student ID: ${Constant.STUDENT_ID}, " +
                            "Student Name: ${Constant.STUDENT_NAME}, " +
                            "Task Title: ${binding.etTask.text}, " +
                            "Select Date: ${binding.etSelectDate.text}, " +
                            "Select Time: ${binding.etTimeSelect.text}, " +
                            "Schedule Date: ${binding.etSelectScheduleDate.text}, " +
                            "Image URI: ${imageUri}"
                )
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

    private fun openTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this, R.style.DialogTheme,
            { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                calendar.set(Calendar.SECOND, 0)
                updateTimeField(calendar)
            },
            hour, minute, true
        )
        timePickerDialog.show()
    }

    private fun updateTimeField(calendar: Calendar) {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val selectedTime = sdf.format(calendar.time)
        binding.etTimeSelect.text = selectedTime
    }

    private fun teacherAddTask(teacher_id: String, booking_id: String, guardian_id: String, student_id: String, student_name: String, task_title: String,
                               task_date: String, task_time: String, schedule_date: String, imageTask: Uri?
    ) {
        showProgressBar()
        val teacherIdMultiPart: RequestBody = teacher_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val bookingIdMultipartBody: RequestBody = booking_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val guardianIdMultipartBody: RequestBody = guardian_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val studentIdMultipartBody: RequestBody = student_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val nameMultipartBody: RequestBody = student_name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val taskTitleMultipartBody: RequestBody = task_title.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val taskDateMultipartBody: RequestBody = task_date.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val taskTimeMultipartBody: RequestBody = task_time.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val scheduleDateMultipartBody: RequestBody = schedule_date.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val imagePart = createImagePart(imageTask, "task_image")

        val apiService = RetrofitClient.retrofit.create(HomeTutorApi::class.java)

        if (imagePart == null) {
            hideProgressBar()
            Toast.makeText(this, "Please select all required images.", Toast.LENGTH_SHORT).show()
            return
        }

        val call = apiService.teacherAddTask(teacherIdMultiPart, bookingIdMultipartBody, guardianIdMultipartBody, studentIdMultipartBody, nameMultipartBody, taskTitleMultipartBody,
            taskDateMultipartBody, taskTimeMultipartBody, scheduleDateMultipartBody, imagePart)

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
                                this@AddTaskActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@AddTaskActivity, TeacherTaskDetailsActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            hideProgressBar()
                            Toast.makeText(
                                this@AddTaskActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(
                        this@AddTaskActivity,
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
                    this@AddTaskActivity,
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

        when (lastClickedField) {
            R.id.etSelectDate -> binding.etSelectDate.text = selectedDate
            R.id.etSelectScheduleDate -> binding.etSelectScheduleDate.text = selectedDate
        }
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