package database

import org.example.db.Database
import org.example.db.GroupDatabase
import org.example.model.Group
import org.example.utils.Mock
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupDatabaseTest {
    private val database = Database("clientTest", "root", "root")
    private val groupDatabase = GroupDatabase(database)

    @BeforeAll
    fun setup() {
        database.dropTables()
        database.createTables()
    }

    @Test
    @Order(1)
    fun createNewGroup_thenSuccess() {
        Mock.group.forEachIndexed { index, group ->
            val result = groupDatabase.createGroup(group)
            assert(result.isSuccess)
            assertEquals(result.getOrThrow(), index + 1)
        }
    }

    @Test
    fun createExistingGroup_thenFail() {
        val group = groupDatabase.getAllGroup().getOrThrow().random()
        val result = groupDatabase.createGroup(group)
        assert(result.isFailure)
    }

    @Test
    @Order(2)
    fun getAllGroup_thenReturnsCorrectAmount() {
        val groups = groupDatabase.getAllGroup()
        assert(groups.isSuccess)
        assertEquals(groups.getOrThrow().size, Mock.group.size)
    }

    @Test
    fun getAnyGroup_thenSuccess() {
        val randomId = groupDatabase.getAllGroup().getOrThrow().random().id
        val group = groupDatabase.getGroupById(randomId)
        assert(group.isSuccess)
    }

    @Test
    fun editToNewGroup_thenSuccess() {
        var group = groupDatabase.getAllGroup().getOrThrow().random()
        group = group.copy(name = "New Name")
        val result = groupDatabase.editGroup(group.id, group)
        assert(result.isSuccess)
    }

    @Test
    fun editToExistingGroup_thenFail() {
        var group = groupDatabase.getAllGroup().getOrThrow().random()
        var anotherGroup: Group
        do {
            anotherGroup = groupDatabase.getAllGroup().getOrThrow().random()
        } while (anotherGroup.name == group.name)
        group = group.copy(name = anotherGroup.name)
        val result = groupDatabase.editGroup(group.id, group)
        assert(result.isFailure)
    }

    @Test
    fun deleteGroup_thenSuccess() {
        val randomId = groupDatabase.getAllGroup().getOrThrow().random().id
        val res = groupDatabase.deleteGroup(randomId)
        assert(res.isSuccess)
    }
}