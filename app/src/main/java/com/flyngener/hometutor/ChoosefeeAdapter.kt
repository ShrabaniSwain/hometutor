package com.flyngener.hometutor

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.hometutor.databinding.SelectBoardBinding

class ChoosefeeAdapter(val freesList: List<Fee>, val applicationContext: Context, private val listener: OnFeeItemSelectedListener) : RecyclerView.Adapter<ChoosefeeAdapter.CardViewHolder>() {
    private var selectedPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_board, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = freesList[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return freesList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = SelectBoardBinding.bind(itemView)

        fun bind(notificationText: Fee) {
            binding.selectPrice.text = "₹" + notificationText.fees_amount
            binding.unitType.visibility = View.VISIBLE
            binding.selectPrice.visibility = View.VISIBLE
            binding.selectBoardName.visibility = View.GONE
            binding.unitType.text = notificationText.unit_type
            setSelected(adapterPosition == selectedPosition)

            setSelected(isSelected = false)

            binding.cardView.setOnClickListener {
                if (adapterPosition == selectedPosition) {
                    setSelected(isSelected = false)
                    selectedPosition = null
                    Constant.BUDGET = ""
                    listener.onItemSelected(false)
                } else {
                    selectedPosition?.let { prevSelectedPosition ->
                        notifyItemChanged(prevSelectedPosition)
                    }
                    setSelected(isSelected = true)
                    selectedPosition = adapterPosition
                    Constant.BUDGET = binding.selectPrice.text.toString()
                    Constant.UNIT_TYPE = notificationText.unit_type
                    listener.onItemSelected(true)
                }
            }

        }

        private fun setSelected(isSelected: Boolean) {
            if (isSelected) {
                binding.selectBoardName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.selectPrice.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.unitType.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.cardView.setBackgroundResource(R.drawable.green_background_color)
            } else {
                binding.selectBoardName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green))
                binding.selectPrice.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green))
                binding.unitType.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                binding.cardView.setBackgroundResource(R.drawable.border_color)
            }
        }

    }

    interface OnFeeItemSelectedListener {
        fun onItemSelected(isSelected: Boolean)
    }

}