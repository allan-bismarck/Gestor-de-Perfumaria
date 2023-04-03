package com.example.gestordeperfumaria

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CosmeticDAO {
    @Insert
    suspend fun insert(cosmetic: CosmeticEntity): Long

    @Query("UPDATE cosmetic SET name = :nameUpdate, idBrand = :idBrandUpdate, price = :priceUpdate WHERE id = :id")
    suspend fun update(nameUpdate: String, idBrandUpdate: Long, priceUpdate: Float,  id: Long)

    @Query("DELETE FROM cosmetic WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM cosmetic")
    suspend fun deleteAll()

    @Query("SELECT * FROM cosmetic")
    fun getAll(): List<CosmeticEntity>
}