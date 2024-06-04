package cn.edu.xmu.proxy.cgclib;

import cn.edu.xmu.proxy.Proxy;
import net.sf.cglib.proxy.Enhancer;

public class CgLibProxyFactory<T> implements Proxy {
    public <T> T getProxy(Class<T> claz) throws InstantiationException, IllegalAccessException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(claz);
        enhancer.setCallback(new CgLibProxy(claz));
        return (T) enhancer.create();
    }
}
