package com.flyngener.tutorhub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.tutorhub.databinding.FragmentAppointmentBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt


class AppointmentFragment : Fragment(), AppointedAdapter.payBtnClickListener,
    PaymentResultListener {

    private lateinit var binding: FragmentAppointmentBinding
    private lateinit var appointed: List<AppointedModel>
    private lateinit var payAfterAssign: PayAfterAssign

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAppointedBookingList()
//        binding.ivSupport.setOnClickListener {
//            val intent = Intent(activity, SupportActivity::class.java)
//            startActivity(intent)
//        }
//
//        binding.ivNotification.setOnClickListener {
//            val intent = Intent(requireContext(), GuardianNotification::class.java)
//            startActivity(intent)
//        }

    }

    private fun getAppointedBookingList() {
        showProgressBar()
        val call = RetrofitClient.api.getAppointedBookingList(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<GuardianAppointedResponse> {
            override fun onResponse(
                call: Call<GuardianAppointedResponse>,
                response: Response<GuardianAppointedResponse>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()

                    appointed = response.body()?.result ?: emptyList()
                    val appointedAdapter = AppointedAdapter(requireContext(), appointed, this@AppointmentFragment)
                    binding.rvAppointed.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.rvAppointed.adapter = appointedAdapter
                    Log.i("TAG", "responseList: ${response.body()}")

                    if (appointed.isEmpty()){
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<GuardianAppointedResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseList: ${t.message}")

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
        binding.progressBar.visibility = View.GONE
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun payBtnClickListener(notificationText: AppointedModel) {
        Constant.PAYMNET_TYPE = "Appointment"

            if (notificationText.due_amount.isEmpty() || notificationText.due_amount == "0") {
                Toast.makeText(
                    requireContext(),
                    "No dues remaining. You're all set!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
//                val amount = freesList.replace(Regex("[^\\d]"), "")
                val pricePay = (notificationText.due_amount.toFloat() * 100).roundToInt()

                Constant.BOOKING_ID = notificationText.id
                Constant.BUDGET = notificationText.due_amount
                Constant.UNIT_TYPE = notificationText.unit_type

                payAfterAssign = PayAfterAssign(
                    notificationText.id,
                    notificationText.due_amount,
                    "",
                   notificationText.unit_type,
                    Constant.INSERT_ID,
                    "online",
                    "Successful"
                )

                Checkout.preload(requireContext())
                val co = Checkout()
                co.setKeyID(Constant.RAZOR_PAY_KEY)
                val obj = JSONObject()
                try {
                    obj.put("description", "Test payment")
                    obj.put("theme.color", "")
                    obj.put("currency", "INR")
                    obj.put("amount", pricePay)
                    obj.put("prefill.contact", Constant.MOBILE_NO)

                    co.open(requireActivity(), obj)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.i("TAG", "PAYMENTResponse: " + e.message)
                }
                Log.i("TAG", "PAYMENTResponse: $payAfterAssign")
        }

    }

    override fun onPaymentSuccess(p0: String?) {
        if (p0 != null) {
            payAfterAssign.transaction_id = p0
        }
        Log.i("TAG", "onPaymentSuccess: $payAfterAssign")
        Toast.makeText(requireContext(), "Payment is successful", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Log.i("TAG", "PAYMENTResponse: $p0")
        Toast.makeText(requireContext(), "Payment Failed due to error", Toast.LENGTH_SHORT)
            .show()
    }

}