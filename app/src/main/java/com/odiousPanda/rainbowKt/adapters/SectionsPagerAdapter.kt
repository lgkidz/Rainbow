package com.odiousPanda.rainbowKt.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionsPagerAdapter(
    fm: FragmentManager,
    private val fragmentList: List<Fragment>
): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }
}