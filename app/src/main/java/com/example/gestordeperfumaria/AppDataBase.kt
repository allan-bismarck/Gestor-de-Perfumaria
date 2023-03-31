package com.example.gestordeperfumaria

import androidx.room.Database

@Database(entities = [BrandEntity::class, CosmeticEntity::class], version = 1)
abstract class AppDataBase {

    abstract val brandDAO: BrandDAO
    abstract val cosmeticDAO: CosmeticDAO

    
}