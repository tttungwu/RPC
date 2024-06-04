package cn.edu.xmu.proxy;

import cn.edu.xmu.common.constants.RpcProxyType;
import cn.edu.xmu.proxy.cgclib.CgLibProxyFactory;

import java.util.HashMap;
import java.util.Map;

public class ProxyFactory {
    private static Map<RpcProxyType, Proxy> proxyMap = new HashMap<>();

    static {
        register(RpcProxyType.CG_LIB, new CgLibProxyFactory());
    }

    // 注册新的代理工厂
    public static void register(RpcProxyType key, Proxy proxy) {
        if (key == null || proxy == null) {
            throw new IllegalArgumentException("Key and Proxy must not be null");
        }
        proxyMap.put(key, proxy);
    }

    // 检查是否已经注册了特定的代理工厂
    public static boolean isRegistered(RpcProxyType key) {
        return proxyMap.containsKey(key);
    }

    // 获取指定类型的代理实例
    public static Proxy getProxy(RpcProxyType rpcProxyType) {
        if (!ProxyFactory.isRegistered(rpcProxyType)) {
            throw new IllegalArgumentException("No proxy registered for key: " + rpcProxyType);
        }
        return proxyMap.get(rpcProxyType);
    }
}
