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

        total = sales - expenses

        val res = context.resources

        if(total > 0) {
            holder.title.text = res.getString(R.string.title_profit)
        }
        if(total < 0) {
            holder.title.text = res.getString(R.string.title_prejudice)
        }

        val strMoney = context.resources.getString(R.string.money)

        holder.profitContent.text = "${strMoney}${sales}"
        holder.expenseContent.text = "${strMoney}${expenses}"
        holder.totalContent.text = "${strMoney}${total}"

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
        val res = context.resources
        return when(month) {
            res.getString(R.string.month_1) -> "1/$year"
            res.getString(R.string.month_2) -> "2/$year"
            res.getString(R.string.month_3) -> "3/$year"
            res.getString(R.string.month_4) -> "4/$year"
            res.getString(R.string.month_5) -> "5/$year"
            res.getString(R.string.month_6) -> "6/$year"
            res.getString(R.string.month_7) -> "7/$year"
            res.getString(R.string.month_8) -> "8/$year"
            res.getString(R.string.month_9) -> "9/$year"
            res.getString(R.string.month_1) -> "10/$year"
            res.getString(R.string.month_1) -> "11/$year"
            else -> "12/$year"
        }
    }
}