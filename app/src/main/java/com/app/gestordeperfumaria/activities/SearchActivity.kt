package com.app.gestordeperfumaria.activities

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.app.gestordeperfumaria.*
import com.app.gestordeperfumaria.adapters.BrandAdapter
import com.app.gestordeperfumaria.adapters.CosmeticAdapter
import com.app.gestordeperfumaria.database.AppDataBase
import com.app.gestordeperfumaria.dataclasses.Brand
import com.app.gestordeperfumaria.dataclasses.BrandWithCosmetic
import com.app.gestordeperfumaria.dataclasses.Cosmetic
import com.app.gestordeperfumaria.entities.BrandEntity
import com.app.gestordeperfumaria.entities.CosmeticEntity
import com.app.gestordeperfumaria.databinding.ActivitySearchBinding
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
    private var mutableListBrandsString: MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            applicationContext.resources.getString(R.string.DATABASE_NAME)
        ).allowMainThreadQueries().build()

        mutableListBrandsString.add(applicationContext.resources.getString(R.string.label_spinner))

        val title = applicationContext.resources.getString(R.string.label_search)
        supportActionBar?.title = title
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(applicationContext.resources.getString(R.color.dark_purple))))

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
                        Toast.makeText(this, R.string.toast_select_brand_to_search, Toast.LENGTH_SHORT).show()
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
                return@runBlocking brandList
            } else {
                val message = R.string.toast_brand_not_found
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
        builder.setTitle(R.string.title_usage_tips)

        val message = R.string.paragraph_one_usage_tips
        builder.setMessage(message)

        builder.setPositiveButton("OK") { dialog, which -> }

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
                return@runBlocking cosmeticList
            } else {
                val message = R.string.toast_cosmetic_not_found
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
                return@runBlocking filterCosmetic
            } else {
                val message = R.string.toast_date
                Toast.makeText(this@SearchActivity, message, Toast.LENGTH_SHORT).show()
                return@runBlocking mutableListOf<CosmeticEntity>()
            }
        } else {
            val message = R.string.toast_write_dates
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