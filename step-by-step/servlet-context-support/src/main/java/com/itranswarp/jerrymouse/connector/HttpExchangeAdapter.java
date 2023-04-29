package com.itranswarp.jerrymouse.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * Adapter to convert HttpExchange to request/response interface.
 */
public class HttpExchangeAdapter implements HttpExchangeRequest, HttpExchangeResponse {

    final HttpExchange exchange;
    byte[] requestBodyData;

    public HttpExchangeAdapter(HttpExchange exchange) {
        this.exchange = exchange;
    }

    /**
     * Copied from super class:
     * 
     * If the response length parameter is greater than {@code zero}, this specifies
     * an exact number of bytes to send and the application must send that exact
     * amount of data. If the response length parameter is {@code zero}, then
     * chunked transfer encoding is used and an arbitrary amount of data may be
     * sent. The application terminates the response body by closing the
     * {@link OutputStream}.
     */
    @Override
    public void sendResponseHeaders(int rCode, long responseLength) throws IOException {
        this.exchange.sendResponseHeaders(rCode, responseLength);
    }

    @Override
    public OutputStream getResponseBody() {
        return this.exchange.getResponseBody();
    }

    @Override
    public String getRequestMethod() {
        return this.exchange.getRequestMethod();
    }

    @Override
    public URI getRequestURI() {
        return this.exchange.getRequestURI();
    }

    @Override
    public Headers getRequestHeaders() {
        return this.exchange.getRequestHeaders();
    }

    @Override
    public Headers getResponseHeaders() {
        return this.exchange.getResponseHeaders();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.exchange.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.exchange.getLocalAddress();
    }

    @Override
    public byte[] getRequestBody() throws IOException {
        if (this.requestBodyData == null) {
            try (InputStream input = this.exchange.getRequestBody()) {
                this.requestBodyData = input.readAllBytes();
            }
        }
        return this.requestBodyData;
    }
}
