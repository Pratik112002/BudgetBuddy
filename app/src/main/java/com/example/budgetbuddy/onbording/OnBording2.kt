package com.example.budgetbuddy.onbording

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.budgetbuddy.MainActivity
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.ActivityOnBording2Binding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class OnBording2 : AppCompatActivity() {
    private lateinit var binding: ActivityOnBording2Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityOnBording2Binding.inflate(layoutInflater)
        auth = Firebase.auth
        db = Firebase.firestore
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.btnContinue.setOnClickListener {

            var amount = binding.txtAmount.text.toString()
            var profession=binding.txtProfession.text.toString()
            var live=binding.txtLive.text.toString()
            saveData(amount,profession,live)
        }


    }

    private fun saveData(amount: String,profession:String,live:String) {
        if (amount.isNotEmpty()) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val financeinfo = mapOf(
                    "amount" to amount,
                    "profession" to profession,
                    "live" to live
                )
                db.collection("Users").document(uid).collection("Information").add(financeinfo)
                navigateToNext()
            } else {
            println("UID is null, cannot save user data.")
            }
        }
    }

    private fun navigateToNext() {
        val intent= Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}