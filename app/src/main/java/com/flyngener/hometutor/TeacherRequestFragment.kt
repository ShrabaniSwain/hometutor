package com.flyngener.hometutor

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
import com.flyngener.hometutor.databinding.FragmentTeacherRequestBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherRequestFragment : Fragment() {

    private lateinit var binding: FragmentTeacherRequestBinding
    private lateinit var teacherBookingList: List<BookingItem>
    private lateinit var topRequestBookingList: List<TeacherRequestTop>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getBookingList()
        getAssignedBookingList()
        binding.filter.setOnClickListener {
            binding.filterCardview.visibility = View.VISIBLE
        }

        binding.ivNotification.setOnClickListener {
            val intent = Intent(requireContext(), TeacherNotificationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getBookingList() {
        showProgressBar()
        val call = RetrofitClient.api.getBookingList(1)
        call.enqueue(object : Callback<TeacherBookingResponse> {
            override fun onResponse(
                call: Call<TeacherBookingResponse>,
                response: Response<TeacherBookingResponse>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    teacherBookingList = response.body()?.result ?: emptyList()
                    val teacherRequestBottomAdapter =
                        TeacherRequestBottomAdapter(requireContext(), teacherBookingList)
                    binding.rvRequestBottom.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.rvRequestBottom.adapter = teacherRequestBottomAdapter
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TeacherBookingResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    private fun getAssignedBookingList() {
        showProgressBar()
        val call = RetrofitClient.api.getAssignedBookingList(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<TeacherRequestTopResponse> {
            override fun onResponse(
                call: Call<TeacherRequestTopResponse>,
                response: Response<TeacherRequestTopResponse>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    topRequestBookingList = response.body()?.result ?: emptyList()
                    val teacherRequestTopAdapter = TeacherRequestTopAdapter(requireContext(), topRequestBookingList)
                    binding.rvRequestTop.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.rvRequestTop.adapter = teacherRequestTopAdapter

                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TeacherRequestTopResponse>, t: Throwable) {
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