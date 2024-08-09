package com.flyngener.hometutor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyngener.hometutor.databinding.ActivityAddressBinding
import com.flyngener.hometutor.databinding.ActivityQualificationAddressBinding

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}