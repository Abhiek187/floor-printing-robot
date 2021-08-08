package com.example.linuxtest.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.linuxtest.databinding.ActivityIntroScreenBinding
import com.example.linuxtest.fragments.Page1
import com.example.linuxtest.fragments.Page2
import com.example.linuxtest.fragments.Page3
import com.example.linuxtest.storage.Prefs
import com.google.android.material.tabs.TabLayoutMediator

class IntroScreen : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIntroScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val page = binding.page
        val skipBtn = binding.skip
        val nextBtn = binding.next
        val left = binding.indicator
        val adapter = MyAdapter(this)

        page.adapter = adapter
        TabLayoutMediator(left, page) { _, _ -> run {} }.attach()
        page.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                skipBtn.visibility = if (position == 2) View.GONE else View.VISIBLE
            }
        })

        skipBtn.setOnClickListener {
            endTutorial()
        }

        nextBtn.setOnClickListener {
            if (page.currentItem == 2) {
                endTutorial()
            }
            else{
                page.setCurrentItem(page.currentItem+1,true)
            }
        }
    }

    private fun endTutorial(){
        val sharedPref = Prefs(this)
        sharedPref.isFirst=false
        startActivity(Intent(this, MainActivity::class.java))
    }
}

class MyAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Page2()
            1 -> Page1()
            2 -> Page3()
            else -> Page1()
        }
    }
}
