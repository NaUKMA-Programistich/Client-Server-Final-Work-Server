package org.example.model

data class Statistics(
    val groups: List<GroupStatistics>,
    val price: Double,
)

data class GroupStatistics(
    val group: Group,
    val products: List<Product>,
    val price: Double
)
