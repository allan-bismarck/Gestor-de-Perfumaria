package com.app.gestordeperfumaria.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cosmetic")
data class CosmeticEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String,
    var idBrand: Long,
    var date: String,
    var price: Float,
    var isSale: Boolean
)
