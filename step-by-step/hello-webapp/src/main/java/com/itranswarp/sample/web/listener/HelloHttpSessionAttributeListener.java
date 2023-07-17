package com.itranswarp.sample.web.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;

@WebListener
public class HelloHttpSessionAttributeListener implements HttpSessionAttributeListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        logger.info(">>> HttpSession attribute added: {} = {}", event.getName(), event.getValue());
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        logger.info(">>> HttpSession attribute removed: {} = {}", event.getName(), event.getValue());
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        logger.info(">>> HttpSession attribute replaced: {} = {}", event.getName(), event.getValue());
    }
}
