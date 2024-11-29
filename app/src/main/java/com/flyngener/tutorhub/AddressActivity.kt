package com.flyngener.tutorhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyngener.tutorhub.databinding.ActivityAddressBinding

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}