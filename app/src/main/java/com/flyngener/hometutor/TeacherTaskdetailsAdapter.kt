package com.flyngener.hometutor

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.hometutor.databinding.TaskDetailsItemBinding

class TeacherTaskdetailsAdapter(val context: Context) : RecyclerView.Adapter<TeacherTaskdetailsAdapter.CardViewHolder>() {

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
            binding.tvExcellent.visibility = View.GONE
            binding.tvTaskInfo.visibility = View.GONE
            binding.btnUpdate.visibility = View.VISIBLE

            binding.btnUpdate.setOnClickListener { view ->
                val context = view.context
                val dialogBuilder = AlertDialog.Builder(context)
                val inflater = LayoutInflater.from(context).inflate(R.layout.update_task_dialog_box, null)
                dialogBuilder.setView(inflater)
                val rvReview = inflater.findViewById<RecyclerView>(R.id.rvReview)
                val close = inflater.findViewById<ImageView>(R.id.ivClose)

                val dialog = dialogBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val taskReviewAdapter = TaskReviewAdapter()
                rvReview.adapter = taskReviewAdapter
                rvReview.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)

                close.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()

            }


            binding.imageCardVIew.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("IMAGE_RES_ID", R.drawable.imagecardview)
                context.startActivity(intent)
            }

        }

    }
}