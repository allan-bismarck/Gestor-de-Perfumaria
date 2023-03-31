package com.example.gestordeperfumaria

import androidx.lifecycle.LiveData

interface BrandRepository {
    suspend fun insertBrand(name: String, profit: Float): Long

    suspend fun updateBrand(id: Long, name: String, profit: Float)

    suspend fun deleteBrand(id: Long)

    suspend fun deleteAllBrands()

    suspend fun getAllBrands(): LiveData<List<BrandEntity>>
}