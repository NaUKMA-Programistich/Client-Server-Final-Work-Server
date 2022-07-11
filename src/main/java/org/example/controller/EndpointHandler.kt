package org.example.controller

import com.sun.net.httpserver.HttpsExchange

data class EndpointHandler(
    val pattern: String,
    val method: String,
    val handler: (HttpsExchange) -> Unit
) {
    fun isMatch(exchange: HttpsExchange): Boolean {
        if (!exchange.requestMethod.equals(method)) return false
        val path: String = exchange.requestURI.path
        return path.matches(pattern.toRegex())
    }

    fun handle(exchange: HttpsExchange) {
        exchange.responseHeaders["Content-Type"] = "application/json"
        handler.invoke(exchange)
    }
}
