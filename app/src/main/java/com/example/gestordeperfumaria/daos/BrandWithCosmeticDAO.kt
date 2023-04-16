package com.example.gestordeperfumaria.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.gestordeperfumaria.dataclasses.BrandWithCosmetic

@Dao
interface BrandWithCosmeticDAO {
    @Transaction
    @Query("SELECT * FROM brand WHERE id = :id")
    suspend fun getBrandWithCosmetics(id: Long) : BrandWithCosmetic
}