package com.example.gestordeperfumaria

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.gestordeperfumaria.databinding.ItemCosmeticBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

    private fun cosmeticEditDialog(name: String, profit: Float, id: Long, position: Int) {
        /*val builder = AlertDialog.Builder(context)
        builder.setTitle("Editar Marca")
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
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.DOWN
            val editProfitFloat = (editProfit.text.toString().toFloat()/100.0).toFloat()
            Log.i("name, profit, id", "$editNameStr, ${editProfitFloat}, $id")
            dbUpdateBrand(editNameStr, df.format(editProfitFloat).toFloat(), id)
            brands[position].name = editNameStr
            brands[position].profit = editProfitFloat
            notifyItemChanged(position)
            dialog.dismiss()
        }*/
    }

    private fun dbDeleteCosmetic(id: Long) = runBlocking {
        val deleteCosmetic = launch {
            db.cosmeticDAO.delete(id)
        }
        deleteCosmetic.join()
    }
}