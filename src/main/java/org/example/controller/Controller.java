package org.example.controller;

import com.sun.net.httpserver.HttpsExchange;
import org.example.utils.Extensions;

import java.io.IOException;

public class Controller {
    public void processUnknown(HttpsExchange exchange) {
        process(exchange, 404, "Unknown request");
    }

    private void process(HttpsExchange exchange, int code, Object content) {
        try {
            byte[] data = Extensions.INSTANCE.getMapper().writeValueAsBytes(content);
            exchange.sendResponseHeaders(code, data.length);
            exchange.getResponseBody().write(data);
            exchange.getResponseBody().close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
