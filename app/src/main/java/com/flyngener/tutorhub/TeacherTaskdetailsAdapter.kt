package com.flyngener.tutorhub

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.tutorhub.databinding.TaskDetailsItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherTaskdetailsAdapter(val context: Context, val taskTeacherList: List<TaskDetails>) : RecyclerView.Adapter<TeacherTaskdetailsAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_details_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = taskTeacherList[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return taskTeacherList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = TaskDetailsItemBinding.bind(itemView)

        fun bind(task : TaskDetails) {
            Utility.itemBackGround(itemView)

            binding.task.text = task.task_title
            binding.tvDate.text = task.task_date
            binding.tvTime.text = task.task_time
            binding.ScheduleDate.text = task.schedule_date
            Glide.with(binding.imageView.context)
                .load(task.task_image).apply(RequestOptions.placeholderOf(R.drawable.noimageavailbale))
                .into(binding.imageView)

            binding.tvExcellent.visibility = View.GONE
            binding.tvTaskInfo.visibility = View.GONE
            binding.btnUpdate.visibility = View.VISIBLE

            binding.btnUpdate.setOnClickListener { view ->
                val context = view.context
                val dialogBuilder = AlertDialog.Builder(context)
                val inflater = LayoutInflater.from(context).inflate(R.layout.update_task_dialog_box, null)
                dialogBuilder.setView(inflater)
                val rvReview = inflater.findViewById<RecyclerView>(R.id.rvReview)
                val close = inflater.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.ivClose)
                val etRemarks = inflater.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.etRemarks)
                val submitButton = inflater.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.submitButton)

                val dialog = dialogBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val taskReviewAdapter = TaskReviewAdapter()
                rvReview.adapter = taskReviewAdapter
                rvReview.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)

                submitButton.setOnClickListener {
                    val rating = taskReviewAdapter.getSelectedText()
                    if (etRemarks.text.toString().isEmpty() || rating.toString().isEmpty()) {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val taskUpdate = TaskDetailsUpdate(Constant.INSERT_ID, task.id, etRemarks.text.toString(), rating.toString())
                        teacherUpdateTask(taskUpdate)
                        dialog.dismiss()
                        val insertId = Constant.INSERT_ID
                        val taskId = task.id
                        val remarks = etRemarks.text.toString()

                        Log.i("TaskDetails", "INSERT_ID: $insertId, Task ID: $taskId, Remarks: $remarks, Rating: $rating")
                    }
                }
                close.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }


            binding.imageCardVIew.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("IMAGE_URL", task.task_image)
                context.startActivity(intent)
            }


        }

        private fun teacherUpdateTask(taskDetailsUpdate: TaskDetailsUpdate) {
            val call = RetrofitClient.api.teacherUpdateTask(taskDetailsUpdate)
            call.enqueue(object : Callback<HomeTutorModel> {
                override fun onResponse(
                    call: Call<HomeTutorModel>,
                    response: Response<HomeTutorModel>
                ) {
                    if (response.isSuccessful) {
                        val updateProfileResponse = response.body()
                        Log.i("TAG", "onResponse: " + response.body())
                        updateProfileResponse?.let {
                            if (it.isSuccess) {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } else {
                        Log.e("API", "API call failed with code ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<HomeTutorModel>, t: Throwable) {
                    Log.e("API", "API call failed with exception: ${t.message}")
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
}