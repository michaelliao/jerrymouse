package com.itranswarp.jerrymouse.utils;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import com.sun.net.httpserver.Headers;

import jakarta.servlet.http.Cookie;

public class HttpUtils {

    static final Pattern QUERY_SPLIT = Pattern.compile("\\&");

    public static final Locale DEFAULT_LOCALE = Locale.getDefault();

    public static final List<Locale> DEFAULT_LOCALES = List.of(DEFAULT_LOCALE);

    public static List<Locale> parseLocales(String acceptLanguage) {
        // try parse Accept-Language: zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7
        String[] ss = acceptLanguage.split(",");
        List<Locale> locales = new ArrayList<>(ss.length);
        for (String s : ss) {
            int n = s.indexOf(';');
            String name = n < 0 ? s : s.substring(0, n);
            int m = name.indexOf('-');
            if (m < 0) {
                locales.add(Locale.of(name));
            } else {
                locales.add(Locale.of(name.substring(0, m), name.substring(m + 1)));
            }
        }
        return locales.isEmpty() ? DEFAULT_LOCALES : locales;
    }

    /**
     * Parse query string.
     */
    public static Map<String, List<String>> parseQuery(String query, Charset charset) {
        if (query == null || query.isEmpty()) {
            return Map.of();
        }
        String[] ss = QUERY_SPLIT.split(query);
        Map<String, List<String>> map = new HashMap<>();
        for (String s : ss) {
            int n = s.indexOf('=');
            if (n >= 1) {
                String key = s.substring(0, n);
                String value = s.substring(n + 1);
                List<String> exist = map.get(key);
                if (exist == null) {
                    exist = new ArrayList<>(4);
                    map.put(key, exist);
                }
                exist.add(URLDecoder.decode(value, charset));
            }
        }
        return map;
    }

    public static Map<String, List<String>> parseQuery(String query) {
        return parseQuery(query, StandardCharsets.UTF_8);
    }

    public static String getHeader(Headers headers, String name) {
        List<String> values = headers.get(name);
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    public static Cookie[] parseCookies(String cookieValue) {
        if (cookieValue == null) {
            return null;
        }
        cookieValue = cookieValue.strip();
        if (cookieValue.isEmpty()) {
            return null;
        }
        String[] ss = cookieValue.split(";");
        Cookie[] cookies = new Cookie[ss.length];
        for (int i = 0; i < ss.length; i++) {
            String s = ss[i].strip();
            int pos = s.indexOf('=');
            String name = s;
            String value = "";
            if (pos >= 0) {
                name = s.substring(0, pos);
                value = s.substring(pos + 1);
            }
            cookies[i] = new Cookie(name, value);
        }
        return cookies;
    }
}
