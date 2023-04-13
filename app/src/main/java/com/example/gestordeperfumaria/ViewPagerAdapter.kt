package com.example.gestordeperfumaria

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.gestordeperfumaria.databinding.ItemViewPagerBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.text.ParsePosition
import java.text.SimpleDateFormat
import kotlin.math.exp

class ViewPagerAdapter(
    private val context: Context,
    val months: List<String>,
    val cosmeticsProfit: List<CosmeticEntity>
): RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    private lateinit var cosmeticListByDate: List<CosmeticEntity>
    inner class ViewPagerViewHolder(binding: ItemViewPagerBinding): RecyclerView.ViewHolder(binding.root) {
        var month = binding.month
        var profitContent = binding.profitContent
        var expenseContent = binding.expenseContent
        var totalContent = binding.totalContent
        val btnBack = binding.btnBack
        val btnNext = binding.btnNext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = ItemViewPagerBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        cosmeticListByDate = getCosmeticsByDate(position)
        var sales = 0.0f
        var expenses = 0.0f
        var total = 0.0f

        cosmeticListByDate.forEach {
            if(it.isSale) {
                sales += it.price
            } else {
                expenses += it.price
            }
        }

        total = sales - expenses

        holder.profitContent.text = "R$${sales}"
        holder.expenseContent.text = "R$${expenses}"
        holder.totalContent.text = "R$${total}"

        holder.month.text = months[position]

        holder.btnBack.visibility = View.VISIBLE
        holder.btnNext.visibility = View.VISIBLE

        if(position == 0) {
            holder.btnBack.visibility = View.INVISIBLE
        }

        if(position == months.size - 1) {
            holder.btnNext.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return months.size
    }

    private fun getCosmeticsByDate(position: Int): MutableList<CosmeticEntity> {
        val cosmeticsListByDate: MutableList<CosmeticEntity> = mutableListOf()
        cosmeticsProfit.forEach {
            val dateSplit = it.date.split('/')
            val datePage = dateToNumberDate(position)
            if(dateSplit[1].toInt() == datePage.split('/')[0].toInt()) {
                cosmeticsListByDate.add(it)
            }
        }
        return cosmeticsListByDate
    }

    private fun dateToNumberDate(position: Int): String {
        val year = months[position].split('/')[1]
        val month = months[position].split('/')[0]
        return when(month) {
            "Janeiro" -> "1/$year"
            "Fevereiro" -> "2/$year"
            "Março" -> "3/$year"
            "Abril" -> "4/$year"
            "Maio" -> "5/$year"
            "Junho" -> "6/$year"
            "Julho" -> "7/$year"
            "Agosto" -> "8/$year"
            "Setembro" -> "9/$year"
            "Outubro" -> "10/$year"
            "Novembro" -> "11/$year"
            else -> "12/$year"
        }
    }
}