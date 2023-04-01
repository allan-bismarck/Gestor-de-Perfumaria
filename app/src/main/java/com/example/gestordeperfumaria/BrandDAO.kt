package com.example.gestordeperfumaria

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BrandDAO {
    @Insert
    suspend fun insert(brand: BrandEntity): Long

    @Query("UPDATE brand SET name = :nameUpdate, profit = :profitUpdate WHERE id = :id")
    suspend fun update(nameUpdate: String, profitUpdate: Float, id: Long)

    @Query("DELETE FROM brand WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM brand WHERE id = :id")
    fun get(id: Long): BrandEntity

    @Query("DELETE FROM brand")
    suspend fun deleteAll()

    @Query("SELECT * FROM brand")
    fun getAll(): List<BrandEntity>
}