package com.example.gestordeperfumaria.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.gestordeperfumaria.dataclasses.Brand
import com.example.gestordeperfumaria.R
import com.example.gestordeperfumaria.database.AppDataBase
import com.example.gestordeperfumaria.databinding.ItemBrandBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.RoundingMode
import java.text.DecimalFormat

class BrandAdapter(
    private val context: Context,
    var brands: MutableList<Brand>
): RecyclerView.Adapter<BrandAdapter.BrandViewHolder>() {

    private lateinit var db: AppDataBase

    inner class BrandViewHolder(binding: ItemBrandBinding): RecyclerView.ViewHolder(binding.root) {
        val nameBrand = binding.name
        val profitBrand = binding.profit
        val btnEdit = binding.btnEdit
        val btnDelete = binding.btnDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val view = ItemBrandBinding.inflate(LayoutInflater.from(context), parent, false)

        db = Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            context.resources.getString(R.string.DATABASE_NAME)
        ).allowMainThreadQueries().build()

        return BrandViewHolder(view)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.nameBrand.text = brands[position].name
        val profitNumber = brands[position].profit * 100.0f
        val int = profitNumber.toString().split('.')[0]
        var dec = profitNumber.toString().split('.')[1]
        dec = dec.substring(0, 1)
        holder.profitBrand.text = (if(dec.toInt() == 0) {
            "$int%"
        } else {
            "$int.${dec}%"
        }).toString()

        holder.btnEdit.setOnClickListener {
            brandEditDialog(brands[position].name, brands[position].profit, brands[position].id, position)
        }

        holder.btnDelete.setOnClickListener {
            if(dbDeleteBrand(brands[position].id)) {
                brands.removeAt(position)
                notifyItemRemoved(position)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return brands.size
    }

    private fun dbDeleteBrand(id: Long) = runBlocking {
        val listCosmetics = async { db.cosmeticDAO.getAll() }.await()
        listCosmetics.forEach {
            if(it.idBrand == id) {
                val message = R.string.toast_dont_delete_brand
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                return@runBlocking false
            }
        }
        val deleteBrand = launch {
            db.brandDAO.delete(id)
        }
        deleteBrand.join()
        return@runBlocking true
    }

    private fun dbUpdateBrand(name: String, profit: Float, id: Long) = runBlocking {
        val updateBrand = launch {
            db.brandDAO.update(name, profit, id)
        }
        updateBrand.join()
    }

    private fun brandEditDialog(name: String, profit: Float, id: Long, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.dialog_edit_brand)
        val view = LayoutInflater.from(context).inflate(R.layout.brand_edit_dialog, null)

        val editName = view.findViewById<EditText>(R.id.edit_text_name_dialog)
        val editProfit = view.findViewById<EditText>(R.id.edit_text_profit_dialog)
        val cancel = view.findViewById<Button>(R.id.edit_cancel)
        val save = view.findViewById<Button>(R.id.edit_save)

        editName.setText(name)
        editProfit.setText((profit * 100.0).toFloat().toString())

        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        save.setOnClickListener {
            val editNameStr = editName.text.toString()
            if(editNameStr.isEmpty()) {
                Toast.makeText(context, R.string.toast_write_name_brand, Toast.LENGTH_SHORT).show()
            }

            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.DOWN
            var editProfitFloat = 0.0f
            try {
                editProfitFloat = (editProfit.text.toString().toFloat()/100.0f)
            } catch (e: Exception) {
                Toast.makeText(context, R.string.toast_write_profit_brand, Toast.LENGTH_SHORT).show()
            }

            if(editNameStr.isNotEmpty() && editProfitFloat != 0.0f) {
                dbUpdateBrand(editNameStr, editProfitFloat, id)
                brands[position].name = editNameStr
                brands[position].profit = editProfitFloat
                notifyItemChanged(position)
                dialog.dismiss()
            }
        }
    }
}