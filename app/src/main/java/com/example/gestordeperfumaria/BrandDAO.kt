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

    @Update
    suspend fun update(brand: BrandEntity)

    @Query("DELETE FROM brand WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM brand")
    suspend fun deleteAll()

    @Query("SELECT * FROM brand")
    fun getAll(): LiveData<List<BrandEntity>>
}