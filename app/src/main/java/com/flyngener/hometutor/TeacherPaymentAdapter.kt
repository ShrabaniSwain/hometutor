package com.flyngener.hometutor

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.hometutor.databinding.ItemPaymentListBinding

class TeacherPaymentAdapter(val context: Context) : RecyclerView.Adapter<TeacherPaymentAdapter.CardViewHolder>() {

    private val name = listOf(
        "500",
        "1000",
        "1500"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_payment_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = name[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return name.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemPaymentListBinding.bind(itemView)

        fun bind(name : String) {
            Utility.itemBackGround(itemView)
            binding.tvFee.text = name
        }

    }
}