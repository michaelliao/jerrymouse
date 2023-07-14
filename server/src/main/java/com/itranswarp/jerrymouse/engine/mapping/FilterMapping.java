package com.itranswarp.jerrymouse.engine.mapping;

import jakarta.servlet.Filter;

public class FilterMapping extends AbstractMapping {

    public final String filterName;
    public final Filter filter;

    public FilterMapping(String filterName, String urlPattern, Filter filter) {
        super(urlPattern);
        this.filterName = filterName;
        this.filter = filter;
    }
}
