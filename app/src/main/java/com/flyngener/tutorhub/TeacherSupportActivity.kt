package com.flyngener.tutorhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.tutorhub.databinding.ActivityTeacherSupportBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherSupportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherSupportBinding
    private lateinit var result: List<SupportPageResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSupport()
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSendEnquiry.setOnClickListener {
            val subject = binding.etSubjectSelect.text.toString().trim()
            val requirements = binding.etRequirements.text.toString().trim()

            if (subject.isEmpty() || requirements.isEmpty()) {
                Toast.makeText(this, "Please fill in both the subject and requirements.", Toast.LENGTH_SHORT).show()
            } else {
                val teacherSubject = TeacherSubject(Constant.INSERT_ID, subject, requirements)
                teacherSupportPackageForm(teacherSubject)
                Log.i("TAG", "onCreate: $teacherSubject")

            }
        }
    }

    private fun teacherSupportPackageForm(guardianSubject: TeacherSubject) {
        showProgressBar()
        val call = RetrofitClient.api.teacherSupportPackageForm(guardianSubject)
        call.enqueue(object : Callback<SupportRequestResponse> {
            override fun onResponse(call: Call<SupportRequestResponse>, response: Response<SupportRequestResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    hideProgressBar()
                    Toast.makeText(applicationContext, "Support details added successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, TeacherSupportActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    hideProgressBar()
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SupportRequestResponse>, t: Throwable) {
                hideProgressBar()
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getSupport() {
        showProgressBar()
        val call = RetrofitClient.api.getTeacherSupport(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<SupportPageResponse> {
            override fun onResponse(call: Call<SupportPageResponse>, response: Response<SupportPageResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    result = response.body()?.result ?: emptyList()

                        val supportDeatilsAdapter =
                            SupportDeatilsAdapter(applicationContext, result)
                        binding.rvSupport.layoutManager = LinearLayoutManager(
                            applicationContext,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        binding.rvSupport.adapter = supportDeatilsAdapter

                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<SupportPageResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
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