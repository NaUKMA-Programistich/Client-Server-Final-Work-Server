package service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpsExchange
import org.example.db.Database
import org.example.db.GroupDatabase
import org.example.db.ProductDatabase
import org.example.model.*
import org.example.service.UtilsService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import utils.TestConstants.*
import utils.Whitebox
import java.io.ByteArrayInputStream
import java.io.OutputStream


@ExtendWith(MockitoExtension::class)
class UtilsServiceTest {
    @Mock
    private lateinit var database: Database

    @Mock
    private lateinit var productDatabase: ProductDatabase

    @Mock
    private lateinit var groupDatabase: GroupDatabase

    @InjectMocks
    private lateinit var utilsService: UtilsService

    @BeforeEach
    fun setUp() {
        utilsService = spy(UtilsService(database))
        Whitebox.setInternalState(utilsService, "groupDatabase", groupDatabase)
        Whitebox.setInternalState(utilsService, "productDatabase", productDatabase)
    }

    @AfterEach
    fun tearDown() {
        verifyNoMoreInteractions(productDatabase)
    }

    @Test
    fun processSearchTest() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = SEARCH_JSON.toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val suspectedResult = getProductsList()
        val filter = getProductFilter(SEARCH_JSON).getOrThrow()
        `when`(httpsExchange.requestBody).thenReturn(inputStream)
        `when`(database.getFilterProduct(filter)).thenReturn(suspectedResult)

        utilsService.processSearch(httpsExchange)

        verify(utilsService).process(httpsExchange, 201, suspectedResult)
        verify(database).getFilterProduct(filter)
    }

    @Test
    fun processStatisticsTest() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val product = getProduct(PRODUCT_JSON).getOrThrow()
        val productsPrice = product.count * product.price * getProductsList().getOrThrow().size
        val groupsResult = arrayListOf<GroupStatistics>()
        groupsResult.add(
            GroupStatistics(
                group = getGroup(GROUP_JSON).getOrThrow(),
                products = getProductsList().getOrThrow(),
                price = productsPrice
            )
        )
        val suspectedPrice = PRODUCT_PRICE.toDouble() * PRODUCT_COUNT.toDouble() * getProductsList().getOrThrow().size
        val suspectedResult = Statistics(
            groups = groupsResult,
            price = suspectedPrice
        )
        `when`(productDatabase.getAllProduct()).thenReturn(getProductsList())
        `when`(groupDatabase.getAllGroup()).thenReturn(getGroupsList())

        utilsService.processStatistics(httpsExchange)

        verify(utilsService).process(httpsExchange, 201, Result.success(suspectedResult))
        verify(productDatabase, only()).getAllProduct()
        verify(groupDatabase, only()).getAllGroup()
    }

    private fun getProductFilter(json: String): Result<ProductFilter> {
        return kotlin.runCatching {
            jacksonObjectMapper().readValue(json, ProductFilter::class.java)
        }
    }

    private fun getProductsList(): Result<List<Product>> {
        return kotlin.runCatching {
            val list = ArrayList<Product>()
            list.add(getProduct(PRODUCT_JSON).getOrThrow())
            list.add(getProduct(PRODUCT_JSON).getOrThrow().copy(name = PRODUCT_NAME + "2"))
            list
        }
    }

    private fun getProduct(json: String): Result<Product> {
        return kotlin.runCatching {
            jacksonObjectMapper().readValue(json, Product::class.java)
        }
    }

    private fun getGroupsList(): Result<List<Group>> {
        return kotlin.runCatching {
            val list = ArrayList<Group>()
            list.add(getGroup(GROUP_JSON).getOrThrow())
            list
        }
    }

    private fun getGroup(json: String): Result<Group> {
        return kotlin.runCatching {
            jacksonObjectMapper().readValue(json, Group::class.java)
        }
    }
}