package service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpsExchange
import org.example.db.Database
import org.example.db.GroupDatabase
import org.example.model.Group
import org.example.service.GroupService
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
import java.net.URI

@ExtendWith(MockitoExtension::class)
class GroupServiceTest {

    @Mock
    private lateinit var database: Database

    @Mock
    private lateinit var groupDatabase: GroupDatabase

    @InjectMocks
    private lateinit var groupService: GroupService

    @BeforeEach
    fun setUp() {
        groupService = spy(GroupService(database))
        Whitebox.setInternalState(groupService, "groupDatabase", groupDatabase)
    }

    @AfterEach
    fun tearDown() {
        verifyNoMoreInteractions(groupDatabase)
    }

    @Test
    fun processCreateGroup_thenSuccess() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = GROUP_JSON.toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val idToCreate = Result.success(3)
        val inObject = getGroup(GROUP_JSON).getOrThrow()
        val suspectedResult = Result.success(mapOf("id" to idToCreate.getOrNull()))
        `when`(httpsExchange.requestBody).thenReturn(inputStream)
        `when`(groupDatabase.createGroup(inObject)).thenReturn(idToCreate)

        groupService.processCreateGroup(httpsExchange)

        verify(groupService).process(httpsExchange, 201, suspectedResult)
        verify(groupDatabase, only()).createGroup(inObject)
    }

    @Test
    fun processCreateEmptyNameGroup_thenFail() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = GROUP_JSON_EMPTY_NAME.toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val inObject = getGroup(GROUP_JSON_EMPTY_NAME).getOrThrow()
        val suspectedResult = NAME_EMPTY_MESSAGE
        `when`(httpsExchange.requestBody).thenReturn(inputStream)

        groupService.processCreateGroup(httpsExchange)

        verify(groupService).process(httpsExchange, 400, suspectedResult)
        verify(groupDatabase, never()).createGroup(inObject)
    }

    @Test
    fun processGetAllGroup_thenSuccess() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val suspectedResult = getGroupsList()
        `when`(groupDatabase.getAllGroup()).thenReturn(suspectedResult)

        groupService.processGetAllGroup(httpsExchange)

        verify(groupService).process(httpsExchange, 201, suspectedResult)
        verify(groupDatabase, only()).getAllGroup()
    }

    @Test
    fun processGetGroup_thenSuccess() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val idToGet = 1
        val suspectedResult = Result.success(getGroup(GROUP_JSON).getOrThrow().copy(id = idToGet))
        `when`(httpsExchange.requestURI).thenReturn(URI("/api/group/$idToGet"))
        `when`(groupDatabase.getGroupById(idToGet)).thenReturn(suspectedResult)

        groupService.processGetGroup(httpsExchange)

        verify(groupService).process(httpsExchange, 201, suspectedResult)
        verify(groupDatabase, only()).getGroupById(idToGet)
    }

    @Test
    fun processEditGroup_thenSuccess() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = GROUP_JSON.toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val idToEdit = 3
        val inObject = getGroup(GROUP_JSON).getOrThrow()
        val suspectedResult = Result.success(Unit)
        `when`(httpsExchange.requestBody).thenReturn(inputStream)
        `when`(httpsExchange.requestURI).thenReturn(URI("/api/group/$idToEdit"))
        `when`(groupDatabase.editGroup(idToEdit, inObject)).thenReturn(suspectedResult)

        groupService.processEditGroup(httpsExchange)

        verify(groupService).process(httpsExchange, 204, suspectedResult)
        verify(groupDatabase, only()).editGroup(idToEdit, inObject)
    }

    @Test
    fun processEditEmptyNameGroup_thenFail() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val byteArray = GROUP_JSON_EMPTY_NAME.toByteArray(Charsets.UTF_8)
        val inputStream = spy(ByteArrayInputStream(byteArray))
        val suspectedResult = NAME_EMPTY_MESSAGE
        `when`(httpsExchange.requestBody).thenReturn(inputStream)

        groupService.processEditGroup(httpsExchange)

        verify(groupService).process(httpsExchange, 400, suspectedResult)
    }

    @Test
    fun processDeleteGroup_thenSuccess() {
        val httpsExchange = mock(HttpsExchange::class.java)
        val headers = mock(Headers::class.java)
        val responseBody = mock(OutputStream::class.java)
        `when`(httpsExchange.responseHeaders).thenReturn(headers)
        `when`(httpsExchange.responseBody).thenReturn(responseBody)
        val idToDelete = 3
        val suspectedResult = Result.success(Unit)
        `when`(httpsExchange.requestURI).thenReturn(URI("/api/product/$idToDelete"))
        `when`(groupDatabase.deleteGroup(idToDelete)).thenReturn(suspectedResult)

        groupService.processDeleteGroup(httpsExchange)

        verify(groupService).process(httpsExchange, 204, suspectedResult)
        verify(groupDatabase, only()).deleteGroup(idToDelete)
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