package com.flyngener.hometutor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.hometutor.databinding.SupportDetailsItemBinding

class SupportDeatilsAdapter(val applicationContext: Context, val result: List<SupportPageResult>) : RecyclerView.Adapter<SupportDeatilsAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.support_details_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = result[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return result.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = SupportDetailsItemBinding.bind(itemView)

        fun bind(supportDetails : SupportPageResult) {
            Utility.itemBackGround(itemView)
            binding.supportDetails.text = supportDetails.subject
            binding.tvSupportFullDetails.text = supportDetails.details
            binding.tvIdNo.text = supportDetails.id

        }

    }
}