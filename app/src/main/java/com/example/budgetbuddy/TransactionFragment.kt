package com.example.budgetbuddy

import TransactionDetail
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.databinding.FragmentTransactionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class TransactionFragment : Fragment() {
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var adapter: TransactionHistoryAdapter
    private lateinit var menuadapter: TransactionMenuAdapter
    private val transactionList = mutableListOf<TransactionDetail>()
    private var filteredTransactionList = mutableListOf<TransactionDetail>()
    private val transactionMenuList = mutableListOf<TransactionMenuDetails>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupMenuRecyclerView()
        setupRecyclerView()
        fetchTransactionHistory()

        return binding.root
    }

    private fun setupMenuRecyclerView() {
        menuadapter = TransactionMenuAdapter(requireContext(), transactionMenuList) { menuItem ->
            // Handle menu item click
            Log.d("TransactionFragment", "Clicked on category: ${menuItem.category}")
            filterTransactionsByCategory(menuItem.category) // Call filtering function
        }

        binding.recyclerViewMenu.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewMenu.adapter = menuadapter

        fetchTransactionMenuData()
    }

    private fun fetchTransactionMenuData() {
        transactionMenuList.add(TransactionMenuDetails("Food & Dining"))
        transactionMenuList.add(TransactionMenuDetails("Electronics & Gadgets"))
        transactionMenuList.add(TransactionMenuDetails("Housing & Utilities"))
        transactionMenuList.add(TransactionMenuDetails("Transportation"))
        transactionMenuList.add(TransactionMenuDetails("Clothing"))
        transactionMenuList.add(TransactionMenuDetails("Accessories"))
        transactionMenuList.add(TransactionMenuDetails("Entertainment"))
        transactionMenuList.add(TransactionMenuDetails("Education"))
        transactionMenuList.add(TransactionMenuDetails("Investments"))
        transactionMenuList.add(TransactionMenuDetails("Miscellaneous"))

        menuadapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        adapter = TransactionHistoryAdapter(requireContext(), filteredTransactionList) {
            // Handle callbacks if necessary
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchTransactionHistory() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("Users").document(uid).collection("Transaction")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("TransactionFragment", "Snapshot error: ${exception.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    transactionList.clear()

                    for (doc in snapshot.documents) {
                        val amount = doc.getString("amount")?.toFloatOrNull() ?: 0.0f
                        val category = doc.getString("category") ?: "Unknown"
                        val paymentType = doc.getString("payment") ?: "Unknown"

                        transactionList.add(
                            TransactionDetail(
                                Id = doc.id,
                                amount = amount.toString(),
                                category = category,
                                paymentType = paymentType
                            )
                        )
                    }

                    // Set filtered data to all by default
                    filteredTransactionList.clear()
                    filteredTransactionList.addAll(transactionList)
                    adapter.notifyDataSetChanged()
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterTransactionsByCategory(category: String) {
        filteredTransactionList.clear()
        if (category == "All") {
            filteredTransactionList.addAll(transactionList) // No filter, show all
        } else {
            filteredTransactionList.addAll(transactionList.filter { it.category == category })
        }

        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

