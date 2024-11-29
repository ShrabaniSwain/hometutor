package com.flyngener.tutorhub

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.flyngener.tutorhub.Constant.PROFILEID
import com.flyngener.tutorhub.databinding.FragmentGuardianHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class GuardianHomeFragment : Fragment() {

    private lateinit var binding: FragmentGuardianHomeBinding
    private lateinit var banners: List<TopBannerItem>
    private lateinit var teacherList: List<PopularTeacher>
    private lateinit var profileGuardian: List<ServiceProfileGuardian>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                getCurrentLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                getCurrentLocation()
            }
            else -> {
                // No location access granted.
            }
        }
    }
    private val bannerImages = listOf(
        R.drawable.banner,
        R.drawable.banner,
        R.drawable.banner
    )
    private var timer: Timer? = null
    private var currentPage = 0
    private val DELAY_MS: Long = 2000
    private val PERIOD_MS: Long = 2000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGuardianHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchTopBannerData()
        getBottomBanner()
        getTeacherList()
        getServiceProfileList()
//        checkLocationPermissions()

        binding.moreTeacherCard.setOnClickListener {
            Constant.CLICKTYPE = Constant.MORECLICK
            val intent = Intent(activity, SelectTeacher::class.java)
            startActivity(intent)
        }
//
//        binding.tvSwitchTeacher.setOnClickListener {
//            showProgressBar()
//            val userTypeSwitch = UserTypeSwitch("guardian", Constant.INSERT_ID, "teacher")
//            Log.i("TAG", "onViewCreated: " + userTypeSwitch)
//            appSaveTodoStatus(userTypeSwitch)
//        }
//
//        binding.ivNotification.setOnClickListener {
//            val intent = Intent(requireContext(), GuardianNotification::class.java)
//            startActivity(intent)
//        }
        binding.etSearch.setOnClickListener {
            val intent = Intent(requireContext(), SearchServiceActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("TAG", "onResume: " + "resume")
    }

    private fun getCurrentLocation() {
        if (isAdded) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permissions not granted
                return
            }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        if (isAdded){
                        // Use Geocoder to get the location name
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (addresses != null) {
                            if (addresses.isNotEmpty()) {
                                val address = addresses[0]
                                val city = address.locality ?: ""
                                val state = address.adminArea ?: ""
                                val postalCode = address.postalCode ?: ""

                                val locationName = "$city, $state"
                                binding.tvLocationName.text = locationName
                            }
                        }
                    }
                    }
                }
        }
    }

    private fun checkLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                getCurrentLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this permission.
                // Request the permission.
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            else -> {
                // You can directly ask for the permission.
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getServiceProfileList() {
        showProgressBar()
        val call = RetrofitClient.api.getServiceProfileList()
        call.enqueue(object : Callback<ServiceProfileGuardianResult> {
            override fun onResponse(call: Call< ServiceProfileGuardianResult>, response: Response<ServiceProfileGuardianResult>) {
                if (response.isSuccessful) {
                    if (isAdded) {
                    hideProgressBar()

                    profileGuardian = response.body()?.result ?: emptyList()

                    response.body()?.result?.let { featuredServices ->

                        val filteredList =
                            featuredServices.filter { it.service_profile_type == "Featured" }
                        val isHome = featuredServices.filter { it.is_show_in_home == "Yes" }
                        val adapter = ClassAdapter(filteredList, requireContext())
                        binding.rvClass.adapter = adapter
                        binding.rvClass.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )

                        if (filteredList.isEmpty()) {
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                        for ((index, service) in isHome.withIndex()) {
                            when (index) {
                                0 -> {
                                    binding.tvScience.text = service.service_profile_name
                                    Glide.with(requireContext())
                                        .load(service.service_profile_image)
                                        .into(binding.ivScience)
                                    binding.scienceTeacherCardView.setOnClickListener {
                                        PROFILEID = service.id.toInt()
                                        Constant.RANDOM_NO = service.random_number.toString()
                                        Constant.CLICKTYPE = Constant.PROFILECLICK
                                        Constant.isQuestion = service.question
                                        if (service.service_profile) {
                                            val intent =
                                                Intent(activity, SelectTeacher::class.java)
                                            startActivity(intent)
                                        } else {
                                            val intent =
                                                Intent(context, SelectBoardOption::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                }

                                1 -> {
                                    binding.tvMath.text = service.service_profile_name
                                    Glide.with(requireContext())
                                        .load(service.service_profile_image)
                                        .into(binding.ivMath)

                                    binding.mathTeacherCard.setOnClickListener {
                                        PROFILEID = service.id.toInt()
                                        Constant.RANDOM_NO = service.random_number.toString()
                                        Constant.CLICKTYPE = Constant.PROFILECLICK
                                        Constant.isQuestion = service.question
                                        if (service.service_profile) {
                                            val intent =
                                                Intent(activity, SelectTeacher::class.java)
                                            startActivity(intent)
                                        } else {
                                            val intent =
                                                Intent(context, SelectBoardOption::class.java)
                                            startActivity(intent)
                                        }
                                    }

                                }

                                2 -> {
                                    binding.tvEnglish.text = service.service_profile_name
                                    Glide.with(requireContext())
                                        .load(service.service_profile_image)
                                        .into(binding.ivEnglish)
                                    binding.englishTeacherCard.setOnClickListener {
                                        PROFILEID = service.id.toInt()
                                        Constant.RANDOM_NO = service.random_number.toString()
                                        Constant.CLICKTYPE = Constant.PROFILECLICK
                                        Constant.isQuestion = service.question
                                        if (service.service_profile) {
                                            val intent =
                                                Intent(activity, SelectTeacher::class.java)
                                            startActivity(intent)
                                        } else {
                                            val intent =
                                                Intent(context, SelectBoardOption::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                }

                                3 -> {
                                    binding.tvGeography.text = service.service_profile_name
                                    Glide.with(requireContext())
                                        .load(service.service_profile_image)
                                        .into(binding.ivGeography)
                                    binding.geographyTeacherCardView.setOnClickListener {
                                        PROFILEID = service.id.toInt()
                                        Constant.RANDOM_NO = service.random_number.toString()
                                        Constant.CLICKTYPE = Constant.PROFILECLICK
                                        Constant.isQuestion = service.question
                                        if (service.service_profile) {
                                            val intent =
                                                Intent(activity, SelectTeacher::class.java)
                                            startActivity(intent)
                                        } else {
                                            val intent =
                                                Intent(context, SelectBoardOption::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                }

                                4 -> {
                                    binding.tvBiology.text = service.service_profile_name
                                    Glide.with(requireContext())
                                        .load(service.service_profile_image)
                                        .into(binding.ivBiology)
                                    binding.biologyTeacherCard.setOnClickListener {
                                        PROFILEID = service.id.toInt()
                                        Constant.RANDOM_NO = service.random_number.toString()
                                        Constant.CLICKTYPE = Constant.PROFILECLICK
                                        Constant.isQuestion = service.question

                                        if (service.service_profile) {
                                            val intent =
                                                Intent(activity, SelectTeacher::class.java)
                                            startActivity(intent)
                                        } else {
                                            val intent =
                                                Intent(context, SelectBoardOption::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                }

                            }
                        }
                    }

//                    val numFeaturedServices = response.body()?.result?.size ?: 0
//                    val viewsToHide = listOf(binding.scienceTeacherCardView, binding.mathTeacherCard, binding.englishTeacherCard, binding.geographyTeacherCardView, binding.biologyTeacherCard)
//                    for (i in numFeaturedServices until viewsToHide.size) {
//                        viewsToHide[i].visibility = View.GONE
//                    }

                    binding.moreTeacherCard.visibility =
                        if (binding.biologyTeacherCard.visibility == View.VISIBLE) View.VISIBLE else View.GONE
                    Log.i("TAG", "responseList: ${response.body()}")
                }
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(requireContext(), "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ServiceProfileGuardianResult>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }
    private fun getTeacherList() {
        showProgressBar()
        val call = RetrofitClient.api.getPopularTeacherList()
        call.enqueue(object : Callback<PopularTeacherResponse> {
            override fun onResponse(call: Call< PopularTeacherResponse>, response: Response<PopularTeacherResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    if (isAdded) {
                        teacherList = response.body()?.result ?: emptyList()
                        val classServicesAdapter =
                            ClassServicesAdapter(requireContext(), teacherList)
                        binding.rvServices.adapter = classServicesAdapter
                        binding.rvServices.layoutManager = GridLayoutManager(
                            requireContext(),
                            2,
                            GridLayoutManager.VERTICAL,
                            false
                        )
                    }
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(requireContext(), "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<PopularTeacherResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }
    private fun fetchTopBannerData() {
        showProgressBar()
        val call = RetrofitClient.api.getTopBanner()
        call.enqueue(object : Callback<TopBannerResponse> {
            override fun onResponse(call: Call< TopBannerResponse>, response: Response<TopBannerResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    banners = response.body()?.result ?: emptyList()

                    if (isAdded) {
                        val guardianBanner = GuardianBanner(banners, requireContext())
                        binding.rvBanner.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.rvBanner.adapter = guardianBanner
                        startAutoSlide()
                    }
                    Log.i("TAG", "bannerImage: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(requireContext(), "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "bannerImage: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TopBannerResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

            }
        })
    }

    private fun getBottomBanner() {
        showProgressBar()
        val call = RetrofitClient.api.getBottomBanner()
        call.enqueue(object : Callback<TopBannerResponse> {
            override fun onResponse(call: Call< TopBannerResponse>, response: Response<TopBannerResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    banners = response.body()?.result ?: emptyList()

                    if (isAdded) {
                        binding.bannerImageView.setOnClickListener {
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

                        if (banners.isNotEmpty()) {
                            Glide.with(requireContext())
                                .load(banners[0].banner_image)
                                .into(binding.bannerImageView)
                        } else {
                            binding.bannerImageView.setImageResource(R.drawable.noimageavailbale)
                        }
                    }

                    Log.i("TAG", "BottomBanner: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(requireContext(), "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "BottomBanner: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TopBannerResponse>, t: Throwable) {
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
                    Constant.INSERT_ID = updateProfileResponse?.user_id.toString()
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            Log.i("TAG", "onResponse: " + response.body())
                            hideProgressBar()
                            if (updateProfileResponse.step == "1"){
                                val intent = Intent(requireContext(), QualificationActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            else if(updateProfileResponse.step == "2"){
                                    val intent = Intent(requireContext(), ServicesActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                            else if(updateProfileResponse.step == "3"){
                                val intent = Intent(requireContext(), KycActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            else if(updateProfileResponse.step == "4"){
                                val intent = Intent(requireContext(), BankDetailsActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            else if(updateProfileResponse.step == "5"){
                                checkTeacherPackage()
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

    private fun checkTeacherPackage() {
        val call = RetrofitClient.api.checkTeacherPackage(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            val intent = Intent(requireContext(), TeacherMainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                            Toast.makeText(
                                context,
                                "Account switch successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val intent = Intent(requireContext(), TeacherPackageActivity::class.java)
                            intent.putExtra("CARD_TYPE", Constant.GUARDIAN_CARD)
                            startActivity(intent)
                        }
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
        if (isAdded) {
            binding.progressBar.visibility = View.GONE
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun startAutoSlide() {
        val handler = Handler()
        val update = Runnable {
            if (currentPage == bannerImages.size) {
                currentPage = 0
            }
            binding.rvBanner.smoothScrollToPosition(currentPage++)
        }

        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
    }

}