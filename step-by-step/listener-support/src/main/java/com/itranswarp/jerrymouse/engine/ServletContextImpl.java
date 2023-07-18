package com.itranswarp.jerrymouse.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.jerrymouse.engine.mapping.FilterMapping;
import com.itranswarp.jerrymouse.engine.mapping.ServletMapping;
import com.itranswarp.jerrymouse.engine.support.Attributes;
import com.itranswarp.jerrymouse.utils.AnnoUtils;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextAttributeEvent;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRequestAttributeEvent;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

public class ServletContextImpl implements ServletContext {

    final Logger logger = LoggerFactory.getLogger(getClass());

    final SessionManager sessionManager = new SessionManager(this, 600);
    private Attributes attributes = new Attributes(true);

    final Map<String, ServletRegistrationImpl> servletRegistrations = new HashMap<>();
    final Map<String, FilterRegistrationImpl> filterRegistrations = new HashMap<>();

    final Map<String, Servlet> nameToServlets = new HashMap<>();
    final Map<String, Filter> nameToFilters = new HashMap<>();

    final List<ServletMapping> servletMappings = new ArrayList<>();
    final List<FilterMapping> filterMappings = new ArrayList<>();

    private List<ServletContextListener> servletContextListeners = null;
    private List<ServletContextAttributeListener> servletContextAttributeListeners = null;
    private List<ServletRequestListener> servletRequestListeners = null;
    private List<ServletRequestAttributeListener> servletRequestAttributeListeners = null;
    private List<HttpSessionAttributeListener> httpSessionAttributeListeners = null;
    private List<HttpSessionListener> httpSessionListeners = null;

    public void initFilters(List<Class<?>> filterClasses) {
        for (Class<?> c : filterClasses) {
            WebFilter wf = c.getAnnotation(WebFilter.class);
            if (wf != null) {
                logger.info("auto register @WebFilter: {}", c.getName());
                @SuppressWarnings("unchecked")
                Class<? extends Filter> clazz = (Class<? extends Filter>) c;
                FilterRegistration.Dynamic registration = this.addFilter(AnnoUtils.getFilterName(clazz), clazz);
                registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, AnnoUtils.getFilterUrlPatterns(clazz));
                registration.setInitParameters(AnnoUtils.getFilterInitParams(clazz));
            }
        }

