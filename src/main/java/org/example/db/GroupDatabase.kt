package org.example.db

import org.example.model.Group
import org.example.utils.ExistException
import org.example.utils.Extensions.toGroup
import java.sql.Connection
import java.sql.Statement

class GroupDatabase(
    private val database: Database,
) {

    private val connection: Connection = database.connection()
    fun createGroup(group: Group): Result<Int> {
        return runCatching<Int> {
            if (database.existNameInGroup(group.name).getOrThrow()) {
                return Result.failure(ExistException("Group with name ${group.name} already exists"))
            }
            val query = """
                    INSERT INTO groups (name, description)
                    VALUES (
                        '${group.name}', 
                        '${group.description}'
                    )
            """.trimIndent()
            val statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
            statement.executeUpdate()
            statement.generatedKeys.next()
            statement.generatedKeys.getInt(1)
        }
    }

    fun editGroup(id: Int, group: Group): Result<Unit> {
        return runCatching<Unit> {
            if (database.existNameInGroup(group.name).getOrThrow() && !database.isSameGroup(id, group.name).getOrThrow()) {
                return Result.failure(ExistException("Group with name ${group.name} already exists"))
            }
            val currentGroup = getGroupById(id).getOrThrow()
            val statement = connection.createStatement()
            val queryGroup = """
                    UPDATE groups
                    SET 
                        name = '${group.name}', 
                        description = '${group.description}'
                    WHERE id = '$id'
            """.trimIndent()
            statement.executeUpdate(queryGroup)
            val queryProduct = """
                    UPDATE products
                    SET 
                        groupName = '${group.name}'
                    WHERE groupName = '${currentGroup.name}'
            """.trimIndent()
            println(queryProduct)
            statement.executeUpdate(queryProduct)
        }
    }

    fun getAllGroup(): Result<List<Group>> {
        return runCatching<List<Group>> {
            val statement = connection.createStatement()
            val query = "SELECT * FROM groups"
            val resultSet = statement.executeQuery(query)
            val groups = mutableListOf<Group>()
            while (resultSet.next()) {
                groups.add(resultSet.toGroup())
            }
            groups
        }
    }

    fun getGroupById(groupId: Int): Result<Group> {
        return runCatching<Group> {
            val statement = connection.createStatement()
            val query = "SELECT * FROM groups WHERE id = '$groupId'"
            val resultSet = statement.executeQuery(query)
            resultSet.next()
            resultSet.toGroup()
        }
    }

    fun deleteGroup(id: Int): Result<Unit> {
        return runCatching<Unit> {
            val statement = connection.createStatement()
            val groupName = getGroupById(id).getOrThrow().name
            deleteProductByGroup(groupName)
            val query = "DELETE FROM groups WHERE id = '$id'"
            statement.executeUpdate(query)
        }
    }

    private fun deleteProductByGroup(groupName: String): Result<Unit> {
        return runCatching<Unit> {
            val statement = connection.createStatement()
            val query = "DELETE FROM products WHERE groupName = '$groupName'"
            println(query)
            statement.executeUpdate(query)
        }
    }
}
