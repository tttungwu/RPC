package cn.edu.xmu.proxy;

public interface Proxy {
    <T> T getProxy(Class<T> claz) throws InstantiationException, IllegalAccessException;
}
