package org.example.db

import org.example.model.Product
import org.example.model.ProductFilter
import org.example.utils.ExistException
import org.example.utils.Extensions.toProduct
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Statement

class ProductDatabase(
    private val database: Database,
    private val connection: Connection = database.connection()
) {

    fun createProduct(product: Product): Result<Int> {
        return runCatching<Int> {
            if (database.existNameInProduct(product.name).getOrThrow()) {
                return Result.failure(ExistException("Product with name ${product.name} already exist"))
            }
            if (!database.existNameInGroup(product.group).getOrThrow()) {
                return Result.failure(ExistException("Group with name ${product.group} already exist"))
            }
            val query = """
                    INSERT INTO products (name, description, groupName, vendor, count, price)
                    VALUES (
                        '${product.name}', 
                        '${product.description}', 
                        '${product.group}', 
                        '${product.vendor}', 
                        '${product.count}',
                        '${product.price}'
                    )
            """.trimIndent()
            val statement: PreparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
            statement.executeUpdate()
            statement.generatedKeys.next()
            statement.generatedKeys.getInt(1)
        }
    }

    fun editProduct(id: Int, product: Product): Result<Unit> {
        return runCatching<Unit> {
            val statement = connection.createStatement()
            val query = """
                    UPDATE products
                    SET name = '${product.name}',
                        description = '${product.description}',
                        groupName = '${product.group}',
                        vendor = '${product.vendor}',
                        count = '${product.count}',
                        price = '${product.price}'
                    WHERE id = '$id'
            """.trimIndent()
            statement.executeUpdate(query)
        }
    }

    fun getAllProduct(productFilter: ProductFilter = ProductFilter()): Result<List<Product>> {
        return runCatching<List<Product>> {
            val products = mutableListOf<Product>()
            var query = "SELECT * FROM products"

            val filters = setOf(
                SqlBuilder.startWith(productFilter.nameStart, "name"),
                SqlBuilder.startWith(productFilter.descriptionStart, "description"),
                SqlBuilder.startWith(productFilter.vendorStart, "vendor"),
                SqlBuilder.startWith(productFilter.groupStart, "groupName"),

                SqlBuilder.less(productFilter.countFrom?.toDouble(), "count"),
                SqlBuilder.more(productFilter.countTo?.toDouble(), "count"),

                SqlBuilder.less(productFilter.priceFrom, "price"),
                SqlBuilder.more(productFilter.priceTo, "price"),
            )

            if (filters.filterNotNull().isNotEmpty()) {
                query += " WHERE " + filters.filterNotNull().joinToString(" AND ")
            }

            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)
            while (resultSet.next()) {
                products.add(resultSet.toProduct())
            }
            products
        }
    }

    fun getProductById(id: Int): Result<Product> {
        return runCatching<Product> {
            val statement = connection.createStatement()
            val query = "SELECT * FROM products WHERE id = '$id'"
            val resultSet = statement.executeQuery(query)
            resultSet.next()
            resultSet.toProduct()
        }
    }

    fun deleteProduct(id: Int): Result<Unit> {
        return runCatching<Unit> {
            val statement = connection.createStatement()
            val query = "DELETE FROM products WHERE id = '$id'"
            statement.executeUpdate(query)
        }
    }
}
