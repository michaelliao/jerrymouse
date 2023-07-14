package com.itranswarp.jerrymouse.engine.support;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InitParameters extends LazyMap<String> {

    public InitParameters() {
        super(false);
    }

    /**
     * Sets the initialization parameter with the given name and value on the
     * Servlet or Filter that is represented by this Registration.
     *
     * @param name  the initialization parameter name
     * @param value the initialization parameter value
     *
     * @return true if the update was successful, i.e., an initialization parameter
     *         with the given name did not already exist for the Servlet or Filter
     *         represented by this Registration, and false otherwise
     *
     * @throws IllegalArgumentException if the given name or value is <tt>null</tt>
     */
    public boolean setInitParameter(String name, String value) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is null or empty.");
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value is null or empty.");
        }
        if (super.containsKey(name)) {
            return false;
        }
        super.put(name, value);
        return true;
    }

    public String getInitParameter(String name) {
        return super.get(name);
    }

    /**
     * Sets the given initialization parameters on the Servlet or Filter that is
     * represented by this Registration.
     *
     * <p>
     * The given map of initialization parameters is processed <i>by-value</i>,
     * i.e., for each initialization parameter contained in the map, this method
     * calls {@link #setInitParameter(String,String)}. If that method would return
     * false for any of the initialization parameters in the given map, no updates
     * will be performed, and false will be returned. Likewise, if the map contains
     * an initialization parameter with a <tt>null</tt> name or value, no updates
     * will be performed, and an IllegalArgumentException will be thrown.
     *
     * <p>
     * The returned set is not backed by the {@code Registration} object, so changes
     * in the returned set are not reflected in the {@code Registration} object, and
     * vice-versa.
     * </p>
     *
     * @param initParameters the initialization parameters
     *
     * @return the (possibly empty) Set of initialization parameter names that are
     *         in conflict
     * 
     * @throws IllegalArgumentException if the given map contains an initialization
     *                                  parameter with a <tt>null</tt> name or value
     */
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        if (initParameters == null) {
            throw new IllegalArgumentException("initParameters is null.");
        }
        if (initParameters.isEmpty()) {
            return Set.of();
        }
        Set<String> conflicts = new HashSet<>();
        for (String name : initParameters.keySet()) {
            String value = initParameters.get(name);
            if (value == null) {
                throw new IllegalArgumentException("initParameters contains null value by name: " + name);
            }
            if (super.containsKey(name)) {
                conflicts.add(name);
            } else {
                super.put(name, value);
            }
        }
        return conflicts;
    }

    public Map<String, String> getInitParameters() {
        return super.map();
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(super.map().keySet());
    }
}
