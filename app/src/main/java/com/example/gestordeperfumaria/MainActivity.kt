package com.example.gestordeperfumaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.gestordeperfumaria.databinding.ActivityMainBinding
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DateFormat
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDataBase
    private lateinit var searchName: String
    private lateinit var searchName2: String

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
            //dbInsertBrand("Avon", 0.2f)
            dbInsertCosmetic("Perfume", 1, 100.0f, true)
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnMonitor.setOnClickListener {
            searchName = binding.editText.text.toString()
            searchName2 = binding.editText2.text.toString()
            //dbShowAllBrands()
            //dbShowBrandByName(searchName)
            //dbDeleteBrand(3)
            //dbDeleteAllBrands()
            //dbUpdateBrand("Boticario", 0.15f, 25)
            //dbShowAllCosmetics()
            //dbShowCosmeticByName(searchName)
            //dbDeleteCosmetic(2)
            //dbDeleteAllCosmetics()
            //dbUpdateCosmetic("Colonia", 2, 25.0f, 6)
            //dbShowBrandWithCosmetics(1)
            dbShowCosmeticByPeriod(searchName, searchName2)
            startActivity(Intent(this, MonitorActivity::class.java))
            binding.editText.text.clear()
            binding.editText2.text.clear()
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

    private fun dbShowBrandByName(name: String) = runBlocking {
        if(name.isNotEmpty()) {
            var nameLower = name.lowercase()
            nameLower = nameLower[0].uppercase() + nameLower.substring(1, nameLower.lastIndex + 1)
            try {
                var brand = async { db.brandDAO.get(nameLower) }
                Log.i("brandList", brand.await().toString())
            } catch (e: Exception) {
                val message = "Não foi possível encontrar a marca pesquisada, tente novamente"
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@MainActivity, "Digite a marca que deseja pesquisar", Toast.LENGTH_SHORT).show()
        }
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

    private fun dbInsertCosmetic(name: String, idBrand: Long, price: Float, isSale: Boolean) = runBlocking {
        val insertCosmetic = launch {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val date = dateFormat.format(Date()).toString()
            val cosmeticEntity = CosmeticEntity(0, name, idBrand, date, price, isSale)
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

    private fun dbShowCosmeticByName(name: String) = runBlocking {
        if(name.isNotEmpty()) {
            var nameLower = name.lowercase()
            nameLower = nameLower[0].uppercase() + nameLower.substring(1, nameLower.lastIndex + 1)
            try {
                var cosmetic = async { db.cosmeticDAO.getByName(nameLower) }
                Log.i("cosmeticList", cosmetic.await().toString())
            } catch (e: Exception) {
                val message = "Não foi possível encontrar o cosmético pesquisado, tente novamente"
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@MainActivity, "Digite o cosmético que deseja pesquisar.", Toast.LENGTH_SHORT).show()
        }
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

    private fun dbDeleteAllCosmetics() = runBlocking {
        val deleteAllCosmetics = launch {
            db.cosmeticDAO.deleteAll()
        }
        deleteAllCosmetics.join()
    }

    private fun dbUpdateCosmetic(name: String, idBrand: Long, price: Float, isSale: Boolean, id: Long) = runBlocking {
        val updateCosmetic = launch {
            db.cosmeticDAO.update(name, idBrand, price, isSale, id)
        }
        updateCosmetic.join()
    }

    private fun dbShowBrandWithCosmetics(id: Long) = runBlocking {
        val showBrandWithCosmetics = launch {
            val brandWithCosmeticList =  async { db.brandWithCosmeticDAO.getBrandsWithCosmetics() }.await()
            Log.i("BrandWithCosmetics", brandWithCosmeticList.toString())
        }
        showBrandWithCosmetics.join()
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
}