package com.flyngener.tutorhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.Constant.TEACHER_SELECT_TAB
import com.flyngener.tutorhub.databinding.ActivityTeacherHomeBinding
import com.flyngener.tutorhub.databinding.HeaderLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherHomeBinding
    private val homeFragment: Fragment = TeacherHomeFragment()
    private val requestFragment: Fragment = TeacherRequestFragment()
    private val paymentFragment: Fragment = TeacherPaymentFragment()
    private var isLoggedOut = false
    private lateinit var headerBinding: HeaderLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTeacherHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Constant.INSERT_ID.isNotEmpty()) {
            getTeacherProfile(Constant.INSERT_ID.toInt())
        }

        TEACHER_SELECT_TAB = 1
        binding.toolbarHome.visibility = View.VISIBLE

        val selectedTab = intent.getStringExtra("SELECTED_TAB")
        if (selectedTab == "request") {
            binding.bottomNavigation.selectedItemId = R.id.actionRequests
            replaceFragment(requestFragment)
        }
        else {
            binding.bottomNavigation.selectedItemId = R.id.actionHome
            replaceFragment(homeFragment)
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionHome -> {
                    TEACHER_SELECT_TAB = 1
                    replaceFragment(homeFragment)
                }
                R.id.actionPayment ->
                {
                    TEACHER_SELECT_TAB = 2
                    replaceFragment(paymentFragment)
                }
                R.id.actionRequests ->{
                    TEACHER_SELECT_TAB = 3
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
                binding.ivNotification.setOnClickListener {
                    val intent = Intent(applicationContext, TeacherNotificationActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                binding.toolbarHome.visibility = View.GONE
                binding.toolbarPayment.visibility = View.GONE
                binding.toolbarRequest.visibility = View.GONE
                binding.toolbar.visibility = View.VISIBLE
            }

            override fun onDrawerClosed(drawerView: View) {
                binding.toolbar.visibility = View.GONE
                when (TEACHER_SELECT_TAB) {
                    1 -> binding.toolbarHome.visibility = View.VISIBLE
                    2 -> binding.toolbarPayment.visibility = View.VISIBLE
                    3 -> binding.toolbarRequest.visibility = View.VISIBLE
                }
            }
        })

        headerBinding = HeaderLayoutBinding.bind(binding.navView.getHeaderView(0))

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            for (i in 0 until binding.navView.menu.size()) {
                val item = binding.navView.menu.getItem(i)
                item.isChecked = false
            }
            menuItem.isChecked = true
            binding.drawerLayout.closeDrawers()

            when (menuItem.itemId) {

                R.id.nav_profile -> {
                    val intent = Intent(this@TeacherMainActivity, MyProfileActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_support -> {
                    val intent = Intent(this@TeacherMainActivity, TeacherSupportActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_privacy -> {
                    val intent = Intent(this@TeacherMainActivity, TeacherPrivacyPolicy::class.java)
                    startActivity(intent)
                }

                R.id.nav_terms -> {
                    val intent = Intent(this@TeacherMainActivity, TeacherTermsCondition::class.java)
                    startActivity(intent)
                }
                R.id.nav_log_out -> {
                    showLogoutConfirmationDialog()
                }

            }

            true
        }

        binding.navView.itemIconTintList = null

        binding.tvSwitchGuardian.setOnClickListener {
            showProgressBar()
            val userTypeSwitch = UserTypeSwitch("teacher", Constant.INSERT_ID, "guardian")
            Log.i("TAG", "onViewCreated: " + userTypeSwitch)
            appSaveTodoStatus(userTypeSwitch)
        }

        binding.ivNotification1.setOnClickListener {
            val intent = Intent(this, TeacherNotificationActivity::class.java)
            startActivity(intent)
        }
        binding.ivNotificationRequest.setOnClickListener {
            val intent = Intent(this, TeacherNotificationActivity::class.java)
            startActivity(intent)
        }
        binding.ivNotificationPayment.setOnClickListener {
            val intent = Intent(this, TeacherNotificationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()

        binding.toolbarHome.visibility = View.GONE
        binding.toolbarPayment.visibility = View.GONE
        binding.toolbarRequest.visibility = View.GONE

        when (TEACHER_SELECT_TAB) {
            1 -> binding.toolbarHome.visibility = View.VISIBLE
            2 -> binding.toolbarPayment.visibility = View.VISIBLE
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

    private fun getTeacherProfile(id: Int) {
        showProgressBar()
        val call = RetrofitClient.api.getTeacherProfile(id)
        call.enqueue(object : Callback<TeacherProfileDetails> {
            override fun onResponse(call: Call<TeacherProfileDetails>, response: Response<TeacherProfileDetails>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    Constant.SERVICE_NAME = response.body()?.result?.get(0)?.service_profile_name ?: ""

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

            override fun onFailure(call: Call<TeacherProfileDetails>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
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
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            Log.i("TAG", "onResponse: " + response.body())
                            hideProgressBar()
                            if (updateProfileResponse.step == "1"){
                                checkGuardianPackage()
                            }

                        } else {
                            Toast.makeText(
                                this@TeacherMainActivity,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(
                        this@TeacherMainActivity,
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
                    this@TeacherMainActivity,
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
                        val intent = Intent(this@TeacherMainActivity, GuardianMainActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(
                            this@TeacherMainActivity,
                            "Account switch successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val intent = Intent(this@TeacherMainActivity, GuardianPackageActivity::class.java)
                        intent.putExtra("CARD_TYPE", Constant.GUARDIAN_CARD)
                        startActivity(intent)
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(this@TeacherMainActivity, t.message, Toast.LENGTH_SHORT).show()
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