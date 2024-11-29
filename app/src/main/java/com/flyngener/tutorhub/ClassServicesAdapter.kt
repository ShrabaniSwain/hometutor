package com.flyngener.tutorhub

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.databinding.ItemClassTeacherBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ClassServicesAdapter(val context: Context, private val teacherList: List<PopularTeacher>) : RecyclerView.Adapter<ClassServicesAdapter.CardViewHolder>() {

    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var dateField: TextView
    private lateinit var timeField: TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_class_teacher, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = teacherList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return teacherList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemClassTeacherBinding.bind(itemView)

        fun bind(notificationText: PopularTeacher) {
            Utility.itemBackGround(itemView)
            binding.tvClassName.text = notificationText.full_name
            binding.tvClassBoard.text = notificationText.unit_name
            binding.tvClassSession.text = notificationText.sub_unit_name
            binding.tvExperience.text = notificationText.experience + " years exp."

            Glide.with(context)
                .load(notificationText.profile_image)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.baseline_person_24)
                )
                .into(binding.ivEditProfileImage)



            binding.btnEnquiryNow.setOnClickListener { view ->
                Constant.CLICKTYPE = Constant.POPULARTEACHERCLICK
                Constant.PROFILEID = notificationText.id.toInt()
                val intent = Intent(context, SelectTeacher::class.java)
                itemView.context.startActivity(intent)
//                val context = view.context
//                val dialogBuilder = AlertDialog.Builder(context)
//                val inflater = LayoutInflater.from(context).inflate(R.layout.enquiry_dialog_box, null)
//                dialogBuilder.setView(inflater)
//                val close = inflater.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.ivClose)
//                dateField = inflater.findViewById(R.id.tvCalender)
//                timeField = inflater.findViewById(R.id.tvTime)
//                val dialog = dialogBuilder.create()
//                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//                updateDateField()
//                updateTimeField()
//
//                close.setOnClickListener {
//                    dialog.dismiss()
//                }
//
//                dateField.setOnClickListener {
//                    openDatePickerDialog()
//                }
//
//                timeField.setOnClickListener {
//                    openTimePickerDialog()
//                }
//
//                dialog.show()

            }
        }

        private fun updateDateField() {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            dateField.text = sdf.format(calendar.time)
        }

        private fun updateTimeField() {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            timeField.text = sdf.format(calendar.time)
        }

        private fun openDatePickerDialog() {
            val datePickerDialog = DatePickerDialog(
                context,R.style.DialogTheme,
                { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(year, monthOfYear, dayOfMonth)
                    updateDateField()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        private fun openTimePickerDialog() {
            val timePickerDialog = TimePickerDialog(
                context, R.style.DialogTheme,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    updateTimeField()
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            timePickerDialog.show()
        }

    }
}