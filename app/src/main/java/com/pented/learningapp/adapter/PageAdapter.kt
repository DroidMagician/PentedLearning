package com.pented.learningapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.pented.learningapp.authScreens.fragment.FragmentRegisterYourselfOne
import com.pented.learningapp.authScreens.fragment.FragmentRegisterYourselfTwo

class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 1;
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return FragmentRegisterYourselfOne()
            }
//            1 -> {
//                return FragmentRegisterYourselfTwo()
//            }
            else -> {
                return FragmentRegisterYourselfOne()
            }
        }
    }

}
