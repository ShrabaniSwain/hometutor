package com.flyngener.tutorhub

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flyngener.tutorhub.databinding.ItemSelectTeacherBinding

class SelectTeacherAdapter(val isHome: List<ServiceProfileGuardian>, val context: Context) : RecyclerView.Adapter<SelectTeacherAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_select_teacher, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = isHome[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return isHome.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSelectTeacherBinding.bind(itemView)

        fun bind(notificationText: ServiceProfileGuardian) {
            Utility.itemBackGround(itemView)
            binding.tvClassName.text = notificationText.service_profile_name
            Glide.with(context).load(notificationText.service_profile_image ?: "").into(binding.ivEditProfileImage)

            binding.selectTeacher.setOnClickListener {
                Constant.PROFILEID = notificationText.id.toInt()
                Constant.RANDOM_NO = notificationText.random_number.toString()
                Constant.isQuestion = notificationText.question

                if (notificationText.service_profile){
                    Constant.CLICKTYPE = Constant.PROFILECLICK
                    Constant.PROFILEID = notificationText.id.toInt()
                    val intent = Intent(context, SelectTeacher::class.java)
                    itemView.context.startActivity(intent)
                }
                else {
                    val intent = Intent(context, SelectBoardOption::class.java)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}