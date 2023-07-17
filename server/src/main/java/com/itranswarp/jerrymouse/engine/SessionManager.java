package com.itranswarp.jerrymouse.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.jerrymouse.utils.DateUtils;

import jakarta.servlet.http.HttpSession;

public class SessionManager implements Runnable {

    final Logger logger = LoggerFactory.getLogger(getClass());

    final ServletContextImpl servletContext;
    final Map<String, HttpSessionImpl> sessions = new ConcurrentHashMap<>();
    final int inactiveInterval;

    public SessionManager(ServletContextImpl servletContext, int interval) {
        this.servletContext = servletContext;
        this.inactiveInterval = interval;
        Thread t = new Thread(this, "Session-Cleanup-Thread");
        t.setDaemon(true);
        t.start();
    }

    public HttpSession getSession(String sessionId) {
        HttpSessionImpl session = sessions.get(sessionId);
        if (session == null) {
            session = new HttpSessionImpl(this.servletContext, sessionId, inactiveInterval);
            sessions.put(sessionId, session);
            this.servletContext.invokeHttpSessionCreated(session);
        } else {
            session.lastAccessedTime = System.currentTimeMillis();
        }
        return session;
    }

    public void remove(HttpSession session) {
        this.sessions.remove(session.getId());
        this.servletContext.invokeHttpSessionDestroyed(session);
    }

    @Override
    public void run() {
        for (;;) {
            try {
                Thread.sleep(60_000L);
            } catch (InterruptedException e) {
                break;
            }
            long now = System.currentTimeMillis();
            for (String sessionId : sessions.keySet()) {
                HttpSession session = sessions.get(sessionId);
                if (session.getLastAccessedTime() + session.getMaxInactiveInterval() * 1000L < now) {
                    logger.atDebug().log("remove expired session: {}, last access time: {}", sessionId,
                            DateUtils.formatDateTimeGMT(session.getLastAccessedTime()));
                    session.invalidate();
                }
            }
        }
    }
}
