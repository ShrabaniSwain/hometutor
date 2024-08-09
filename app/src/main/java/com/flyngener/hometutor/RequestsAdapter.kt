package com.flyngener.hometutor

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.hometutor.Constant.BOOKING_ID
import com.flyngener.hometutor.Constant.SERVICE_PROFILE_PAYMENT_ID
import com.flyngener.hometutor.databinding.RequestsItemListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class RequestsAdapter(
    val requireContext: Context,
    private val teacherBookingList: List<GuardianRequestResult>,
    private val assignButtonClickListener: OnAssignButtonClickListener
) : RecyclerView.Adapter<RequestsAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.requests_item_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = teacherBookingList[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return teacherBookingList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ChoosefeeAdapter.OnFeeItemSelectedListener {
        private val binding = RequestsItemListBinding.bind(itemView)
        private lateinit var freesList: List<Fee>

        fun bind(notificationText: GuardianRequestResult) {
            Utility.itemBackGround(itemView)
            getPaymentAmount(notificationText.service_profile_id.toInt())
            binding.tvFee.text = notificationText.fees_amount
            binding.tvAcceptedName.text = notificationText.teacher_name
            binding.tvMobileNo.text = notificationText.teacher_mobile
            binding.tvIdNo.text =
                notificationText.teacher_code.ifEmpty { "0" }
            binding.tvServiceName.text = notificationText.service_profile_name
            if (adapterPosition < notificationText.service_profile_details.size) {
                binding.tvServiceDetails.text =
                    notificationText.service_profile_details[adapterPosition].value
            } else {
                binding.tvServiceDetails.text = ""
            }
            val formattedDate =
                formatDateString(notificationText.booking_date, "yyyy-MM-dd", "dd MMM yyyy")
            binding.dateAndTime.text = formattedDate
            Glide.with(requireContext)
                .load(notificationText.teacher_image)
                .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                .into(binding.imageView)
            BOOKING_ID = notificationText.booking_id
            SERVICE_PROFILE_PAYMENT_ID = notificationText.service_profile_id.toInt()

            if (notificationText.assigned_or_reject_by_gaurdian == "2") {
                binding.rejectedLogo.visibility = View.VISIBLE
                binding.rejectButton.isEnabled = false
                binding.assignButton.isEnabled = false
                binding.assignButton.isFocusable = false
                binding.assignButton.isFocusable = false

                binding.rejectButton.backgroundTintList =
                    ContextCompat.getColorStateList(itemView.context, R.color.grey)
                binding.assignButton.backgroundTintList =
                    ContextCompat.getColorStateList(itemView.context, R.color.grey)
                binding.assignButton.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                binding.rejectButton.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
            }

            if (notificationText.only_free_demo_booked == "1") {
                binding.freeDemo.visibility = View.VISIBLE
            } else {
                binding.paid.visibility = View.VISIBLE
                binding.tvRpee.visibility = View.VISIBLE
                binding.tvFee.visibility = View.VISIBLE
            }

            binding.rejectButton.setOnClickListener { view ->
                val context = view.context
                val dialogBuilder = AlertDialog.Builder(context)
                val inflater =
                    LayoutInflater.from(context).inflate(R.layout.rejected_dialog_box, null)
                dialogBuilder.setView(inflater)
                val close = inflater.findViewById<ImageView>(R.id.ivClose)
                val reject = inflater.findViewById<TextView>(R.id.rejectButton)
                val etRequirements = inflater.findViewById<EditText>(R.id.etRequirements)

                val dialog = dialogBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                reject.setOnClickListener {
                    if (etRequirements.text.toString().isEmpty()) {
                        Toast.makeText(
                            context,
                            "Please enter the requirements.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val rejectBookingByGuardian = RejectBookingByGuardian(
                            notificationText.booking_id,
                            etRequirements.text.toString()
                        )
                        rejectBookingByTeacher(rejectBookingByGuardian)
                        dialog.dismiss()
                        Log.i("TAG", "bind: $rejectBookingByGuardian")
                    }
                }
                close.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()

            }

            binding.assignButton.setOnClickListener {
                assignButtonClickListener.onAssignButtonClick(notificationText, freesList)
            }
        }

        private fun rejectBookingByTeacher(rejectBookingByGuardian: RejectBookingByGuardian) {
            val call = RetrofitClient.api.rejectBookingByTeacher(rejectBookingByGuardian)
            call.enqueue(object : Callback<HomeTutorModel> {
                override fun onResponse(
                    call: Call<HomeTutorModel>,
                    response: Response<HomeTutorModel>
                ) {
                    if (response.isSuccessful) {
                        Log.i("TAG", "onResponse: " + response.body())
                        Toast.makeText(
                            requireContext,
                            "You rejected your booking successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(requireContext, GuardianMainActivity::class.java)
                        itemView.context.startActivity(intent)
                        if (requireContext is Activity) {
                            requireContext.finish()
                        }
                    } else {
                        Log.e("API", "API call failed with code ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                    Log.e("API", "API call failed with exception: ${t.message}")
                    Toast.makeText(requireContext, t.message, Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(requireContext, "Something went wrong", Toast.LENGTH_SHORT)
                            .show()
                        Log.i("TAG", "responseList: ${response.body()}")
                    }
                }

                override fun onFailure(call: Call<FeesResponse>, t: Throwable) {
                    Toast.makeText(requireContext, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${t.message}")

                }
            })
        }




        private fun formatDateString(
            dateString: String,
            inputFormat: String,
            outputFormat: String
        ): String {
            val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
            val outputDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
            val date = inputDateFormat.parse(dateString)
            return outputDateFormat.format(date)
        }

        override fun onItemSelected(isSelected: Boolean) {

        }

    }

    interface OnAssignButtonClickListener {
        fun onAssignButtonClick(notificationText: GuardianRequestResult, freesList: List<Fee>)
    }

}