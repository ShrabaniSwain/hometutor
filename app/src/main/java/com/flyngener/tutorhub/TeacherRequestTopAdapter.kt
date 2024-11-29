package com.flyngener.tutorhub

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.databinding.TeachersRequestItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TeacherRequestTopAdapter(
    val context: Context,
    val topRequestBookingList: List<TeacherRequestTop>
) : RecyclerView.Adapter<TeacherRequestTopAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.teachers_request_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = topRequestBookingList[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return topRequestBookingList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = TeachersRequestItemBinding.bind(itemView)

        fun bind(name: TeacherRequestTop) {
            Utility.itemBackGround(itemView)
            binding.tvPostedByName.text = name.gaurdian_name
            val formattedDate = formatDateString(name.submit_date, "yyyy-MM-dd", "dd MMM yyyy")
            binding.tvDate.text = formattedDate
            binding.tvServiceName.text = name.service_profile_name
            binding.tvServiceName.text = name.service_profile_name
            binding.tvAddressName.text = name.gaurdian_address
            binding.tvFee.text = name.fees_amount.ifEmpty { "0" }
            binding.tvDetailsName.text = name.service_profile_details
            binding.btnCall.setOnClickListener {
                if (!name.gaurdian_mobile_number.isNullOrEmpty()) {
                    val phoneNumber = name.gaurdian_mobile_number
                    val dialIntent = Intent(Intent.ACTION_DIAL)
                    dialIntent.data = Uri.parse("tel:$phoneNumber")
                    context.startActivity(dialIntent)
                }
            }
            if (name.status == "2") {
                binding.btnReject.visibility = View.VISIBLE
                binding.btnAssigned.visibility = View.GONE
            } else {
                binding.btnReject.visibility = View.GONE
                binding.btnAssigned.visibility = View.VISIBLE
                binding.addStudent.visibility = View.VISIBLE
            }

            Glide.with(context)
                .load(name.service_profile_image)
                .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                .into(binding.imageView)
            Glide.with(context)
                .load(name.gaurdian_image)
                .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                .into(binding.imageView1)

            binding.addStudent.setOnClickListener {
                Constant.BOOKING_ID = name.id
                Constant.GUARDIAN_ID = name.gaurdian_id
                Constant.PROFILE_TYPE = false
                val intent = Intent(context, CompleteProfileActivity::class.java)
                context.startActivity(intent)
            }

            binding.btnReject.setOnClickListener { view ->
                val context = view.context
                val dialogBuilder = AlertDialog.Builder(context)
                val inflater =
                    LayoutInflater.from(context).inflate(R.layout.reject_reason_dialog_box, null)
                dialogBuilder.setView(inflater)
                val close = inflater.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.ivClose)
                val tvTeacherBehaviour = inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvTeacherBehaviour)
                tvTeacherBehaviour.text = name.rejected_message

                val dialog = dialogBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                close.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()

            }


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

    }
}