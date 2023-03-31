package com.example.gestordeperfumaria

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

interface CosmeticDAO {
    @Insert
    suspend fun insert(cosmetic: CosmeticEntity): Long

    @Update
    suspend fun update(cosmetic: CosmeticEntity)

    @Query("DELETE FROM cosmetic WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM cosmetic")
    suspend fun deleteAll()

    @Query("SELECT * FROM cosmetic")
    fun getAll(): LiveData<List<CosmeticEntity>>
}