package com.example.gestordeperfumaria

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CosmeticDAO {
    @Insert
    suspend fun insert(cosmetic: CosmeticEntity): Long

    @Query("UPDATE cosmetic SET name = :nameUpdate, idBrand = :idBrandUpdate, price = :priceUpdate, isSale = :isSaleUpdate WHERE id = :id")
    suspend fun update(nameUpdate: String, idBrandUpdate: Long, priceUpdate: Float, isSaleUpdate: Boolean,  id: Long)

    @Query("DELETE FROM cosmetic WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM cosmetic")
    suspend fun deleteAll()

    @Query("SELECT * FROM cosmetic WHERE name = :name")
    fun getByName(name: String): CosmeticEntity

    @Query("SELECT * FROM cosmetic")
    fun getAll(): List<CosmeticEntity>
}