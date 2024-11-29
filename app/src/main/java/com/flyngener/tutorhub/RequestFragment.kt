package com.flyngener.tutorhub

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.tutorhub.databinding.FragmentRequestBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class RequestFragment : Fragment(), RequestsAdapter.OnAssignButtonClickListener, PaymentResultListener, ChoosefeeAdapter.OnFeeItemSelectedListener {

    private lateinit var binding: FragmentRequestBinding
    private lateinit var teacherBookingList: List<GuardianRequestResult>
    private lateinit var freesList: List<Fee>
    private lateinit var payAfterAssign: PayAfterAssign

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getBookingList()
        getPaymentAmount(Constant.SERVICE_PROFILE_PAYMENT_ID)

//        binding.ivNotification.setOnClickListener {
//            val intent = Intent(requireContext(), GuardianNotification::class.java)
//            startActivity(intent)
//        }
    }

    private fun getBookingList() {
        showProgressBar()
        val call = RetrofitClient.api.getAcceptedBookingList(Constant.INSERT_ID.toInt())
        call.enqueue(object : Callback<RequestGuardianBooking> {
            override fun onResponse(
                call: Call<RequestGuardianBooking>,
                response: Response<RequestGuardianBooking>
            ) {
                Log.i("TAG", "onResponse: " + response)
                if (response.isSuccessful) {
                    hideProgressBar()

                    teacherBookingList = response.body()?.result ?: emptyList()
                    val requestsAdapter = RequestsAdapter(requireContext(), teacherBookingList, this@RequestFragment)
                    binding.rvRequest.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.rvRequest.adapter = requestsAdapter

                    if (teacherBookingList.isEmpty()){
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    hideProgressBar()
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<RequestGuardianBooking>, t: Throwable) {
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

    override fun onAssignButtonClick(
        notificationText: GuardianRequestResult,
        freesList: List<Fee>
    ) {
        Constant.PAYMNET_TYPE = "Request"
        getPaymentAmount(Constant.SERVICE_PROFILE_PAYMENT_ID)

        val assignBookingByGuardian = AssignBookingByGuardian(
            notificationText.booking_id,
            notificationText.teacher_id,
            Constant.INSERT_ID
        )
        assignBookingByTeacher(assignBookingByGuardian)
        Log.i("TAG", "onAssignButtonClick: " + assignBookingByGuardian)
    }

    override fun onPaymentSuccess(p0: String?) {
        if (p0 != null) {
            payAfterAssign.transaction_id = p0
        }
        Log.i("TAG", "onPaymentSuccess: " + payAfterAssign)
        Toast.makeText(requireContext(), "Payment is successful", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Log.i("TAG", "PAYMENTResponse: " + p0 )
        Toast.makeText(requireContext(), "Payment Failed due to error", Toast.LENGTH_SHORT)
            .show()
    }


    private fun assignBookingByTeacher(assignBookingByGuardian: AssignBookingByGuardian) {
        showProgressBar()
        val call = RetrofitClient.api.assignBookingByTeacher(assignBookingByGuardian)
        call.enqueue(object : Callback<AssignBookingResponse> {
            override fun onResponse(
                call: Call<AssignBookingResponse>,
                response: Response<AssignBookingResponse>
            ) {
                Log.i("TAG", "assignBookingByTeacher: " + assignBookingByGuardian)

                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true){
                    hideProgressBar()
                    Log.i("TAG", "onResponse: " + response.body())
                    Toast.makeText(
                        requireContext(),
                        "You assigned your booking successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(requireContext(), GuardianMainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    hideProgressBar()
                    val dialogBuilder = AlertDialog.Builder(requireContext())
                    val inflater =
                        LayoutInflater.from(requireContext())
                            .inflate(R.layout.assign_dialog_box, null)
                    dialogBuilder.setView(inflater)
                    val close =
                        inflater.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.ivClose)
                    val rvChooseFee = inflater.findViewById<RecyclerView>(R.id.rvChooseFee)
                    val tvNoData =
                        inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvNoData)
                    val rejectButton =
                        inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.rejectButton)

                    val dialog = dialogBuilder.create()
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    if (freesList.isEmpty()) {
                        tvNoData.visibility = View.VISIBLE
                    } else {
                        val chooseFeeAdapter =
                            ChoosefeeAdapter(freesList, requireContext(), this@RequestFragment)
                        rvChooseFee.adapter = chooseFeeAdapter
                        rvChooseFee.layoutManager =
                            GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
                    }

                    rejectButton.setOnClickListener {

                        if (Constant.BUDGET.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Please select the price",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val amount = Constant.BUDGET.replace(Regex("[^\\d]"), "")
                            val pricePay = (amount.toFloat() * 100).roundToInt()

                            payAfterAssign = PayAfterAssign(
                                Constant.BOOKING_ID,
                                amount,
                                "",
                                Constant.UNIT_TYPE,
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
                            dialog.dismiss()
                        }
                    }

                    close.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }
            }

            override fun onFailure(call: Call<AssignBookingResponse>, t: Throwable) {
                hideProgressBar()
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getPaymentAmount(id: Int) {
        val call = RetrofitClient.api.getPaymentAmount(id)
        call.enqueue(object : Callback<FeesResponse> {
            override fun onResponse(
                call: Call<FeesResponse>,
                response: Response<FeesResponse>
            ) {
                if (response.isSuccessful) {

                    freesList = response.body()?.fees_array ?: emptyList()

                    Log.i("TAG", "responseList: ${response.body()}")
                } else {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<FeesResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                Log.i("TAG", "responseList: ${t.message}")

            }
        })
    }

    override fun onItemSelected(isSelected: Boolean) {
        return
    }
}