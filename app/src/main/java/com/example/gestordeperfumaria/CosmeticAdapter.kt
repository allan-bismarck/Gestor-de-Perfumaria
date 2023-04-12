package com.example.gestordeperfumaria

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CosmeticViewHolder {
        val view = ItemCosmeticBinding.inflate(LayoutInflater.from(context), parent, false)

        db = Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "db-perfumery"
        ).allowMainThreadQueries().build()

        return CosmeticViewHolder(view)
    }

    override fun onBindViewHolder(holder: CosmeticViewHolder, position: Int) {
        holder.nameCosmetic.text = cosmetics[position].name
        holder.idBrandCosmetic.text = cosmetics[position].idBrand.toString()
        holder.dateStr.text = cosmetics[position].date
        holder.price.text = cosmetics[position].price.toString()
        holder.isSale.text = cosmetics[position].isSale.toString()

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
        builder.setTitle("Editar Cosmético")
        val view = LayoutInflater.from(context).inflate(R.layout.cosmetic_edit_dialog, null)

        val editName = view.findViewById<EditText>(R.id.edit_text_name_dialog)
        val editIdBrand = view.findViewById<EditText>(R.id.edit_text_id_brand_dialog)
        val editPrice = view.findViewById<EditText>(R.id.edit_text_price_dialog)
        val btnPurchase = view.findViewById<RadioButton>(R.id.btn_purchase)
        val btnSale = view.findViewById<RadioButton>(R.id.btn_sale)
        val cancel = view.findViewById<Button>(R.id.edit_cancel)
        val save = view.findViewById<Button>(R.id.edit_save)

        editName.setText(name)
        editIdBrand.setText(idBrand.toString())
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
                Toast.makeText(context, "Digite um nome para o cosmético", Toast.LENGTH_SHORT).show()
            }

            val editIdBrandStr = editIdBrand.text.toString()
            if(editIdBrandStr.isEmpty()) {
                Toast.makeText(context, "Selecione uma marca para o cosmético", Toast.LENGTH_SHORT).show()
            }

            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.DOWN
            var editPriceFloat = 0.0f
            try {
                editPriceFloat = (editPrice.text.toString().toFloat() / 100.0).toFloat()
            } catch (e: Exception) {
                Toast.makeText(context, "Digite um preço válido para o cosmético", Toast.LENGTH_SHORT).show()
            }

            val editIsSale = btnSale.isChecked

            if(editNameStr.isNotEmpty() && editIdBrandStr.isNotEmpty() && editPriceFloat != 0.0f) {
                dbUpdateCosmetic(editNameStr, editIdBrandStr.toLong(), df.format(editPriceFloat).toFloat() * 100.0f, editIsSale, id)
                cosmetics[position].name = editNameStr
                cosmetics[position].idBrand = editIdBrandStr.toLong()
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
}