package com.flyngener.tutorhub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.databinding.TeacherRequestBottomItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class TeacherRequestBottomAdapter(val context: Context, val result: List<BookingItem>) :
    RecyclerView.Adapter<TeacherRequestBottomAdapter.CardViewHolder>() {

    private var filteredMarketItems: List<BookingItem> = result

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.teacher_request_bottom_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = filteredMarketItems[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return filteredMarketItems.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = TeacherRequestBottomItemBinding.bind(itemView)

        fun bind(name: BookingItem) {
            Utility.itemBackGround(itemView)
            binding.tvPostedByName.text = name.gaurdian_name
            binding.tvServiceName.text = name.service_profile_name
            Glide.with(context)
                .load(name.service_profile_image)
                .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                .into(binding.imageView)

            Glide.with(context)
                .load(name.gaurdian_image)
                .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                .into(binding.imageView1)

            val formattedDate = formatDateString(name.submit_date, "yyyy-MM-dd", "dd MMM yyyy")
            binding.tvDate.text = formattedDate
            binding.tvAddressName.text = name.gaurdian_address
            val rawDetails = name.service_profile_details
            val cleanedDetails = rawDetails
                .replace("\"", "")
                .replace("\\r", "")
            val formattedDetails = cleanedDetails.replace(",", "\n")
            binding.tvDetails.text = formattedDetails

            if (name.is_free_demo_booked == "1") {
                binding.paid.visibility = View.VISIBLE
                binding.btnCall.visibility = View.GONE
                binding.tvFee.visibility = View.VISIBLE
                binding.tvRpee.visibility = View.VISIBLE
                binding.tvFee.text = name.fees_amount.ifEmpty { "0" }

            } else if (name.only_free_demo_booked == "1") {
                binding.paid.visibility = View.GONE
                binding.btnCall.visibility = View.VISIBLE
                binding.tvFee.visibility = View.GONE
            }

            if (name.booking_status != "Not Accepted"){
                binding.btnAccept.backgroundTintList =
                    ContextCompat.getColorStateList(itemView.context, R.color.grey)
                binding.btnAccept.isEnabled = false
                binding.btnAccept.isFocusable = false

            }
            else {
                binding.btnAccept.backgroundTintList =
                    ContextCompat.getColorStateList(itemView.context, R.color.green)
                binding.btnAccept.isEnabled = true
                binding.btnAccept.isFocusable = true

                binding.btnAccept.setOnClickListener {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Are you sure you want to accept this booking?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            val acceptBooking =
                                AcceptBooking(Constant.INSERT_ID, name.id, name.gaurdian_id)
                            acceptBooking(acceptBooking)
                        }
                        .setNegativeButton("No") { dialog, id ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                }

            }

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

    fun updateList(newList: List<BookingItem>) {
        filteredMarketItems = newList
        notifyDataSetChanged()
    }
}