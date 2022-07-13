package org.example.service

import com.sun.net.httpserver.HttpsExchange
import org.example.utils.Extensions
import java.io.IOException

interface IService {
    fun process(exchange: HttpsExchange, code: Int, content: Result<Any>) {
        try {
            val data = if (content.isSuccess) Extensions.mapper.writeValueAsBytes(content.getOrThrow())
            else Extensions.mapper.writeValueAsBytes(content.exceptionOrNull()?.message ?: "")

            val codeResult = if (content.isSuccess) code else if (code > 400) code else 400

            exchange.responseHeaders.add("Access-Control-Allow-Origin", "*");
            exchange.responseHeaders.add("Access-Control-Allow-Headers", "Content-type");
            exchange.responseHeaders.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE")
            exchange.sendResponseHeaders(codeResult, data.size.toLong())
            exchange.responseBody.write(data)
            exchange.responseBody.close()
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    fun process(exchange: HttpsExchange, code: Int, content: String) {
        try {
            exchange.responseHeaders.add("Access-Control-Allow-Origin", "*");
            exchange.responseHeaders.add("Access-Control-Allow-Headers", "Content-type");
            exchange.responseHeaders.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE, PUT")
            val data = Extensions.mapper.writeValueAsBytes(content)
            exchange.sendResponseHeaders(code, data.size.toLong())
            exchange.responseBody.write(data)
            exchange.responseBody.close()
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    fun validation(message: String?, good: () -> Unit, error: (String) -> Unit) {
        if (message == null) good() else error(message)
    }
}
