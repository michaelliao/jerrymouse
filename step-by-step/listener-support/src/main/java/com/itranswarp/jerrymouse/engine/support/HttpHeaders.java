package com.itranswarp.jerrymouse.engine.support;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

import com.itranswarp.jerrymouse.utils.DateUtils;
import com.sun.net.httpserver.Headers;

public class HttpHeaders {

    final Headers headers;

    public HttpHeaders(Headers headers) {
        this.headers = headers;
    }

    public void addDateHeader(String name, long date) {
        String strDate = DateUtils.formatDateTimeGMT(date);
        addHeader(name, strDate);
    }

    public void addHeader(String name, String value) {
        this.headers.add(name, value);
    }

    public void addIntHeader(String name, int value) {
        addHeader(name, Integer.toString(value));
    }

    public boolean containsHeader(String name) {
        List<String> values = this.headers.get(name);
        return values != null && !values.isEmpty();
    }

    public long getDateHeader(String name) {
        String value = getHeader(name);
        if (value == null) {
            return -1;
        }
        try {
            return DateUtils.parseDateTimeGMT(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Cannot parse date header: " + value);
        }
    }

    public int getIntHeader(String name) {
        String value = getHeader(name);
        return value == null ? -1 : Integer.parseInt(value);
    }

    public String getHeader(String name) {
        List<String> values = this.headers.get(name);
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    public List<String> getHeaders(String name) {
        return this.headers.get(name);
    }

    public Set<String> getHeaderNames() {
        return this.headers.keySet();
    }

    public void setDateHeader(String name, long date) {
        setHeader(name, DateUtils.formatDateTimeGMT(date));
    }

    public void setHeader(String name, String value) {
        this.headers.set(name, value);
    }

    public void setIntHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    public void clearHeaders() {
        this.headers.clear();
    }
}
