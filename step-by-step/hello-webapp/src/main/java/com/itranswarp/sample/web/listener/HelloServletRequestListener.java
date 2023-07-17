package com.itranswarp.sample.web.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class HelloServletRequestListener implements ServletRequestListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        logger.info(">>> ServletRequest initialized: {}", sre.getServletRequest());
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        logger.info(">>> ServletRequest destroyed: {}", sre.getServletRequest());
    }
}
