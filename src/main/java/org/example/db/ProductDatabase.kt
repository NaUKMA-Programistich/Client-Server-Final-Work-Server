package org.example.db

import org.example.model.Product
import org.example.utils.ExistException
import org.example.utils.Extensions.toProduct
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Statement

class ProductDatabase(
    private val database: Database,
) {

    private val connection: Connection = database.connection()

    fun createProduct(product: Product): Result<Int> {
        return runCatching<Int> {
            if (database.existNameInProduct(product.name).getOrThrow()) {
                return Result.failure(ExistException("Product with name ${product.name} already exists"))
            }
            if (!database.existNameInGroup(product.group).getOrThrow()) {
                return Result.failure(ExistException("Group with name ${product.group} does not exist yet"))
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
            if (database.existNameInProduct(product.name).getOrThrow() && !database.isSameProduct(id, product.name).getOrThrow()) {
                return Result.failure(ExistException("Product with name ${product.name} already exists"))
            }
            if (!database.existNameInGroup(product.group).getOrThrow()) {
                return Result.failure(ExistException("Group with name ${product.group} does not exist yet"))
            }
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

    fun getAllProduct(): Result<List<Product>> {
        return runCatching<List<Product>> {
            val products = mutableListOf<Product>()
            val query = "SELECT * FROM products"
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

    fun getProductByName(name: String): Result<Product> {
        return runCatching<Product> {
            val statement = connection.createStatement()
            val query = "SELECT * FROM products WHERE name = '$name'"
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
