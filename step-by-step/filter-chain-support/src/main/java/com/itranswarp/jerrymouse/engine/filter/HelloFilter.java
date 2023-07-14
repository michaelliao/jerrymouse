package com.itranswarp.jerrymouse.engine.filter;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = "/hello")
public class HelloFilter implements Filter {

    final Logger logger = LoggerFactory.getLogger(getClass());

    Set<String> names = Set.of("Bob", "Alice", "Tom", "Jerry");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String name = req.getParameter("name");
        logger.info("Check parameter name = {}", name);
        if (name != null && names.contains(name)) {
            chain.doFilter(request, response);
        } else {
            logger.warn("Access denied: name = {}", name);
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.sendError(403, "Forbidden");
        }
    }
}
