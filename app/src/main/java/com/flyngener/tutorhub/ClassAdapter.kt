package com.flyngener.tutorhub

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flyngener.tutorhub.databinding.ItemClassBinding

class ClassAdapter(private val profileGuardian: List<ServiceProfileGuardian>, val context: Context) : RecyclerView.Adapter<ClassAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_class, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = profileGuardian[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return profileGuardian.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemClassBinding.bind(itemView)

        fun bind(notificationText: ServiceProfileGuardian) {
            Utility.itemBackGround(itemView)
            itemView.setOnClickListener {
                Constant.PROFILEID = notificationText.id.toInt()
                Constant.RANDOM_NO = notificationText.random_number.toString()
                Constant.CLICKTYPE = Constant.PROFILECLICK
                Constant.isQuestion = notificationText.question
                if (notificationText.service_profile) {
                    val intent = Intent(context, SelectTeacher::class.java)
                    itemView.context.startActivity(intent)
                }
                else{
                    val intent = Intent(context, SelectBoardOption::class.java)
                    itemView.context.startActivity(intent)
                }
            }
            Glide.with(binding.bannerImageView.context)
                .load(notificationText.service_profile_image)
                .into(binding.bannerImageView)

            binding.className.text = notificationText.service_profile_name
        }
    }
}