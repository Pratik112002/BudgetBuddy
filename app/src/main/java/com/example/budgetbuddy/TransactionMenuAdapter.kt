package com.example.budgetbuddy


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TransactionMenuAdapter(
    private val context: Context,
    private val transactionMenuList:MutableList<TransactionMenuDetails>,
    private val onItemClick: (TransactionMenuDetails) -> Unit // Callback for item clicks



) : RecyclerView.Adapter<TransactionMenuAdapter.TransactionMenuAdapterViewHolder>() {


    class TransactionMenuAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category:TextView=itemView.findViewById(R.id.txt_menu_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionMenuAdapterViewHolder {
        val itemView=LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_menu,parent,false)
        return TransactionMenuAdapterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionMenuAdapterViewHolder, position: Int) {
        val transactionMenu=transactionMenuList[position]
        holder.category.text=transactionMenu.category

        holder.itemView.setOnClickListener {
            onItemClick(transactionMenu) // Invoke the callback with the clicked item
        }
    }


    override fun getItemCount(): Int = transactionMenuList.size
}
