package com.itranswarp.sample.web.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class HelloHttpSessionListener implements HttpSessionListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        logger.info(">>> HttpSession created: {}", se.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        logger.info(">>> HttpSession destroyed: {}", se.getSession());
    }
}
