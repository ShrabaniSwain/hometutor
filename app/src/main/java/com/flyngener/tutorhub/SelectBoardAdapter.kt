package com.flyngener.tutorhub

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.tutorhub.databinding.UnitTypeSelectBinding


class SelectBoardAdapter(val selectUnit: List<SelectUnitResult>, val applicationContext: Context) : RecyclerView.Adapter<SelectBoardAdapter.CardViewHolder>(), OnSubUnitSelectedListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.unit_type_select, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = selectUnit[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return selectUnit.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = UnitTypeSelectBinding.bind(itemView)

        fun bind(notificationText: SelectUnitResult) {
            binding.selectBoard.text = notificationText.unit_name

            val selectMediumAdapter = SelectMediumAdapter(notificationText.sub_unit_name,this@SelectBoardAdapter)
            binding.rvMedium.adapter = selectMediumAdapter
            binding.rvMedium.layoutManager = GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)

        }

    }
    override fun onSubUnitSelected(subUnit: SubUnit) {
        if (subUnit.value.isNotEmpty()) {
            val selectedUnit = selectUnit.find { unit ->
                unit.sub_unit_name.any { it.value == subUnit.value }
            }

            selectedUnit?.let {
                val newUnit = UnitSelectList(it.id, it.unit_name, subUnit.value)

                // Check if the list already contains an entry for the same unit_name
                val existingIndex = Constant.selectUnitList.indexOfFirst { unit ->
                    unit.unit_name == it.unit_name
                }

                // If the unit_name already exists, update it
                if (existingIndex != -1) {
                    // Update the element in the MutableList
                    Constant.selectUnitList[existingIndex] = newUnit
                } else {
                    // Otherwise, add the new unit selection
                    Constant.selectUnitList.add(newUnit)
                }
            }
        } else {
            Constant.selectUnitList.clear()
        }

        Log.i("TAG", "Selected Unit: ${Constant.selectUnitList}")
    }



}