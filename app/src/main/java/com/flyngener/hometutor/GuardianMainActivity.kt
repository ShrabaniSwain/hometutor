package com.flyngener.hometutor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.flyngener.hometutor.databinding.ActivityGuardianHomeBinding
import com.flyngener.hometutor.databinding.HeaderLayoutBinding
import com.razorpay.PaymentResultListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GuardianMainActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityGuardianHomeBinding
    private lateinit var headerBinding: HeaderLayoutBinding
    private val homeFragment: Fragment = GuardianHomeFragment()
    private val requestFragment: Fragment = RequestFragment()
    private val appointmentFragment: Fragment = AppointmentFragment()
    private var isLoggedOut = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuardianHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionHome -> replaceFragment(homeFragment)
                R.id.actionAppointed -> replaceFragment(appointmentFragment)
                R.id.actionRequests -> replaceFragment(requestFragment)
                R.id.actionMore ->
                    openNavView()
            }
            true
        }

        replaceFragment(homeFragment)

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val toolbarVisibility = if (slideOffset > 0) View.VISIBLE else View.GONE
                binding.toolbar.visibility = toolbarVisibility

                binding.ivNotification.setOnClickListener {
                    val intent = Intent(applicationContext, GuardianNotification::class.java)
                    startActivity(intent)
                }

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {

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

//        headerBinding = HeaderLayoutBinding.bind(binding.navView.getHeaderView(0))

        binding.navView.itemIconTintList = null


    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun openNavView(){
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.logout_dialog_box, null)
        val dialogOk = dialogView.findViewById<TextView>(R.id.btnOk)
        val dialogCancel = dialogView.findViewById<TextView>(R.id.btnCancel)

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
        val payAfterAssign = PayAfterAssign(Constant.BOOKING_ID, amount, "", Constant.UNIT_TYPE, Constant.INSERT_ID)

        if (p0 != null) {
            payAfterAssign.transaction_id = p0
        }
        Log.i("TAG", "onPaymentSuccess: " + payAfterAssign)
        payAfterAssignBooking(payAfterAssign)
        Toast.makeText(applicationContext, "Payment is successful", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(applicationContext, "Payment Failed due to error", Toast.LENGTH_SHORT)
            .show()
    }
}