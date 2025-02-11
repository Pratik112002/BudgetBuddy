package com.example.budgetbuddy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.budgetbuddy.databinding.FragmentBottomSheetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class BottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)

        // Initialize Firebase instances here
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup options for the dropdown list
        val options = arrayOf("Food & Dining","Electronics & Gadgets","Housing & Utilities",
            "Transportation","Health","Clothing","Accessories","Entertainment","Education","Investments","Miscellaneous")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options)
        binding.autoCompleteTextView.setAdapter(adapter)

        // Collect inputs from user and handle the save button click
        binding.btnAdd.setOnClickListener {
            val amount = binding.txtAmount.text.toString().trim()
            val category = binding.autoCompleteTextView.text.toString().trim()

            // Validate inputs
            if (amount.isEmpty() || category.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get the selected RadioButton's value
            val selectedRadioId = binding.radioGroup.checkedRadioButtonId
            if (selectedRadioId == -1) {
                Toast.makeText(context, "Please select a payment method", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRadioButton = view.findViewById<RadioButton>(selectedRadioId)
            val paymentMethod = selectedRadioButton.text.toString()

// Generate the current timestamp
            val formattedDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
            val timestamp = formattedDate

// Save data to Firestore
            saveData(amount, category, paymentMethod, timestamp)

        }
    }

    private fun saveData(amount: String, category: String, paymentMethod: String, timestamp: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique document reference
        val transactionRef = db.collection("Users")
            .document(uid)
            .collection("Transaction")
            .document()
        val transactionId = transactionRef.id
// Get the generated unique ID

        val transaction = hashMapOf(
            "id" to transactionId, // Save the unique ID
            "amount" to amount,
            "category" to category,
            "payment" to paymentMethod,
            "timestamp" to timestamp
        )

        transactionRef.set(transaction)
            .addOnSuccessListener {
                Toast.makeText(context, "Transaction saved successfully", Toast.LENGTH_SHORT).show()
                // Clear fields
                binding.txtAmount.text?.clear()
                binding.autoCompleteTextView.text?.clear()
                binding.radioGroup.clearCheck()
                dismiss() // Close the BottomSheet
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
