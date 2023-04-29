package com.itranswarp.jerrymouse.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.Test;

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
}
