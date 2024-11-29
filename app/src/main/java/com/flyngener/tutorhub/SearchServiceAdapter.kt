package com.flyngener.tutorhub

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.tutorhub.databinding.ItemSearchListBinding

class SearchServiceAdapter(private val notification: List<SearchKeyword>, val context: Context) : RecyclerView.Adapter<SearchServiceAdapter.CardViewHolder>() {

    private var filteredMarketItems: List<SearchKeyword> = notification

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_list, parent, false)
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
        private val binding = ItemSearchListBinding.bind(itemView)

        fun bind(service: SearchKeyword) {
            Utility.itemBackGround(itemView)
            binding.serviceName.text = service.service_profile_name
            binding.serviceName.setOnClickListener {
                    Constant.PROFILEID = service.id.toInt()
                    Constant.RANDOM_NO = service.random_number.toString()
                    Constant.CLICKTYPE = Constant.PROFILECLICK
                    if (service.service_profile) {
                        val intent =
                            Intent(context, SelectTeacher::class.java)
                        itemView.context.startActivity(intent)
                    }
                    else{
                        val intent = Intent(context, SelectBoardOption::class.java)
                        itemView.context.startActivity(intent)
                    }

            }
        }
    }

    fun updateList(newList: List<SearchKeyword>) {
        filteredMarketItems = newList
        notifyDataSetChanged()
    }
}