package com.itranswarp.jerrymouse.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletSecurityElement;

public class ServletRegistrationImpl implements ServletRegistration.Dynamic {

    final ServletContext servletContext;
    final String name;
    final Servlet servlet;
    final List<String> urlPatterns = new ArrayList<>(4);

    boolean initialized = false;

    public ServletRegistrationImpl(ServletContext servletContext, String name, Servlet servlet) {
        this.servletContext = servletContext;
        this.name = name;
        this.servlet = servlet;
    }

    public ServletConfig getServletConfig() {
        return new ServletConfig() {
            @Override
            public String getServletName() {
                return ServletRegistrationImpl.this.name;
            }

            @Override
            public ServletContext getServletContext() {
                return ServletRegistrationImpl.this.servletContext;
            }

            @Override
            public String getInitParameter(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return null;
            }
        };
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getClassName() {
        return servlet.getClass().getName();
    }

    @Override
    public Set<String> addMapping(String... urlPatterns) {
        if (urlPatterns == null || urlPatterns.length == 0) {
            throw new IllegalArgumentException("Missing urlPatterns.");
        }
        for (String urlPattern : urlPatterns) {
            this.urlPatterns.add(urlPattern);
        }
        return Set.of();
    }

    @Override
    public Collection<String> getMappings() {
        return this.urlPatterns;
    }

    @Override
    public String getRunAsRole() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        // TODO Auto-generated method stub
    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setRunAsRole(String roleName) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getInitParameter(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getInitParameters() {
        // TODO Auto-generated method stub
        return null;
    }
}
