package com.itranswarp.jerrymouse.classloader;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WebAppClassLoaderTest {

    WebAppClassLoader cl;

    @BeforeEach
    void setUp() throws Exception {
        Path classPath = Path.of("src", "test", "resources", "test-classpath", "WEB-INF", "classes");
        Path libPath = Path.of("src", "test", "resources", "test-classpath", "WEB-INF", "lib");
        this.cl = new WebAppClassLoader(classPath, libPath);
    }

    @Test
    void testUrls() {
        URL[] urls = this.cl.getURLs();
        System.out.println(cl);
        Arrays.stream(urls).forEach(System.out::println);
        assertEquals(3, urls.length);
        assertTrue(urls[0].toString().endsWith("/test-classpath/WEB-INF/classes/"));
        assertTrue(urls[1].toString().endsWith("/test-classpath/WEB-INF/lib/commons-io-2.11.0.jar"));
        assertTrue(urls[2].toString().endsWith("/test-classpath/WEB-INF/lib/commons-lang3-3.12.0.jar"));
    }

    @Test
    void testLoadFromClasses() throws Exception {
        String cacheName = "com.itranswarp.sample.webapp.Cache";
        Class<?> cacheClass = cl.loadClass(cacheName);
        assertEquals(cacheName, cacheClass.getName());
        assertSame(cl, cacheClass.getClassLoader());
        @SuppressWarnings("unchecked")
        Map<String, String> instance = (Map<String, String>) cacheClass.getConstructor().newInstance();
        assertEquals(cacheName, instance.getClass().getName());
        instance.put("A", "a");

        String faviconName = "com.itranswarp.sample.webapp.FaviconServlet";
        Class<?> faviconClass = cl.loadClass(faviconName);
        assertEquals(faviconName, faviconClass.getName());
        assertSame(cl, faviconClass.getClassLoader());

        Class<?> servletClass = Class.forName("jakarta.servlet.http.HttpServlet");
        assertNotSame(cl, servletClass.getClassLoader());
    }

    @Test
    void testLoadFromClassesNotFound() throws Exception {
        String notFound = "com.itranswarp.sample.webapp.NotFoundServlet";
        assertThrows(ClassNotFoundException.class, () -> {
            cl.loadClass(notFound);
        });
    }

    @Test
    void testLoadFromJar() throws Exception {
        String mutableIntName = "org.apache.commons.lang3.mutable.MutableInt";
        Class<?> mutableIntClass = cl.loadClass(mutableIntName);
        assertEquals(mutableIntName, mutableIntClass.getName());
        assertSame(cl, mutableIntClass.getClassLoader());
        // instantiate:
        Number mutableInt = (Number) mutableIntClass.getConstructor(int.class).newInstance(12345);
        assertSame(mutableIntClass, mutableInt.getClass());
        assertEquals(12345, mutableInt.intValue());
    }

    @Test
    void testLoadFromJarNotFound() throws Exception {
        String notFound = "org.apache.commons.lang3.mutable.MutableBigInt";
        assertThrows(ClassNotFoundException.class, () -> {
            cl.loadClass(notFound);
        });
    }

    @Test
    void testScanClassesDir() throws Exception {
        List<String> resources = new ArrayList<>();
        Consumer<Resource> handler = (r) -> {
            resources.add(r.name());
            System.out.println("--> " + r);
        };
        cl.scanClassPath(handler);
        assertTrue(resources.contains("com/itranswarp/sample/webapp/Cache.class"));
        assertTrue(resources.contains("com/itranswarp/sample/webapp/DispatcherServlet.class"));
        assertTrue(resources.contains("com/itranswarp/sample/webapp/FaviconServlet.class"));
        assertTrue(resources.contains("com/itranswarp/sample/webapp/LogFilter.class"));
    }

    @Test
    void testScanJarLibs() throws Exception {
        List<String> resources = new ArrayList<>();
        Consumer<Resource> handler = (r) -> {
            resources.add(r.name());
            System.out.println("--> " + r);
        };
        cl.scanJar(handler);
        assertTrue(resources.contains("org/apache/commons/io/Charsets.class"));
        assertTrue(resources.contains("org/apache/commons/io/CopyUtils.class"));
        assertTrue(resources.contains("org/apache/commons/io/DirectoryWalker$CancelException.class"));
        assertTrue(resources.contains("org/apache/commons/io/comparator/SizeFileComparator.class"));
        assertTrue(resources.contains("org/apache/commons/io/file/Counters$PathCounters.class"));
        assertTrue(resources.contains("org/apache/commons/io/filefilter/AbstractFileFilter.class"));
        assertTrue(resources.contains("org/apache/commons/io/function/IOConsumer.class"));
        assertTrue(resources.contains("org/apache/commons/io/input/CountingInputStream.class"));
        assertTrue(resources.contains("org/apache/commons/io/monitor/FileEntry.class"));
        assertTrue(resources.contains("org/apache/commons/io/serialization/ClassNameMatcher.class"));

        assertTrue(resources.contains("org/apache/commons/lang3/AnnotationUtils.class"));
        assertTrue(resources.contains("org/apache/commons/lang3/Functions$FailableBiConsumer.class"));
        assertTrue(resources.contains("org/apache/commons/lang3/JavaVersion.class"));
        assertTrue(resources.contains("org/apache/commons/lang3/ThreadUtils$1.class"));
        assertTrue(resources.contains("org/apache/commons/lang3/ThreadUtils.class"));
        assertTrue(resources.contains("org/apache/commons/lang3/builder/Builder.class"));
        assertTrue(resources.contains("org/apache/commons/lang3/builder/DiffBuilder$1.class"));
        assertTrue(resources.contains("org/apache/commons/lang3/concurrent/AbstractCircuitBreaker$1.class"));
        assertTrue(resources.contains("org/apache/commons/lang3/concurrent/locks/LockingVisitors$LockVisitor.class"));
        assertTrue(resources.contains("org/apache/commons/lang3/function/Failable.class"));
        assertTrue(resources.contains("org/apache/commons/lang3/math/IEEE754rUtils.class"));
        assertTrue(resources.contains("org/apache/commons/lang3/tuple/Triple.class"));
    }
}
