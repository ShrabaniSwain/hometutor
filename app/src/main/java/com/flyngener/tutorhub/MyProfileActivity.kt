package com.flyngener.tutorhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.databinding.ActivityMyProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val insertId = Constant.INSERT_ID.toIntOrNull() ?: 0
        getTeacherProfile(insertId)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivProfileEdit.setOnClickListener {
            Constant.PROFILE_TYPE = false
            val intent = Intent(applicationContext, EditProflieActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getTeacherProfile(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getTeacherProfile(id)
        call.enqueue(object : Callback<TeacherProfileDetails> {
            override fun onResponse(call: Call<TeacherProfileDetails>, response: Response<TeacherProfileDetails>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    Constant.SERVICE_NAME = response.body()?.result?.get(0)?.service_profile_name ?: ""
                    binding.tvName.setText(response.body()?.result?.get(0)?.full_name ?: "")
                    val city = response.body()?.result?.get(0)?.full_address ?: ""
                    val state = response.body()?.result?.get(0)?.State ?: ""
                    val pincode = response.body()?.result?.get(0)?.pincode ?: ""

                    val fullAddress = city
                    binding.tvAddressDetails.text = fullAddress

                    binding.tvPhone.setText(response.body()?.result?.get(0)?.mobile_number ?: "")
                    binding.tvEmail.setText(response.body()?.result?.get(0)?.email_id ?: "")
                    binding.tvMale.setText(response.body()?.result?.get(0)?.gender ?: "")
                    binding.tvGeaduate.setText(response.body()?.result?.get(0)?.qualification ?: "")
                    binding.tvYearOfExp.text = (response.body()?.result?.get(0)?.experience ?: "0") + "Y.O.E"
                    binding.tvAge.text = (response.body()?.result?.get(0)?.age ?: "") + "Years"
                    binding.tvAboutDetails.setText(response.body()?.result?.get(0)?.about ?: "")
                    binding.tvBankName.setText(response.body()?.result?.get(0)?.bank_name ?: "")
                    binding.tvBankholderName.setText(response.body()?.result?.get(0)?.bank_name ?: "")
                    binding.tvAccountName.setText(response.body()?.result?.get(0)?.account_name ?: "")
                    binding.tvBankAccountNumber.setText(response.body()?.result?.get(0)?.account_number ?: "")
                    binding.tvIFSCCode.setText(response.body()?.result?.get(0)?.ifsc_code ?: "")
                    binding.tvBranchName.setText(response.body()?.result?.get(0)?.branch_name ?: "")
                    binding.tvService1.setText(response.body()?.result?.get(0)?.service_profile_name ?: "")
                    binding.tvService1.setText(response.body()?.result?.get(0)?.service_profile_name ?: "")
                    val rawDetails = response.body()?.result?.get(0)?.unit_subunit_details ?: ""
                    val cleanedDetails = rawDetails
                        .replace("\"", "") // Remove all double quotes
                        .replace("\\r", "") // Remove unwanted \r characters
                    val keyValuePairs = cleanedDetails.split(",(?=\\s*\"[A-Za-z])".toRegex())
                    val formattedDetails = keyValuePairs.joinToString("\n") { keyValue ->
                        keyValue.replace(":", ": ")
                    }
                    binding.tvUnit1.text = formattedDetails
//                    binding.etDObSelect.setText(response.body()?.result?.get(0)?. ?: "")
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