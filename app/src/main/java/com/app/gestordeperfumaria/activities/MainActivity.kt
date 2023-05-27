package com.app.gestordeperfumaria.activities
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.app.gestordeperfumaria.R
import com.app.gestordeperfumaria.database.AppDataBase
import com.app.gestordeperfumaria.entities.CosmeticEntity
import com.app.gestordeperfumaria.databinding.ActivityMainBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        //Glide.with(this).load(R.drawable.gestor_de_perfumaria_icon).into(binding.imgView)

        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            applicationContext.resources.getString(R.string.DATABASE_NAME)
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
        return@runBlocking cosmeticList
    }

    private fun dbDeleteCosmetic(id: Long) = runBlocking {
        val deleteCosmetic = launch {
            db.cosmeticDAO.delete(id)
        }
        deleteCosmetic.join()
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