package org.example.service

import com.sun.net.httpserver.HttpsExchange
import org.example.db.Database
import org.example.db.GroupDatabase
import org.example.db.ProductDatabase
import org.example.model.GroupStatistics
import org.example.model.ProductFilter
import org.example.model.Statistics
import org.example.utils.Extensions.mapper

open class UtilsService(
    private val database: Database
) : IService {
    private val groupDatabase = GroupDatabase(database)
    private val productDatabase = ProductDatabase(database)
    fun processUnknown(exchange: HttpsExchange) {
        process(exchange, 404, Result.failure(Exception("Unknown request")))
    }
    fun processSearch(exchange: HttpsExchange) {
        val filter = mapper.readValue(exchange.requestBody, ProductFilter::class.java)
        val result = database.getFilterProduct(filter)
        process(exchange, 201, result)
    }

    fun processStatistics(exchange: HttpsExchange) {
        val products = productDatabase.getAllProduct()
        val groups = groupDatabase.getAllGroup()
        products.onFailure {
            process(exchange, 400, products)
        }
        groups.onFailure {
            process(exchange, 400, groups)
        }
        val result = arrayListOf<GroupStatistics>()
        var price = 0.0
        groups.onSuccess {
            it.forEach { group ->
                val productsByGroup = products.getOrThrow().filter { it.group == group.name }
                val priceInGroup = productsByGroup.sumOf { it.price }
                val countInGroup = productsByGroup.sumOf { it.count }
                price += (priceInGroup * countInGroup)
                result.add(
                    GroupStatistics(
                        group = group,
                        products = productsByGroup,
                        price = priceInGroup * countInGroup,
                    )
                )
            }
        }
        val statistics = Statistics(
            groups = result,
            price = price
        )
        process(exchange, 201, Result.success(statistics))
    }
}
