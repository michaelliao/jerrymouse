package com.itranswarp.sample.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    Map<String, String> users = Map.of( // user database
            "bob", "bob123", //
            "alice", "alice123", //
            "root", "admin123" //
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");
        String html;
        if (username == null) {
            html = """
                    <h1>Index Page</h1>
                    <form method="post" action="/login">
                        <legend>Please Login</legend>
                        <p>User Name: <input type="text" name="username"></p>
                        <p>Password: <input type="password" name="password"></p>
                        <p><button type="submit">Login</button></p>
                    </form>
                    """;
        } else {
            html = """
                    <h1>Index Page</h1>
                    <p>Welcome, {username}!</p>
                    <p><a href="/logout">Logout</a></p>
                    """.replace("{username}", username);
        }
        resp.setContentType("text/html");
        PrintWriter pw = resp.getWriter();
        pw.write(html);
        pw.flush();
    }

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
                    <p><a href="/login">Try again</a></p>
                    """);
            pw.close();
        } else {
            req.getSession().setAttribute("username", username);
            resp.sendRedirect("/login");
        }
    }
}
