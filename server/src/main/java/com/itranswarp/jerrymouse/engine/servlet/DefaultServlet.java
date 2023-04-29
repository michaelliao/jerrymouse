package com.itranswarp.jerrymouse.engine.servlet;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.jerrymouse.utils.ClassPathUtils;
import com.itranswarp.jerrymouse.utils.DateUtils;
import com.itranswarp.jerrymouse.utils.HtmlUtils;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Default servlet is mapping on "/" and serves as file browsing.
 */
public class DefaultServlet extends HttpServlet {

    final Logger logger = LoggerFactory.getLogger(getClass());
    String indexTemplate;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.indexTemplate = ClassPathUtils.readString("/index.html");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        logger.info("list file or directory: {}", uri);
        if (!uri.startsWith("/")) {
            // insecure uri:
            logger.debug("skip process insecure uri: {}", uri);
            resp.sendError(404, "Not Found");
            return;
        }
        if (uri.equals("/WEB-INF") || uri.startsWith("/WEB-INF/")) {
            // prevent access WEB-INF:
            logger.debug("prevent access uri: {}", uri);
            resp.sendError(403, "Forbidden");
            return;
        }
        if (uri.indexOf("/../") > 0) {
            // prevent access /abc/../../xyz:
            logger.debug("prevent access insecure uri: {}", uri);
            resp.sendError(404, "Not Found");
            return;
        }
        String realPath = req.getServletContext().getRealPath(uri);
        Path path = Paths.get(realPath);
        logger.debug("try access path: {}", path);
        if (uri.endsWith("/")) {
            if (Files.isDirectory(path)) {
                // list dir:
                List<Path> files = Files.list(path).collect(Collectors.toList());
                Collections.sort(files, (f1, f2) -> {
                    var s1 = f1.toString();
                    var s2 = f2.toString();
                    return s1.compareToIgnoreCase(s2);
                });
                StringBuilder sb = new StringBuilder(4096);
                if (!uri.equals("/")) {
                    sb.append(tr(path.getParent(), -1, ".."));
                }

                for (Path file : files) {
                    String name = file.getFileName().toString();
                    long size = -1;
                    if (Files.isDirectory(file)) {
                        name = name + "/";
                    } else if (Files.isRegularFile(file)) {
                        size = Files.size(file);
                    }
                    sb.append(tr(file, size, name));
                }
                String trs = sb.toString();
                String html = this.indexTemplate.replace("${URI}", HtmlUtils.encodeHtml(uri)) //
                        .replace("${SERVER}", getServletContext().getServerInfo()) //
                        .replace("${TRS}", trs);
                PrintWriter pw = resp.getWriter();
                pw.write(html);
                pw.flush();
                return;
            }
        } else if (Files.isReadable(path) && Files.isReadable(path)) {
            logger.debug("read file: {}", path);
            resp.setContentType(getServletContext().getMimeType(uri));
            ServletOutputStream output = resp.getOutputStream();
            try (InputStream input = new BufferedInputStream(new FileInputStream(path.toFile()))) {
                input.transferTo(output);
            }
            output.flush();
            return;
        }
        resp.sendError(404, "Not Found");
        return;
    }

    static String tr(Path file, long size, String name) throws IOException {
        return "<tr><td><a href=\"" + name + "\">" + HtmlUtils.encodeHtml(name) + "</a></td><td>" + size(size) + "</td><td>"
                + DateUtils.formatDateTimeGMT(Files.getLastModifiedTime(file).toMillis()) + "</td>";
    }

    static String size(long size) {
        if (size >= 0) {
            if (size > 1024 * 1024 * 1024) {
                return String.format("%.3f GB", size / (1024 * 1024 * 1024.0));
            }
            if (size > 1024 * 1024) {
                return String.format("%.3f MB", size / (1024 * 1024.0));
            }
            if (size > 1024) {
                return String.format("%.3f KB", size / 1024.0);
            }
            return size + " B";
        }
        return "";
    }

}
