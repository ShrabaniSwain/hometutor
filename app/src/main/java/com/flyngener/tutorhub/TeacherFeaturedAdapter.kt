package com.flyngener.tutorhub

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.databinding.ItemTeacherFeaturedBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class TeacherFeaturedAdapter(val context: Context, val teacherBookingList: List<BookingItem>) : RecyclerView.Adapter<TeacherFeaturedAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_teacher_featured, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = teacherBookingList[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return teacherBookingList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTeacherFeaturedBinding.bind(itemView)

        @SuppressLint("SuspiciousIndentation")
        fun bind(fee : BookingItem) {
            Utility.itemBackGround(itemView)
            binding.tvServiceName.text = fee.service_profile_name
            val formattedDate = formatDateString(fee.submit_date, "yyyy-MM-dd", "dd MMM yyyy")
            binding.tvdateAndtime.text = formattedDate
            binding.tvLocationName.text = "Location: " + fee.gaurdian_address
            val rawDetails = fee.service_profile_details
            val cleanedDetails = rawDetails
                .replace("\"", "")
                .replace("\\r", "")
            val formattedDetails = cleanedDetails.replace(",", "\n")
            binding.tvDetails.text = formattedDetails


            if (fee.is_free_demo_booked == "1") {
                binding.paid.visibility = View.VISIBLE
                binding.tvFee.visibility = View.VISIBLE
                binding.tvRpee.visibility = View.VISIBLE
                binding.tvInc.visibility = View.GONE
                binding.tvIncDetails.visibility = View.GONE
                binding.tvFee.text = fee.fees_amount
            } else if (fee.only_free_demo_booked == "1") {
                binding.paid.visibility = View.GONE
                binding.tvFee.visibility = View.GONE
                binding.tvRpee.visibility = View.GONE
                binding.tvInc.visibility = View.VISIBLE
                binding.tvIncDetails.visibility = View.VISIBLE
            }

            //            binding.tvFeaturedDetails.text = fee.service_profile_details
            Glide.with(context)
                .load(fee.service_profile_image)
                .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                .into(binding.imageView)

            binding.acceptBtn.setOnClickListener {
                val builder = androidx.appcompat.app.AlertDialog.Builder(context)
                    builder.setMessage("Are you sure you want to accept this booking?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            val acceptBooking = AcceptBooking(Constant.INSERT_ID, fee.id, fee.gaurdian_id)
                            acceptBooking(acceptBooking)
                            Log.i("TAG", "bind: " + fee.id)
                            Log.i("TAG", "bind: " + Constant.INSERT_ID)
                            Log.i("TAG", "bind: " + fee.gaurdian_id)
                        }
                        .setNegativeButton("No") { dialog, id ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
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

        private fun acceptBooking(acceptBooking: AcceptBooking) {
            val call = RetrofitClient.api.acceptBookingByTeacher(acceptBooking)
            call.enqueue(object : Callback<HomeTutorModel> {
                override fun onResponse(
                    call: Call<HomeTutorModel>,
                    response: Response<HomeTutorModel>
                ) {
                    if (response.isSuccessful) {
                        Log.i("TAG", "onResponse: " + response.body())
                        Toast.makeText(context, "You accepted your booking successfully", Toast.LENGTH_SHORT).show()

                        // Start TeacherMainActivity with extra to select the "Request" tab
                        val intent = Intent(context, TeacherMainActivity::class.java)
                        intent.putExtra("SELECTED_TAB", "request")
                        context.startActivity(intent)

                        if (context is Activity) {
                            context.finish()
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