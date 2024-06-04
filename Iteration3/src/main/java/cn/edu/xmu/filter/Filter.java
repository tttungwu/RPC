package cn.edu.xmu.filter;

public interface Filter<T> {
    FilterResponse doFilter(FilterData<T> filterData);
}
