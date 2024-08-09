package com.flyngener.hometutor

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.hometutor.databinding.ItemRadioBtnBinding

class RadioBtnAdpter(private val notification: List<Option>, val context: Context) : RecyclerView.Adapter<RadioBtnAdpter.CardViewHolder>() {

    private var filteredMarketItems: List<Option> = notification
    private val selectedServices: MutableList<Answer> = mutableListOf()
    val selectedStates = mutableMapOf<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_radio_btn, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = filteredMarketItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return filteredMarketItems.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemRadioBtnBinding.bind(itemView)

        fun bind(service: Option) {

            val options = service.value
            binding.rvRadioBtn.text = options

            binding.rvRadioBtn.setOnClickListener {
                Constant.ANSWER = binding.rvRadioBtn.text.toString()

                val itemId = Constant.QuestionId
                val currentState = selectedStates[itemId] ?: false

                val newState = !currentState
                selectedStates[itemId] = newState

                binding.rvRadioBtn.isChecked = newState

                if (newState) {
                    // Add the item to selectedServices if it was selected
                    selectedServices.add(Answer(question_id = itemId, answer = binding.rvRadioBtn.text.toString()))
                } else {
                    // Remove the item from selectedServices if it was deselected
                    selectedServices.removeIf { it.question_id == itemId }
                }

                Log.i("TAG", "selectedServices: $selectedServices")
            }
        }
    }

}