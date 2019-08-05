package com.example.music_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.music_app.Adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureTabLayout()
    }
    private fun configureTabLayout(){
        tab_layout.addTab(tab_layout.newTab().setText("Tab 1"))
        tab_layout.addTab(tab_layout.newTab().setText("Tab 2"))
        val adapter = ViewPagerAdapter(supportFragmentManager, tab_layout.tabCount)
        view_pager.adapter = adapter
        tab_layout.setupWithViewPager(view_pager)
    }
}
