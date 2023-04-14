package com.example.gestordeperfumaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import com.example.gestordeperfumaria.databinding.ActivityMainBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "db-perfumery"
        ).allowMainThreadQueries().build()

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.btnMonitor.setOnClickListener {
            startActivity(Intent(this, MonitorActivity::class.java))
        }

        binding.btnRules.setOnClickListener {
            startActivity(Intent(this, RulesActivity::class.java))
        }

        clearOldTransactions()
    }

    private fun dbShowAllCosmetics() = runBlocking {
        var cosmeticList = async { db.cosmeticDAO.getAll() }.await()
        Log.i("cosmeticList", cosmeticList.toString())
        return@runBlocking cosmeticList
    }

    private fun dbShowCosmeticByPeriod(initialPeriod: String, endPeriod: String) = runBlocking {
        if((initialPeriod.isNotEmpty() && endPeriod.isNotEmpty()) && (isDateValid(initialPeriod) && isDateValid(endPeriod))) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val initialPos = ParsePosition(0)
            val convertInitialDate = dateFormat.parse(initialPeriod, initialPos)
            val endPos = ParsePosition(0)
            val convertEndDate = dateFormat.parse(endPeriod, endPos)
            if(convertEndDate.after(convertInitialDate) || convertEndDate == convertInitialDate) {
                var cosmetic = async { db.cosmeticDAO.getAll() }.await()
                var filterCosmetic = mutableListOf<CosmeticEntity>()
                cosmetic.forEach {
                    val cosmeticPos = ParsePosition(0)
                    val cosmeticDate = dateFormat.parse(it.date, cosmeticPos)
                    if ((convertInitialDate.before(cosmeticDate) || initialPeriod == it.date)
                        && (convertEndDate.after(cosmeticDate) || endPeriod == it.date)
                    ) {
                        filterCosmetic.add(it)
                    }
                }
                Log.i("cosmeticList", filterCosmetic.toString())
            } else {
                val message = "A data inicial deve anteceder ou ser igual à data final"
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        } else {
            val message = "Digite as datas inicial e final para realizar a pesquisa"
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun dbDeleteCosmetic(id: Long) = runBlocking {
        val deleteCosmetic = launch {
            db.cosmeticDAO.delete(id)
        }
        deleteCosmetic.join()
    }

    private fun isDateValid(date: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val pos = ParsePosition(0)
        val convertDate = dateFormat.parse(date, pos)
        try {
            val localDate = dateFormat.format(convertDate)
            return true
        } catch(e: Exception) { }
        return false
    }

    private fun clearOldTransactions() {
        var cosmeticList = dbShowAllCosmetics() as MutableList<CosmeticEntity>
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = dateFormat.format(Date()).toString()
        val dateSplit = date.split('/')
        cosmeticList.forEach {
            val listDateSplit = it.date.split('/')
            if((listDateSplit[2].toInt() <= (dateSplit[2].toInt() - 1))) {
                if(listDateSplit[2].toInt() <= (dateSplit[2].toInt() - 2)) {
                    dbDeleteCosmetic(it.id)
                }
                if(listDateSplit[1].toInt() <= dateSplit[1].toInt()) {
                    dbDeleteCosmetic(it.id)
                }
            }
        }
    }
}