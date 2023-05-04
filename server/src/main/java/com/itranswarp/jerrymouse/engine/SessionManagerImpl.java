package com.itranswarp.jerrymouse.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;

public class SessionManagerImpl implements SessionManager, Runnable {

    final Logger logger = LoggerFactory.getLogger(getClass());
    final Map<String, HttpSessionImpl> sessions = new ConcurrentHashMap<>();
    final int inactiveInterval;
    final String sessionCookieName;

    public SessionManagerImpl(String sessionCookieName, int interval) {
        this.sessionCookieName = sessionCookieName;
        this.inactiveInterval = interval;
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public HttpSession getSession(ServletContextImpl ctx, String sessionId) {
        HttpSessionImpl session = sessions.get(sessionId);
        if (session == null) {
            session = new HttpSessionImpl(ctx, sessionId, inactiveInterval);
            sessions.put(sessionId, session);
        }
        return session;
    }

    @Override
    public void remove(HttpSession session) {
        this.sessions.remove(session.getId());
    }

    @Override
    public void run() {
        for (;;) {
            try {
                Thread.sleep(60_000L);
            } catch (InterruptedException e) {
                break;
            }
            long now = System.currentTimeMillis() + 60_000L;
            for (String sessionId : sessions.keySet()) {
                HttpSession session = sessions.get(sessionId);
                if (now + session.getMaxInactiveInterval() * 1000L > session.getLastAccessedTime()) {
                    logger.atDebug().log("remove expired session: {}", sessionId);
                    sessions.remove(sessionId);
                }
            }
        }
    }
}
