package com.flyngener.hometutor

import android.app.AlertDialog
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.hometutor.databinding.ItemTeacherFeaturedBinding

class TeacherFeaturedAdapter(val context: Context) : RecyclerView.Adapter<TeacherFeaturedAdapter.CardViewHolder>() {

    private val fee = listOf(
        "1000",
        "550",
        "500"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_teacher_featured, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = fee[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return fee.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTeacherFeaturedBinding.bind(itemView)

        fun bind(fee : String) {
            Utility.itemBackGround(itemView)
            binding.tvFee.text = fee
            binding.acceptBtn.setOnClickListener {
                showAcceptDialog()
            }
        }

        private fun showAcceptDialog() {
            val alertDialogBuilder = AlertDialog.Builder(context)
            val titleTextView = TextView(context)
            titleTextView.text = "Accept"
            titleTextView.setTextColor(ContextCompat.getColor(context, R.color.guardian_background_color))
            titleTextView.textSize = 20f
            titleTextView.setPadding(40, 30, 40, 15)
            alertDialogBuilder.setCustomTitle(titleTextView)
            titleTextView.typeface = Typeface.DEFAULT_BOLD

            alertDialogBuilder.setMessage("Are you sure you want to accept?")

            alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
            }

            alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

    }
}