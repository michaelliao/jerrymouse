package com.itranswarp.jerrymouse.engine;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Locale;

import com.itranswarp.jerrymouse.connector.HttpExchangeResponse;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class HttpServletResponseImpl implements HttpServletResponse {

    final HttpExchangeResponse exchangeResponse;

    int status = 200;
    String contentType;

    public HttpServletResponseImpl(HttpExchangeResponse exchangeResponse) {
        this.exchangeResponse = exchangeResponse;
        this.setContentType("text/html");
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        this.exchangeResponse.sendResponseHeaders(status, 0);
        return new PrintWriter(this.exchangeResponse.getResponseBody(), true, StandardCharsets.UTF_8);
    }

    @Override
    public void setContentType(String type) {
        setHeader("Content-Type", type);
        this.contentType = type;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public void setHeader(String name, String value) {
        this.exchangeResponse.getResponseHeaders().set(name, value);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        this.status = sc;
        PrintWriter pw = getWriter();
        pw.write(String.format("<h1>%d %s</h1>", sc, msg));
        pw.close();
    }

    @Override
    public void sendError(int sc) throws IOException {
        sendError(sc, "Error");
    }

    // not implemented yet:

    @Override
    public String getCharacterEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setContentLength(int len) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setContentLengthLong(long len) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setBufferSize(int size) {
        // TODO Auto-generated method stub
    }

    @Override
    public int getBufferSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    public void resetBuffer() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isCommitted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    }

    @Override
    public void setLocale(Locale loc) {
        // TODO Auto-generated method stub
    }

    @Override
    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addCookie(Cookie cookie) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean containsHeader(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String encodeURL(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setDateHeader(String name, long date) {
        // TODO Auto-generated method stub
    }

    @Override
    public void addDateHeader(String name, long date) {
        // TODO Auto-generated method stub
    }

    @Override
    public void addHeader(String name, String value) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setIntHeader(String name, int value) {
        // TODO Auto-generated method stub
    }

    @Override
    public void addIntHeader(String name, int value) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setStatus(int sc) {
        // TODO Auto-generated method stub
    }

    @Override
    public int getStatus() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getHeader(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        // TODO Auto-generated method stub
        return null;
    }
}
