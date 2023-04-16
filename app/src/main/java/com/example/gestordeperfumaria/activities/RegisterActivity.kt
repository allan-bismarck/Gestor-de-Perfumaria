package com.example.gestordeperfumaria.activities

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.example.gestordeperfumaria.entities.BrandEntity
import com.example.gestordeperfumaria.entities.CosmeticEntity
import com.example.gestordeperfumaria.R
import com.example.gestordeperfumaria.database.AppDataBase
import com.example.gestordeperfumaria.databinding.ActivityRegisterBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: AppDataBase
    private var isSale: Boolean = false
    private lateinit var listBrands: List<BrandEntity>
    private var mutableListBrandsString: MutableList<String> = mutableListOf(R.string.label_spinner.toString())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog()

        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "db-perfumery"
        ).allowMainThreadQueries().build()

        binding.radioGroup.setOnCheckedChangeListener { _, _ ->

            if(binding.btnRegisterBrand.isChecked) {
                binding.registerBrand.visibility = View.VISIBLE
                binding.registerCosmetic.visibility = View.GONE
            }
            if(binding.btnRegisterCosmetic.isChecked) {
                binding.registerBrand.visibility = View.GONE
                binding.registerCosmetic.visibility = View.VISIBLE
            }
        }

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

        binding.transactionTypeCosmetic.setOnCheckedChangeListener { _, _ ->
            isSale = !binding.btnPurchase.isChecked
        }

        binding.register.setOnClickListener {
            if(binding.btnRegisterBrand.isChecked) {
                var brandName = binding.nameBrand.text.toString()

                if(brandName.isEmpty()) {
                    Toast.makeText(this,R.string.toast_write_name_brand, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                brandName = brandName.lowercase()
                brandName = brandName[0].uppercase() + brandName.substring(1, brandName.lastIndex + 1)
                val brandProfit = binding.profitBrand.text.toString().toDouble()/100.0

                listBrands.forEach {
                    if(it.name == brandName) {
                        val message = R.string.toast_exist_brand_name
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                dbInsertBrand(brandName, brandProfit.toFloat())
                binding.nameBrand.text.clear()
                binding.profitBrand.text.clear()
                finish()
            } else {

                val listCosmetics: List<CosmeticEntity>
                runBlocking {
                    listCosmetics = dbShowAllCosmetics()
                }
                var cosmeticName = binding.nameCosmetic.text.toString()

                if(cosmeticName.isEmpty()) {
                    Toast.makeText(this,R.string.toast_write_name_cosmetic, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                cosmeticName = cosmeticName.lowercase()
                cosmeticName = cosmeticName[0].uppercase() + cosmeticName.substring(1, cosmeticName.lastIndex + 1)
                val cosmeticPrice = binding.priceCosmetic.text.toString().toDouble()/100.0

                val itemSelected = binding.brandsSpinner.selectedItemPosition

                if(itemSelected == 0) {
                    val message = R.string.toast_register_or_select_brand
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val nameItemSelected = binding.brandsSpinner.selectedItem
                var idBrand: Long = 0

                listBrands.forEach {
                    if(it.name == nameItemSelected) {
                        idBrand = it.id
                    }
                }

                dbInsertCosmetic(cosmeticName, idBrand, (cosmeticPrice.toFloat()) * 100.0f, isSale)
                binding.nameCosmetic.text.clear()
                binding.priceCosmetic.text.clear()
                finish()
            }
        }
    }

    private fun dbInsertBrand(name: String, profit: Float) = runBlocking {
        val insertBrands = launch {
            val brandEntity = BrandEntity(0, name, profit)
            val result = db.brandDAO.insert(brandEntity)
            if(result > 0) {
                val message = R.string.toast_brand_register_success
                Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
        insertBrands.join()
    }

    private suspend fun dbShowAllBrands() = runBlocking {
        var brandList = async { db.brandDAO.getAll() }
        return@runBlocking brandList
    }.await()

    private fun dbInsertCosmetic(name: String, idBrand: Long, price: Float, isSale: Boolean) = runBlocking {
        val insertCosmetic = launch {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val date = dateFormat.format(Date()).toString()
            val cosmeticEntity = CosmeticEntity(0, name, idBrand, date, price, isSale)
            val result = db.cosmeticDAO.insert(cosmeticEntity)
            if(result > 0) {
                val message = R.string.toast_cosmetic_register_success
                Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
        insertCosmetic.join()
    }

    private suspend fun dbShowAllCosmetics() = runBlocking {
        var cosmesticList = async { db.cosmeticDAO.getAll() }
        return@runBlocking cosmesticList
    }.await()

    private fun dialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.title_usage_tips)

        val message = R.string.paragraph_one_rules
        builder.setMessage(message)

        builder.setPositiveButton("OK") { dialog, which -> }

        builder.show()
    }
}