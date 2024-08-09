package com.flyngener.hometutor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.flyngener.hometutor.databinding.ActivityBankDetailsBinding
import com.flyngener.hometutor.databinding.ActivityServicesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBankDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            val etBankName = binding.etBankName.text.toString()
            val etAccountName = binding.etAccountName.text.toString()
            val etAccountNumber = binding.etAccountNumber.text.toString()
            val etIFSCCode = binding.etIFSCCode.text.toString()
            val etBranchName = binding.etBranchName.text.toString()

            if (etBankName.isBlank() || etAccountName.isBlank() || etAccountNumber.isBlank() ||  etIFSCCode.isBlank() ||  etBranchName.isBlank()) {
                Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                bankDetailsRegistration(Constant.INSERT_ID,etBankName,etBranchName,etAccountNumber,etAccountName,etIFSCCode)
            }
        }

        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, LogInHomeTutor::class.java)
            startActivity(intent)
        }
    }

    private fun bankDetailsRegistration(insertId: String, bankName: String,
                                        branchName: String,
                                        accountNumber: String,
                                        accountName: String,
                                        ifscCode: String) {
        val addUserDetails = BankAccountModel(insertId,bankName,branchName,accountNumber,accountName,ifscCode)
        Log.i("TAG", "addCustomer: $addUserDetails")
        val call = RetrofitClient.api.bankDetailsRegistration(addUserDetails)
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(call: Call<HomeTutorModel>, response: Response<HomeTutorModel>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            Toast.makeText(applicationContext, "Bank details added successfully...", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, TeacherPackageActivity::class.java)
                            intent.putExtra("CARD_TYPE", Constant.TEACHER_CARD)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
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
}