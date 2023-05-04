package com.itranswarp.jerrymouse.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.Cookie;

public class HttpUtilsTest {

    @Test
    void testParseQuery() {
        var map = HttpUtils.parseQuery("a=a+b&b=hello%26world&cc=hello&cc=你好&dd=");
        assertArrayEquals(new Object[] { "a b" }, map.get("a").toArray());
        assertArrayEquals(new Object[] { "hello&world" }, map.get("b").toArray());
        assertArrayEquals(new Object[] { "hello", "你好" }, map.get("cc").toArray());
        assertArrayEquals(new Object[] { "" }, map.get("dd").toArray());
        assertNull(map.get("ee"));
    }

    @Test
    void testParseLocales() {
        String acceptLanguage = "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7";
        var list = HttpUtils.parseLocales(acceptLanguage);
        assertEquals(4, list.size());
        assertEquals(Locale.of("zh", "CN"), list.get(0));
        assertEquals(Locale.of("zh"), list.get(1));
        assertEquals(Locale.of("en", "US"), list.get(2));
        assertEquals(Locale.of("en"), list.get(3));
    }

    @Test
    void testParseCookies() {
        String cookieValue = "_locale_=zh-CN; __gads=ID=9cea9a:T=538083:RT=650183:S=AYcFxG; _session_=d2VpE3ODA1cyOjE41ODh; log=; Hm_lvt_fd4ab4=813452,823531,925127,620704";
        Cookie[] cookies = HttpUtils.parseCookies(cookieValue);
        assertEquals(5, cookies.length);
        assertEquals("_locale_", cookies[0].getName());
        assertEquals("zh-CN", cookies[0].getValue());

        assertEquals("__gads", cookies[1].getName());
        assertEquals("ID=9cea9a:T=538083:RT=650183:S=AYcFxG", cookies[1].getValue());

        assertEquals("_session_", cookies[2].getName());
        assertEquals("d2VpE3ODA1cyOjE41ODh", cookies[2].getValue());

        assertEquals("log", cookies[3].getName());
        assertEquals("", cookies[3].getValue());

        assertEquals("Hm_lvt_fd4ab4", cookies[4].getName());
        assertEquals("813452,823531,925127,620704", cookies[4].getValue());

        assertNull(HttpUtils.parseCookies(null));
        assertNull(HttpUtils.parseCookies(""));
    }
}
