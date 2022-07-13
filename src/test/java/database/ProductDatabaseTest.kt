package database

import org.example.db.Database
import org.example.db.GroupDatabase
import org.example.db.ProductDatabase
import org.example.model.Product
import org.example.model.ProductFilter
import org.example.utils.Mock
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductDatabaseTest {
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
    }

    @Test
    @Order(1)
    fun createNewProduct_thenSuccess() {
        Mock.products.forEachIndexed { index, product ->
            val result = productDatabase.createProduct(product)
            assert(result.isSuccess)
            assertEquals(result.getOrThrow(), index + 1)
        }
    }

    @Test
    fun createExistingProduct_thenFail() {
        val product = productDatabase.getAllProduct().getOrThrow().random()
        val result = productDatabase.createProduct(product)
        assert(result.isFailure)
    }

    @Test
    fun createNonexistentGroupProduct_thenFail() {
        val product = getProductWithBadGroup()
        val result = productDatabase.createProduct(product)
        assert(result.isFailure)
    }

    @Test
    @Order(2)
    fun getAllProduct_thenReturnsCorrectAmount() {
        val products = productDatabase.getAllProduct()
        assert(products.isSuccess)
        assertEquals(products.getOrThrow().size, Mock.products.size)
    }

    @Test
    @Order(3)
    fun getAllFilteredProducts_thenReturnsCorrectProducts() {
        val products = database.getFilterProduct(ProductFilter(groupStart = "Group B"))
        assert(products.isSuccess)
        assertEquals(products.getOrThrow().size, 2)
        assert(products.getOrThrow().contains(productDatabase.getProductById(2).getOrThrow()))
        assert(!products.getOrThrow().contains(productDatabase.getProductById(1).getOrThrow()))
    }

    @Test
    fun getAnyProductById_thenSuccess() {
        val randomId = productDatabase.getAllProduct().getOrThrow().random().id
        val product = productDatabase.getProductById(randomId)
        assert(product.isSuccess)
    }

    @Test
    fun getAnyProductByName_thenSuccess() {
        val randomId = productDatabase.getAllProduct().getOrThrow().random().name
        val product = productDatabase.getProductByName(randomId)
        assert(product.isSuccess)
    }

    @Test
    fun editToCorrectProduct_thenSuccess() {
        var product = productDatabase.getAllProduct().getOrThrow().random()
        product = product.copy(name = "New Name")
        val result = productDatabase.editProduct(product.id, product)
        assert(result.isSuccess)
    }

    @Test
    fun editToExistingProduct_thenFail() {
        var product = productDatabase.getAllProduct().getOrThrow().random()
        var anotherProduct: Product
        do {
            anotherProduct = productDatabase.getAllProduct().getOrThrow().random()
        } while (anotherProduct.name == product.name)
        product = product.copy(name = anotherProduct.name)
        val result = productDatabase.editProduct(product.id, product)
        assert(result.isFailure)
    }

    @Test
    fun editToNonexistentGroupProduct_thenFail() {
        var product = productDatabase.getAllProduct().getOrThrow().random()
        product = product.copy(group = getProductWithBadGroup().group)
        val result = productDatabase.editProduct(product.id, product)
        assert(result.isFailure)
    }

    @Test
    fun deleteProduct_thenSuccess() {
        val randomId = productDatabase.getAllProduct().getOrThrow().random().id
        val res = productDatabase.deleteProduct(randomId)
        assert(res.isSuccess)
    }

    @Test
    fun deleteGroup_thenItsProductsDeleted() {
        val group = groupDatabase.getAllGroup().getOrThrow().random()
        val products = database.getFilterProduct(ProductFilter(groupStart = group.name))
        val res = groupDatabase.deleteGroup(group.id)
        products.onSuccess {
            it.forEach { product ->
                assert(!productDatabase.getAllProduct().getOrThrow().contains(product))
            }
        }
        assert(res.isSuccess)
    }

    private fun getProductWithBadGroup(): Product {
        return Product(
            name = "Bad Group Product",
            vendor = "Bad Group Vendor",
            count = 13,
            price = 13.0,
            group = "Bad Group"
        )
    }
}