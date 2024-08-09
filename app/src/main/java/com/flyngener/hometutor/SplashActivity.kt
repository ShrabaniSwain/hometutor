package com.flyngener.hometutor

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.flyngener.hometutor.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefHelper = SharedPreferenceHelper(this)
        if (sharedPrefHelper.isLoggedIn()) {

            Constant.INSERT_ID = sharedPrefHelper.getCustomerId(this)
            Constant.is_guardian = sharedPrefHelper.getCustomerName(this)

            loadMainActivityWithDelay()
        }
        else {
            loadSignUpActivityWithDelay()
        }
    }

    private fun loadSignUpActivityWithDelay() {
        Handler().postDelayed({
            val intent = Intent(this, LoginMobileNoHome::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun loadMainActivityWithDelay() {
        Handler().postDelayed({

            if (Constant.is_guardian == "1"){
                val intent = Intent(applicationContext, GuardianMainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                val intent = Intent(applicationContext, TeacherMainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}