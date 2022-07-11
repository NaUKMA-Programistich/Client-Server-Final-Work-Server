package org.example.controller

import com.sun.net.httpserver.*
import java.io.FileInputStream
import java.net.InetSocketAddress
import java.security.KeyStore
import javax.net.ssl.*

class Server {
    private val controller = Controller()
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
                if (handler == null) controller.processUnknown(exchange as HttpsExchange)
                else handler.handle(exchange as HttpsExchange)
            }
            executor = null
        }
        return server
    }

    private val handlers = listOf(
        EndpointHandler("/api/group/?", "GET") { controller.processUnknown(it) }, // get all group
        EndpointHandler("/api/group/?", "PUT") { controller.processUnknown(it) }, // add group
        EndpointHandler("/api/group/(\\d+)", "DELETE") { controller.processUnknown(it) }, // delete group by id
        EndpointHandler("/api/group/(\\d+)", "PUT") { controller.processUnknown(it) }, // update group by id

        EndpointHandler("/api/product/?", "GET") { controller.processUnknown(it) }, // get all product
        EndpointHandler("/api/product/?", "PUT") { controller.processUnknown(it) }, // add product count
        EndpointHandler("/api/product/add/?", "PUT") { controller.processUnknown(it) }, // remove product count
        EndpointHandler("/api/product/remove/?", "PUT") { controller.processUnknown(it) }, // add product
        EndpointHandler("/api/product/(\\d+)", "DELETE") { controller.processUnknown(it) }, // delete product by id
        EndpointHandler("/api/product/(\\d+)", "PUT") { controller.processUnknown(it) }, // update product by id

        EndpointHandler("/api/search/?", "GET") { controller.processUnknown(it) }, // search by product filter
        EndpointHandler("/api/stats/?", "GET") { controller.processUnknown(it) }, // search by product filter
    )
}
