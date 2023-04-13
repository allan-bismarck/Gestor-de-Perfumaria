package com.example.gestordeperfumaria

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gestordeperfumaria.databinding.ItemViewPagerBinding

class ViewPagerAdapter(
    private val context: Context,
    val months: List<String>,
    val cosmeticsProfit: List<CosmeticEntity>
): RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

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
        holder.month.text = months[position]
        holder.profitContent.text = months[position]

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

}