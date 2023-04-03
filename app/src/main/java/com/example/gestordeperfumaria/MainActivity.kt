package com.example.gestordeperfumaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
        ).fallbackToDestructiveMigration()
            .allowMainThreadQueries().build()

        binding.btnRegister.setOnClickListener {
            dbInsertBrand("Avon", 0.2f)
            //dbInsertCosmetic("Perfume", 1, 100.0f)
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnMonitor.setOnClickListener {
            //dbShowAllBrands()
            //dbShowBrand(23)
            dbDeleteBrand(3)
            //dbDeleteAllBrands()
            //dbUpdateBrand("Boticario", 0.15f, 25)
            //dbShowAllCosmetics()
            //dbDeleteCosmetic(2)
            //dbDeleteAllCosmetics()
            //dbUpdateCosmetic("Colonia", 2, 25.0f, 6)
            startActivity(Intent(this, MonitorActivity::class.java))
        }
    }

    private fun dbInsertBrand(name: String, profit: Float) = runBlocking {
        val insertBrands = launch {
            val brandEntity = BrandEntity(0, name, profit)
            val result = db.brandDAO.insert(brandEntity)
            if(result > 0) {
                val message = "Marca cadastrada com sucesso!"
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
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
        val listCosmetics = async { db.cosmeticDAO.getAll() }.await()
        listCosmetics.forEach {
            if(it.idBrand == id) {
                val message = "Não é possível excluir a marca selecionada, pois existem cosméticos cadastrados dessa marca!"
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                return@runBlocking
            }
        }
        val deleteBrand = launch {
            db.brandDAO.delete(id)
        }
        deleteBrand.join()
    }

    private fun dbDeleteAllBrands() = runBlocking {
        val listCosmetics = db.cosmeticDAO.getAll()
        if(listCosmetics.isNotEmpty()) {
            val message = "Ainda existem cosméticos cadastrados, não é possível excluir todas as marcas."
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        } else {
            val deleteAllBrands = launch {
                db.brandDAO.deleteAll()
            }
            deleteAllBrands.join()
        }
    }

    private fun dbUpdateBrand(name: String, profit: Float, id: Long) = runBlocking {
        val updateBrand = launch {
            db.brandDAO.update(name, profit, id)
        }
        updateBrand.join()
    }

    private fun dbInsertCosmetic(name: String, idBrand: Long, price: Float) = runBlocking {
        val insertCosmetic = launch {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val date = dateFormat.format(Date()).toString()
            val cosmeticEntity = CosmeticEntity(0, name, idBrand, date, price)
            val result = db.cosmeticDAO.insert(cosmeticEntity)
            if(result > 0) {
                val message = "Cosmético cadastrado com sucesso!"
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
        insertCosmetic.join()
    }

    private fun dbShowAllCosmetics() = runBlocking {
        var cosmesticList = async { db.cosmeticDAO.getAll() }
        Log.i("cosmeticList", cosmesticList.await().toString())
    }

    private fun dbDeleteCosmetic(id: Long) = runBlocking {
        val deleteCosmetic = launch {
            db.cosmeticDAO.delete(id)
        }
        deleteCosmetic.join()
    }

    private fun dbDeleteAllCosmetics() = runBlocking {
        val deleteAllCosmetics = launch {
            db.cosmeticDAO.deleteAll()
        }
        deleteAllCosmetics.join()
    }

    private fun dbUpdateCosmetic(name: String, idBrand: Long, price: Float, id: Long) = runBlocking {
        val updateCosmetic = launch {
            db.cosmeticDAO.update(name, idBrand, price, id)
        }
        updateCosmetic.join()
    }
}