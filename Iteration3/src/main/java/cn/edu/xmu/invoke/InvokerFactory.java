package cn.edu.xmu.invoke;


import cn.edu.xmu.common.constants.RpcInvokerType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InvokerFactory {

    private static final ConcurrentMap<RpcInvokerType, Invoker> invokerMap = new ConcurrentHashMap<>();

    static {
        register(RpcInvokerType.JDK, new JdkInvoker());
    }

    // 注册新的Invoker
    public static void register(RpcInvokerType key, Invoker invoker) {
        if (key == null || invoker == null) {
            throw new IllegalArgumentException("Key and Invoker must not be null");
        }
        invokerMap.put(key, invoker);
    }

    // 检查是否已经注册了特定的Invoker
    public static boolean isRegistered(RpcInvokerType key) {
        return invokerMap.containsKey(key);
    }

    // 获取Invoker
    public static Invoker get(RpcInvokerType rpcInvoker) {
        if (!InvokerFactory.isRegistered(rpcInvoker)) {
            throw new IllegalArgumentException("No invoker registered for key: " + rpcInvoker);
        }
        return invokerMap.get(rpcInvoker);
    }

}