package com.itranswarp.jerrymouse.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class UrlUtils {

    public static Map<String, Pattern> compileUrlPatterns(Set<String> urlPatterns) {
        Map<String, Pattern> map = new HashMap<>();
        for (String urlPattern : urlPatterns) {
            map.put(urlPattern, compileUrlPattern(urlPattern));
        }
        return map;
    }

    public static Pattern compileUrlPattern(String urlPattern) {
        StringBuilder sb = new StringBuilder(urlPattern.length() + 16);
        sb.append('^');
        for (int i = 0; i < urlPattern.length(); i++) {
            char ch = urlPattern.charAt(i);
            if (ch == '*') {
                sb.append(".+");
            } else if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9') {
                sb.append(ch);
            } else {
                sb.append('\\').append(ch);
            }
        }
        sb.append('$');
        return Pattern.compile(sb.toString());
    }
}
