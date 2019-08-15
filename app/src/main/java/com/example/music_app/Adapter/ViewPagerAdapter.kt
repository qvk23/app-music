package com.example.music_app.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.music_app.View.FavoriteSongFragment
import com.example.music_app.View.PlayListFragment

class ViewPagerAdapter(fm: FragmentManager,private var tabCount: Int): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment? {
        when(position){
            0 -> return PlayListFragment()
            1 -> return FavoriteSongFragment()
            else -> return null
        }
    }

    override fun getCount(): Int {
        return tabCount
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        when(position){
            0 -> title = "Play List"
            1 -> title = "Favorite Song"
        }
        return title
    }
}
