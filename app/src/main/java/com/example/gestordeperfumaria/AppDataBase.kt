package com.example.gestordeperfumaria

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

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