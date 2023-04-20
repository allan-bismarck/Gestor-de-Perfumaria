package com.example.gestordeperfumaria.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.gestordeperfumaria.dataclasses.Cosmetic
import com.example.gestordeperfumaria.entities.BrandEntity
import com.example.gestordeperfumaria.entities.CosmeticEntity
import com.example.gestordeperfumaria.R
import com.example.gestordeperfumaria.database.AppDataBase
import com.example.gestordeperfumaria.databinding.ItemCosmeticBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.RoundingMode
import java.text.DecimalFormat

class CosmeticAdapter(
    private val context: Context,
    var cosmetics: MutableList<Cosmetic>
): RecyclerView.Adapter<CosmeticAdapter.CosmeticViewHolder>() {

    private lateinit var db: AppDataBase

    inner class CosmeticViewHolder(binding: ItemCosmeticBinding): RecyclerView.ViewHolder(binding.root) {
        val nameCosmetic = binding.name
        val idBrandCosmetic = binding.idBrand
        val dateStr = binding.dateStr
        val price = binding.price
        val isSale = binding.isSale
        val btnEdit = binding.btnEdit
        val btnDelete = binding.btnDelete
        val cv = binding.cv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CosmeticViewHolder {
        val view = ItemCosmeticBinding.inflate(LayoutInflater.from(context), parent, false)

        db = Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            context.resources.getString(R.string.DATABASE_NAME)
        ).allowMainThreadQueries().build()

        return CosmeticViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: CosmeticViewHolder, position: Int) {
        holder.nameCosmetic.text = cosmetics[position].name
        holder.dateStr.text = cosmetics[position].date

        val priceNumber = cosmetics[position].price
        val int = priceNumber.toString().split('.')[0]
        var dec = priceNumber.toString().split('.')[1]
        dec = dec.substring(0, 1)
        holder.price.text = "${context.resources.getString(R.string.money)}$int.${dec}"

        var listBrands: List<BrandEntity>
        runBlocking {
            listBrands = dbShowAllBrands()
        }

        listBrands.forEach {
            if(it.id == cosmetics[position].idBrand) {
                holder.idBrandCosmetic.text = it.name
            }
        }

        if(cosmetics[position].isSale) {
            holder.isSale.text = context.resources.getString(R.string.label_sale)
        } else {
            holder.isSale.text = context.resources.getString(R.string.label_purchase)
        }


        holder.btnEdit.setOnClickListener {
            cosmeticEditDialog(cosmetics[position].name, cosmetics[position].idBrand, cosmetics[position].price, cosmetics[position].isSale, cosmetics[position].id, position)
        }

        holder.btnDelete.setOnClickListener {
            try {
                dbDeleteCosmetic(cosmetics[position].id)
            } catch (e: Exception) {}
            cosmetics.removeAt(position)
            notifyItemRemoved(position)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return cosmetics.size
    }

    private fun cosmeticEditDialog(name: String, idBrand: Long, price: Float, isSale: Boolean, id: Long, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.dialog_edit_cosmetic)
        val view = LayoutInflater.from(context).inflate(R.layout.cosmetic_edit_dialog, null)

        lateinit var listBrands: List<BrandEntity>
        var mutableListBrandsString: MutableList<String> = mutableListOf()
        mutableListBrandsString.add(context.resources.getString(R.string.label_spinner))

        lateinit var listCosmetics: List<CosmeticEntity>

        var validate = true

        val editName = view.findViewById<EditText>(R.id.edit_text_name_dialog)
        val brandsSpinner = view.findViewById<Spinner>(R.id.brands_spinner)
        val editPrice = view.findViewById<EditText>(R.id.edit_text_price_dialog)
        val btnPurchase = view.findViewById<RadioButton>(R.id.btn_purchase)
        val btnSale = view.findViewById<RadioButton>(R.id.btn_sale)
        val cancel = view.findViewById<Button>(R.id.edit_cancel)
        val save = view.findViewById<Button>(R.id.edit_save)

        runBlocking {
            listBrands = async { dbShowAllBrands() }.await()
            listCosmetics = async { dbShowAllCosmetics() }.await()
        }

        listBrands.forEach {
            mutableListBrandsString.add(it.name)
        }

        var adapter = ArrayAdapter(
            context,
            R.layout.my_spinner_style,
            mutableListBrandsString
        )

        brandsSpinner.adapter = adapter

        listBrands.indices.forEach {
            if(listBrands[it].id == idBrand) {
                brandsSpinner.setSelection(it + 1)
            }
        }

        editName.setText(name)

        editPrice.setText(price.toString())
        if(isSale) {
            btnPurchase.isChecked = false
            btnSale.isChecked = true
        } else {
            btnPurchase.isChecked = true
            btnSale.isChecked = false
        }

        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        save.setOnClickListener {
            val editNameStr = editName.text.toString()
            if(editNameStr.isEmpty()) {
                Toast.makeText(context, R.string.toast_write_name_cosmetic, Toast.LENGTH_SHORT).show()
                validate = false
            }

            val nameItemSelected = brandsSpinner.selectedItem
            var idBrandSelected: Long = 0

            listBrands.forEach {
                if(it.name == nameItemSelected) {
                    idBrandSelected = it.id
                }
            }

            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.DOWN
            var editPriceFloat = 0.0f
            try {
                editPriceFloat = (editPrice.text.toString().toFloat() / 100.0).toFloat()
            } catch (e: Exception) {
                Toast.makeText(context, R.string.toast_write_price_cosmetic, Toast.LENGTH_SHORT).show()
                validate = false
            }

            val editIsSale = btnSale.isChecked

            if(validate) {
                dbUpdateCosmetic(editNameStr, idBrandSelected, df.format(editPriceFloat).toFloat() * 100.0f, editIsSale, id)
                cosmetics[position].name = editNameStr
                cosmetics[position].idBrand = idBrandSelected
                cosmetics[position].price = df.format(editPriceFloat).toFloat() * 100.0f
                cosmetics[position].isSale = editIsSale
                notifyItemChanged(position)
                dialog.dismiss()
            }
        }
    }

    private fun dbDeleteCosmetic(id: Long) = runBlocking {
        val deleteCosmetic = launch {
            db.cosmeticDAO.delete(id)
        }
        deleteCosmetic.join()
    }

    private fun dbUpdateCosmetic(name: String, idBrand: Long, price: Float, isSale: Boolean, id: Long) = runBlocking {
        val updateCosmetic = launch {
            db.cosmeticDAO.update(name, idBrand, price, isSale, id)
        }
        updateCosmetic.join()
    }

    private suspend fun dbShowAllBrands() = runBlocking {
        var brandList = async { db.brandDAO.getAll() }
        return@runBlocking brandList
    }.await()

    private suspend fun dbShowAllCosmetics() = runBlocking {
        var cosmeticList = async { db.cosmeticDAO.getAll() }
        return@runBlocking cosmeticList
    }.await()
}