package com.example.gestordeperfumaria
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cosmetic")
data class CosmeticEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var name: String,
    var nameBrand: String,
    var price: Float
)
