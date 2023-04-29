package com.itranswarp.jerrymouse.engine;

import java.util.Enumeration;

import com.itranswarp.jerrymouse.engine.support.Attributes;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

public class HttpSessionImpl implements HttpSession {

    final ServletContext servletContext;
    final String sessionId;
    long creationTime;
    long lastAccessedTime;
    Attributes attributes = new Attributes();

    public HttpSessionImpl(ServletContext servletContext, String sessionId) {
        this.servletContext = servletContext;
        this.sessionId = sessionId;
        this.creationTime = this.lastAccessedTime = System.currentTimeMillis();
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getId() {
        return this.sessionId;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getMaxInactiveInterval() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void invalidate() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isNew() {
        return this.creationTime == this.lastAccessedTime;
    }

    // attribute operations ///////////////////////////////////////////////////

    @Override
    public Object getAttribute(String name) {
        return this.attributes.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return this.attributes.getAttributeNames();
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (value == null) {
            removeAttribute(name);
        } else {
            this.attributes.setAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        this.attributes.removeAttribute(name);
    }
}
