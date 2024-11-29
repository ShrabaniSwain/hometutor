package com.flyngener

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.FullScreenImageActivity
import com.flyngener.tutorhub.GuardianTask
import com.flyngener.tutorhub.R
import com.flyngener.tutorhub.Utility
import com.flyngener.tutorhub.databinding.TaskDetailsItemBinding

class TaskDeatilsAdapter(val context: Context, val taskGuardianList: List<GuardianTask>) : RecyclerView.Adapter<TaskDeatilsAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_details_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = taskGuardianList[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return taskGuardianList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = TaskDetailsItemBinding.bind(itemView)

        fun bind(task : GuardianTask) {
            Utility.itemBackGround(itemView)
            binding.task.text = task.task_title
            binding.tvDate.text = task.task_date
            binding.tvTime.text = task.task_time
            binding.ScheduleDate.text = task.schedule_date
            binding.tvExcellent.text = task.task_rating
            binding.tvTaskInfo.text = task.task_remark
            Glide.with(binding.imageView.context)
                .load(task.task_image).apply(RequestOptions.placeholderOf(R.drawable.noimageavailbale))
                .into(binding.imageView)

            binding.tvExcellent.visibility = View.VISIBLE
            binding.tvTaskInfo.visibility = View.VISIBLE
            binding.btnUpdate.visibility = View.GONE


            binding.imageCardVIew.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("IMAGE_URL", task.task_image)
                context.startActivity(intent)
            }
        }

        private fun showDialog(context: Context) {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.task_details_dialog)

            val imageView = dialog.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.ivMainImage)
            val close = dialog.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.ivClose)
            imageView.setImageResource(R.drawable.imagecardview)
            close.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

    }
}