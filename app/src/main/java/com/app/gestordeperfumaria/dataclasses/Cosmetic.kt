package com.app.gestordeperfumaria.dataclasses

data class Cosmetic(
    var id: Long,
    var name: String,
    var idBrand: Long,
    var date: String,
    var price: Float,
    var isSale: Boolean
)