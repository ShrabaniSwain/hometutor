package com.flyngener.tutorhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentUnitListAdapter : RecyclerView.Adapter<StudentUnitListAdapter.ViewHolder>() {
    private val selectedItems = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.selected_items_services, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = selectedItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = selectedItems.size

    fun addService(unitName: String) {
        // Replace the existing item if it's already in the list
        val index = selectedItems.indexOf(unitName)
        if (index != -1) {
            selectedItems[index] = unitName // Update existing item (this step is optional if the value is the same)
        } else {
            // Clear the list if a new item is added
            selectedItems.clear()
            selectedItems.add(unitName)
        }
        notifyDataSetChanged()
    }

    fun removeService(position: Int) {
        if (position in selectedItems.indices) {
            selectedItems.removeAt(position)
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val serviceNameTextView: TextView = itemView.findViewById(R.id.tvSelectedServicesName)
        private val removeButton: ImageView = itemView.findViewById(R.id.btnRemove)

        fun bind(serviceName: String) {
            serviceNameTextView.text = serviceName

            removeButton.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    removeService(currentPosition)
                }
            }
        }
    }
}
