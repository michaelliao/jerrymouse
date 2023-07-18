package com.itranswarp.jerrymouse.engine.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextAttributeEvent;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class HelloServletContextAttributeListener implements ServletContextAttributeListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        logger.info(">>> ServletContext attribute added: {} = {}", event.getName(), event.getValue());
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        logger.info(">>> ServletContext attribute removed: {} = {}", event.getName(), event.getValue());
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        logger.info(">>> ServletContext attribute replaced: {} = {}", event.getName(), event.getValue());
    }
}
