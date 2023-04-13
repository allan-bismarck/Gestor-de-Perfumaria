package com.example.gestordeperfumaria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.gestordeperfumaria.databinding.ActivitySearchBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.text.ParsePosition
import java.text.SimpleDateFormat

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var db: AppDataBase
    private lateinit var adapterBrands: BrandAdapter
    private var brands: MutableList<Brand> = mutableListOf()
    private var brandsEntitys: List<BrandEntity> = listOf()
    private lateinit var adapterCosmetics: CosmeticAdapter
    private var cosmetics: MutableList<Cosmetic> = mutableListOf()
    private var cosmeticsEntitys: List<CosmeticEntity> = listOf()
    private lateinit var listBrands: List<BrandEntity>
    private var mutableListBrandsString: MutableList<String> = mutableListOf("Selecione a marca")
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

        runBlocking {
            listBrands = async { dbShowAllBrands() }.await()
        }

        listBrands.forEach {
            mutableListBrandsString.add(it.name)
        }

        var adapter = ArrayAdapter(
            this,
            R.layout.my_spinner_style,
            mutableListBrandsString
        )

        binding.brandsSpinner.adapter = adapter

        binding.radioBrandCosmetic.setOnCheckedChangeListener { _, _ ->
            if(binding.brand.isChecked) {
                binding.textSearch.visibility = View.VISIBLE
                binding.dateInitial.visibility = View.INVISIBLE
                binding.dateFinal.visibility = View.INVISIBLE
                binding.brandsSpinner.visibility = View.INVISIBLE
                binding.radioDateNameBrand.visibility = View.GONE
                cosmetics.clear()
                showCosmeticsRecyclerView()
            }
            if(binding.cosmetic.isChecked) {
                if(binding.date.isChecked) {
                    binding.textSearch.visibility = View.INVISIBLE
                    binding.dateInitial.visibility = View.VISIBLE
                    binding.dateFinal.visibility = View.VISIBLE
                    binding.brandsSpinner.visibility = View.INVISIBLE
                    cosmetics.clear()
                    showCosmeticsRecyclerView()
                }
                if(binding.name.isChecked) {
                    binding.textSearch.visibility = View.VISIBLE
                    binding.dateInitial.visibility = View.INVISIBLE
                    binding.dateFinal.visibility = View.INVISIBLE
                    binding.brandsSpinner.visibility = View.INVISIBLE
                    cosmetics.clear()
                    showCosmeticsRecyclerView()
                }
                if(binding.forBrand.isChecked) {
                    binding.textSearch.visibility = View.INVISIBLE
                    binding.dateInitial.visibility = View.INVISIBLE
                    binding.dateFinal.visibility = View.INVISIBLE
                    binding.brandsSpinner.visibility = View.VISIBLE
                    cosmetics.clear()
                    showCosmeticsRecyclerView()
                }
                binding.radioDateNameBrand.visibility = View.VISIBLE
                brands.clear()
                showBrandsRecyclerView()
            }
        }

        binding.radioDateNameBrand.setOnCheckedChangeListener { _, _ ->
            if(binding.name.isChecked) {
                binding.textSearch.visibility = View.VISIBLE
                binding.dateInitial.visibility = View.INVISIBLE
                binding.dateFinal.visibility = View.INVISIBLE
                binding.brandsSpinner.visibility = View.INVISIBLE
            }
            if(binding.forBrand.isChecked) {
                binding.textSearch.visibility = View.INVISIBLE
                binding.dateInitial.visibility = View.INVISIBLE
                binding.dateFinal.visibility = View.INVISIBLE
                binding.brandsSpinner.visibility = View.VISIBLE
            }
            if(binding.date.isChecked) {
                binding.textSearch.visibility = View.INVISIBLE
                binding.dateInitial.visibility = View.VISIBLE
                binding.dateFinal.visibility = View.VISIBLE
                binding.brandsSpinner.visibility = View.INVISIBLE
            }
        }

        binding.btnSearch.setOnClickListener {
            val name = binding.textSearch.text.toString()
            if(binding.brand.isChecked) {
                searchBrandByName(name)
            } else {
                cosmetics.clear()
                showCosmeticsRecyclerView()
                if(binding.name.isChecked) {
                    searchCosmeticByName(name)
                }

                if(binding.forBrand.isChecked) {
                    val nameItemSelected = binding.brandsSpinner.selectedItem
                    var idBrand: Long = 0

                    listBrands.forEach {
                        if(it.name == nameItemSelected) {
                            idBrand = it.id
                            searchCosmeticsForBrand(idBrand)
                        }
                    }

                    if(idBrand == 0.toLong()) {
                        Toast.makeText(this, "Selecione uma marca para pesquisar.", Toast.LENGTH_SHORT).show()
                    }
                }

                if(binding.date.isChecked) {
                    searchCosmeticsByPeriod(binding.dateInitial.text.toString(), binding.dateFinal.text.toString())
                }
            }
        }
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
            brandsEntitys = dbShowBrandByName(name)
        }

        if(brandsEntitys.isNotEmpty()) {
            getBrandsFromBrandEntity(brandsEntitys)
            showBrandsRecyclerView()
        }
    }

    private fun dbShowAllCosmetics() = runBlocking {
        var cosmeticList = async { db.cosmeticDAO.getAll() }.await()
        Log.i("cosmeticList", cosmeticList.toString())
        cosmeticList = cosmeticList.sortedBy {
            it.name
        }
        return@runBlocking cosmeticList
    }

    private fun searchCosmeticByName(name: String) {
        cosmeticsEntitys = listOf()
        cosmetics = mutableListOf()
        runBlocking {
            cosmeticsEntitys = dbShowCosmeticByName(name)
        }

        if(cosmeticsEntitys.isNotEmpty()) {
            getCosmeticsFromCosmeticEntity(cosmeticsEntitys)
            showCosmeticsRecyclerView()
        }
    }

    private fun dbShowCosmeticByName(name: String) = runBlocking {
        var cosmeticList: List<CosmeticEntity> = dbShowAllCosmetics()
        var returnCosmetic: MutableList<CosmeticEntity> = mutableListOf()
        if(name.isNotEmpty()) {
            var nameLower = name.lowercase()
            cosmeticList.forEach {
                val itNameLower = it.name.lowercase()
                if(itNameLower.contains(nameLower)) {
                    returnCosmetic.add(it)
                }
            }
            cosmeticList = returnCosmetic
            if(cosmeticList.isNotEmpty()) {
                Log.i("cosmeticList", cosmeticList.toString())
                return@runBlocking cosmeticList
            } else {
                val message = "Não foi possível encontrar o cosmético pesquisado, tente novamente"
                Toast.makeText(this@SearchActivity, message, Toast.LENGTH_SHORT).show()
                return@runBlocking cosmeticList
            }
        } else {
            return@runBlocking cosmeticList
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
                return@runBlocking filterCosmetic
            } else {
                val message = "A data inicial deve anteceder ou ser igual à data final"
                Toast.makeText(this@SearchActivity, message, Toast.LENGTH_SHORT).show()
                return@runBlocking mutableListOf<CosmeticEntity>()
            }
        } else {
            val message = "Digite as datas inicial e final para realizar a pesquisa"
            Toast.makeText(this@SearchActivity, message, Toast.LENGTH_SHORT).show()
            return@runBlocking mutableListOf<CosmeticEntity>()
        }
    }

    private fun searchCosmeticsByPeriod(dateInitial: String, dateFinal: String) {
        cosmeticsEntitys = listOf()
        cosmetics = mutableListOf()
        runBlocking {
            cosmeticsEntitys = dbShowCosmeticByPeriod(dateInitial, dateFinal)
        }

        if(cosmeticsEntitys.isNotEmpty()) {
            getCosmeticsFromCosmeticEntity(cosmeticsEntitys)
            showCosmeticsRecyclerView()
        }
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

    private fun dbShowBrandWithCosmetics(id: Long): BrandWithCosmetic = runBlocking {
        val brandWithCosmeticList =  async { db.brandWithCosmeticDAO.getBrandWithCosmetics(id) }.await()
        Log.i("BrandWithCosmetics", brandWithCosmeticList.toString())
        return@runBlocking brandWithCosmeticList
    }

    private fun searchCosmeticsForBrand(id: Long) {
        cosmeticsEntitys = listOf()
        cosmetics = mutableListOf()
        val brandWithCosmeticList: BrandWithCosmetic
        runBlocking {
            brandWithCosmeticList = dbShowBrandWithCosmetics(id)
        }

        cosmeticsEntitys = brandWithCosmeticList.cosmetics

        if(cosmeticsEntitys.isNotEmpty()) {
            getCosmeticsFromCosmeticEntity(cosmeticsEntitys)
            showCosmeticsRecyclerView()
        }
    }
}