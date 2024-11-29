package com.flyngener.tutorhub

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.tutorhub.Constant.TEACHER_SELECT_TAB
import com.flyngener.tutorhub.databinding.FragmentTeacherRequestBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import kotlin.math.log

class TeacherRequestFragment : Fragment() {

    private lateinit var binding: FragmentTeacherRequestBinding
    private lateinit var teacherBookingList: List<BookingItem>
    private lateinit var topRequestBookingList: List<TeacherRequestTop>

    private lateinit var teacherRequestBottomAdapter: TeacherRequestBottomAdapter
    private val calendar: Calendar = Calendar.getInstance()

    private var filteredList: List<BookingItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TEACHER_SELECT_TAB = 2
        getBookingList()
        getAssignedBookingList()
        binding.filter.setOnClickListener {
            binding.filterCardview.visibility = View.VISIBLE
        }

//        binding.ivNotification.setOnClickListener {
//            val intent = Intent(requireContext(), TeacherNotificationActivity::class.java)
//            startActivity(intent)
//        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = p0.toString()

                val filteredList = filteredList.filter { marketDetail ->
                    marketDetail.service_profile_name.contains(searchText, ignoreCase = true)
                }
                teacherRequestBottomAdapter.updateList(filteredList)

            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun getBookingList() {
        showProgressBar()
        val call = RetrofitClient.api.getBookingList2(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<TeacherBookingResponse> {
            override fun onResponse(
                call: Call<TeacherBookingResponse>,
                response: Response<TeacherBookingResponse>
            ) {
                Log.i("TAG", "onResponse: " + response)
                if (response.isSuccessful) {
                    hideProgressBar()

                    filteredList = response.body()?.result ?: emptyList()
                    if (isAdded) {
                        teacherRequestBottomAdapter =
                            TeacherRequestBottomAdapter(requireContext(), filteredList)
                        binding.rvRequestBottom.layoutManager =
                            LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        binding.rvRequestBottom.adapter = teacherRequestBottomAdapter
                    }

                    if (filteredList.isEmpty()){
                        binding.tvNoData.visibility = View.VISIBLE
                        binding.etSearch.visibility = View.GONE
                    }
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

                    if (isAdded) {
                        topRequestBookingList = response.body()?.result ?: emptyList()
                        val teacherRequestTopAdapter = TeacherRequestTopAdapter(requireContext(), topRequestBookingList)
                        binding.rvRequestTop.layoutManager =
                            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        binding.rvRequestTop.adapter = teacherRequestTopAdapter
                    }



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
        if (isAdded) {
            binding.progressBar.visibility = View.GONE
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

}