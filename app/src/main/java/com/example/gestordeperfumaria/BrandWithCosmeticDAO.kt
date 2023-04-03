package com.example.gestordeperfumaria

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface BrandWithCosmeticDAO {
    @Transaction
    @Query("SELECT * FROM brand")
    suspend fun getBrandsWithCosmetics() : List<BrandWithCosmetic>
}