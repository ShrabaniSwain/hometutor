package com.flyngener.tutorhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.tutorhub.databinding.TaskReviewItemBinding

class TaskReviewAdapter() : RecyclerView.Adapter<TaskReviewAdapter.CardViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION
    private var selectedText: String? = null
    private val taskReview = listOf(
        "Good",
        "Excellent",
        "Bad",
        "Very Bad"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_review_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = taskReview[position]
        holder.bind(item, position)

    }

    override fun getItemCount(): Int {
        return taskReview.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = TaskReviewItemBinding.bind(itemView)

        init {
            binding.radioButton.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition != selectedPosition) {
                    selectedPosition = currentPosition
                    selectedText = taskReview[currentPosition]
                    notifyDataSetChanged()
                }
            }
        }

        fun bind(notificationText: String, position: Int) {
            binding.radioButton.text = notificationText
            binding.radioButton.isChecked = (position == selectedPosition)
        }



    }

    fun getSelectedText(): String? {
        return selectedText
    }
}