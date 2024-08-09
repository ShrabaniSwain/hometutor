package com.flyngener.hometutor

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
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.flyngener.hometutor.databinding.ActivityKycBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class KycActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKycBinding
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    private var imageUri: Uri? = null
    private val VOTER_ID_REQUEST_CODE = 1
    private val AADHAR_ID_REQUEST_CODE = 2
    private val PAN_ID_REQUEST_CODE = 3
    private val PASSBOOK_REQUEST_CODE = 4
    private val DRIVING_LICENSE_REQUEST_CODE = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKycBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            showProgressBar()
            val etVoterIdNumber = binding.etVotarIdNumber.text.toString()
            val etAadhaarIdNumber = binding.etAadharIdNumber.text.toString()
            val etPanNumber = binding.etPanNumber.text.toString()
            val etDrivingLicenseNumber = binding.etDrivingLicenseNumber.text.toString()
            kycRegistration(
                Constant.INSERT_ID,
                etVoterIdNumber,
                etAadhaarIdNumber,
                etPanNumber,
                etDrivingLicenseNumber,
                imageUri,
                imageUri,
                imageUri,
                imageUri,
                imageUri
            )
        }
        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, LogInHomeTutor::class.java)
            startActivity(intent)
        }
        binding.etUploadVotar.setOnClickListener {
            openGallery(VOTER_ID_REQUEST_CODE)
        }
        binding.etUploadAadhar.setOnClickListener {
            openGallery(AADHAR_ID_REQUEST_CODE)
        }
        binding.etUploadPan.setOnClickListener {
            openGallery(PAN_ID_REQUEST_CODE)
        }
        binding.etBankPassbookUpload.setOnClickListener {
            openGallery(PASSBOOK_REQUEST_CODE)
        }
        binding.etUploadDrivingLicense.setOnClickListener {
            openGallery(DRIVING_LICENSE_REQUEST_CODE)
        }

    }

    private fun openGallery(requestCode: Int) {
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
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, requestCode)
            }
        } else {
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
            } else {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, requestCode)
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
                    openGallery(requestCode)
                } else {
                    // Permission denied, show a message or take appropriate action
                    Toast.makeText(
                        this,
                        "Permission denied. To use this feature, please grant the permission in App Settings.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                VOTER_ID_REQUEST_CODE -> {
                    handleImageResult(data, binding.imageVoterId)
                }

                AADHAR_ID_REQUEST_CODE -> {
                    handleImageResult(data, binding.imageAadharId)
                }

                PAN_ID_REQUEST_CODE -> {
                    handleImageResult(data, binding.imagePanId)
                }

                PASSBOOK_REQUEST_CODE -> {
                    handleImageResult(data, binding.imagePassbook)
                }

                DRIVING_LICENSE_REQUEST_CODE -> {
                    handleImageResult(data, binding.imageDrivingLicense)
                }
            }
        }
    }

    private fun handleImageResult(data: Intent?, imageView: ImageView) {
        val selectedImage: Uri? = data?.data
        selectedImage?.let {
            imageUri = it
            imageView.visibility = View.VISIBLE
            Glide.with(this)
                .load(imageUri)
                .into(imageView)
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

    private fun kycRegistration(
        insertId: String,
        voterId: String,
        aadhaarId: String,
        panNo: String,
        drivingLicenseNo: String,
        voterCard: Uri?,
        aadhaarCard: Uri?,
        panCard: Uri?,
        bankPassbook: Uri?,
        drivingLicense: Uri?
    ) {
        Log.i("KycActivity", "Starting KYC Registration")

        // Convert simple data types to RequestBody
        val insertIdMultiPart: RequestBody =
            insertId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val voterIdMultipartBody: RequestBody =
            voterId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val aadhaarIdMultipartBody: RequestBody =
            aadhaarId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val panNoMultipartBody: RequestBody =
            panNo.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val drivingLicenseNoMultipartBody: RequestBody =
            drivingLicenseNo.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        // Create MultipartBody.Part for each file
        val voterImagePart = createImagePart(voterCard, "voter_card")
        val aadhaarImagePart = createImagePart(aadhaarCard, "aadhar_card")
        val panImagePart = createImagePart(panCard, "pan_card")
        val bankPassbookPart = createImagePart(bankPassbook, "bank_passbook")
        val drivingLicensePart = createImagePart(drivingLicense, "driving_license")

        // Create Retrofit service
        val apiService = RetrofitClient.retrofit.create(HomeTutorApi::class.java)

        // Handle nullable MultipartBody.Part values
        if (voterImagePart == null ||
            aadhaarImagePart == null ||
            panImagePart == null ||
            bankPassbookPart == null ||
            drivingLicensePart == null
        ) {
            Log.e("KycActivity", "One or more image parts are null")
            hideProgressBar()
            Toast.makeText(this, "Please select all required images.", Toast.LENGTH_SHORT).show()
            return
        }

        // Make API call with all parts
        val call = apiService.kycRegistration(
            insertIdMultiPart,
            voterIdMultipartBody,
            aadhaarIdMultipartBody,
            panNoMultipartBody,
            drivingLicenseNoMultipartBody,
            voterImagePart,
            aadhaarImagePart,
            panImagePart,
            bankPassbookPart,
            drivingLicensePart
        )

        Log.i("KycActivity", "kycRegistration: $call")
        call.enqueue(object : Callback<KycModel> {
            override fun onResponse(call: Call<KycModel>, response: Response<KycModel>) {
                Log.i("KycActivity", "onResponse called")
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.i("KycActivity", "onResponse: $apiResponse")
                    apiResponse?.let {
                        if (it.isSuccess) {
                            hideProgressBar()
                            Toast.makeText(
                                this@KycActivity,
                                "KYC details added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(applicationContext, BankDetailsActivity::class.java)
                            startActivity(intent)
                        } else {
                            hideProgressBar()
                            Toast.makeText(
                                this@KycActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(
                        this@KycActivity,
                        "Response not successful: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("KycActivity", "Response error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<KycModel>, t: Throwable) {
                Log.i("KycActivity", "onFailure called")
                hideProgressBar()
                Toast.makeText(
                    this@KycActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("KycActivity", "API call failure", t)
            }
        })
    }


}