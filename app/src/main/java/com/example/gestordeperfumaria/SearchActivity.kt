package com.example.gestordeperfumaria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.gestordeperfumaria.databinding.ActivityRegisterBinding
import com.example.gestordeperfumaria.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.radioBrandCosmetic.setOnCheckedChangeListener { _, _ ->
            if(binding.brand.isChecked) {
                binding.textSearch.visibility = View.VISIBLE
                binding.dateInitial.visibility = View.INVISIBLE
                binding.dateFinal.visibility = View.INVISIBLE
                binding.radioDateNameBrand.visibility = View.INVISIBLE
            }
            if(binding.cosmetic.isChecked) {
                if(binding.date.isChecked) {
                    binding.textSearch.visibility = View.INVISIBLE
                    binding.dateInitial.visibility = View.VISIBLE
                    binding.dateFinal.visibility = View.VISIBLE
                } else {
                    binding.textSearch.visibility = View.VISIBLE
                    binding.dateInitial.visibility = View.INVISIBLE
                    binding.dateFinal.visibility = View.INVISIBLE
                }
                binding.radioDateNameBrand.visibility = View.VISIBLE
            }
        }

        binding.radioDateNameBrand.setOnCheckedChangeListener { _, _ ->
            if(binding.name.isChecked) {
                binding.textSearch.visibility = View.VISIBLE
                binding.dateInitial.visibility = View.INVISIBLE
                binding.dateFinal.visibility = View.INVISIBLE
            }
            if(binding.forBrand.isChecked) {
                binding.textSearch.visibility = View.VISIBLE
                binding.dateInitial.visibility = View.INVISIBLE
                binding.dateFinal.visibility = View.INVISIBLE
            }
            if(binding.date.isChecked) {
                binding.textSearch.visibility = View.INVISIBLE
                binding.dateInitial.visibility = View.VISIBLE
                binding.dateFinal.visibility = View.VISIBLE
            }
        }
    }
}