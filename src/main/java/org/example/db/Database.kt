package org.example.db

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

    fun existNameInGroup(name: String): Result<Boolean> {
        return runCatching<Boolean> {
            val statement = connection.createStatement()
            val query = "SELECT name FROM groups WHERE name = '$name'"
            val resultSet = statement.executeQuery(query)
            resultSet.next()
        }
    }
}
