package org.example.controller

import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpsExchange
import com.sun.net.httpserver.HttpsServer
import java.net.InetSocketAddress

class Server {
    private val controller = Controller()
    // TODO https://stackoverflow.com/questions/2308479/simple-java-https-server
    fun instance(): HttpsServer {
        val server: HttpsServer = HttpsServer.create()
        server.apply {
            bind(InetSocketAddress(8080), 0)

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
