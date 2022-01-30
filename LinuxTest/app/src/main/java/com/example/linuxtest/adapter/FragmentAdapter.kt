package com.example.linuxtest.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.linuxtest.fragments.Page1
import com.example.linuxtest.fragments.Page2
import com.example.linuxtest.fragments.Page3

class FragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Page1()
            1 -> Page2()
            else -> Page3()
        }
    }
}
