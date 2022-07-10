package org.example.model

data class ProductFilter(
    val nameStart: String? = null,
    val descriptionStart: String? = null,
    val vendorStart: String? = null,
    val countFrom: Int? = null,
    val countTo: Int? = null,
    val priceFrom: Double? = null,
    val priceTo: Double? = null,
    val groupStart: String? = null,
)
