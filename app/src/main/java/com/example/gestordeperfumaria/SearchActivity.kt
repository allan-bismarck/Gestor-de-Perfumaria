package com.example.gestordeperfumaria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.gestordeperfumaria.databinding.ActivityRegisterBinding
import com.example.gestordeperfumaria.databinding.ActivitySearchBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var db: AppDataBase
    private lateinit var adapterBrands: BrandAdapter
    private val brands: MutableList<Brand> = mutableListOf()
    private lateinit var brandsEntitys: List<BrandEntity>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "db-perfumery"
        ).allowMainThreadQueries().build()

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.radioBrandCosmetic.setOnCheckedChangeListener { _, _ ->
            if(binding.brand.isChecked) {
                binding.textSearch.visibility = View.VISIBLE
                binding.dateInitial.visibility = View.INVISIBLE
                binding.dateFinal.visibility = View.INVISIBLE
                binding.radioDateNameBrand.visibility = View.INVISIBLE
            }
            if(binding.cosmetic.isChecked) {
                if(binding.date.isChecked) {
                    binding.textSearch.visibility = View.INVISIBLE
                    binding.dateInitial.visibility = View.VISIBLE
                    binding.dateFinal.visibility = View.VISIBLE
                } else {
                    binding.textSearch.visibility = View.VISIBLE
                    binding.dateInitial.visibility = View.INVISIBLE
                    binding.dateFinal.visibility = View.INVISIBLE
                }
                binding.radioDateNameBrand.visibility = View.VISIBLE
            }
        }

        binding.radioDateNameBrand.setOnCheckedChangeListener { _, _ ->
            if(binding.name.isChecked) {
                binding.textSearch.visibility = View.VISIBLE
                binding.dateInitial.visibility = View.INVISIBLE
                binding.dateFinal.visibility = View.INVISIBLE
            }
            if(binding.forBrand.isChecked) {
                binding.textSearch.visibility = View.VISIBLE
                binding.dateInitial.visibility = View.INVISIBLE
                binding.dateFinal.visibility = View.INVISIBLE
            }
            if(binding.date.isChecked) {
                binding.textSearch.visibility = View.INVISIBLE
                binding.dateInitial.visibility = View.VISIBLE
                binding.dateFinal.visibility = View.VISIBLE
            }
        }

        runBlocking {
            brandsEntitys = async { dbShowAllBrands() }.await()
        }

        getBrandsFromBrandEntity(brandsEntitys)

        val recyclerViewBrands = binding.rvBrands
        recyclerViewBrands.layoutManager = LinearLayoutManager(this)
        recyclerViewBrands.setHasFixedSize(true)
        adapterBrands = BrandAdapter(this, brands)
        recyclerViewBrands.adapter = adapterBrands
    }

    private suspend fun dbShowAllBrands() = runBlocking {
        var brandList = async { db.brandDAO.getAll() }
        Log.i("brandList", brandList.await().toString())
        return@runBlocking brandList
    }.await()

    private fun dbShowBrandByName(name: String) = runBlocking {
        if(name.isNotEmpty()) {
            var nameLower = name.lowercase()
            nameLower = nameLower[0].uppercase() + nameLower.substring(1, nameLower.lastIndex + 1)
            try {
                var brand = async { db.brandDAO.get(nameLower) }
                Log.i("brandList", brand.await().toString())
            } catch (e: Exception) {
                val message = "Não foi possível encontrar a marca pesquisada, tente novamente"
                Toast.makeText(this@SearchActivity, message, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@SearchActivity, "Digite a marca que deseja pesquisar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getBrandsFromBrandEntity(listBrandEntity: List<BrandEntity>) {
        listBrandEntity.forEach {
            brands.add(Brand(it.name, it.profit))
        }
    }
}