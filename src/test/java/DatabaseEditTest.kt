import org.example.db.Database
import org.example.db.GroupDatabase
import org.example.db.ProductDatabase
import org.example.model.Group
import org.example.model.Product
import org.example.utils.Mock
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseEditTest {
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
    fun `Edit group`() {
        val group = Group(name = "Group A1", description = "Description A1")
        val result = groupDatabase.editGroup(1, group)
        println(result.exceptionOrNull())
        assert(result.isSuccess)
    }

    @Test
    fun `Edit Products`() {
        val product = Product(
            name = "Product 1A",
            description = "Description 1A",
            vendor = "Vendor 1A",
            count = 12,
            price = 14.0,
            group = "Group B"
        )
        val result = productDatabase.editProduct(3, product)
        println(result.exceptionOrNull())
        assert(result.isSuccess)
    }
}
