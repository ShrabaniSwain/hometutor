package com.flyngener.tutorhub

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.databinding.AppointedItemListBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppointedAdapter(val context: Context, val appointed: List<AppointedModel>, private val payButtonClickListener: payBtnClickListener) : RecyclerView.Adapter<AppointedAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.appointed_item_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = appointed[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return appointed.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = AppointedItemListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(name : AppointedModel) {
            binding.tvName.text = name.teacher_name
            binding.tvId.text = "ID: " + name.teacher_id
            binding.tvMobileNo.text = name.teacher_mobile
            binding.tvExpYear.text = name.teacher_experience + "," + name.teacher_location
            binding.tvdateAndtime.text = "Appoint Date: " + name.appointment_date
            binding.tvFee.text = name.total_paid.ifEmpty { "0" }

            binding.payBtn.setOnClickListener {
                Constant.PAYMNET_TYPE = "Appointment"
                payButtonClickListener.payBtnClickListener(name)
            }

            if (name.due_amount == "0" || name.due_amount.isEmpty() || name.enable_pay_button_show == 0 || name.status == "2"){
                binding.tvBtnDue.visibility = View.VISIBLE
                binding.payBtn.backgroundTintList =
                    ContextCompat.getColorStateList(itemView.context, R.color.grey)
                binding.payBtn.isEnabled = false
                binding.payBtn.isFocusable = false
            }
            else{
                binding.tvBtnDueDay.visibility = View.VISIBLE
                val dueAmountText = name.due_amount.ifEmpty { "0" }
                binding.tvBtnDueDay.text = "Dues : $dueAmountText"

            }
            binding.tvNone.text = name.appointment_for
            binding.tvAppointedIdNo.text = name.appointment_id
            binding.tvAppointmentDays.text = name.appointment_week_days

            if (name.status == "1"){
                binding.btnActive.visibility = View.VISIBLE
            }
            else{
                binding.btnResigned.visibility = View.VISIBLE
            }
            Glide.with(binding.imageView.context)
                .load(name.teacher_image)
                .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                .into(binding.imageView)
            Utility.itemBackGround(itemView)

            binding.btnTask.setOnClickListener {
                Constant.BOOKING_ID = name.id
                val intent = Intent(context, TaskdeatilsActivity::class.java)
                context.startActivity(intent)
            }
            binding.tvBtnDetails.setOnClickListener { view ->
                val context = view.context
                val dialogBuilder = AlertDialog.Builder(context)
                val inflater = LayoutInflater.from(context).inflate(R.layout.details_dialog_box, null)
                dialogBuilder.setView(inflater)
                val close = inflater.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.ivClose)

                val imageView = inflater.findViewById<ShapeableImageView>(R.id.imageView)
                val tvId = inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvId)
                val tvName = inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvName)
                val tvDob = inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvDate)
                val tvAgeNo = inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvAgeNo)
                val tvCat = inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvCat)
                val tvAppointmentIdNo = inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvIdNo)
                val tvAppointedLocationName = inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvAppointedLocationName)
                val weekDays = inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvAppointmentDays)

                tvName.text = name.student_name
                tvId.text = "ID: " + name.id
                tvDob.text= name.dob
                tvAgeNo.text = name.student_age
                val unitDetailsJson = name.unit_details // This should be the JSON string from your API
                tvCat.text = formatUnitDetails(unitDetailsJson)
                tvAppointmentIdNo.text = name.appointment_id
                tvAppointedLocationName.text = name.student_appointment_location
                weekDays.text = name.appointment_week_days

                Glide.with(imageView)
                    .load(name.student_image)
                    .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                    .into(imageView)

                val dialog = dialogBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                close.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()

            }

        }

        private fun formatUnitDetails(unitDetailsJson: String): String {
            val gson = Gson()
            // Define the type for deserialization
            val type = object : TypeToken<List<Map<String, String>>>() {}.type

            // Parse the JSON string
            val unitDetails: List<Map<String, String>> = gson.fromJson(unitDetailsJson, type)

            // Format the details
            return unitDetails.joinToString("\n") { detail ->
                detail.entries.joinToString("\n") { entry ->
                    "${entry.key.trim()}: ${entry.value.trim()}"
                }
            }
        }

    }

    interface payBtnClickListener {
        fun payBtnClickListener(notificationText: AppointedModel)
    }
}