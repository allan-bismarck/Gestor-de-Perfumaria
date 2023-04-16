package com.example.gestordeperfumaria.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gestordeperfumaria.entities.CosmeticEntity
import com.example.gestordeperfumaria.R
import com.example.gestordeperfumaria.databinding.ItemViewPagerBinding

class ViewPagerAdapter(
    private val context: Context,
    private val months: List<String>,
    private val cosmeticsProfit: List<CosmeticEntity>
): RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    private lateinit var cosmeticListByDate: List<CosmeticEntity>
    inner class ViewPagerViewHolder(binding: ItemViewPagerBinding): RecyclerView.ViewHolder(binding.root) {
        var month = binding.month
        var profitContent = binding.profitContent
        var expenseContent = binding.expenseContent
        var totalContent = binding.totalContent
        val title = binding.resultTitle
        val btnBack = binding.btnBack
        val btnNext = binding.btnNext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = ItemViewPagerBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewPagerViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
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

        holder.title.text = ""
        holder.title.setBackgroundColor(R.color.white)

        total = sales - expenses

        if(total > 0) {
            holder.title.text = R.string.title_profit.toString()
            holder.title.setBackgroundColor(R.color.teal_200)
        }
        if(total < 0) {
            holder.title.text = R.string.title_prejudice.toString()
            holder.title.setBackgroundColor(R.color.teal_700)
        }

        holder.profitContent.text = "${R.string.money}${sales}"
        holder.expenseContent.text = "${R.string.money}${expenses}"
        holder.totalContent.text = "${R.string.money}${total}"

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
            R.string.month_1.toString() -> "1/$year"
            R.string.month_2.toString() -> "2/$year"
            R.string.month_3.toString() -> "3/$year"
            R.string.month_4.toString() -> "4/$year"
            R.string.month_5.toString() -> "5/$year"
            R.string.month_6.toString() -> "6/$year"
            R.string.month_7.toString() -> "7/$year"
            R.string.month_8.toString() -> "8/$year"
            R.string.month_9.toString() -> "9/$year"
            R.string.month_10.toString() -> "10/$year"
            R.string.month_11.toString() -> "11/$year"
            else -> "12/$year"
        }
    }
}