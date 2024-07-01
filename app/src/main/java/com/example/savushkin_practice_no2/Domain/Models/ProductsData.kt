package com.example.savushkin_practice_no2.Domain.Models

data class ProductsData(
    var GTIN: String? = null,
    var SNM: String? = null,
    var Capacity: String? = null,
    var Weight: Int? = null,
    var Length: Int? = null,
    var Square: Int? = null,
    var Quantity: Int? = null,
    var ProductionDate: String? = null,
    var LotNumber: String? = null,
    var PalletNumber: String? = null,
    var Volume: Int? = null,
)
