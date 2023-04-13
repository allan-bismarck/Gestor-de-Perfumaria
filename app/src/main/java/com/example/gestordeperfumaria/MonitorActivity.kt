package com.example.gestordeperfumaria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gestordeperfumaria.databinding.ActivityMonitorBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.*

class MonitorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonitorBinding
    private lateinit var months: List<String>
    private lateinit var cosmetics: MutableList<CosmeticEntity>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListMonths()

        val adapter = ViewPagerAdapter(this, months, listOf(CosmeticEntity(0,"",0,"",0f,false)))
        binding.pager.adapter = adapter
        binding.pager.currentItem = months.lastIndex
    }

    private fun initListMonths() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = dateFormat.format(Date()).toString()
        val dateSplit = date.split('/')
        val year = dateSplit[2].toInt()
        val month = dateSplit[1].toInt()

        when(month) {
            1 -> months = listOf(
                "Fevereiro/${year-1}",
                "Março/${year-1}",
                "Abril/${year-1}",
                "Maio/${year-1}",
                "Junho/${year-1}",
                "Julho/${year-1}",
                "Agosto/${year-1}",
                "Setembro/${year-1}",
                "Outubro/${year-1}",
                "Novembro/${year-1}",
                "Dezembro/${year-1}",
                "Janeiro/${year}",
            )
            2 -> months = listOf(
                "Março/${year-1}",
                "Abril/${year-1}",
                "Maio/${year-1}",
                "Junho/${year-1}",
                "Julho/${year-1}",
                "Agosto/${year-1}",
                "Setembro/${year-1}",
                "Outubro/${year-1}",
                "Novembro/${year-1}",
                "Dezembro/${year-1}",
                "Janeiro/${year}",
                "Fevereiro/${year}",
            )
            3 -> months = listOf(
                "Abril/${year-1}",
                "Maio/${year-1}",
                "Junho/${year-1}",
                "Julho/${year-1}",
                "Agosto/${year-1}",
                "Setembro/${year-1}",
                "Outubro/${year-1}",
                "Novembro/${year-1}",
                "Dezembro/${year-1}",
                "Janeiro/${year}",
                "Fevereiro/${year}",
                "Março/${year}",
            )
            4 -> months = listOf(
                "Maio/${year-1}",
                "Junho/${year-1}",
                "Julho/${year-1}",
                "Agosto/${year-1}",
                "Setembro/${year-1}",
                "Outubro/${year-1}",
                "Novembro/${year-1}",
                "Dezembro/${year-1}",
                "Janeiro/${year}",
                "Fevereiro/${year}",
                "Março/${year}",
                "Abril/${year}",
            )
            5 -> months = listOf(
                "Junho/${year-1}",
                "Julho/${year-1}",
                "Agosto/${year-1}",
                "Setembro/${year-1}",
                "Outubro/${year-1}",
                "Novembro/${year-1}",
                "Dezembro/${year-1}",
                "Janeiro/${year}",
                "Fevereiro/${year}",
                "Março/${year}",
                "Abril/${year}",
                "Maio/${year}",
            )
            6 -> months = listOf(
                "Julho/${year-1}",
                "Agosto/${year-1}",
                "Setembro/${year-1}",
                "Outubro/${year-1}",
                "Novembro/${year-1}",
                "Dezembro/${year-1}",
                "Janeiro/${year}",
                "Fevereiro/${year}",
                "Março/${year}",
                "Abril/${year}",
                "Maio/${year}",
                "Junho/${year}",
            )
            7 -> months = listOf(
                "Agosto/${year-1}",
                "Setembro/${year-1}",
                "Outubro/${year-1}",
                "Novembro/${year-1}",
                "Dezembro/${year-1}",
                "Janeiro/${year}",
                "Fevereiro/${year}",
                "Março/${year}",
                "Abril/${year}",
                "Maio/${year}",
                "Junho/${year}",
                "Julho/${year}",
            )
            8 -> months = listOf(
                "Setembro/${year-1}",
                "Outubro/${year-1}",
                "Novembro/${year-1}",
                "Dezembro/${year-1}",
                "Janeiro/${year}",
                "Fevereiro/${year}",
                "Março/${year}",
                "Abril/${year}",
                "Maio/${year}",
                "Junho/${year}",
                "Julho/${year}",
                "Agosto/${year}",
            )
            9 -> months = listOf(
                "Outubro/${year-1}",
                "Novembro/${year-1}",
                "Dezembro/${year-1}",
                "Janeiro/${year}",
                "Fevereiro/${year}",
                "Março/${year}",
                "Abril/${year}",
                "Maio/${year}",
                "Junho/${year}",
                "Julho/${year}",
                "Agosto/${year}",
                "Setembro/${year}",
            )
            10 -> months = listOf(
                "Novembro/${year-1}",
                "Dezembro/${year-1}",
                "Janeiro/${year}",
                "Fevereiro/${year}",
                "Março/${year}",
                "Abril/${year}",
                "Maio/${year}",
                "Junho/${year}",
                "Julho/${year}",
                "Agosto/${year}",
                "Setembro/${year}",
                "Outubro/${year}",
            )
            11 -> months = listOf(
                "Dezembro/${year-1}",
                "Janeiro/${year}",
                "Fevereiro/${year}",
                "Março/${year}",
                "Abril/${year}",
                "Maio/${year}",
                "Junho/${year}",
                "Julho/${year}",
                "Agosto/${year}",
                "Setembro/${year}",
                "Outubro/${year}",
                "Novembro/${year}",
            )
            else -> months = listOf(
                "Janeiro/${year}",
                "Fevereiro/${year}",
                "Março/${year}",
                "Abril/${year}",
                "Maio/${year}",
                "Junho/${year}",
                "Julho/${year}",
                "Agosto/${year}",
                "Setembro/${year}",
                "Outubro/${year}",
                "Novembro/${year}",
                "Dezembro/${year}",
            )
        }
    }
}