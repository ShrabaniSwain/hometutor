package com.flyngener.hometutor

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.hometutor.databinding.ItemActiveListBinding

class TeacherActiveListAdapter(val context: Context) : RecyclerView.Adapter<TeacherActiveListAdapter.CardViewHolder>() {

    private val name = listOf(
        "Shrabani swain",
        "Sudip Das",
        "Ashit Kumar",
        "Komal Kalal"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_active_list, parent, false)
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
        private val binding = ItemActiveListBinding.bind(itemView)

        fun bind(name : String) {
            Utility.itemBackGround(itemView)
            binding.tvName.text = name

            binding.btnInfo.setOnClickListener {
                val intent = Intent(context, DetailsActivity::class.java)
                context.startActivity(intent)
            }
            binding.btnTask.setOnClickListener {
                val intent = Intent(context, TeacherTaskDetailsActivity::class.java)
                context.startActivity(intent)
            }
        }

    }
}