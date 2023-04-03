package com.example.gestordeperfumaria

import androidx.room.Embedded
import androidx.room.Relation

data class BrandWithCosmetic(
    @Embedded val brand: BrandEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "idBrand"
    )
    val cosmetics: List<CosmeticEntity>
)
