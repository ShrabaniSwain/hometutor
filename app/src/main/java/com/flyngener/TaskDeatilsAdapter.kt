package com.flyngener

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.hometutor.FullScreenImageActivity
import com.flyngener.hometutor.R
import com.flyngener.hometutor.Utility
import com.flyngener.hometutor.databinding.TaskDetailsItemBinding

class TaskDeatilsAdapter(val context: Context) : RecyclerView.Adapter<TaskDeatilsAdapter.CardViewHolder>() {

    private val task = listOf(
        "Lorem Ipsum Dolor Sit Amet",
        "Lorem Ipsum Dolor Sit Amet",
        "Lorem Ipsum Dolor Sit Amet"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_details_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = task[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return task.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = TaskDetailsItemBinding.bind(itemView)

        fun bind(task : String) {
            Utility.itemBackGround(itemView)
            binding.task.text = task
            binding.tvExcellent.visibility = View.VISIBLE
            binding.tvTaskInfo.visibility = View.VISIBLE
            binding.btnUpdate.visibility = View.GONE
            binding.imageCardVIew.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("IMAGE_RES_ID", R.drawable.imagecardview)
                context.startActivity(intent)
            }
        }

        private fun showDialog(context: Context) {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.task_details_dialog)

            val imageView = dialog.findViewById<ImageView>(R.id.ivMainImage)
            val close = dialog.findViewById<ImageView>(R.id.ivClose)
            imageView.setImageResource(R.drawable.imagecardview)
            close.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

    }
}