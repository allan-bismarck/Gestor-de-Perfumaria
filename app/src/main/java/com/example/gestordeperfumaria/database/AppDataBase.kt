package com.example.gestordeperfumaria.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gestordeperfumaria.daos.BrandDAO
import com.example.gestordeperfumaria.daos.BrandWithCosmeticDAO
import com.example.gestordeperfumaria.daos.CosmeticDAO
import com.example.gestordeperfumaria.entities.BrandEntity
import com.example.gestordeperfumaria.entities.CosmeticEntity

@Database(entities = [BrandEntity::class, CosmeticEntity::class], version = 3)
abstract class AppDataBase: RoomDatabase() {

    abstract val brandDAO: BrandDAO
    abstract val cosmeticDAO: CosmeticDAO
    abstract val brandWithCosmeticDAO: BrandWithCosmeticDAO

    companion object {
        @Volatile
        private  var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            synchronized(this) {
                var instance: AppDataBase? = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        AppDataBase::class.java, "db-perfumery"
                    ).build()
                }
                return instance
            }
        }
    }
}