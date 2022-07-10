import org.example.db.Database
import org.example.db.GroupDatabase
import org.example.db.ProductDatabase
import org.example.model.ProductFilter
import org.example.utils.Mock
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseGetTest {
    private val database = Database("client", "root", "root")
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
    fun `Get products`() {
        val result = productDatabase.getAllProduct()
        assert(result.isSuccess)
        println(result.getOrThrow().forEach(::println))
    }

    @Test
    fun `Get products with filter`() {
        val filter = ProductFilter(
            priceFrom = 100.0,
            nameStart = "Product",
            groupStart = "Group B"
        )
        val result = productDatabase.getAllProduct(filter)
        assert(result.isSuccess)
        println(result.getOrThrow().forEach(::println))
    }

    @Test
    fun `Get product by id`() {
        val result = productDatabase.getProductById(3)
        assert(result.isSuccess)
        println(result.getOrThrow())
    }

    @Test
    fun `Get groups`() {
        val result = groupDatabase.getAllGroup()
        assert(result.isSuccess)
        println(result.getOrThrow().forEach(::println))
    }

    @Test
    fun `Get group by id`() {
        val result = groupDatabase.getGroupById(2)
        assert(result.isSuccess)
        println(result.getOrThrow())
    }
}
