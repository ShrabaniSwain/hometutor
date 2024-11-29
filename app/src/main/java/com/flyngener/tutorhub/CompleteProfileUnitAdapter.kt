package com.flyngener.tutorhub

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.tutorhub.databinding.ItemUnitListBinding

class CompleteProfileUnitAdapter(
    private var unitData: List<SelectUnitResult>,
    private val context: Context
) : RecyclerView.Adapter<CompleteProfileUnitAdapter.CardViewHolder>() {

    private val allSelectedUnits = mutableListOf<SelectUnitResult>()
    private var alreadyEnteredText = ""
    private val selectedSubUnitsMap = mutableMapOf<String, MutableSet<String>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_unit_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(unitData[position])
    }

    override fun getItemCount(): Int = unitData.size

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemUnitListBinding.bind(itemView)

        private val selectedSubUnits = mutableSetOf<String>()
        private val unit1SelectedAdapter = StudentUnitListAdapter()

        init {
            binding.rvServices.adapter = unit1SelectedAdapter
            binding.rvServices.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        }

        @SuppressLint("NotifyDataSetChanged")
        fun bind(notificationText: SelectUnitResult) {

            binding.tvUnit.text = notificationText.unit_name

            val subUnitNames = notificationText.sub_unit_name.map { it.value.trim() }
            val adapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_dropdown_item_1line,
                subUnitNames
            )
            binding.etSelectUnit.setAdapter(adapter)

            binding.etSelectUnit.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) binding.etSelectUnit.showDropDown()
            }
            binding.etSelectUnit.setOnClickListener {
                binding.etSelectUnit.showDropDown()
            }


            binding.etSelectUnit.setOnItemClickListener { parent, _, subUnitPosition, _ ->
                val selectedSubUnit = parent.getItemAtPosition(subUnitPosition) as String
                handleSubUnitSelection(selectedSubUnit, notificationText)
            }

        }
        // A map to track sub-units for each unit_name

        private fun handleSubUnitSelection(subUnit: String, notificationText: SelectUnitResult) {
            unit1SelectedAdapter.addService(subUnit)
            val subUnitsForCurrentUnit = selectedSubUnitsMap.getOrPut(notificationText.unit_name) { mutableSetOf() }
            subUnitsForCurrentUnit.clear()
            subUnitsForCurrentUnit.add(subUnit)
            val selectedUnit = SelectUnitResult(
                id = notificationText.id,
                unit_name = notificationText.unit_name,
                sub_unit_name = subUnitsForCurrentUnit.map { SubUnit(it) }
            )
            allSelectedUnits.removeAll { it.unit_name == notificationText.unit_name }
            allSelectedUnits.add(selectedUnit)

            Constant.unitDetailsList = allSelectedUnits
            Log.d("SelectedUnit", "All Selected Units: $allSelectedUnits")
        }

        private fun parseUnitDetails(unitDetails: String): Map<String, String> {
            val cleanedDetails = unitDetails.replace("\"\"", "\"").replace("\"", "").trim()
            val detailMap = mutableMapOf<String, String>()
            val keyValuePairs = cleanedDetails.split(",")

            var currentKey: String? = null
            for (pair in keyValuePairs) {
                val keyValue = pair.split(":").map { it.trim() }
                if (keyValue.size == 2) {
                    currentKey = keyValue[0]
                    detailMap[currentKey] = keyValue[1]
                } else if (keyValue.size == 1 && currentKey != null) {
                    detailMap[currentKey] = detailMap[currentKey] + ", " + keyValue[0]
                }
            }
            return detailMap
        }

        private fun updateUnitDetails(detailMap: Map<String, String>) {
            val currentUnit = binding.tvUnit.text.toString().trim()
            val correspondingValue = detailMap[currentUnit]
            if (correspondingValue != null) {
                binding.etSelectUnit.setText(correspondingValue)
                alreadyEnteredText = binding.etSelectUnit.text.toString()
                Log.i("TAG", "etSelectUnit: $alreadyEnteredText")

            }
        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}