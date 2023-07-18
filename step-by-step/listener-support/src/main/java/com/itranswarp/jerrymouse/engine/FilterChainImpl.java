package com.itranswarp.jerrymouse.engine;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class FilterChainImpl implements FilterChain {

    final Filter[] filters;
    final Servlet servlet;
    final int total;
    int index = 0;

    public FilterChainImpl(Filter[] filters, Servlet servlet) {
        this.filters = filters;
        this.servlet = servlet;
        this.total = filters.length;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (index < total) {
            int current = index;
            index++;
            filters[current].doFilter(request, response, this);
        } else {
            servlet.service(request, response);
        }
    }
}
