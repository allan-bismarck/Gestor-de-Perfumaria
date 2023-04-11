package com.example.gestordeperfumaria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.gestordeperfumaria.databinding.ActivityRegisterBinding
import com.example.gestordeperfumaria.databinding.ActivitySearchBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var db: AppDataBase
    private lateinit var adapterBrands: BrandAdapter
    private var brands: MutableList<Brand> = mutableListOf()
    private lateinit var brandsEntitys: List<BrandEntity>
    private lateinit var adapterCosmetics: CosmeticAdapter
    private var cosmetics: MutableList<Cosmetic> = mutableListOf()
    private lateinit var cosmeticsEntitys: List<CosmeticEntity>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "db-perfumery"
        ).allowMainThreadQueries().build()

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog()

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

        binding.btnSearch.setOnClickListener {
            val name = binding.textSearch.text.toString()
            searchBrandByName(name)
        }

        searchCosmeticByName()
    }

    private suspend fun dbShowAllBrands() = runBlocking {
        var brandList = async { db.brandDAO.getAll() }.await()
        Log.i("brandList", brandList.toString())
        brandList = brandList.sortedBy {
            it.name
        }
        return@runBlocking brandList
    }

    private fun dbShowBrandByName(name: String) = runBlocking {
        var brandList: List<BrandEntity> = dbShowAllBrands()
        var returnBrand: MutableList<BrandEntity> = mutableListOf()
        if(name.isNotEmpty()) {
            var nameLower = name.lowercase()
            brandList.forEach {
                val itNameLower = it.name.lowercase()
                if(itNameLower.contains(nameLower)) {
                    returnBrand.add(it)
                }
            }
            brandList = returnBrand
            if(brandList.isNotEmpty()) {
                Log.i("brandList", brandList.toString())
                return@runBlocking brandList
            } else {
                val message = "Não foi possível encontrar a marca pesquisada, tente novamente"
                Toast.makeText(this@SearchActivity, message, Toast.LENGTH_SHORT).show()
                return@runBlocking brandList
            }
        } else {
            return@runBlocking brandList
        }
    }

    private fun getBrandsFromBrandEntity(listBrandEntity: List<BrandEntity>) {
        listBrandEntity.forEach {
            brands.add(Brand(it.id, it.name, it.profit))
        }
    }

    private fun getCosmeticsFromCosmeticEntity(listCosmeticEntity: List<CosmeticEntity>) {
        listCosmeticEntity.forEach {
            cosmetics.add(Cosmetic(it.id, it.name, it.idBrand, it.date, it.price, it.isSale))
        }
    }

    private fun dialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("DICA DE USO")

        val message = "Para exibir todas as marcas ou todos os cosméticos, faça uma pesquisa por nome e não digite nada no campo de pesquisa, apenas clique no botão de pesquisar."
        builder.setMessage(message)

        builder.setPositiveButton("Fechar") { dialog, which -> }

        builder.show()
    }

    private fun showBrandsRecyclerView() {
        val recyclerViewBrands = binding.rv
        recyclerViewBrands.layoutManager = LinearLayoutManager(this)
        recyclerViewBrands.setHasFixedSize(true)
        adapterBrands = BrandAdapter(this, brands)
        recyclerViewBrands.adapter = adapterBrands
    }

    private fun showCosmeticsRecyclerView() {
        cosmeticsEntitys
        val recyclerViewCosmetics = binding.rv
        recyclerViewCosmetics.layoutManager = LinearLayoutManager(this)
        recyclerViewCosmetics.setHasFixedSize(true)
        adapterCosmetics = CosmeticAdapter(this, cosmetics)
        recyclerViewCosmetics.adapter = adapterCosmetics
    }

    private fun searchBrandByName(name: String) {
        brandsEntitys = listOf()
        brands = mutableListOf()
        runBlocking {
            brandsEntitys = dbShowBrandByName(name) as List<BrandEntity>
        }

        if(brandsEntitys.isNotEmpty()) {
            getBrandsFromBrandEntity(brandsEntitys)
            showBrandsRecyclerView()
        }
    }

    private fun dbUpdateBrand(name: String, profit: Float, id: Long) = runBlocking {
        val updateBrand = launch {
            db.brandDAO.update(name, profit, id)
        }
        updateBrand.join()
    }

    private fun dbDeleteBrand(id: Long) = runBlocking {
        val listCosmetics = async { db.cosmeticDAO.getAll() }.await()
        listCosmetics.forEach {
            if(it.idBrand == id) {
                val message = "Não é possível excluir a marca selecionada, pois existem cosméticos cadastrados dessa marca!"
                Toast.makeText(this@SearchActivity, message, Toast.LENGTH_SHORT).show()
                return@runBlocking
            }
        }
        val deleteBrand = launch {
            db.brandDAO.delete(id)
        }
        deleteBrand.join()
    }

    private fun dbShowAllCosmetics() = runBlocking {
        var cosmeticList = async { db.cosmeticDAO.getAll() }.await()
        Log.i("cosmeticList", cosmeticList.toString())
        cosmeticList = cosmeticList.sortedBy {
            it.name
        }
        return@runBlocking cosmeticList
    }

    private fun searchCosmeticByName() {
        cosmeticsEntitys = listOf()
        cosmetics = mutableListOf()
        runBlocking {
            cosmeticsEntitys = dbShowAllCosmetics() as List<CosmeticEntity>
        }

        if(cosmeticsEntitys.isNotEmpty()) {
            getCosmeticsFromCosmeticEntity(cosmeticsEntitys)
            showCosmeticsRecyclerView()
        }
    }
}