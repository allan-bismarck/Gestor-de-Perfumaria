package com.app.gestordeperfumaria.dataclasses

import androidx.room.Embedded
import androidx.room.Relation
import com.app.gestordeperfumaria.entities.BrandEntity
import com.app.gestordeperfumaria.entities.CosmeticEntity

data class BrandWithCosmetic(
    @Embedded val brand: BrandEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "idBrand"
    )
    val cosmetics: List<CosmeticEntity>
)
