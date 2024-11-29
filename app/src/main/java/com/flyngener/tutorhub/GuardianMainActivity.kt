package com.flyngener.tutorhub

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.Constant.GUARDIAN_SELECT_TAB
import com.flyngener.tutorhub.databinding.ActivityGuardianHomeBinding
import com.flyngener.tutorhub.databinding.HeaderLayoutBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.razorpay.PaymentResultListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class GuardianMainActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityGuardianHomeBinding
    private lateinit var headerBinding: HeaderLayoutBinding
    private val homeFragment: Fragment = GuardianHomeFragment()
    private val requestFragment: Fragment = RequestFragment()
    private val appointmentFragment: Fragment = AppointmentFragment()
    private var isLoggedOut = false
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuardianHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLocationPermissions()

        GUARDIAN_SELECT_TAB = 1
        binding.toolbarHome.visibility = View.VISIBLE

        if (Constant.INSERT_ID.isNotEmpty()) {
            getGuardianProfile(Constant.INSERT_ID.toInt())
        }

        val selectedTab = intent.getStringExtra("selected")

        when (selectedTab) {
            "appointed" -> {
                binding.bottomNavigation.selectedItemId = R.id.actionAppointed
                replaceFragment(appointmentFragment)
            }
            "request" -> {
                binding.bottomNavigation.selectedItemId = R.id.actionRequests
                replaceFragment(requestFragment)
            }
            else -> {
                binding.bottomNavigation.selectedItemId = R.id.actionHome
                replaceFragment(homeFragment)
            }
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionHome -> {
                    GUARDIAN_SELECT_TAB = 1
                    replaceFragment(homeFragment)
                }
                R.id.actionAppointed -> {
                    GUARDIAN_SELECT_TAB = 2
                    replaceFragment(appointmentFragment)
                }
                R.id.actionRequests -> {
                    GUARDIAN_SELECT_TAB = 3
                    replaceFragment(requestFragment)
                }
                R.id.actionMore ->
                    openNavView()
            }
            true
        }

