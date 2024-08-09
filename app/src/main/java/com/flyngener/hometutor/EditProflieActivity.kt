package com.flyngener.hometutor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.flyngener.hometutor.databinding.ActivityEditProflieBinding

class EditProflieActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProflieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProflieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setupConstraintToggle(binding.tvPersonalDetailsClose, binding.personalDetailsConstraint)
        setupConstraintToggle(binding.tvAddressDetailsClose, binding.addressDetailsConstraint)
        setupConstraintToggle(binding.tvServiceDetailsClose, binding.servicesDetailsConstraint)
        setupConstraintToggle(binding.tvKycClose, binding.kycDetailsConstraint)
        setupConstraintToggle(binding.tvBankDetailsClose, binding.bankDetailsConstraint)

        binding.personalDetailsConstraint.setOnClickListener {
            hideAndShow(binding.personalDetailsConstraint, binding.tvPersonalDetailsClose)
        }
        binding.addressDetailsConstraint.setOnClickListener {
            hideAndShow(binding.addressDetailsConstraint, binding.tvAddressDetailsClose)
        }
        binding.servicesDetailsConstraint.setOnClickListener {
            hideAndShow(binding.servicesDetailsConstraint, binding.tvServiceDetailsClose)
        }
        binding.kycDetailsConstraint.setOnClickListener {
            hideAndShow(binding.kycDetailsConstraint, binding.tvKycClose)
        }
        binding.bankDetailsConstraint.setOnClickListener {
            hideAndShow(binding.bankDetailsConstraint, binding.tvBankDetailsClose)
        }

    }

    private fun setupConstraintToggle(textView: TextView, constraintLayout: ConstraintLayout) {
        textView.setOnClickListener {
            val isVisible = constraintLayout.visibility == View.VISIBLE
            hideAllConstraintsExcept(constraintLayout)
            if (!isVisible) {
                textView.visibility = View.GONE
                constraintLayout.visibility = View.VISIBLE
            } else {
                textView.visibility = View.VISIBLE
                constraintLayout.visibility = View.GONE
            }
        }
    }

    private fun hideAllConstraintsExcept(exceptConstraint: ConstraintLayout) {
        val constraints = listOf(
            binding.personalDetailsConstraint,
            binding.addressDetailsConstraint,
            binding.servicesDetailsConstraint,
            binding.kycDetailsConstraint,
            binding.bankDetailsConstraint
        )

        for (constraint in constraints) {
            if (constraint != exceptConstraint && constraint.visibility == View.VISIBLE) {
                constraint.visibility = View.GONE
                when (constraint) {
                    binding.personalDetailsConstraint -> binding.tvPersonalDetailsClose.visibility = View.VISIBLE
                    binding.addressDetailsConstraint -> binding.tvAddressDetailsClose.visibility = View.VISIBLE
                    binding.servicesDetailsConstraint -> binding.tvServiceDetailsClose.visibility = View.VISIBLE
                    binding.kycDetailsConstraint -> binding.tvKycClose.visibility = View.VISIBLE
                    binding.bankDetailsConstraint -> binding.tvBankDetailsClose.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hideAndShow(constraintLayout: ConstraintLayout, textView: TextView) {
        constraintLayout.visibility = View.GONE
        textView.visibility = View.VISIBLE
    }
}