package com.itranswarp.jerrymouse.engine.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = "/")
public class IndexServlet extends HttpServlet {

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
        pw.close();
    }
}
