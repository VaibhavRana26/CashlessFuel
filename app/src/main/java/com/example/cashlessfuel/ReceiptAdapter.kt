package com.example.cashlessfuel

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashlessfuel.model.mapModel.Invoice


class ReceiptAdapter(val userList: ArrayList<Invoice>) : RecyclerView.Adapter<ReceiptAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_receipts, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ReceiptAdapter.ViewHolder, position: Int) {
        holder.bindItems(userList[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(user: Invoice) {
            val textViewName = itemView.findViewById(R.id.tv_gas_station) as TextView
            val textViewAddress  = itemView.findViewById(R.id.tv_amount) as TextView
            textViewName.text = user.name+"\n"+user.date
            textViewAddress.text = user.amount

            itemView.setOnClickListener {
                val intent = Intent(itemView.context,InvoiceActivity::class.java)
                itemView.context.startActivity(intent)
            }
        }


    }
}