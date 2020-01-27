package com.example.linuxtest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.ogaclejapan.smarttablayout.SmartTabLayout


class IntroScreen : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_screen)
        val page = findViewById<ViewPager>(R.id.page)
        val skipBtn = findViewById<Button>(R.id.skip)
        val nextBtn = findViewById<Button>(R.id.next)
        val left = findViewById<SmartTabLayout>(R.id.indicator)
        val adapter = MyAdapter(supportFragmentManager)
        page.adapter=adapter
        left.setViewPager(page)
        left.setOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    skipBtn.visibility = View.GONE
                    nextBtn.text = "Done"
                } else {
                    skipBtn.visibility = View.VISIBLE
                }
            }
        })

        skipBtn.setOnClickListener {
            endTutorial()
        }

        nextBtn.setOnClickListener {
            if(page.currentItem==2){
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
        startActivity(Intent(this,MainActivity::class.java))
    }
}

class MyAdapter(fragManager: FragmentManager) :
    FragmentPagerAdapter(fragManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Page1()
            1 -> Page2()
            2 -> Page3()
            else -> Page1()
        }
    }
}
