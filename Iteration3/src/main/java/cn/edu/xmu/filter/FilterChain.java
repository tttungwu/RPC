package cn.edu.xmu.filter;

import java.util.ArrayList;
import java.util.List;

public class FilterChain {
    private List<Filter> filters = new ArrayList<>();

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    public void addFilter(List<Filter> filters) {
        for (Filter filter : filters) {
            addFilter(filter);
        }
    }

    public FilterResponse doFilter(FilterData data) {
        for (Filter filter : this.filters) {
            final FilterResponse filterResponse = filter.doFilter(data);
            if (filterResponse.getException() != null) {
                return filterResponse;
            }
        }
        return new FilterResponse(true);
    }
}
