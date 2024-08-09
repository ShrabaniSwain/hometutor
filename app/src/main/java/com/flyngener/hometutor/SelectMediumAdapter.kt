package com.flyngener.hometutor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.hometutor.databinding.SelectBoardBinding

class SelectMediumAdapter(val subUnitName: List<SubUnit>,     private val listener: OnSubUnitSelectedListener) : RecyclerView.Adapter<SelectMediumAdapter.CardViewHolder>() {
    private var selectedPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_board, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = subUnitName[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return subUnitName.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = SelectBoardBinding.bind(itemView)

        fun bind(notificationText: SubUnit) {
            binding.selectBoardName.text = notificationText.value

            setSelected(adapterPosition == selectedPosition)
            setSelected(isSelected = false)

            binding.cardView.setOnClickListener {
                selectedPosition = if (adapterPosition == selectedPosition) {
                    setSelected(isSelected = false)
                    listener.onSubUnitSelected(SubUnit(""))
                    null
                } else {
                    selectedPosition?.let { prevSelectedPosition ->
                        notifyItemChanged(prevSelectedPosition)
                    }
                    setSelected(isSelected = true)
                    listener.onSubUnitSelected(notificationText)
                    adapterPosition

                }
            }


        }


        private fun setSelected(isSelected: Boolean) {
            if (isSelected) {
                binding.selectBoardName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.cardView.setBackgroundResource(R.drawable.green_background_color)
            } else {
                binding.selectBoardName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.guardian_background_color))
                binding.cardView.setBackgroundResource(R.drawable.border_color)
            }
        }

    }
}

interface OnSubUnitSelectedListener {
    fun onSubUnitSelected(subUnit: SubUnit)
}