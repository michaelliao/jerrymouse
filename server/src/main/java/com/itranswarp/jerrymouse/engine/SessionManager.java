package com.itranswarp.jerrymouse.engine;

import jakarta.servlet.http.HttpSession;

public interface SessionManager {

    void remove(HttpSession session);
}
