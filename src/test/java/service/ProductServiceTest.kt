package service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpsExchange
import org.example.db.Database
import org.example.db.ProductDatabase
import org.example.model.Product
import org.example.service.ProductService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import utils.TestConstants.*
import utils.Whitebox
import java.io.ByteArrayInputStream
import java.io.OutputStream
import java.net.URI
import java.util.stream.IntStream

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {
    @Mock
    private lateinit var database: Database

    @Mock
    private lateinit var productDatabase: ProductDatabase

    @InjectMocks
    private lateinit var productService: ProductService

    @BeforeEach
    fun setUp() {
        productService = spy(ProductService(database))
        Whitebox.setInternalState(productService, "productDatabase", productDatabase)
    }

    @AfterEach
    fun tearDown() {
        verifyNoMoreInteractions(productDatabase)
    }

    @Test
    fun processCreateProduct_thenSuccess() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = PRODUCT_JSON.toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val idToCreate = Result.success(2)
        val inObject = getProduct(PRODUCT_JSON).getOrThrow()
        val suspectedResult = Result.success(mapOf("id" to idToCreate.getOrNull()))
        `when`(httpsExchange.requestBody).thenReturn(inputStream)
        `when`(productDatabase.createProduct(inObject)).thenReturn(idToCreate)

        productService.processCreateProduct(httpsExchange)

        verify(productService).process(httpsExchange, 201, suspectedResult)
        verify(productDatabase, only()).createProduct(inObject)
    }

    @ParameterizedTest
    @MethodSource("badProductAmount")
    fun processCreateBadProduct_thenFail(incorrectNumber: Int) {
        val httpsExchange = mock(HttpsExchange::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = getIncorrectProductJson(incorrectNumber).toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val inObject = getProduct(getIncorrectProductJson(incorrectNumber)).getOrThrow()
        val suspectedResult = getIncorrectMessage(incorrectNumber)
        `when`(httpsExchange.requestBody).thenReturn(inputStream)

        productService.processCreateProduct(httpsExchange)

        verify(productService).process(httpsExchange, 400, suspectedResult)
        verify(productDatabase, never()).createProduct(inObject)
    }

    @Test
    fun processGetAllProducts_thenSuccess() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val suspectedResult = getProductsList()
        `when`(productDatabase.getAllProduct()).thenReturn(suspectedResult)

        productService.processGetAllProduct(httpsExchange)

        verify(productService).process(httpsExchange, 201, suspectedResult)
        verify(productDatabase, only()).getAllProduct()
    }

    @Test
    fun processGetGroup_thenSuccess() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val idToGet = 1
        val suspectedResult = Result.success(getProduct(PRODUCT_JSON).getOrThrow().copy(id = idToGet))
        `when`(httpsExchange.requestURI).thenReturn(URI("/api/product/$idToGet"))
        `when`(productDatabase.getProductById(idToGet)).thenReturn(suspectedResult)

        productService.processGetProduct(httpsExchange)

        verify(productService).process(httpsExchange, 201, suspectedResult)
        verify(productDatabase, only()).getProductById(idToGet)
    }

    @Test
    fun processEditProduct_thenSuccess() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = PRODUCT_JSON.toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val idToEdit = 2
        val inObject = getProduct(PRODUCT_JSON).getOrThrow()
        val suspectedResult = Result.success(Unit)
        `when`(httpsExchange.requestBody).thenReturn(inputStream)
        `when`(httpsExchange.requestURI).thenReturn(URI("/api/product/$idToEdit"))
        `when`(productDatabase.editProduct(idToEdit, inObject)).thenReturn(suspectedResult)

        productService.processEditProduct(httpsExchange)

        verify(productService).process(httpsExchange, 204, suspectedResult)
        verify(productDatabase, only()).editProduct(idToEdit, inObject)
    }

    @ParameterizedTest
    @MethodSource("badProductAmount")
    fun processEditEmptyNameProduct_thenFail(incorrectNumber: Int) {
        val httpsExchange = mock(HttpsExchange::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = getIncorrectProductJson(incorrectNumber).toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val suspectedResult = getIncorrectMessage(incorrectNumber)
        `when`(httpsExchange.requestBody).thenReturn(inputStream)

        productService.processEditProduct(httpsExchange)

        verify(productService).process(httpsExchange, 400, suspectedResult)
    }

    @Test
    fun processDeleteGroup_thenSuccess() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val idToDelete = 2
        val suspectedResult = Result.success(Unit)
        `when`(httpsExchange.requestURI).thenReturn(URI("/api/product/$idToDelete"))
        `when`(productDatabase.deleteProduct(idToDelete)).thenReturn(suspectedResult)

        productService.processDeleteProduct(httpsExchange)

        verify(productService).process(httpsExchange, 204, suspectedResult)
        verify(productDatabase, only()).deleteProduct(idToDelete)
    }

    @Test
    fun processAddProduct_thenSuccess() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = COUNT_PRODUCT_JSON.toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val getId = 1
        val getProductResult = Result.success(getProduct(PRODUCT_JSON).getOrThrow().copy(id = getId))
        val getAddedProductResult =
            getProductResult.getOrThrow().copy(count = PRODUCT_COUNT.toInt() + COUNT_PRODUCT_COUNT.toInt())
        val suspectedResult = Result.success(mapOf("count" to getAddedProductResult.count))
        `when`(httpsExchange.requestBody).thenReturn(inputStream)
        `when`(productDatabase.getProductByName(COUNT_PRODUCT_NAME)).thenReturn(getProductResult)
        `when`(productDatabase.editProduct(getId, getAddedProductResult)).thenReturn(Result.success(Unit))

        productService.processAddProduct(httpsExchange)

        verify(productService).process(httpsExchange, 201, suspectedResult)
        verify(productDatabase).getProductByName(COUNT_PRODUCT_NAME)
        verify(productDatabase).editProduct(getId, getAddedProductResult)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 3])
    fun processAddBadProduct_thenFail(incorrectCount: Int) {
        val httpsExchange = mock(HttpsExchange::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = getIncorrectCountJson(incorrectCount).toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val suspectedResult = getIncorrectMessage(incorrectCount)
        `when`(httpsExchange.requestBody).thenReturn(inputStream)

        productService.processAddProduct(httpsExchange)

        verify(productService).process(httpsExchange, 400, suspectedResult)
        verify(productDatabase, never()).getProductByName("")
    }

    @Test
    fun processRemoveProduct_thenSuccess() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = COUNT_PRODUCT_JSON.toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val getId = 1
        val getProductResult = Result.success(getProduct(PRODUCT_JSON).getOrThrow().copy(id = getId))
        val getAddedProductResult =
            getProductResult.getOrThrow().copy(count = PRODUCT_COUNT.toInt() - COUNT_PRODUCT_COUNT.toInt())
        val suspectedResult = Result.success(mapOf("count" to getAddedProductResult.count))
        `when`(httpsExchange.requestBody).thenReturn(inputStream)
        `when`(productDatabase.getProductByName(COUNT_PRODUCT_NAME)).thenReturn(getProductResult)
        `when`(productDatabase.editProduct(getId, getAddedProductResult)).thenReturn(Result.success(Unit))

        productService.processRemoveProduct(httpsExchange)

        verify(productService).process(httpsExchange, 201, suspectedResult)
        verify(productDatabase).getProductByName(COUNT_PRODUCT_NAME)
        verify(productDatabase).editProduct(getId, getAddedProductResult)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 3])
    fun processRemoveBadProduct_thenFail(incorrectCount: Int) {
        val httpsExchange = mock(HttpsExchange::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = getIncorrectCountJson(incorrectCount).toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val suspectedResult = getIncorrectMessage(incorrectCount)
        `when`(httpsExchange.requestBody).thenReturn(inputStream)

        productService.processRemoveProduct(httpsExchange)

        verify(productService).process(httpsExchange, 400, suspectedResult)
        verify(productDatabase, never()).getProductByName("")
    }

    @Test
    fun processRemoveTooMuchProduct_thenFail() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = COUNT_PRODUCT_JSON_TOOMUCH_COUNT.toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val getId = 1
        val getProductResult = Result.success(getProduct(PRODUCT_JSON).getOrThrow().copy(id = getId))

        `when`(httpsExchange.requestBody).thenReturn(inputStream)
        `when`(productDatabase.getProductByName(COUNT_PRODUCT_NAME)).thenReturn(getProductResult)

        productService.processRemoveProduct(httpsExchange)

        verify(productDatabase, only()).getProductByName(COUNT_PRODUCT_NAME)
    }

    private fun getIncorrectProductJson(incorrectNumber: Int): String {
        return when (incorrectNumber) {
            1 -> PRODUCT_JSON_EMPTY_NAME
            2 -> PRODUCT_JSON_EMPTY_VENDOR
            3 -> PRODUCT_JSON_BAD_COUNT
            4 -> PRODUCT_JSON_BAD_PRICE
            5 -> PRODUCT_JSON_EMPTY_GROUP
            else -> {
                throw IndexOutOfBoundsException()
            }
        }
    }

    private fun getIncorrectCountJson(incorrectNumber: Int): String {
        return when (incorrectNumber) {
            1 -> COUNT_PRODUCT_JSON_EMPTY_NAME
            3 -> COUNT_PRODUCT_JSON_BAD_COUNT
            else -> {
                throw IndexOutOfBoundsException()
            }
        }
    }


    private fun getIncorrectMessage(incorrectNumber: Int): String {
        return when (incorrectNumber) {
            1 -> NAME_EMPTY_MESSAGE
            2 -> VENDOR_EMPTY_MESSAGE
            3 -> COUNT_BAD_MESSAGE
            4 -> PRICE_BAD_MESSAGE
            5 -> GROUP_EMPTY_MESSAGE
            else -> {
                throw IndexOutOfBoundsException()
            }
        }
    }

    private fun getProductsList(): Result<List<Product>> {
        return kotlin.runCatching {
            val list = ArrayList<Product>()
            list.add(getProduct(PRODUCT_JSON).getOrThrow())
            list
        }
    }

    private fun getProduct(json: String): Result<Product> {
        return kotlin.runCatching {
            jacksonObjectMapper().readValue(json, Product::class.java)
        }
    }

    companion object {
        @JvmStatic
        fun badProductAmount(): IntArray {
            return IntStream.rangeClosed(1, 5).toArray()
        }

    }

}