package com.flyngener.tutorhub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.tutorhub.Constant.TEACHER_SELECT_TAB
import com.flyngener.tutorhub.databinding.FragmentTeacherPaymentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherPaymentFragment : Fragment() {

    private lateinit var binding: FragmentTeacherPaymentBinding
    private lateinit var payment: List<TeacherPaymentData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTeacherPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TEACHER_SELECT_TAB = 3
//        binding.ivNotification.setOnClickListener {
//            val intent = Intent(requireContext(), TeacherNotificationActivity::class.java)
//            startActivity(intent)
//        }
        getPaymentList()
    }

    private fun getPaymentList() {
        showProgressBar()
        val call = RetrofitClient.api.getPaymentList(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<TeacherPaymentResponse> {
            override fun onResponse(
                call: Call<TeacherPaymentResponse>,
                response: Response<TeacherPaymentResponse>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    payment = response.body()?.result ?: emptyList()
                    val teacherPaymentAdapter = TeacherPaymentAdapter(requireContext(), payment)
                    binding.rvPayment.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.rvPayment.adapter = teacherPaymentAdapter

                    if (payment.isEmpty()){
                        binding.tvNoData.visibility = View.VISIBLE
                        binding.tvStartDate.visibility = View.GONE
                        binding.tvEndDate.visibility = View.GONE
                        binding.btnFilter.visibility = View.GONE
                    }
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TeacherPaymentResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

}