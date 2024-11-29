package com.flyngener.tutorhub

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.tutorhub.databinding.ItemServiceUnitBinding

class ServiceUnitAdapter(val unitData: List<SelectUnitResult>, val context: Context) : RecyclerView.Adapter<ServiceUnitAdapter.CardViewHolder>() {
    private val allSelectedUnits = mutableListOf<SelectUnitResult>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service_unit, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = unitData[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return unitData.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemServiceUnitBinding.bind(itemView)

        @SuppressLint("NotifyDataSetChanged")
        fun bind(notificationText: SelectUnitResult) {
            binding.etSelectUnit.hint = notificationText.unit_name

            val subUnitNames = notificationText.sub_unit_name.map { it.value.trim() }
            val adapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_dropdown_item_1line,
                subUnitNames
            )
            binding.etSelectUnit.setAdapter(adapter)

            binding.etSelectUnit.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.etSelectUnit.showDropDown()
                }
            }
            binding.etSelectUnit.setOnClickListener {
                binding.etSelectUnit.showDropDown()
            }

            val selectedSubUnits = mutableSetOf<String>()
            val unit1SelectedAdapter = Unit1SelectedAdapter()
            binding.rvServices.adapter = unit1SelectedAdapter
            binding.rvServices.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

            binding.etSelectUnit.setOnItemClickListener { parent, _, subUnitPosition, _ ->
                val selectedSubUnit = parent.getItemAtPosition(subUnitPosition) as String
                unit1SelectedAdapter.addService(selectedSubUnit)

                if (selectedSubUnits.add(selectedSubUnit)) {

                    val unitId = notificationText.id
                    val unitName = notificationText.unit_name
                    val selectedUnit = SelectUnitResult(
                        id = unitId,
                        unit_name = unitName,
                        sub_unit_name = selectedSubUnits.map { SubUnit(it) }
                    )

                    val existingUnitIndex = allSelectedUnits.indexOfFirst { it.id == unitId }
                    if (existingUnitIndex != -1) {
                        allSelectedUnits[existingUnitIndex] = selectedUnit
                    } else {
                        allSelectedUnits.add(selectedUnit)
                    }

                    Constant.unitDetailsList = allSelectedUnits
                    Log.d("SelectedUnit", "All Selected Units: $allSelectedUnits")
                }
            }
        }



    }
}