package com.example.gestordeperfumaria

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gestordeperfumaria.databinding.ActivitySearchBinding
import com.example.gestordeperfumaria.databinding.ItemBrandBinding

class BrandAdapter(
    private val context: Context,
    var brands: MutableList<Brand>
): RecyclerView.Adapter<BrandAdapter.BrandViewHolder>() {

    inner class BrandViewHolder(binding: ItemBrandBinding): RecyclerView.ViewHolder(binding.root) {
        val nameBrand = binding.name
        val profitBrand = binding.profit

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val view = ItemBrandBinding.inflate(LayoutInflater.from(context), parent, false)
        return BrandViewHolder(view)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.nameBrand.text = brands[position].name
        holder.profitBrand.text = brands[position].profit.toString()
    }

    override fun getItemCount(): Int {
        return brands.size
    }
}