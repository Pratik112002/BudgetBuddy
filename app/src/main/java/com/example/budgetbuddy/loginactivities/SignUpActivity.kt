package com.example.budgetbuddy.loginactivities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.budgetbuddy.onbording.OnBording1
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db=Firebase.firestore

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.btnContinue.setOnClickListener{
            val email=binding.txtEmail.text.toString()
            val password=binding.txtPassword.text.toString()
            val name=binding.txtName.text.toString()
            SignUpUser(email,password,name)
        }

        binding.loginButton.setOnClickListener {
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }


    }



    private fun SignUpUser(email: String, password: String,name:String) {
        if(email.isNotEmpty()&&password.isNotEmpty()){
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                       // val user = auth.currentUse
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            val user = mapOf(
                                "name" to name,
                                "email" to email
                            )
                            db.collection("Users").document(uid).collection("email").add(user)
                        } else {
                            println("UID is null, cannot save user data.")
                        }
                        Toast.makeText(this,"Successfully Login",Toast.LENGTH_SHORT).show()
                        navigateToOnbording()

                    } else {
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT,).show()

                    }
                }

        }

    }

    private fun navigateToOnbording() {
        val i=Intent(this, OnBording1::class.java)
        startActivity(i)
        finish()
    }

//    private fun navigateToHome() {
//        val i=Intent(this,MainActivity::class.java)
//        startActivity(i)
//        finish()
//    }
}