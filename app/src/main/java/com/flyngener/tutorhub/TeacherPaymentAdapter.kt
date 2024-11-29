package com.flyngener.tutorhub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.tutorhub.databinding.ItemPaymentListBinding

class TeacherPaymentAdapter(val context: Context, val payment: List<TeacherPaymentData>) : RecyclerView.Adapter<TeacherPaymentAdapter.CardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_payment_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = payment[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return payment.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemPaymentListBinding.bind(itemView)

        fun bind(name : TeacherPaymentData) {
            Utility.itemBackGround(itemView)
            binding.tvFee.text = name.payment_amount
            binding.tvPaymentId.text = "Reference Id : #" + name.id
            binding.tvTransactionId.text = "TransactionI d : " + name.transaction_id
            binding.tvPaymentMethodName.text = name.payment_type
            binding.tvPaymentMethodName.text = name.payment_type
            binding.tvDate.text = name.payment_date
            if (name.payment_status == "Successful"){
                binding.btnSuccessful.visibility = View.VISIBLE
            }else{
                binding.btnUnSuccessful.visibility = View.VISIBLE
            }
        }

    }
}