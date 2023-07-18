package com.itranswarp.jerrymouse.engine.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class HelloServletContextListener implements ServletContextListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info(">>> ServletContext initialized: {}", sce.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info(">>> ServletContext destroyed: {}", sce.getServletContext());
    }
}
