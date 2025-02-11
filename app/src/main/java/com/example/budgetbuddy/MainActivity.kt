package com.example.budgetbuddy

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.budgetbuddy.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Set up ViewPager2 with Adapter
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = adapter

        // Link TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.icon = when(position) {
                0 -> ContextCompat.getDrawable(this, R.drawable.home)
                1 -> ContextCompat.getDrawable(this, R.drawable.transaction)
                2 -> ContextCompat.getDrawable(this, R.drawable.profile)
                else -> null
            }
        }.attach()
    }
}
