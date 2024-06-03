package com.Downshifting.invoke;

import com.Downshifting.common.constants.RpcInvokerType;

import java.util.HashMap;
import java.util.Map;


public class InvokerFactory {

    public static Map<RpcInvokerType,Invoker> invokerInvokerMap = new HashMap();

    static {
        invokerInvokerMap.put(RpcInvokerType.JDK,new JdkReflectionInvoker());
    }

    public static Invoker get(RpcInvokerType rpcInvoker){
        return invokerInvokerMap.get(rpcInvoker);
    }
}