//        replaceFragment(homeFragment)

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val toolbarVisibility = if (slideOffset > 0) View.VISIBLE else View.GONE
//                binding.toolbar.visibility = toolbarVisibility

                binding.ivNotification.setOnClickListener {
                    val intent = Intent(applicationContext, GuardianNotification::class.java)
                    startActivity(intent)
                }

            }

            override fun onDrawerOpened(drawerView: View) {
                binding.toolbarHome.visibility = View.GONE
                binding.toolbarAppointed.visibility = View.GONE
                binding.toolbarRequest.visibility = View.GONE
                binding.toolbar.visibility = View.VISIBLE
            }

            override fun onDrawerClosed(drawerView: View) {
                binding.toolbar.visibility = View.GONE
                when (Constant.GUARDIAN_SELECT_TAB) {
                    1 -> binding.toolbarHome.visibility = View.VISIBLE
                    2 -> binding.toolbarAppointed.visibility = View.VISIBLE
                    3 -> binding.toolbarRequest.visibility = View.VISIBLE
                }
            }
        })

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            for (i in 0 until binding.navView.menu.size()) {
                val item = binding.navView.menu.getItem(i)
                item.isChecked = false
            }
            menuItem.isChecked = true
            binding.drawerLayout.closeDrawers()

            when (menuItem.itemId) {

                R.id.nav_profile -> {
                    val intent = Intent(this@GuardianMainActivity, GuardianProfileActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_support -> {
                    val intent = Intent(this@GuardianMainActivity, SupportActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_privacy -> {
                    val intent = Intent(this@GuardianMainActivity, GuardianPrivacyActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_terms -> {
                    val intent = Intent(this@GuardianMainActivity, GuardianTermsActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_log_out -> {
                    showLogoutConfirmationDialog()
                }

            }

            true
        }

        headerBinding = HeaderLayoutBinding.bind(binding.navView.getHeaderView(0))

        binding.navView.itemIconTintList = null

        binding.tvSwitchTeacher.setOnClickListener {
            showProgressBar()
            val userTypeSwitch = UserTypeSwitch("guardian", Constant.INSERT_ID, "teacher")
            Log.i("TAG", "onViewCreated: " + userTypeSwitch)
            appSaveTodoStatus(userTypeSwitch)
        }

        binding.ivNotification.setOnClickListener {
            val intent = Intent(this, GuardianNotification::class.java)
            startActivity(intent)
        }

        binding.ivNotificationRequest.setOnClickListener {
            val intent = Intent(this, GuardianNotification::class.java)
            startActivity(intent)
        }

        binding.ivSupport.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }
        binding.ivNotificationAppointed.setOnClickListener {
            val intent = Intent(this, GuardianNotification::class.java)
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()

        binding.toolbarHome.visibility = View.GONE
        binding.toolbarAppointed.visibility = View.GONE
        binding.toolbarRequest.visibility = View.GONE

        when (Constant.GUARDIAN_SELECT_TAB) {
            1 -> binding.toolbarHome.visibility = View.VISIBLE
            2 -> binding.toolbarAppointed.visibility = View.VISIBLE
            3 -> binding.toolbarRequest.visibility = View.VISIBLE
        }
    }

    private fun openNavView(){
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.logout_dialog_box, null)
        val dialogOk = dialogView.findViewById<Button>(R.id.btnOk)
        val dialogCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogOk.setOnClickListener {
            clearSessionData()
            navigateToLoginScreen()
            alertDialog.dismiss()

            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
        }

        dialogCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun clearSessionData() {
        val sharedPrefHelper = SharedPreferenceHelper(this)
        sharedPrefHelper.clearSession()
    }

    private fun navigateToLoginScreen() {
        isLoggedOut = true
        val intent = Intent(this, LoginMobileNoHome::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (isLoggedOut) {
            navigateToLoginScreen()
        }
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
                                val intent = Intent(this@GuardianMainActivity, QualificationActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else if(updateProfileResponse.step == "2"){
                                val intent = Intent(this@GuardianMainActivity, ServicesActivity::class.java)
                                startActivity(intent)
                                this@GuardianMainActivity.finish()
                            }
                            else if(updateProfileResponse.step == "3"){
                                val intent = Intent(this@GuardianMainActivity, KycActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else if(updateProfileResponse.step == "4"){
                                val intent = Intent(this@GuardianMainActivity, BankDetailsActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else if(updateProfileResponse.step == "5"){
                                checkTeacherPackage()
                            }

                        } else {
                            Toast.makeText(
                                this@GuardianMainActivity,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(
                        this@GuardianMainActivity,
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
                    this@GuardianMainActivity,
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
                            val intent = Intent(this@GuardianMainActivity, TeacherMainActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(
                                this@GuardianMainActivity,
                                "Account switch successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val intent = Intent(this@GuardianMainActivity, TeacherPackageActivity::class.java)
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
                Toast.makeText(this@GuardianMainActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getGuardianProfile(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getGuardianProfile(id)
        call.enqueue(object : Callback<GuardianProfileResponse> {
            override fun onResponse(call: Call< GuardianProfileResponse>, response: Response<GuardianProfileResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    Glide.with(applicationContext)
                        .load(response.body()?.result?.get(0)?.profile_image)
                        .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                        .into(headerBinding.imageView)
                    headerBinding.tvPhone.text = Editable.Factory.getInstance().newEditable(response.body()?.result?.get(0)?.mobile_number
                        ?: ""
                    )
                    headerBinding.tvEmail.text = Editable.Factory.getInstance().newEditable(response.body()?.result?.get(0)?.email_id
                        ?: ""
                    )
                    headerBinding.tvName.text = Editable.Factory.getInstance().newEditable(response.body()?.result?.get(0)?.full_name
                        ?: ""
                    )
                    val profileId = "ID: " + response.body()?.result?.get(0)?.id
                    headerBinding.tvProfileId.text = profileId

                    binding.versionId.text = "Version ${Constant.APP_VERSION_NAME}"

                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<GuardianProfileResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    private fun payAfterAssignBooking(payAfterAssign: PayAfterAssign) {
        val call = RetrofitClient.api.payAfterAssignBooking(payAfterAssign)
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                if (response.isSuccessful) {
                    Log.i("TAG", "responseList: ${response.body()}")
                    val intent = Intent(applicationContext, GuardianMainActivity::class.java)
                    intent.putExtra("selected", "request")
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    private fun payInDetails(payAfterAssign: PayAfterAssign) {
        val call = RetrofitClient.api.payInDetails(payAfterAssign)
        call.enqueue(object : Callback<HomeTutorModel> {
            override fun onResponse(
                call: Call<HomeTutorModel>,
                response: Response<HomeTutorModel>
            ) {
                if (response.isSuccessful) {
                    Log.i("TAG", "responseList: ${response.body()}")
                    val intent = Intent(applicationContext, GuardianMainActivity::class.java)
                    intent.putExtra("selected", "appointed")
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    override fun onPaymentSuccess(p0: String?) {
        Log.i("TAG", "PAYMENTResponse: " + p0 )
        val amount = Constant.BUDGET.replace(Regex("[^\\d]"), "")
        val pricePay = Math.round(amount.toFloat() * 100)
        val payAfterAssign = PayAfterAssign(Constant.BOOKING_ID, amount, "", Constant.UNIT_TYPE, Constant.INSERT_ID, "online", "Successful")

        if (p0 != null) {
            payAfterAssign.transaction_id = p0
        }
        Log.i("TAG", "onPaymentSuccess: " + payAfterAssign)

        if (Constant.PAYMNET_TYPE == "Request") {
            payAfterAssignBooking(payAfterAssign)
        }
        else if(Constant.PAYMNET_TYPE == "Appointment") {
            payInDetails(payAfterAssign)
        }
        Toast.makeText(applicationContext, "Payment is successful", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(applicationContext, "Payment Failed due to error", Toast.LENGTH_SHORT)
            .show()
    }

    private fun getCurrentLocation() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permissions not granted
                return
            }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                            // Use Geocoder to get the location name
                            val geocoder = Geocoder(this, Locale.getDefault())
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

                                    Log.i("TAG", "getCurrentLocation: " + locationName)
                                }
                            }
                    }
                }
    }

    private fun checkLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
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