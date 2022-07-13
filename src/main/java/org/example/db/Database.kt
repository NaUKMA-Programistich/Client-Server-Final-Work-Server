package org.example.db

import org.example.model.Product
import org.example.model.ProductFilter
import org.example.utils.Extensions.toProduct
import java.sql.Connection
import java.sql.DriverManager

class Database(name: String, user: String, password: String) {
    private val connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/$name", user, password)
    fun stop(): Unit = connection.close()
    fun connection(): Connection = connection

    fun dropTables(): Result<Unit> {
        return kotlin.runCatching {
            connection.createStatement().execute("DROP TABLE IF EXISTS groups")
            connection.createStatement().execute("DROP TABLE IF EXISTS products")
        }
    }

    fun createTables() {
        createTableProduct()
        createTableGroup()
    }

    init {
        createTables()
    }

    private fun createTableProduct() {
        val statement = connection.createStatement()
        val query = """
            CREATE TABLE IF NOT EXISTS products (
                id SERIAL NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                groupname TEXT NOT NULL,
                description TEXT NULL,
                vendor TEXT NOT NULL,
                count INTEGER NOT NULL,
                price DOUBLE PRECISION NOT NULL
            );
        """.trimIndent()
        statement.executeUpdate(query)
    }

    private fun createTableGroup() {
        val statement = connection.createStatement()
        val query = """
            CREATE TABLE IF NOT EXISTS groups (
                id SERIAL NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                description TEXT NULL
            );
        """.trimIndent()
        statement.executeUpdate(query)
    }

    fun existNameInProduct(name: String): Result<Boolean> {
        return runCatching<Boolean> {
            val statement = connection.createStatement()
            val query = "SELECT name FROM products WHERE name = '$name'"
            val resultSet = statement.executeQuery(query)
            resultSet.next()
        }
    }

    fun isSameProduct(id: Int, name: String) : Result<Boolean> {
        return runCatching<Boolean> {
            val statement = connection.createStatement()
            val query = "SELECT * FROM products WHERE id = '$id' AND name = '$name'"
            val resultSet = statement.executeQuery(query)
            resultSet.next()
        }
    }

    fun existNameInGroup(name: String): Result<Boolean> {
        return runCatching<Boolean> {
            val statement = connection.createStatement()
            val query = "SELECT name FROM groups WHERE name = '$name'"
            val resultSet = statement.executeQuery(query)
            resultSet.next()
        }
    }

    fun isSameGroup(id: Int, name: String) : Result<Boolean> {
        return runCatching<Boolean> {
            val statement = connection.createStatement()
            val query = "SELECT * FROM groups WHERE id = '$id' AND name = '$name'"
            val resultSet = statement.executeQuery(query)
            resultSet.next()
        }
    }

    fun getFilterProduct(productFilter: ProductFilter = ProductFilter()): Result<List<Product>> {
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
}
