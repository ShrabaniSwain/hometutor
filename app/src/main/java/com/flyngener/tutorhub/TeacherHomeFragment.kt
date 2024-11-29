package com.flyngener.tutorhub

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.flyngener.tutorhub.Constant.TEACHER_SELECT_TAB
import com.flyngener.tutorhub.databinding.FragmentTeacherHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TeacherHomeFragment : Fragment() {

    private lateinit var binding: FragmentTeacherHomeBinding
    private lateinit var activeTeacherList: List<Student>
    private lateinit var teacherBookingList: List<BookingItem>
    private lateinit var banners: List<Banner>

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

        TEACHER_SELECT_TAB = 1
        if (Constant.INSERT_ID.isNotEmpty()) {
            getActiveListForTeacher(Constant.INSERT_ID.toInt())
            getTeacherEarnedList(Constant.INSERT_ID.toInt())
        }
        getBookingList()
        getBanner()

//        binding.btnCompleteProfile.setOnClickListener {
//            Constant.PROFILE_TYPE = true
//            val intent = Intent(requireContext(), CompleteTeacherProfile::class.java)
//            startActivity(intent)
//        }

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

    private fun getBanner() {
        showProgressBar()
        val call = RetrofitClient.api.getBanner()
        call.enqueue(object : Callback<BannerResponse> {
            override fun onResponse(call: Call< BannerResponse>, response: Response<BannerResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    banners = response.body()?.result ?: emptyList()

                    if (isAdded) {

                        Glide.with(requireContext())
                            .load(banners[0].banner_image)
                            .into(binding.ivImage)

                        binding.ivImage.setOnClickListener {
                            val bannerLink = banners[0].external_link
                            if (bannerLink.isNotEmpty()) {
                                var validUrl = bannerLink
                                if (!bannerLink.startsWith("http://") && !bannerLink.startsWith("https://")) {
                                    validUrl = "http://$bannerLink"
                                }

                                if (Patterns.WEB_URL.matcher(validUrl).matches()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
                                    try {
                                        startActivity(intent)
                                    } catch (e: ActivityNotFoundException) {
                                        Toast.makeText(
                                            context,
                                            "No application can handle this request.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        e.printStackTrace()
                                    }
                                } else {
                                    Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    }
                    Log.i("TAG", "BottomBanner: ${response.body()}")
                } else {
                    hideProgressBar()
                    Toast.makeText(requireContext(), "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "BottomBanner: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<BannerResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "BottomBanner: ${t.message}")

            }
        })
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

    private fun getTeacherEarnedList(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getTeacherEarnedList(id)
        call.enqueue(object : Callback<TeacherEarningsResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call< TeacherEarningsResponse>, response: Response<TeacherEarningsResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    binding.tvEarnedPrice.text = "₹" + response.body()?.result?.total_earned
                    binding.tvPendingPrice.text = "₹" + response.body()?.result?.total_pending
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TeacherEarningsResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    private fun getBookingList() {
        showProgressBar()
        val call = RetrofitClient.api.getBookingList(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<TeacherBookingResponse> {
            override fun onResponse(
                call: Call<TeacherBookingResponse>,
                response: Response<TeacherBookingResponse>
            ) {
                Log.i("TAG", "onResponse: " + response)
                if (response.isSuccessful) {
                    hideProgressBar()

                    if (isAdded) {
                        teacherBookingList = response.body()?.result ?: emptyList()
                        val teacherFeaturedAdapter =
                            TeacherFeaturedAdapter(requireContext(), teacherBookingList)
                        binding.rvFeatured.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.rvFeatured.setHasFixedSize(false)

                        binding.rvFeatured.adapter = teacherFeaturedAdapter
                        binding.rvFeatured.setItemViewCacheSize(0)

                        if (teacherBookingList.isEmpty()){
                            binding.tvNoDataFeatured.visibility = View.VISIBLE
                        }
                    }


                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
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

    private fun getActiveListForTeacher(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getActiveListForTeacher(id)
        call.enqueue(object : Callback<ActiveListResponse> {
            override fun onResponse(call: Call< ActiveListResponse>, response: Response<ActiveListResponse>) {
                Log.i("TAG", "onResponse: " + response)
                if (response.isSuccessful) {
                    hideProgressBar()

                    activeTeacherList = response.body()?.result  ?: emptyList()

                    if (isAdded) {
                        val teacherActiveListAdapter =
                            TeacherActiveListAdapter(requireContext(), activeTeacherList)
                        binding.rvActiveList.adapter = teacherActiveListAdapter
                        binding.rvActiveList.layoutManager = GridLayoutManager(
                            requireContext(),
                            2,
                            GridLayoutManager.VERTICAL,
                            false
                        )
                    }

                    if (activeTeacherList.isEmpty()){
                        binding.tvNoData.visibility = View.VISIBLE
                    }

                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ActiveListResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
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
        if (isAdded && activity != null) {
            binding.progressBar.visibility = View.GONE
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }


}