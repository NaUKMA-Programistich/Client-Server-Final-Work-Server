package org.example.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.model.Group
import org.example.model.Product
import java.sql.ResultSet

object Extensions {

    val mapper = jacksonObjectMapper()
    fun ResultSet.toGroup(): Group {
        return Group(
            id = getInt("id"),
            name = getString("name"),
            description = getString("description")
        )
    }

    fun ResultSet.toProduct(): Product {
        return Product(
            getInt("id"),
            getString("name"),
            getString("description"),
            getString("vendor"),
            getInt("count"),
            getDouble("price"),
            getString("groupname")
        )
    }
}
