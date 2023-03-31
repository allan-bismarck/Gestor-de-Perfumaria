package com.example.gestordeperfumaria

import androidx.lifecycle.LiveData

class DataBaseDataSource(
    private val brandDAO: BrandDAO,
    private val cosmeticDAO: CosmeticDAO
): BrandRepository, CosmeticRepository  {
    override suspend fun insertBrand(name: String, profit: Float): Long {
        val brand = BrandEntity(
            name = name,
            profit = profit
        )

        return brandDAO.insert(brand)
    }

    override suspend fun updateBrand(id: Long, name: String, profit: Float) {
        val brand = BrandEntity(
            name = name,
            profit = profit
        )

        brandDAO.update(brand)
    }

    override suspend fun deleteBrand(id: Long) {
        brandDAO.delete(id)
    }

    override suspend fun deleteAllBrands() {
        brandDAO.deleteAll()
    }

    override suspend fun getAllBrands(): LiveData<List<BrandEntity>> {
        return brandDAO.getAll()
    }

    override suspend fun insertCosmetic(name: String, brand: String, price: Float): Long {
        val cosmetic = CosmeticEntity(
            name = name,
            nameBrand = brand,
            price = price
        )

        return cosmeticDAO.insert(cosmetic)
    }

    override suspend fun updateCosmetic(id: Long, name: String, brand: String, price: Float) {
        val cosmetic = CosmeticEntity(
            name = name,
            nameBrand = brand,
            price = price
        )

        cosmeticDAO.update(cosmetic)
    }

    override suspend fun deleteCosmetic(id: Long) {
        cosmeticDAO.delete(id)
    }

    override suspend fun deleteAllCosmetics() {
        cosmeticDAO.deleteAll()
    }

    override suspend fun getAllCosmetics(): LiveData<List<CosmeticEntity>> {
        return cosmeticDAO.getAll()
    }

}