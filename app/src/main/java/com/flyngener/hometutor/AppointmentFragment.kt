package com.flyngener.hometutor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.hometutor.databinding.FragmentAppointmentBinding


class AppointmentFragment : Fragment() {

    private lateinit var binding: FragmentAppointmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivSupport.setOnClickListener {
            val intent = Intent(activity, SupportActivity::class.java)
            startActivity(intent)
        }

        val appointedAdapter = AppointedAdapter(requireContext())
        binding.rvAppointed.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvAppointed.adapter = appointedAdapter

        binding.ivNotification.setOnClickListener {
            val intent = Intent(requireContext(), GuardianNotification::class.java)
            startActivity(intent)
        }

    }

}