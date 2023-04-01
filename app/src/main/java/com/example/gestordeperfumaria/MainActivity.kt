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
            dbInsertBrand("Avon", 0.2f)
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnMonitor.setOnClickListener {
            dbShowAllBrands()
            //dbShowBrand(23)
            //dbDeleteBrand(10)
            //dbDeleteAllBrands()
            //dbUpdateBrand(BrandEntity(0, "Boticario", 0.15f), 25)
            startActivity(Intent(this, MonitorActivity::class.java))
        }
    }

    private fun dbInsertBrand(name: String, prefit: Float) = runBlocking {
        val insertBrands = launch {
            val brandEntity = BrandEntity(0, name, prefit)
            val result = db.brandDAO.insert(brandEntity)
            if(result > 0) {
                Log.i(result.toString(), "Marca cadastrada com sucesso!")
            }
        }
        insertBrands.join()
    }

    private fun dbShowAllBrands() = runBlocking{
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
}