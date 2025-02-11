package com.example.budgetbuddy

import TransactionDetail
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionHistoryAdapter(
    private val context: Context,
    private val transactionList: MutableList<TransactionDetail>,
    private val onDataChange: () -> Unit // Callback to notify the fragment
) : RecyclerView.Adapter<TransactionHistoryAdapter.TransactionHistoryViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class TransactionHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryText: TextView = itemView.findViewById(R.id.txt_Category)
        val amountText: TextView = itemView.findViewById(R.id.txt_amount)
        val paymentText: TextView = itemView.findViewById(R.id.txt_paymentType)
        val btn_delete: ImageView = itemView.findViewById(R.id.btn_delete)
        val time: TextView = itemView.findViewById(R.id.txt_timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionHistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionHistoryViewHolder, position: Int) {
        val transaction = transactionList[position]

        holder.categoryText.text = transaction.category
        holder.amountText.text = transaction.amount
        holder.paymentText.text = transaction.paymentType

        val formattedDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(transaction.createdAt)
        holder.time.text = formattedDate




        holder.btn_delete.setOnClickListener {
            showDialog(position, transaction.Id)
        }
    }



    private fun showDialog(position: Int, transactionId: String) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteTransactionFromFirestore(transactionId, position)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteTransactionFromFirestore(transactionId: String, position: Int) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("Users")
            .document(uid)
            .collection("Transaction")
            .document(transactionId) // Use the unique transaction ID
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Transaction deleted successfully", Toast.LENGTH_SHORT).show()

                // Ensure that the position index is valid before performing list operations
                if (position >= 0 && position < transactionList.size) {
                    transactionList.removeAt(position)
                    notifyItemRemoved(position) // Notify adapter only if index is valid
                    onDataChange() // Notify the fragment to recalculate spent/available amounts and update the Pie Chart
                }
            }

    }
    override fun getItemCount(): Int = transactionList.size
}
