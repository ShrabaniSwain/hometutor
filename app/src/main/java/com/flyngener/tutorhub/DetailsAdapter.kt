package com.flyngener.tutorhub

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.databinding.DetailsItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsAdapter(val context: Context, val activeTeacherList: List<StudentDetails>) : RecyclerView.Adapter<DetailsAdapter.CardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.details_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = activeTeacherList[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return activeTeacherList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = DetailsItemBinding.bind(itemView)

        fun bind(name : StudentDetails) {
            binding.tvGuardiansName.text = name.gaurdian_name
            binding.tvRegistrationIdNo.text = name.gaurdian_id
            binding.tvDateNo.text = name.gaurdian_booking_date
            binding.tvMobileNumber.text = name.gaurdian_phone
            binding.tvPerUnitValue.text = name.appointment_location
            binding.tvFeesAmountValue.text = name.fees_amount
            binding.tvEmailId.text = name.gaurdian_email
            binding.btnCall.setOnClickListener {
                if (name.gaurdian_phone.isNotEmpty()) {
                    val phoneNumber = name.gaurdian_phone
                    val dialIntent = Intent(Intent.ACTION_DIAL)
                    dialIntent.data = Uri.parse("tel:$phoneNumber")
                    itemView.context.startActivity(dialIntent)
                }
            }

            Glide.with(binding.imageView.context)
                .load(name.guardian_image).apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                .into(binding.imageView)

            Glide.with(binding.imageView2.context)
                .load(name.student_image).apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                .into(binding.imageView2)

            binding.tvStudentName.text = name.student_name
            binding.tvDobDate.text = name.student_dob
            binding.tvAgeNo.text = name.student_age
            binding.tvAppointmentIdNo.text = name.appointmet_id
            binding.tvLocationName.text = name.appointment_location
            binding.tvFeesDateNo.text = name.fees_date
            binding.tvAppointedDateNo.text = name.appointment_date
            binding.tvFee.text = name.fees_amount.takeIf { it.isNotEmpty() } ?: "0"
            binding.tvPaidAmount.text = "₹" + name.total_paid
            binding.tvBtnDueDay.text = "Dues: " + name.due_amount
            binding.tvSurviceName.text = "{#"+name.service_profile_name + "#}"
            binding.ivToggleBtn.isChecked = name.enable_pay_button_show == 1
            val formattedUnitDetails = formatUnitDetails(name.unit_details)
            binding.tvUnit1.text = formattedUnitDetails

            Utility.itemBackGround(itemView)

            binding.btnEdit.setOnClickListener {
                Constant.PROFILE_TYPE = false
                val intent = Intent(context, CompleteProfileActivity::class.java)
                itemView.context.startActivity(intent)
            }


            binding.ivToggleBtn.setOnCheckedChangeListener { _, isChecked ->
                val teacherPackageData = EnablePaymentModel(name.teacher_id,name.booking_id,name.gaurdian_id ?: "")

                if (isChecked) {
                    enableBookingPayment(teacherPackageData)
                    Log.i("TAG", "paymentBtn: " + teacherPackageData)
                } else {
                    disableBookingPayment(teacherPackageData)
                    Log.i("TAG", "paymentBtn: " + teacherPackageData)
                }
            }

            binding.btnResign.setOnClickListener { view ->
                val context = view.context
                val dialogBuilder = AlertDialog.Builder(context)
                val inflater = LayoutInflater.from(context).inflate(R.layout.resign_dialog_box, null)
                dialogBuilder.setView(inflater)
                val close = inflater.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.ivClose)
                val btnSendEnquiry = inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.btnSendEnquiry)
                val etRequirements = inflater.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.etRequirements)

                val dialog = dialogBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                close.setOnClickListener {
                    dialog.dismiss()
                }

                btnSendEnquiry.setOnClickListener {
                    if (etRequirements.text.toString().isEmpty()) {
                        Toast.makeText(context, "Please enter your requirements", Toast.LENGTH_SHORT).show()
                    } else {
                        val resignRequest = ResignRequest(name.booking_id, Constant.INSERT_ID, etRequirements.text.toString())
                        resignByTeacher(resignRequest)
                        dialog.dismiss()
                    }
                }

                dialog.show()

            }

        }

        fun formatUnitDetails(unitDetails: List<Map<String, String>>): String {
            return unitDetails.joinToString("\n") { detail ->
                detail.entries.joinToString("\n") { entry ->
                    "${entry.key.trim()}: ${entry.value.trim()}"
                }
            }
        }

        private fun resignByTeacher(resignRequest: ResignRequest) {
            val call = RetrofitClient.api.resignByTeacher(resignRequest)
            call.enqueue(object : Callback<HomeTutorModel> {
                override fun onResponse(
                    call: Call<HomeTutorModel>,
                    response: Response<HomeTutorModel>
                ) {
                    if (response.isSuccessful) {
                        Log.i("TAG", "responseList: ${response.body()}")
                        Toast.makeText(context, "Successfully submitted..", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, DetailsActivity::class.java)
                        itemView.context.startActivity(intent)
                        if (context is Activity) {
                            context.finish()
                        }
                    } else {
                        val errorBody = response.errorBody()
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                            .show()
                        Log.i("TAG", "responseList: ${response.body()}")
                    }
                }

                override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseList: ${t.message}")

                }
            })
        }

        private fun enableBookingPayment(teacherPackageData: EnablePaymentModel) {
            val call = RetrofitClient.api.enableBookingPayment(teacherPackageData)
            call.enqueue(object : Callback<HomeTutorModel> {
                override fun onResponse(
                    call: Call<HomeTutorModel>,
                    response: Response<HomeTutorModel>
                ) {
                    if (response.isSuccessful) {
                        val updateProfileResponse = response.body()
                        Log.i("TAG", "onResponse: " + response.body())
                        updateProfileResponse?.let {
                            if (it.isSuccess) {
                                Toast.makeText(context, "Payment Enabled", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } else {
                        Log.e("API", "API call failed with code ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                    Log.e("API", "API call failed with exception: ${t.message}")
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

        private fun disableBookingPayment(teacherPackageData: EnablePaymentModel) {
            val call = RetrofitClient.api.disableBookingPayment(teacherPackageData)
            call.enqueue(object : Callback<HomeTutorModel> {
                override fun onResponse(
                    call: Call<HomeTutorModel>,
                    response: Response<HomeTutorModel>
                ) {
                    if (response.isSuccessful) {
                        val updateProfileResponse = response.body()
                        Log.i("TAG", "onResponse: " + response.body())
                        updateProfileResponse?.let {
                            if (it.isSuccess) {
                                Toast.makeText(context, "Payment Disabled", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } else {
                        Log.e("API", "API call failed with code ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                    Log.e("API", "API call failed with exception: ${t.message}")
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
}