import org.example.db.Database
import org.example.db.GroupDatabase
import org.example.db.ProductDatabase
import org.example.utils.Mock
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseDeleteTest {
    private val database = Database("clientTest", "root", "root")
    private val groupDatabase = GroupDatabase(database)
    private val productDatabase = ProductDatabase(database)

    @BeforeAll
    fun setup() {
        database.dropTables()
        database.createTables()
        Mock.group.forEach {
            val result = groupDatabase.createGroup(it)
            println(result)
        }
        Mock.products.forEach {
            val result = productDatabase.createProduct(it)
            println(result)
        }
    }

    @Test
    fun `Delete group by id 2`() {
        val result = groupDatabase.deleteGroup(2)
        println(result.exceptionOrNull())
        assert(result.isSuccess)
    }

    @Test
    fun `Delete product by id 2`() {
        val result = productDatabase.deleteProduct(2)
        println(result.exceptionOrNull())
        assert(result.isSuccess)
    }
}
