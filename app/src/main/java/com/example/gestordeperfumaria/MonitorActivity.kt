package com.example.gestordeperfumaria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gestordeperfumaria.databinding.ActivityMonitorBinding
import com.google.android.material.tabs.TabLayoutMediator

class MonitorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonitorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.pager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when(position) {
                0 -> {
                    tab.text = "Janeiro"
                }
                1 -> {
                    tab.text = "Fevereiro"
                }
                2 -> {
                    tab.text = "Março"
                }
                3 -> {
                    tab.text = "Abril"
                }
                4 -> {
                    tab.text = "Maio"
                }
                5 -> {
                    tab.text = "Junho"
                }
                6 -> {
                    tab.text = "Julho"
                }
                7 -> {
                    tab.text = "Agosto"
                }
                8 -> {
                    tab.text = "Setembro"
                }
                9 -> {
                    tab.text = "Outubro"
                }
                10 -> {
                    tab.text = "Novembro"
                }
                else -> {
                    tab.text = "Dezembro"
                }
            }
        }.attach()

    }
}