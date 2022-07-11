package org.example.controller

import com.sun.net.httpserver.*
import org.example.db.Database
import org.example.service.GroupService
import org.example.service.ProductService
import org.example.service.UtilsService
import java.io.FileInputStream
import java.net.InetSocketAddress
import java.security.KeyStore
import javax.net.ssl.*

class Server {
    private val database = Database("client", "root", "root")
    private val groupService = GroupService(database)
    private val productService = ProductService(database)
    private val utilsService = UtilsService(database)

    fun instance(): HttpsServer {
        val server: HttpsServer = HttpsServer.create()
        server.apply {
            bind(InetSocketAddress(8080), 0)

            val password = "naukma".toCharArray()
            val key = KeyStore.getInstance("JKS")
            key.load(FileInputStream("testkey.jks"), password)

            val keysManagerFactory = KeyManagerFactory.getInstance("SunX509")
            keysManagerFactory.init(key, password)
            val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
            trustManagerFactory.init(key)

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(keysManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)
            server.httpsConfigurator = object : HttpsConfigurator(sslContext) {
                override fun configure(params: HttpsParameters) {
                    val context = getSSLContext()
                    val engine = context.createSSLEngine()
                    params.needClientAuth = false
                    params.cipherSuites = engine.enabledCipherSuites
                    params.protocols = engine.enabledProtocols
                    val sslParameters = context.supportedSSLParameters
                    params.setSSLParameters(sslParameters)
                }
            }

            val context = createContext("/")
            context.handler = HttpHandler { exchange ->
                val handler = handlers.firstOrNull { it.isMatch(exchange as HttpsExchange) }
                if (handler == null) utilsService.processUnknown(exchange as HttpsExchange)
                else handler.handle(exchange as HttpsExchange)
            }
            executor = null
        }
        return server
    }

    private val handlers = listOf(
        EndpointHandler("/api/group/?", "GET") { groupService.processGetAllGroup(it) }, // get all group
        EndpointHandler("/api/group/?", "POST") { groupService.processCreateGroup(it) }, // add group
        EndpointHandler("/api/group/(\\d+)", "DELETE") { groupService.processDeleteGroup(it) }, // delete group by id
        EndpointHandler("/api/group/(\\d+)", "PUT") { groupService.processEditGroup(it) }, // update group by id

        EndpointHandler("/api/product/?", "GET") { productService.processGetAllProduct(it) }, // get all product
        EndpointHandler("/api/product/?", "POST") { productService.processCreateProduct(it) }, // add product count
        EndpointHandler("/api/product/(\\d+)", "DELETE") { productService.processDeleteProduct(it) }, // delete product by id
        EndpointHandler("/api/product/(\\d+)", "PUT") { productService.processEditProduct(it) }, // update product by id
        EndpointHandler("/api/add/product/?", "POST") { productService.processAddProduct(it) }, // remove product count
        EndpointHandler("/api/remove/product/?", "POST") { productService.processRemoveProduct(it) }, // add product

        EndpointHandler("/api/search/?", "GET") { utilsService.processSearch(it) }, // search by product filter
        EndpointHandler("/api/stats/?", "GET") { utilsService.processUnknown(it) }, // search by product filter
    )
}
