package com.itranswarp.sample.web;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/hello", initParams = @WebInitParam(name = "prefix", value = "Hello"))
public class HelloServlet extends HttpServlet {

    String prefix;

    @Override
    public void init() throws ServletException {
        this.prefix = getServletConfig().getInitParameter("prefix");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("name", "World");
        String name = req.getParameter("name");
        if (name == null) {
            name = "World";
        }
        req.setAttribute("name", name);
        PrintWriter pw = resp.getWriter();
        pw.write("<h1>");
        pw.write(prefix);
        pw.write(", ");
        pw.write(name);
        pw.write("!</h1>");
        pw.flush();
        req.setAttribute("name", null);
    }
}
