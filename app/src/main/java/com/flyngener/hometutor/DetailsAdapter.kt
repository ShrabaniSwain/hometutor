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
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.hometutor.databinding.DetailsItemBinding

class DetailsAdapter(val context: Context) : RecyclerView.Adapter<DetailsAdapter.CardViewHolder>() {

    private val name = listOf(
        "Sudip Das",
        "Shrabani Meghamala",
        "Rajesh Sas"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.details_item, parent, false)
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
        private val binding = DetailsItemBinding.bind(itemView)

        fun bind(name : String) {
            binding.tvGuardiansName.text = name
            Utility.itemBackGround(itemView)

            binding.btnEdit.setOnClickListener {
                val intent = Intent(context, CompleteProfileActivity::class.java)
                context.startActivity(intent)
            }

            binding.btnResign.setOnClickListener { view ->
                val context = view.context
                val dialogBuilder = AlertDialog.Builder(context)
                val inflater = LayoutInflater.from(context).inflate(R.layout.resign_dialog_box, null)
                dialogBuilder.setView(inflater)
                val close = inflater.findViewById<ImageView>(R.id.ivClose)

                val dialog = dialogBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                close.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()

            }

        }

    }
}