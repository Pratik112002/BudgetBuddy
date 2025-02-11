package com.example.budgetbuddy

import TransactionDetail
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.eazegraph.lib.models.PieModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var transactionHistoryAdapter: TransactionHistoryAdapter
    private val transactionList = mutableListOf<TransactionDetail>()

    private var totalBalance: Float = 0.0f
    private var totalSpent: Float = 0.0f // Track total spending dynamically

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup RecyclerView
        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(parentFragmentManager, BottomSheetFragment::class.java.simpleName)
        }

        val uid = auth.currentUser?.uid
        if (uid != null) {
            fetchUserDetails(uid)
        } else {
            Log.e("HomeFragment", "Firebase Auth: UID is null.")
            binding.txtName.text = "Guest"
        }
    }

    private fun fetchUserDetails(uid: String) {
        fetchUserName(uid)
        fetchUserAmount(uid)
    }

    private fun fetchUserName(uid: String) {
        db.collection("Users").document(uid).collection("email").get()
            .addOnSuccessListener { snapshot ->
                val name = snapshot.documents.firstOrNull()?.getString("name") ?: "Guest"
                binding.txtName.text = name
            }
            .addOnFailureListener {
                Log.e("HomeFragment", "Error fetching user name: ${it.message}")
                binding.txtName.text = "Guest"
            }
    }

    private fun fetchUserAmount(uid: String) {
        db.collection("Users").document(uid).collection("Information").get()
            .addOnSuccessListener { snapshot ->
                totalBalance = snapshot.documents.firstOrNull()?.getString("amount")?.toFloatOrNull() ?: 0.0f
                binding.txtAmount.text = "₹ $totalBalance"
                fetchTransactionHistory()
            }
            .addOnFailureListener {
                Log.e("HomeFragment", "Error fetching amount: ${it.message}")
                binding.txtAmount.text = "₹ 0.0"
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchTransactionHistory() {
        db.collection("Users").document(auth.currentUser?.uid ?: "").collection("Transaction")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("HomeFragment", "Snapshot error: ${exception.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    transactionList.clear()
                    totalSpent = 0.0f
                    for (doc in snapshot.documents) {
                        val amount = doc.getString("amount")?.toFloatOrNull() ?: 0.0f
                        val category = doc.getString("category") ?: "Unknown"
                        val paymentType = doc.getString("payment") ?: "Unknown"

                        totalSpent += amount
                        transactionList.add(TransactionDetail(Id = doc.id, amount = amount.toString(), category = category, paymentType = paymentType))
                    }

                    transactionHistoryAdapter.notifyDataSetChanged()
                    updateExpensesUI()
                } else {
                    Log.e("HomeFragment", "No transactions found.")
                    totalSpent = 0.0f
                    updateExpensesUI()
                }
            }
    }

    private fun setupRecyclerView() {
        transactionHistoryAdapter = TransactionHistoryAdapter(requireContext(), transactionList) {
            updateExpensesUI()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = transactionHistoryAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun updateExpensesUI() {
        binding.txtExpenses.text = "₹ $totalSpent"
        val availableBalance = totalBalance - totalSpent
        binding.txtAmountAvailable.text = "₹ $availableBalance"
        updatePieChart(totalSpent, availableBalance)
    }

    private fun updatePieChart(amountSpent: Float, remainingBalance: Float) {
        binding.piechart.clearChart()

        binding.piechart.addPieSlice(PieModel("Spent", amountSpent, Color.parseColor("#ffb9aa")))
        binding.piechart.addPieSlice(PieModel("Available", remainingBalance, Color.parseColor("#b7d9ae")))

        binding.piechart.startAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
