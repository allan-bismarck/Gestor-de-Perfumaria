package com.example.gestordeperfumaria.activities

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.example.gestordeperfumaria.R
import com.example.gestordeperfumaria.entities.CosmeticEntity
import com.example.gestordeperfumaria.adapters.ViewPagerAdapter
import com.example.gestordeperfumaria.database.AppDataBase
import com.example.gestordeperfumaria.databinding.ActivityMonitorBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class MonitorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonitorBinding
    private lateinit var db: AppDataBase
    private lateinit var months: List<String>
    private lateinit var cosmetics: List<CosmeticEntity>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "db-perfumery"
        ).allowMainThreadQueries().build()

        initLists()

        val adapter = ViewPagerAdapter(this, months, cosmetics)
        binding.pager.adapter = adapter
        binding.pager.currentItem = months.lastIndex
    }

    private fun initLists() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = dateFormat.format(Date()).toString()
        val dateSplit = date.split('/')
        val year = dateSplit[2].toInt()
        val month = dateSplit[1].toInt()

        initCosmetics()
        initListMonths(month, year)
    }

    private fun initListMonths(month: Int, year: Int) {
        val res = Resources.getSystem()

        when(month) {
            1 -> months = listOf(
                "${res.getString(R.string.month_2)}/${year-1}",
                "${res.getString(R.string.month_3)}/${year-1}",
                "${res.getString(R.string.month_4)}/${year-1}",
                "${res.getString(R.string.month_5)}/${year-1}",
                "${res.getString(R.string.month_6)}/${year-1}",
                "${res.getString(R.string.month_7)}/${year-1}",
                "${res.getString(R.string.month_8)}/${year-1}",
                "${res.getString(R.string.month_9)}/${year-1}",
                "${res.getString(R.string.month_10)}/${year-1}",
                "${res.getString(R.string.month_11)}/${year-1}",
                "${res.getString(R.string.month_12)}/${year-1}",
                "${res.getString(R.string.month_1)}/${year}",
            )
            2 -> months = listOf(
                "${res.getString(R.string.month_3)}/${year-1}",
                "${res.getString(R.string.month_4)}/${year-1}",
                "${res.getString(R.string.month_5)}/${year-1}",
                "${res.getString(R.string.month_6)}/${year-1}",
                "${res.getString(R.string.month_7)}/${year-1}",
                "${res.getString(R.string.month_8)}/${year-1}",
                "${res.getString(R.string.month_9)}/${year-1}",
                "${res.getString(R.string.month_10)}/${year-1}",
                "${res.getString(R.string.month_11)}/${year-1}",
                "${res.getString(R.string.month_12)}/${year-1}",
                "${res.getString(R.string.month_1)}/${year}",
                "${res.getString(R.string.month_2)}/${year}",
            )
            3 -> months = listOf(
                "${res.getString(R.string.month_4)}/${year-1}",
                "${res.getString(R.string.month_5)}/${year-1}",
                "${res.getString(R.string.month_6)}/${year-1}",
                "${res.getString(R.string.month_7)}/${year-1}",
                "${res.getString(R.string.month_8)}/${year-1}",
                "${res.getString(R.string.month_9)}/${year-1}",
                "${res.getString(R.string.month_10)}/${year-1}",
                "${res.getString(R.string.month_11)}/${year-1}",
                "${res.getString(R.string.month_12)}/${year-1}",
                "${res.getString(R.string.month_1)}/${year}",
                "${res.getString(R.string.month_2)}/${year}",
                "${res.getString(R.string.month_3)}/${year}",
            )
            4 -> months = listOf(
                "${res.getString(R.string.month_5)}/${year-1}",
                "${res.getString(R.string.month_6)}/${year-1}",
                "${res.getString(R.string.month_7)}/${year-1}",
                "${res.getString(R.string.month_8)}/${year-1}",
                "${res.getString(R.string.month_9)}/${year-1}",
                "${res.getString(R.string.month_10)}/${year-1}",
                "${res.getString(R.string.month_11)}/${year-1}",
                "${res.getString(R.string.month_12)}/${year-1}",
                "${res.getString(R.string.month_1)}/${year}",
                "${res.getString(R.string.month_2)}/${year}",
                "${res.getString(R.string.month_3)}/${year}",
                "${res.getString(R.string.month_4)}/${year}",
            )
            5 -> months = listOf(
                "${res.getString(R.string.month_6)}/${year-1}",
                "${res.getString(R.string.month_7)}/${year-1}",
                "${res.getString(R.string.month_8)}/${year-1}",
                "${res.getString(R.string.month_9)}/${year-1}",
                "${res.getString(R.string.month_10)}/${year-1}",
                "${res.getString(R.string.month_11)}/${year-1}",
                "${res.getString(R.string.month_12)}/${year-1}",
                "${res.getString(R.string.month_1)}/${year}",
                "${res.getString(R.string.month_2)}/${year}",
                "${res.getString(R.string.month_3)}/${year}",
                "${res.getString(R.string.month_4)}/${year}",
                "${res.getString(R.string.month_5)}/${year}",
            )
            6 -> months = listOf(
                "${res.getString(R.string.month_7)}/${year-1}",
                "${res.getString(R.string.month_8)}/${year-1}",
                "${res.getString(R.string.month_9)}/${year-1}",
                "${res.getString(R.string.month_10)}/${year-1}",
                "${res.getString(R.string.month_11)}/${year-1}",
                "${res.getString(R.string.month_12)}/${year-1}",
                "${res.getString(R.string.month_1)}/${year}",
                "${res.getString(R.string.month_2)}/${year}",
                "${res.getString(R.string.month_3)}/${year}",
                "${res.getString(R.string.month_4)}/${year}",
                "${res.getString(R.string.month_5)}/${year}",
                "${res.getString(R.string.month_6)}/${year}",
            )
            7 -> months = listOf(
                "${res.getString(R.string.month_8)}/${year-1}",
                "${res.getString(R.string.month_9)}/${year-1}",
                "${res.getString(R.string.month_10)}/${year-1}",
                "${res.getString(R.string.month_11)}/${year-1}",
                "${res.getString(R.string.month_12)}/${year-1}",
                "${res.getString(R.string.month_1)}/${year}",
                "${res.getString(R.string.month_2)}/${year}",
                "${res.getString(R.string.month_3)}/${year}",
                "${res.getString(R.string.month_4)}/${year}",
                "${res.getString(R.string.month_5)}/${year}",
                "${res.getString(R.string.month_6)}/${year}",
                "${res.getString(R.string.month_7)}/${year}",
            )
            8 -> months = listOf(
                "${res.getString(R.string.month_9)}/${year-1}",
                "${res.getString(R.string.month_10)}/${year-1}",
                "${res.getString(R.string.month_11)}/${year-1}",
                "${res.getString(R.string.month_12)}/${year-1}",
                "${res.getString(R.string.month_1)}/${year}",
                "${res.getString(R.string.month_2)}/${year}",
                "${res.getString(R.string.month_3)}/${year}",
                "${res.getString(R.string.month_4)}/${year}",
                "${res.getString(R.string.month_5)}/${year}",
                "${res.getString(R.string.month_6)}/${year}",
                "${res.getString(R.string.month_7)}/${year}",
                "${res.getString(R.string.month_8)}/${year}",
            )
            9 -> months = listOf(
                "${res.getString(R.string.month_10)}/${year-1}",
                "${res.getString(R.string.month_11)}/${year-1}",
                "${res.getString(R.string.month_12)}/${year-1}",
                "${res.getString(R.string.month_1)}/${year}",
                "${res.getString(R.string.month_2)}/${year}",
                "${res.getString(R.string.month_3)}/${year}",
                "${res.getString(R.string.month_4)}/${year}",
                "${res.getString(R.string.month_5)}/${year}",
                "${res.getString(R.string.month_6)}/${year}",
                "${res.getString(R.string.month_7)}/${year}",
                "${res.getString(R.string.month_8)}/${year}",
                "${res.getString(R.string.month_9)}/${year}",
            )
            10 -> months = listOf(
                "${res.getString(R.string.month_11)}/${year-1}",
                "${res.getString(R.string.month_12)}/${year-1}",
                "${res.getString(R.string.month_1)}/${year}",
                "${res.getString(R.string.month_2)}/${year}",
                "${res.getString(R.string.month_3)}/${year}",
                "${res.getString(R.string.month_4)}/${year}",
                "${res.getString(R.string.month_5)}/${year}",
                "${res.getString(R.string.month_6)}/${year}",
                "${res.getString(R.string.month_7)}/${year}",
                "${res.getString(R.string.month_8)}/${year}",
                "${res.getString(R.string.month_9)}/${year}",
                "${res.getString(R.string.month_10)}/${year}",
            )
            11 -> months = listOf(
                "${res.getString(R.string.month_12)}/${year-1}",
                "${res.getString(R.string.month_1)}/${year}",
                "${res.getString(R.string.month_2)}/${year}",
                "${res.getString(R.string.month_3)}/${year}",
                "${res.getString(R.string.month_4)}/${year}",
                "${res.getString(R.string.month_5)}/${year}",
                "${res.getString(R.string.month_6)}/${year}",
                "${res.getString(R.string.month_7)}/${year}",
                "${res.getString(R.string.month_8)}/${year}",
                "${res.getString(R.string.month_9)}/${year}",
                "${res.getString(R.string.month_10)}/${year}",
                "${res.getString(R.string.month_11)}/${year}",
            )
            else -> months = listOf(
                "${res.getString(R.string.month_1)}/${year}",
                "${res.getString(R.string.month_2)}/${year}",
                "${res.getString(R.string.month_3)}/${year}",
                "${res.getString(R.string.month_4)}/${year}",
                "${res.getString(R.string.month_5)}/${year}",
                "${res.getString(R.string.month_6)}/${year}",
                "${res.getString(R.string.month_7)}/${year}",
                "${res.getString(R.string.month_8)}/${year}",
                "${res.getString(R.string.month_9)}/${year}",
                "${res.getString(R.string.month_10)}/${year}",
                "${res.getString(R.string.month_11)}/${year}",
                "${res.getString(R.string.month_12)}/${year}",
            )
        }
    }

    private fun initCosmetics() {
        runBlocking {
            cosmetics = dbShowAllCosmetics()
        }
    }

    private suspend fun dbShowAllCosmetics() = runBlocking {
        var cosmesticList = async { db.cosmeticDAO.getAll() }
        return@runBlocking cosmesticList
    }.await()
}