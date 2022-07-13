package org.example.service

import com.sun.net.httpserver.HttpsExchange
import org.example.db.Database
import org.example.db.ProductDatabase
import org.example.model.CountProduct
import org.example.model.Product
import org.example.utils.Extensions.mapper

class ProductService(
    database: Database
) : IService {

    private val productDatabase = ProductDatabase(database)

    fun processGetAllProduct(exchange: HttpsExchange) {
        val result = productDatabase.getAllProduct()
        process(exchange, 201, result)
    }

    fun processGetProduct(exchange: HttpsExchange) {
        val id = exchange.requestURI.path.split("/").last().toInt()
        val result = productDatabase.getProductById(id)
        process(exchange, 201, result)
    }

    fun processEditProduct(exchange: HttpsExchange) {
        val product = mapper.readValue(exchange.requestBody, Product::class.java)
        validation(
            message = product.isValid(),
            good = {
                val id = exchange.requestURI.path.split("/").last().toInt()
                val result = productDatabase.editProduct(id, product)
                process(exchange, 204, result)
            },
            error = {
                process(exchange, 400, it)
            }
        )
    }

    fun processDeleteProduct(exchange: HttpsExchange) {
        val id = exchange.requestURI.path.split("/").last().toInt()
        val result = productDatabase.deleteProduct(id)
        process(exchange, 204, result)
    }

    fun processCreateProduct(exchange: HttpsExchange) {
        val product = mapper.readValue(exchange.requestBody, Product::class.java)
        validation(
            message = product.isValid(),
            good = {
                val resultID = productDatabase.createProduct(product)
                val result = if (resultID.isSuccess) {
                    Result.success(mapOf("id" to resultID.getOrNull()))
                } else Result.failure(resultID.exceptionOrNull() ?: Exception("Unknown error"))
                process(exchange, 201, result)
            },
            error = {
                process(exchange, 400, it)
            }
        )
    }

    fun processAddProduct(exchange: HttpsExchange) {
        val countProduct = mapper.readValue(exchange.requestBody, CountProduct::class.java)
        validation(
            message = countProduct.isValid(),
            good = {
                val productFromDB = productDatabase.getProductByName(countProduct.name)
                productFromDB.onFailure {
                    process(exchange, 400, productFromDB)
                }
                productFromDB.onSuccess {
                    val product = productFromDB.getOrThrow()
                    val count = product.count + countProduct.count
                    val changeProduct = product.copy(count = count)
                    processProduct(exchange, changeProduct)
                }
            },
            error = {
                process(exchange, 400, it)
            }
        )
    }

    fun processRemoveProduct(exchange: HttpsExchange) {
        val countProduct = mapper.readValue(exchange.requestBody, CountProduct::class.java)
        validation(
            message = countProduct.isValid(),
            good = {
                val productFromDB = productDatabase.getProductByName(countProduct.name)
                productFromDB.onFailure {
                    process(exchange, 400, productFromDB)
                }
                productFromDB.onSuccess {
                    val product = productFromDB.getOrThrow()
                    val count = product.count - countProduct.count
                    if (count >= 0) {
                        val changeProduct = product.copy(count = count)
                        processProduct(exchange, changeProduct)
                    } else {
                        process(exchange, 400, Result.failure(Exception("Not enough count")))
                    }
                }
            },
            error = {
                process(exchange, 400, it)
            }
        )
    }

    private fun processProduct(exchange: HttpsExchange, product: Product) {
        val result = productDatabase.editProduct(product.id, product)
        val content = if (result.isSuccess) Result.success(mapOf("count" to product.count))
        else Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
        process(exchange, 201, content)
    }
}
