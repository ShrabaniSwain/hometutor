package com.flyngener.tutorhub

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.databinding.ItemActiveListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray

class TeacherActiveListAdapter(val context: Context, val activeTeacherList: List<Student>) : RecyclerView.Adapter<TeacherActiveListAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_active_list, parent, false)
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
        private val binding = ItemActiveListBinding.bind(itemView)

        fun bind(name : Student) {
            Utility.itemBackGround(itemView)
            binding.tvName.text = name.student_name
            binding.tvAppointmentIdNo.text = name.appointment_id
            binding.tvLocationName.text = name.appointment_location
            binding.tvWeekDays.text = name.week_days
            binding.tvAppointedTeacherName.text = name.appointed_as
            Constant.STUDENT_NAME = name.student_name
            Constant.STUDENT_ID = name.student_id
            Constant.GUARDIAN_ID = name.gaurdian_id.toString()
            Glide.with(binding.imageView.context)
                .load(name.student_image).apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                .into(binding.imageView)

            val unitDetailsJson = name.unit_details
            binding.tvUnit1.text = formatUnitDetails(unitDetailsJson)

            binding.btnCall.setOnClickListener {
                if (name.phone_number?.isNotEmpty() == true) {
                    val phoneNumber = name.phone_number
                    val dialIntent = Intent(Intent.ACTION_DIAL)
                    dialIntent.data = Uri.parse("tel:$phoneNumber")
                    itemView.context.startActivity(dialIntent)
                }
            }

            binding.btnInfo.setOnClickListener {
                Constant.BOOKING_ID = name.booking_id
                val intent = Intent(context, DetailsActivity::class.java)
                context.startActivity(intent)
            }
            binding.btnTask.setOnClickListener {
                Constant.BOOKING_ID = name.booking_id
                Constant.GUARDIAN_ID = name.gaurdian_id.toString()
                val intent = Intent(context, TeacherTaskDetailsActivity::class.java)
                context.startActivity(intent)
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
}