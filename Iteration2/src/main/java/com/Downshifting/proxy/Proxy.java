package com.Downshifting.proxy;

public interface Proxy {
    <T> T getProxy(Class<T> claz) throws InstantiationException, IllegalAccessException;
}
