package com.flyngener.hometutor

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.hometutor.databinding.FragmentTeacherPaymentBinding
import com.flyngener.hometutor.databinding.FragmentTeacherRequestBinding

class TeacherPaymentFragment : Fragment() {

    private lateinit var binding: FragmentTeacherPaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTeacherPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val teacherPaymentAdapter = TeacherPaymentAdapter(requireContext())
        binding.rvPayment.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvPayment.adapter = teacherPaymentAdapter

        binding.ivNotification.setOnClickListener {
            val intent = Intent(requireContext(), TeacherNotificationActivity::class.java)
            startActivity(intent)
        }
    }

}