package com.itranswarp.jerrymouse.engine.mapping;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class AbstractMappingTest {

    @Test
    void testMatchAll() {
        var m = new AbstractMapping("/*");
        assertTrue(m.matches("/"));
        assertTrue(m.matches("/a"));
        assertTrue(m.matches("/abc/"));
        assertTrue(m.matches("/abc/x.y.z"));
        assertTrue(m.matches("/a-b-c"));
        assertTrue(m.matches("/a/b/c.php"));
    }

    @Test
    void testMatchPrefix() {
        var m = new AbstractMapping("/hello/*");
        assertTrue(m.matches("/hello/"));
        assertTrue(m.matches("/hello/1"));
        assertTrue(m.matches("/hello/a%20c"));
        assertTrue(m.matches("/hello/world/"));
        assertTrue(m.matches("/hello/world/123"));

        assertFalse(m.matches("/hello"));
        assertFalse(m.matches("/Hello/"));
        assertFalse(m.matches("/Hello"));
    }

    @Test
    void testMatchSuffix() {
        var m = new AbstractMapping("*.php");
        assertTrue(m.matches("/hello.php"));
        assertTrue(m.matches("/hello/.php"));
        assertTrue(m.matches("/hello/%25.php"));
        assertTrue(m.matches("/hello/world/123.php"));

        assertFalse(m.matches("/hello-php"));
        assertFalse(m.matches("/hello.php1"));
        assertFalse(m.matches("/php"));
    }

    @Test
    void testSort() {
        var p1 = new AbstractMapping("/hello/world/*");
        var p2 = new AbstractMapping("/hello/*");
        var p3 = new AbstractMapping("/world/*");
        var p4 = new AbstractMapping("*.asp");
        var p5 = new AbstractMapping("*.php");
        var p6 = new AbstractMapping("/");
        AbstractMapping[] arr = new AbstractMapping[] { p6, p5, p4, p3, p2, p1 };
        Arrays.sort(arr);
        assertSame(p1, arr[0]);
        assertSame(p2, arr[1]);
        assertSame(p3, arr[2]);
        assertSame(p4, arr[3]);
        assertSame(p5, arr[4]);
        assertSame(p6, arr[5]);
    }
}
