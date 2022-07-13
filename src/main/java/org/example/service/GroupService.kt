package org.example.service

import com.sun.net.httpserver.HttpsExchange
import org.example.db.Database
import org.example.db.GroupDatabase
import org.example.model.Group
import org.example.utils.Extensions.mapper

class GroupService(
    private val database: Database
) : IService {

    private val groupDatabase = GroupDatabase(database)

    fun processGetAllGroup(exchange: HttpsExchange) {
        val result = groupDatabase.getAllGroup()
        process(exchange, 201, result)
    }

    fun processGetGroup(exchange: HttpsExchange) {
        val id = exchange.requestURI.path.split("/").last().toInt()
        val result = groupDatabase.getGroupById(id)
        process(exchange, 201, result)
    }

    fun processEditGroup(exchange: HttpsExchange) {
        val group = mapper.readValue(exchange.requestBody, Group::class.java)
        validation(
            message = group.isValid(),
            good = {
                val id = exchange.requestURI.path.split("/").last().toInt()
                val result = groupDatabase.editGroup(id, group)
                process(exchange, 204, result)
            },
            error = {
                process(exchange, 400, it)
            }
        )
    }

    fun processDeleteGroup(exchange: HttpsExchange) {
        val id = exchange.requestURI.path.split("/").last().toInt()
        val result = groupDatabase.deleteGroup(id)
        process(exchange, 204, result)
    }

    fun processCreateGroup(exchange: HttpsExchange) {
        val group = mapper.readValue(exchange.requestBody, Group::class.java)
        validation(
            message = group.isValid(),
            good = {
                val resultID = groupDatabase.createGroup(group)
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
}
