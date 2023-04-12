package com.example.gestordeperfumaria

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 12
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                MonitorFragment.cOText = "1"
                MonitorFragment()
            }
            1 -> {
                MonitorFragment.cOText = "2"
                MonitorFragment()
            }
            2 -> {
                MonitorFragment.cOText = "3"
                MonitorFragment()
            }
            3 -> {
                MonitorFragment.cOText = "4"
                MonitorFragment()
            }
            4 -> {
                MonitorFragment.cOText = "5"
                MonitorFragment()
            }
            5 -> {
                MonitorFragment.cOText = "6"
                MonitorFragment()
            }
            6 -> {
                MonitorFragment.cOText = "7"
                MonitorFragment()
            }
            7 -> {
                MonitorFragment.cOText = "8"
                MonitorFragment()
            }
            8 -> {
                MonitorFragment.cOText = "9"
                MonitorFragment()
            }
            9 -> {
                MonitorFragment.cOText = "10"
                MonitorFragment()
            }
            10 -> {
                MonitorFragment.cOText = "11"
                MonitorFragment()
            }
            else ->  {
                MonitorFragment.cOText = "12"
                MonitorFragment()
            }
        }
    }
}