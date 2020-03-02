package com.example.linuxtest.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.example.linuxtest.databinding.ActivityIntroScreenBinding
import com.example.linuxtest.fragments.Page1
import com.example.linuxtest.fragments.Page2
import com.example.linuxtest.fragments.Page3
import com.example.linuxtest.storage.Prefs

class IntroScreen : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIntroScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val page = binding.page
        val skipBtn = binding.skip
        val nextBtn = binding.next
        val left = binding.indicator
        val adapter = MyAdapter(supportFragmentManager)

        page.adapter=adapter
        left.setViewPager(page)
        left.setOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    skipBtn.visibility = View.GONE
                } else {
                    skipBtn.visibility = View.VISIBLE
                }
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

class MyAdapter(fragManager: FragmentManager) :
    FragmentPagerAdapter(fragManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Page2()
            1 -> Page1()
            2 -> Page3()
            else -> Page1()
        }
    }
}
