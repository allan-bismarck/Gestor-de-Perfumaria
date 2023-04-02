package com.example.gestordeperfumaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.gestordeperfumaria.databinding.ActivityMainBinding
import kotlinx.coroutines.Deferred
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

        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "db-perfumery"
        )
            .allowMainThreadQueries().build()

        binding.btnRegister.setOnClickListener {
            //dbInsertBrand("Avon", 0.2f)
            dbInsertCosmetic("Perfume", "Natura", 100.0f)
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnMonitor.setOnClickListener {
            //dbShowAllBrands()
            //dbShowBrand(23)
            //dbDeleteBrand(10)
            //dbDeleteAllBrands()
            //dbUpdateBrand(BrandEntity(0, "Boticario", 0.15f), 25)
            dbShowAllCosmetics()
            startActivity(Intent(this, MonitorActivity::class.java))
        }
    }

    private fun dbInsertBrand(name: String, profit: Float) = runBlocking {
        val insertBrands = launch {
            val brandEntity = BrandEntity(0, name, profit)
            val result = db.brandDAO.insert(brandEntity)
            if(result > 0) {
                Log.i(result.toString(), "Marca cadastrada com sucesso!")
            }
        }
        insertBrands.join()
    }

    private fun dbShowAllBrands() = runBlocking {
        var brandList = async { db.brandDAO.getAll() }
        Log.i("brandList", brandList.await().toString())
    }

    private fun dbShowBrand(id: Long) = runBlocking {
        var brand = async { db.brandDAO.get(id) }
        Log.i("brandList", brand.await().toString())
    }

    private fun dbDeleteBrand(id: Long) = runBlocking {
        val deleteBrand = launch {
            db.brandDAO.delete(id)
        }
        deleteBrand.join()
    }

    private fun dbDeleteAllBrands() = runBlocking {
        val deleteAllBrands = launch {
            db.brandDAO.deleteAll()
        }
        deleteAllBrands.join()
    }

    private fun dbUpdateBrand(brand: BrandEntity, id: Long) = runBlocking {
        val updateBrand = launch {
            val nameUpdate = brand.name
            val profitUpdate = brand.profit
            db.brandDAO.update(nameUpdate, profitUpdate, id)
        }
        updateBrand.join()
    }

    private fun dbInsertCosmetic(name: String, nameBrand: String, price: Float) = runBlocking {
        val insertCosmetic = launch {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val date = dateFormat.format(Date()).toString()
            val cosmeticEntity = CosmeticEntity(0, name, nameBrand, date, price)
            val result = db.cosmeticDAO.insert(cosmeticEntity)
            if(result > 0) {
                Log.i(result.toString(), "Cosmético cadastrado com sucesso!")
            }
        }
        insertCosmetic.join()
    }

    private fun dbShowAllCosmetics() = runBlocking {
        var cosmesticList = async { db.cosmeticDAO.getAll().value }
        Log.i("cosmeticList", cosmesticList.await().toString())
    }
}