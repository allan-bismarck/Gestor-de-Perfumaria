package com.example.gestordeperfumaria

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BrandDAO {
    @Insert
    suspend fun insert(brand: BrandEntity): Long

    @Query("UPDATE brand SET name = :nameUpdate, profit = :profitUpdate WHERE id = :id")
    suspend fun update(nameUpdate: String, profitUpdate: Float, id: Long)

    @Query("DELETE FROM brand WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM brand WHERE name = :name")
    fun get(name: String): BrandEntity

    @Query("DELETE FROM brand")
    suspend fun deleteAll()

    @Query("SELECT * FROM brand")
    fun getAll(): List<BrandEntity>
}