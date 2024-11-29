package com.flyngener.tutorhub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.tutorhub.databinding.GuardianNotificationItemListBinding

class GuardianNotificationAdapter(
    val context: Context,
    private val notification: List<PaymentReminder>
) : RecyclerView.Adapter<GuardianNotificationAdapter.CardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.guardian_notification_item_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = notification[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return notification.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = GuardianNotificationItemListBinding.bind(itemView)

        fun bind(notificationText: PaymentReminder) {
            Utility.itemBackGround(itemView)
            binding.tvNotificationdetails.text = notificationText.notification_details
            binding.tvPaymentReminder.text = notificationText.notification_title
            binding.tvPaymentAmount.text = "₹" + notificationText.amount
            binding.tvPaymentDate.text = notificationText.notification_date
        }
    }
}
