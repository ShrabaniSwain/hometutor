package com.flyngener.tutorhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.tutorhub.databinding.ActivitySelectQuestionOneBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectQuestionOne : AppCompatActivity() {

    private lateinit var binding: ActivitySelectQuestionOneBinding
    private lateinit var questionnaireList: List<Question>
    private var currentIndex = 0
    private lateinit var adapter: RadioBtnAdpter
    var textRequirements = ""
    var userResponses: MutableList<Answer> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectQuestionOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCustomerDetails(Constant.PROFILEID)
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnNext.setOnClickListener {
            if (binding.etRequirements.text.toString().isEmpty() && binding.textRequirements.visibility == View.VISIBLE) {
                Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                currentIndex++
                textRequirements = binding.etRequirements.text.toString()
                if (currentIndex < questionnaireList.size) {
                    displayQuestionnaireItem(currentIndex)

                    Log.i("TAG", "questionnaireList: " + Constant.questionnaireList)
                } else {
                    if (currentIndex == questionnaireList.size) {
                        if (textRequirements.isNotBlank()) {
                            userResponses.add(Answer(Constant.QuestionId, textRequirements))
                        }
                        if (Constant.ANSWER.isNotBlank()) {
                            userResponses.add(Answer(Constant.QuestionId, Constant.ANSWER))
                        }

                        hideProgressBar()
                        val intent = Intent(applicationContext, SelectquestionThree::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

    }

    private fun getCustomerDetails(id: Int) {
        showProgressBar()

        val call = RetrofitClient.api.getCustomerDetails(id)
        call.enqueue(object : Callback<QuestionResponse> {
            override fun onResponse(call: Call<QuestionResponse>, response: Response<QuestionResponse>) {
                if (response.isSuccessful) {
                        hideProgressBar()
                        val questionnaireResponse = response.body()
                        questionnaireResponse?.let { it ->
                            questionnaireList = it.result
                            displayQuestionnaireItem(currentIndex)
                            val radioBtnQuestion: List<Option> = questionnaireResponse.result
                                .filter { it.question_type == "Radio" }
                                .flatMap { it.options ?: emptyList()
                                }

                            adapter = RadioBtnAdpter(radioBtnQuestion, applicationContext)
                            binding.rvRadioBtn.adapter = adapter
                            binding.rvRadioBtn.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                        }

                        Log.i("TAG", "getServiceNameByType: ${response.body()}")
                } else {
                    hideProgressBar()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "getServiceNameByType: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "getServiceNameByType: ${t.message}")

            }
        })
    }

    private fun displayQuestionnaireItem(index: Int) {
        if (questionnaireList.isNotEmpty()) {
            val questionnaire = questionnaireList[index]
            if (textRequirements.isNotBlank()) {
                userResponses.add(Answer(Constant.QuestionId, textRequirements))
            }
            if (Constant.ANSWER.isNotBlank()) {
                userResponses.add(
                    Answer(
                        Constant.QuestionId,
                        Constant.ANSWER
                    )
                )
            }

            val data = userResponses
            Log.i("TAG", "displayQuestionnaireItem: " + data)
            Constant.questionnaireList = userResponses

            if (questionnaire.question_type == "Text") {
                Constant.QuestionId = questionnaire.id
                binding.etRequirements.text?.clear()
                Constant.ANSWER = ""
                binding.tellUsMore.text = questionnaire.question
                binding.textRequirements.visibility = View.VISIBLE
                binding.radioBtnCard.visibility = View.GONE

            } else if (questionnaire.question_type == "Radio") {
                binding.etRequirements.text?.clear()
                Constant.ANSWER = ""
                Constant.QuestionId = questionnaire.id
                binding.textRequirements.visibility = View.GONE
                binding.radioBtnCard.visibility = View.VISIBLE
                binding.questionDescriptiom.text = questionnaire.question
            }
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