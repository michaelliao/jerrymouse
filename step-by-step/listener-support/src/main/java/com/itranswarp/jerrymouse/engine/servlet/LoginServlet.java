package com.itranswarp.jerrymouse.engine.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    Map<String, String> users = Map.of( // user database
            "bob", "bob123", //
            "alice", "alice123", //
            "root", "admin123" //
    );

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String expectedPassword = users.get(username.toLowerCase());
        if (expectedPassword == null || !expectedPassword.equals(password)) {
            PrintWriter pw = resp.getWriter();
            pw.write("""
                    <h1>Login Failed</h1>
                    <p>Invalid username or password.</p>
                    <p><a href="/">Try again</a></p>
                    """);
            pw.close();
        } else {
            req.getSession().setAttribute("username", username);
            resp.sendRedirect("/");
        }
    }
}
