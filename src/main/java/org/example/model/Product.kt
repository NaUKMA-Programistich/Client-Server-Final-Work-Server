package org.example.model

data class Product(
    val name: String,
    val description: String? = null,
    val vendor: String,
    val count: Int,
    val price: Double,
    val group: Group
)
