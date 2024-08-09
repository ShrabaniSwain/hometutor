package com.flyngener.hometutor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.hometutor.databinding.GuardianNotificationItemListBinding

class GuardianNotificationAdapter : RecyclerView.Adapter<GuardianNotificationAdapter.CardViewHolder>() {

    private val notificationData = listOf(
        "Notification 1: Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "Notification 2: Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "Notification 3: Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        "Notification 4: Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "Notification 5: Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "Notification 6: Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.guardian_notification_item_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = notificationData[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return notificationData.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = GuardianNotificationItemListBinding.bind(itemView)

        fun bind(notificationText: String) {
            Utility.itemBackGround(itemView)
            binding.tvNotificationdetails.text = notificationText
        }
    }
}
