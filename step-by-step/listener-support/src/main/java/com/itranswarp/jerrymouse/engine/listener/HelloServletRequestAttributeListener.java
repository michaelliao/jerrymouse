package com.itranswarp.jerrymouse.engine.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletRequestAttributeEvent;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class HelloServletRequestAttributeListener implements ServletRequestAttributeListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void attributeAdded(ServletRequestAttributeEvent srae) {
        logger.info(">>> ServletRequest attribute added: {} = {}", srae.getName(), srae.getValue());
    }

    @Override
    public void attributeRemoved(ServletRequestAttributeEvent srae) {
        logger.info(">>> ServletRequest attribute removed: {} = {}", srae.getName(), srae.getValue());
    }

    @Override
    public void attributeReplaced(ServletRequestAttributeEvent srae) {
        logger.info(">>> ServletRequest attribute replaced: {} = {}", srae.getName(), srae.getValue());
    }
}
