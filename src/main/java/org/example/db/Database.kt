package org.example.db

import java.sql.DriverManager

class Database(name: String, user: String, password: String) {
    private val connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/$name", user, password)
    fun stop() = connection.close()

    init {
        createTableProduct()
        createTableGroup()
    }

    private fun createTableProduct() {
        val statement = connection.createStatement()
        val query = """
            CREATE TABLE IF NOT EXISTS products (
                id SERIAL NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
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
                description TEXT NULL,
                FOREIGN KEY (id) REFERENCES products(id)
            );
        """.trimIndent()
        statement.executeUpdate(query)
    }
}
