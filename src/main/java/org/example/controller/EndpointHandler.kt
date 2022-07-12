package org.example.controller

import com.sun.net.httpserver.HttpsExchange
import com.sun.tools.javac.Main
import java.util.logging.Logger

data class EndpointHandler(
    val pattern: String,
    val method: String,
    val handler: (HttpsExchange) -> Unit
) {
    private val log = Logger.getLogger(Main::class.java.name)

    fun isMatch(exchange: HttpsExchange): Boolean {
        if (!exchange.requestMethod.equals(method)) return false
        val path: String = exchange.requestURI.path
        return path.matches(pattern.toRegex())
    }

    fun handle(exchange: HttpsExchange) {
        exchange.responseHeaders["Content-Type"] = "application/json"
        log.info("URI: " + exchange.requestURI)
        handler.invoke(exchange)
    }
}
