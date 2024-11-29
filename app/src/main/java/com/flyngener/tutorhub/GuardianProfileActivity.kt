package com.flyngener.tutorhub

import android.Manifest
import android.app.Activity
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
import com.flyngener.tutorhub.databinding.ActivityGuardianProfileBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class GuardianProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGuardianProfileBinding
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    private val FILE_REQUEST_CODE = 100
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuardianProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.imageView.setOnClickListener {
            openGallery()
        }

        getGuardianProfile(Constant.INSERT_ID.toInt())

        binding.btnNext.setOnClickListener {
            val etNameSelect = binding.etNameSelect.text.toString()
            val etMobileNo = binding.etMobileNo.text.toString()
            val etEmailIDSelect = binding.etEmailIDSelect.text.toString()
            val etGenderSelect = binding.etGenderSelect.text.toString()
            val etCitySelect = binding.etCitySelect.text.toString()
            val etStateSelect = binding.etStateSelect.text.toString()
            val etAddressSelect = binding.etAddressSelect.text.toString()
            if (etNameSelect.isEmpty() || etMobileNo.isEmpty() || etEmailIDSelect.isEmpty() || etGenderSelect.isEmpty() || etCitySelect.isEmpty() || etStateSelect.isEmpty() || etAddressSelect.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                editGuardianProfile(
                    etNameSelect,
                    etEmailIDSelect,
                    etMobileNo,
                    etGenderSelect,
                    etCitySelect,
                    etStateSelect,
                    "",
                    etAddressSelect,
                    imageUri,
                    Constant.INSERT_ID
                )
                Log.d(
                    "InputValues", """
    Name: $etNameSelect
    Email: $etEmailIDSelect
    Mobile No: $etMobileNo
    Gender: $etGenderSelect
    City: $etCitySelect
    State: $etStateSelect
    Address: $etAddressSelect
    Image URI: $imageUri
    Insert ID: ${Constant.INSERT_ID}
""".trimIndent()
                )

            }
        }
    }

    private fun openGallery() {
        // Check if permission is not granted
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
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
            } else {
                // Permission is already granted, open the gallery
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, FILE_REQUEST_CODE)
            }
        } else {
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
            } else {
                // Permission is already granted, open the gallery
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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

    private fun getGuardianProfile(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getGuardianProfile(id)
        call.enqueue(object : Callback<GuardianProfileResponse> {
            override fun onResponse(call: Call< GuardianProfileResponse>, response: Response<GuardianProfileResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    binding.etNameSelect.setText(response.body()?.result?.get(0)?.full_name ?: "")
                    binding.etMobileNo.setText(response.body()?.result?.get(0)?.mobile_number ?: "")
                    binding.etEmailIDSelect.setText(response.body()?.result?.get(0)?.email_id ?: "")
                    binding.etGenderSelect.setText(response.body()?.result?.get(0)?.gender ?: "")
                    binding.etCitySelect.setText(response.body()?.result?.get(0)?.city ?: "")
                    binding.etStateSelect.setText(response.body()?.result?.get(0)?.State ?: "")
                    binding.etAddressSelect.setText(response.body()?.result?.get(0)?.full_address ?: "")
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

            override fun onFailure(call: Call<GuardianProfileResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    private fun editGuardianProfile(
        full_name: String,
        email_id: String,
        mobile_number: String,
        gender: String,
        city: String,
        State: String,
        pincode: String,
        full_address: String,
        profileImage: Uri?,
        gaurdian_id: String
    ) {
        showProgressBar()
        val nameMultiPart: RequestBody =
            full_name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val emailMultipartBody: RequestBody =
            email_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mobileMultipartBody: RequestBody =
            mobile_number.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val genderMultipartBody: RequestBody =
            gender.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val cityMultipartBody: RequestBody =
            city.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val stateMultipartBody: RequestBody =
            State.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val pincodeMultipartBody: RequestBody =
            pincode.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val addressMultipartBody: RequestBody =
            full_address.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val guardianIdMultipartBody: RequestBody =
            gaurdian_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val imagePart = createImagePart(profileImage, "profile_image")

        val apiService = RetrofitClient.retrofit.create(HomeTutorApi::class.java)

//        if (imagePart == null) {
//            hideProgressBar()
//            Toast.makeText(this, "Please select all required images.", Toast.LENGTH_SHORT).show()
//            return
//        }

        val call = apiService.editGuardianProfile(
            nameMultiPart,
            emailMultipartBody,
            mobileMultipartBody,
            genderMultipartBody,
            cityMultipartBody,
            stateMultipartBody,
            pincodeMultipartBody,
            addressMultipartBody,
            imagePart,
            guardianIdMultipartBody
        )

        Log.i("KycActivity", "kycRegistration: $call")
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                Log.i("KycActivity", "onResponse called")
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.i("KycActivity", "onResponse: $apiResponse")
                    apiResponse?.let {
                        if (it.isSuccess) {
                            hideProgressBar()
                            Toast.makeText(
                                this@GuardianProfileActivity,
                                "Profile details updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(
                                this@GuardianProfileActivity,
                                GuardianMainActivity::class.java
                            )
                            startActivity(intent)
                            runOnUiThread {
                                finish()
                            }
                        } else {
                            hideProgressBar()
                            Toast.makeText(
                                this@GuardianProfileActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(
                        this@GuardianProfileActivity,
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
                    this@GuardianProfileActivity,
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