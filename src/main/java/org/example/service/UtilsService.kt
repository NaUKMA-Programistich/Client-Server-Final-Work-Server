package org.example.service

import com.sun.net.httpserver.HttpsExchange
import org.example.db.Database
import org.example.model.ProductFilter
import org.example.utils.Extensions.mapper

open class UtilsService(
    private val database: Database
) : IService {
    fun processUnknown(exchange: HttpsExchange) {
        process(exchange, 404, Result.failure(Exception("Unknown request")))
    }

    fun processSearch(exchange: HttpsExchange) {
        val filter = mapper.readValue(exchange.requestBody, ProductFilter::class.java)
        val result = database.getFilterProduct(filter)
        process(exchange, 201, result)
    }
}
