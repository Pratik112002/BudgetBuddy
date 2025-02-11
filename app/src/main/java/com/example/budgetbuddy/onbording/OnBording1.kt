package com.example.budgetbuddy.onbording

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.ActivityOnbording1Binding


class OnBording1 : AppCompatActivity() {

    private lateinit var binding:ActivityOnbording1Binding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding=ActivityOnbording1Binding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnNext.setOnClickListener{

            val intent= Intent(this,OnBording2::class.java)
            startActivity(intent)

        }

    }
}