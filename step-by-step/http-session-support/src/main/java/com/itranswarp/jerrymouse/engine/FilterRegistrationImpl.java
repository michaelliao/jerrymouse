package com.itranswarp.jerrymouse.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.itranswarp.jerrymouse.engine.support.InitParameters;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;

public class FilterRegistrationImpl implements FilterRegistration.Dynamic {

    final ServletContext servletContext;
    final String name;
    final Filter filter;

    final InitParameters initParameters = new InitParameters();
    final List<String> urlPatterns = new ArrayList<>(4);
    boolean initialized = false;

    public FilterRegistrationImpl(ServletContext servletContext, String name, Filter filter) {
        this.servletContext = servletContext;
        this.name = name;
        this.filter = filter;
    }

    public FilterConfig getFilterConfig() {
        return new FilterConfig() {
            @Override
            public String getFilterName() {
                return FilterRegistrationImpl.this.name;
            }

            @Override
            public ServletContext getServletContext() {
                return FilterRegistrationImpl.this.servletContext;
            }

            @Override
            public String getInitParameter(String name) {
                return FilterRegistrationImpl.this.initParameters.getInitParameter(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return FilterRegistrationImpl.this.initParameters.getInitParameterNames();
            }
        };
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getClassName() {
        return filter.getClass().getName();
    }

    // proxy to InitParameters:

    @Override
    public boolean setInitParameter(String name, String value) {
        checkNotInitialized("setInitParameter");
        return this.initParameters.setInitParameter(name, value);
    }

    @Override
    public String getInitParameter(String name) {
        return this.initParameters.getInitParameter(name);
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        checkNotInitialized("setInitParameter");
        return this.initParameters.setInitParameters(initParameters);
    }

    @Override
    public Map<String, String> getInitParameters() {
        return this.initParameters.getInitParameters();
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        checkNotInitialized("setInitParameter");
        if (isAsyncSupported) {
            throw new UnsupportedOperationException("Async is not supported.");
        }
    }

    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
        throw new UnsupportedOperationException("addMappingForServletNames");
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
        checkNotInitialized("addMappingForUrlPatterns");
        if (!dispatcherTypes.contains(DispatcherType.REQUEST) || dispatcherTypes.size() != 1) {
            throw new IllegalArgumentException("Only support DispatcherType.REQUEST.");
        }
        if (urlPatterns == null || urlPatterns.length == 0) {
            throw new IllegalArgumentException("Missing urlPatterns.");
        }
        for (String urlPattern : urlPatterns) {
            this.urlPatterns.add(urlPattern);
        }
    }

    @Override
    public Collection<String> getServletNameMappings() {
        return List.of();
    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        return this.urlPatterns;
    }

    private void checkNotInitialized(String name) {
        if (this.initialized) {
            throw new IllegalStateException("Cannot call " + name + " after initialization.");
        }
    }
}
