package com.example.budgetbuddy

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    private val fragments = listOf(
        HomeFragment(),
        TransactionFragment(),
        ProfileFragment()

    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
