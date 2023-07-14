package com.itranswarp.jerrymouse.engine;

import java.util.Enumeration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.itranswarp.jerrymouse.engine.support.Attributes;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

public class HttpSessionImpl implements HttpSession {

    final ServletContextImpl servletContext;
    final Lock lock = new ReentrantLock();

    String sessionId;
    int maxInactiveInterval;
    long creationTime;
    long lastAccessedTime;
    Attributes attributes;

    public HttpSessionImpl(ServletContextImpl servletContext, String sessionId, int interval) {
        this.servletContext = servletContext;
        this.sessionId = sessionId;
        this.creationTime = this.lastAccessedTime = System.currentTimeMillis();
        this.attributes = new Attributes();
        setMaxInactiveInterval(interval);
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
        this.maxInactiveInterval = interval;

    }

    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    @Override
    public void invalidate() {
        lock.lock();
        try {
            checkValid();
            this.servletContext.sessionManager.remove(this);
            this.sessionId = null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isNew() {
        return this.creationTime == this.lastAccessedTime;
    }

    // attribute operations ///////////////////////////////////////////////////

    @Override
    public Object getAttribute(String name) {
        lock.lock();
        try {
            checkValid();
            return this.attributes.getAttribute(name);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        lock.lock();
        try {
            checkValid();
            return this.attributes.getAttributeNames();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setAttribute(String name, Object value) {
        lock.lock();
        try {
            checkValid();
            if (value == null) {
                removeAttribute(name);
            } else {
                this.attributes.setAttribute(name, value);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeAttribute(String name) {
        lock.lock();
        try {
            checkValid();
            this.attributes.removeAttribute(name);
        } finally {
            lock.unlock();
        }
    }

    void setLastAccessTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    void checkValid() {
        if (this.sessionId == null) {
            throw new IllegalStateException("Session is already invalidated.");
        }
    }
}
