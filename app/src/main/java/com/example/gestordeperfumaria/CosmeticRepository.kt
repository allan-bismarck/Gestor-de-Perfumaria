package com.example.gestordeperfumaria

import androidx.lifecycle.LiveData

interface CosmeticRepository {
    suspend fun insertCosmetic(name: String, brand: String, price: Float): Long

    suspend fun updateCosmetic(id: Long, name: String, brand: String, price: Float)

    suspend fun deleteCosmetic(id: Long)

    suspend fun deleteAllCosmetics()

    suspend fun getAllCosmetics(): LiveData<List<CosmeticEntity>>
}