        // init filters:
        for (String name : this.filterRegistrations.keySet()) {
            var registration = this.filterRegistrations.get(name);
            try {
                registration.filter.init(registration.getFilterConfig());
                this.nameToFilters.put(name, registration.filter);
                for (String urlPattern : registration.getUrlPatternMappings()) {
                    this.filterMappings.add(new FilterMapping(urlPattern, registration.filter));
                }
                registration.initialized = true;
            } catch (ServletException e) {
                logger.error("init filter failed: " + name + " / " + registration.filter.getClass().getName(), e);
            }
        }
    }

    public void initServlets(List<Class<?>> servletClasses) {
        for (Class<?> c : servletClasses) {
            WebServlet ws = c.getAnnotation(WebServlet.class);
            if (ws != null) {
                logger.info("auto register @WebServlet: {}", c.getName());
                @SuppressWarnings("unchecked")
                Class<? extends Servlet> clazz = (Class<? extends Servlet>) c;
                ServletRegistration.Dynamic registration = this.addServlet(AnnoUtils.getServletName(clazz), clazz);
                registration.addMapping(AnnoUtils.getServletUrlPatterns(clazz));
                registration.setInitParameters(AnnoUtils.getServletInitParams(clazz));
            }
        }

        // init servlets:
        for (String name : this.servletRegistrations.keySet()) {
            var registration = this.servletRegistrations.get(name);
            try {
                registration.servlet.init(registration.getServletConfig());
                this.nameToServlets.put(name, registration.servlet);
                for (String urlPattern : registration.getMappings()) {
                    this.servletMappings.add(new ServletMapping(urlPattern, registration.servlet));
                }
                registration.initialized = true;
            } catch (ServletException e) {
                logger.error("init servlet failed: " + name + " / " + registration.servlet.getClass().getName(), e);
            }
        }
    }

    public void process(HttpServletRequestImpl request, HttpServletResponseImpl response) throws IOException, ServletException {
        String path = request.getRequestURI();
        // search servlet:
        Servlet servlet = null;
        for (ServletMapping mapping : this.servletMappings) {
            if (mapping.matches(path)) {
                servlet = mapping.servlet;
                break;
            }
        }
        if (servlet == null) {
            // 404 Not Found:
            PrintWriter pw = response.getWriter();
            pw.write("<h1>404 Not Found</h1><p>No mapping for URL: " + path + "</p>");
            pw.flush();
            response.cleanup();
            return;
        }
        // search filter:
        List<Filter> enabledFilters = new ArrayList<>();
        for (FilterMapping mapping : this.filterMappings) {
            if (mapping.matches(path)) {
                enabledFilters.add(mapping.filter);
            }
        }
        Filter[] filters = enabledFilters.toArray(Filter[]::new);
        logger.atDebug().log("process {} by filter {}, servlet {}", path, Arrays.toString(filters), servlet);
        FilterChain chain = new FilterChainImpl(filters, servlet);
        try {
            this.invokeServletRequestInitialized(request);
            chain.doFilter(request, response);
        } catch (ServletException e) {
            logger.error(e.getMessage(), e);
            throw new IOException(e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            this.invokeServletRequestDestroyed(request);
        }
    }

    @Override
    public void addListener(String className) {
        EventListener listener = null;
        try {
            Class<EventListener> clazz = createInstance(className);
            listener = createInstance(clazz);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        addListener(listener);
    }

    @Override
    public void addListener(Class<? extends EventListener> clazz) {
        EventListener listener = null;
        try {
            listener = createInstance(clazz);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        addListener(listener);
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        if (t instanceof ServletContextListener listener) {
            if (this.servletContextListeners == null) {
                this.servletContextListeners = new ArrayList<>();
            }
            this.servletContextListeners.add(listener);
        } else if (t instanceof ServletContextAttributeListener listener) {
            if (this.servletContextAttributeListeners == null) {
                this.servletContextAttributeListeners = new ArrayList<>();
            }
            this.servletContextAttributeListeners.add(listener);
        } else if (t instanceof ServletRequestListener listener) {
            if (this.servletRequestListeners == null) {
                this.servletRequestListeners = new ArrayList<>();
            }
            this.servletRequestListeners.add(listener);
        } else if (t instanceof ServletRequestAttributeListener listener) {
            if (this.servletRequestAttributeListeners == null) {
                this.servletRequestAttributeListeners = new ArrayList<>();
            }
            this.servletRequestAttributeListeners.add(listener);
        } else if (t instanceof HttpSessionAttributeListener listener) {
            if (this.httpSessionAttributeListeners == null) {
                this.httpSessionAttributeListeners = new ArrayList<>();
            }
            this.httpSessionAttributeListeners.add(listener);
        } else if (t instanceof HttpSessionListener listener) {
            if (this.httpSessionListeners == null) {
                this.httpSessionListeners = new ArrayList<>();
            }
            this.httpSessionListeners.add(listener);
        } else {
            throw new IllegalArgumentException("Unsupported listener: " + t.getClass().getName());
        }
    }

    // invoke listeners ///////////////////////////////////////////////////////

    void invokeServletContextAttributeAdded(String name, Object value) {
        logger.info("invoke ServletContextAttributeAdded: {} = {}", name, value);
        if (this.servletContextAttributeListeners != null) {
            var event = new ServletContextAttributeEvent(this, name, value);
            for (var listener : this.servletContextAttributeListeners) {
                listener.attributeAdded(event);
            }
        }
    }

    void invokeServletContextAttributeRemoved(String name, Object value) {
        logger.info("invoke ServletContextAttributeRemoved: {} = {}", name, value);
        if (this.servletContextAttributeListeners != null) {
            var event = new ServletContextAttributeEvent(this, name, value);
            for (var listener : this.servletContextAttributeListeners) {
                listener.attributeRemoved(event);
            }
        }
    }

    void invokeServletContextAttributeReplaced(String name, Object value) {
        logger.info("invoke ServletContextAttributeReplaced: {} = {}", name, value);
        if (this.servletContextAttributeListeners != null) {
            var event = new ServletContextAttributeEvent(this, name, value);
            for (var listener : this.servletContextAttributeListeners) {
                listener.attributeReplaced(event);
            }
        }
    }

    void invokeServletRequestAttributeAdded(HttpServletRequest request, String name, Object value) {
        logger.info("invoke ServletRequestAttributeAdded: {} = {}, request = {}", name, value, request);
        if (this.servletRequestAttributeListeners != null) {
            var event = new ServletRequestAttributeEvent(this, request, name, value);
            for (var listener : this.servletRequestAttributeListeners) {
                listener.attributeAdded(event);
            }
        }
    }

    void invokeServletRequestAttributeRemoved(HttpServletRequest request, String name, Object value) {
        logger.info("invoke ServletRequestAttributeRemoved: {} = {}, request = {}", name, value, request);
        if (this.servletRequestAttributeListeners != null) {
            var event = new ServletRequestAttributeEvent(this, request, name, value);
            for (var listener : this.servletRequestAttributeListeners) {
                listener.attributeRemoved(event);
            }
        }
    }

    void invokeServletRequestAttributeReplaced(HttpServletRequest request, String name, Object value) {
        logger.info("invoke ServletRequestAttributeReplaced: {} = {}, request = {}", name, value, request);
        if (this.servletRequestAttributeListeners != null) {
            var event = new ServletRequestAttributeEvent(this, request, name, value);
            for (var listener : this.servletRequestAttributeListeners) {
                listener.attributeReplaced(event);
            }
        }
    }

    void invokeHttpSessionAttributeAdded(HttpSession session, String name, Object value) {
        logger.info("invoke HttpSessionAttributeAdded: {} = {}, session = {}", name, value, session);
        if (this.httpSessionAttributeListeners != null) {
            var event = new HttpSessionBindingEvent(session, name, value);
            for (var listener : this.httpSessionAttributeListeners) {
                listener.attributeAdded(event);
            }
        }
    }

    void invokeHttpSessionAttributeRemoved(HttpSession session, String name, Object value) {
        logger.info("invoke ServletContextAttributeRemoved: {} = {}, session = {}", name, value, session);
        if (this.httpSessionAttributeListeners != null) {
            var event = new HttpSessionBindingEvent(session, name, value);
            for (var listener : this.httpSessionAttributeListeners) {
                listener.attributeRemoved(event);
            }
        }
    }

    void invokeHttpSessionAttributeReplaced(HttpSession session, String name, Object value) {
        logger.info("invoke ServletContextAttributeReplaced: {} = {}, session = {}", name, value, session);
        if (this.httpSessionAttributeListeners != null) {
            var event = new HttpSessionBindingEvent(session, name, value);
            for (var listener : this.httpSessionAttributeListeners) {
                listener.attributeReplaced(event);
            }
        }
    }

    void invokeServletRequestInitialized(HttpServletRequest request) {
        logger.info("invoke ServletRequestInitialized: request = {}", request);
        if (this.servletRequestListeners != null) {
            var event = new ServletRequestEvent(this, request);
            for (var listener : this.servletRequestListeners) {
                listener.requestInitialized(event);
            }
        }
    }

    void invokeServletRequestDestroyed(HttpServletRequest request) {
        logger.info("invoke ServletRequestDestroyed: request = {}", request);
        if (this.servletRequestListeners != null) {
            var event = new ServletRequestEvent(this, request);
            for (var listener : this.servletRequestListeners) {
                listener.requestDestroyed(event);
            }
        }
    }

    void invokeHttpSessionCreated(HttpSession session) {
        logger.info("invoke HttpSessionCreated: session = {}", session);
        if (this.httpSessionListeners != null) {
            var event = new HttpSessionEvent(session);
            for (var listener : this.httpSessionListeners) {
                listener.sessionCreated(event);
            }
        }
    }

    void invokeHttpSessionDestroyed(HttpSession session) {
        logger.info("invoke HttpSessionDestroyed: session = {}", session);
        if (this.httpSessionListeners != null) {
            var event = new HttpSessionEvent(session);
            for (var listener : this.httpSessionListeners) {
                listener.sessionDestroyed(event);
            }
        }
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributes.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return this.attributes.getAttributeNames();
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (value == null) {
            removeAttribute(name);
        } else {
            Object old = this.attributes.setAttribute(name, value);
            if (old == null) {
                this.invokeServletContextAttributeAdded(name, value);
            } else {
                this.invokeServletContextAttributeReplaced(name, value);
            }
        }
    }

    @Override
    public void removeAttribute(String name) {
        Object old = this.attributes.removeAttribute(name);
        this.invokeServletContextAttributeRemoved(name, old);
    }

    @Override
    public String getContextPath() {
        // only support root context path:
        return "";
    }

    @Override
    public ServletContext getContext(String uripath) {
        if ("".equals(uripath)) {
            return this;
        }
        // all others are not exist:
        return null;
    }

    @Override
    public String getMimeType(String file) {
        String defaultMime = "application/octet-stream";
        Map<String, String> mimes = Map.of(".html", "text/html", ".txt", "text/plain", ".png", "image/png", ".jpg", "image/jpeg");
        int n = file.lastIndexOf('.');
        if (n == -1) {
            return defaultMime;
        }
        String ext = file.substring(n);
        return mimes.getOrDefault(ext, defaultMime);
    }

    @Override
    public String getInitParameter(String name) {
        // no init parameters:
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        // no init parameters:
        return Collections.emptyEnumeration();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        throw new UnsupportedOperationException("setInitParameter");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String name, String className) {
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("class name is null or empty.");
        }
        Servlet servlet = null;
        try {
            Class<? extends Servlet> clazz = createInstance(className);
            servlet = createInstance(clazz);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        return addServlet(name, servlet);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String name, Class<? extends Servlet> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("class is null.");
        }
        Servlet servlet = null;
        try {
            servlet = createInstance(clazz);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        return addServlet(name, servlet);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String name, Servlet servlet) {
        if (name == null) {
            throw new IllegalArgumentException("name is null.");
        }
        if (servlet == null) {
            throw new IllegalArgumentException("servlet is null.");
        }
        var registration = new ServletRegistrationImpl(this, name, servlet);
        this.servletRegistrations.put(name, registration);
        return registration;
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        return createInstance(clazz);
    }

    @Override
    public ServletRegistration getServletRegistration(String name) {
        return this.servletRegistrations.get(name);
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return Map.copyOf(this.servletRegistrations);
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String name, String className) {
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("class name is null or empty.");
        }
        Filter filter = null;
        try {
            Class<? extends Filter> clazz = createInstance(className);
            filter = createInstance(clazz);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        return addFilter(name, filter);
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String name, Class<? extends Filter> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("class is null.");
        }
        Filter filter = null;
        try {
            filter = createInstance(clazz);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        return addFilter(name, filter);
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String name, Filter filter) {
        if (name == null) {
            throw new IllegalArgumentException("name is null.");
        }
        if (filter == null) {
            throw new IllegalArgumentException("filter is null.");
        }
        var registration = new FilterRegistrationImpl(this, name, filter);
        this.filterRegistrations.put(name, registration);
        return registration;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        return createInstance(clazz);
    }

    @Override
    public FilterRegistration getFilterRegistration(String name) {
        return this.filterRegistrations.get(name);
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return Map.copyOf(this.filterRegistrations);
    }

    // Servlet API version: 6.0.0

    @Override
    public int getMajorVersion() {
        return 6;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 6;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(String className) throws ServletException {
        Class<T> clazz;
        try {
            clazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found.", e);
        }
        return createInstance(clazz);
    }

    private <T> T createInstance(Class<T> clazz) throws ServletException {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ServletException("Cannot instantiate class " + clazz.getName(), e);
        }
    }

    @Override
    public int getSessionTimeout() {
        return this.sessionManager.inactiveInterval;
    }

    // TODO ///////////////////////////////////////////////////////////////////

    @Override
    public Set<String> getResourcePaths(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void log(String msg) {
        // TODO Auto-generated method stub
    }

    @Override
    public void log(String message, Throwable throwable) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getRealPath(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getServerInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getServletContextName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        // TODO Auto-generated method stub
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void declareRoles(String... roleNames) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getVirtualServerName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSessionTimeout(int sessionTimeout) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getRequestCharacterEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setRequestCharacterEncoding(String encoding) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getResponseCharacterEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setResponseCharacterEncoding(String encoding) {
        // TODO Auto-generated method stub
    }

    @Override
    public jakarta.servlet.ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
        // TODO Auto-generated method stub
        return null;
    }
}
