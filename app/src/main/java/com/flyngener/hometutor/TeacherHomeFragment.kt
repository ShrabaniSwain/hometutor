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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.hometutor.databinding.FragmentTeacherHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TeacherHomeFragment : Fragment() {

    private lateinit var binding: FragmentTeacherHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTeacherHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val teacherFeaturedAdapter = TeacherFeaturedAdapter(requireContext())
        binding.rvFeatured.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFeatured.adapter = teacherFeaturedAdapter

        val teacherActiveListAdapter = TeacherActiveListAdapter(requireContext())
        binding.rvActiveList.adapter = teacherActiveListAdapter
        binding.rvActiveList.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        binding.btnCompleteProfile.setOnClickListener {
            val intent = Intent(requireContext(), CompleteProfileActivity::class.java)
            startActivity(intent)
        }

        binding.tvSwitchGuardian.setOnClickListener {
            showProgressBar()
            val userTypeSwitch = UserTypeSwitch("teacher", Constant.INSERT_ID, "guardian")
            Log.i("TAG", "onViewCreated: " + userTypeSwitch)
            appSaveTodoStatus(userTypeSwitch)
        }

        binding.ivNotification.setOnClickListener {
            val intent = Intent(requireContext(), TeacherNotificationActivity::class.java)
            startActivity(intent)
        }

        binding.tvViewAll.setOnClickListener {
            val intent = Intent(requireContext(), ViewAllActivity::class.java)
            startActivity(intent)
        }
    }

    private fun appSaveTodoStatus(userTypeSwitch: UserTypeSwitch) {
        val call = RetrofitClient.api.switchAccount(userTypeSwitch)
        call.enqueue(object : Callback<SwitchResponse> {
            override fun onResponse(call: Call<SwitchResponse>, response: Response<SwitchResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            Log.i("TAG", "onResponse: " + response.body())
                            hideProgressBar()
                            if (updateProfileResponse.step == "1"){
                                checkGuardianPackage()
                            }

                        } else {
                            Toast.makeText(
                                context,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(
                        context,
                        response.body()?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SwitchResponse>, t: Throwable) {
                hideProgressBar()
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    context,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun checkGuardianPackage() {
        val call = RetrofitClient.api.checkGuardianPackage(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    if (response.body()?.isSuccess == true) {
                        val intent = Intent(requireContext(), GuardianMainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                        Toast.makeText(
                            context,
                            "Account switch successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val intent = Intent(requireContext(), GuardianPackageActivity::class.java)
                        intent.putExtra("CARD_TYPE", Constant.GUARDIAN_CARD)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
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