package com.example.gestordeperfumaria

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brand")
data class BrandEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val profit: Float = 0f
)