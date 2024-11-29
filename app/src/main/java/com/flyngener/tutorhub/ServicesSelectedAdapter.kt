package com.flyngener.tutorhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ServicesSelectedAdapter(val servicesList: MutableList<ServiceProfile>) :
    RecyclerView.Adapter<ServicesSelectedAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.selected_items_services, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(servicesList[position])
    }

    override fun getItemCount(): Int = servicesList.size

    fun addService(service: ServiceProfile) {
        if (!servicesList.contains(service)) {
            servicesList.add(service)
            notifyDataSetChanged()
        }

    }

    fun removeService(position: Int) {
        servicesList.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val serviceNameTextView: TextView = itemView.findViewById(R.id.tvSelectedServicesName)
        private val removeButton: ImageView = itemView.findViewById(R.id.btnRemove)

        fun bind(service: ServiceProfile) {
            serviceNameTextView.text = service.serviceProfileName

            removeButton.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    removeService(currentPosition)
                }
            }
        }
    }
}
