package com.Downshifting.invoke;

import com.Downshifting.common.RPC.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JdkInvoker implements Invoker{

    private final ConcurrentMap<Integer, MethodInvocation> methodCache = new ConcurrentHashMap<>();

    @Override
    public Object invoke(RpcRequest rpcRequest) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
        final Integer methodCode = rpcRequest.getMethodCode();
        if (!methodCache.containsKey(methodCode)) {
            final Class<?> objClass = Class.forName(rpcRequest.getClassName());
            final Method method = objClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            methodCache.put(methodCode,new MethodInvocation(objClass.newInstance(), method));
        }
        final MethodInvocation methodInvocation = methodCache.get(methodCode);
        return methodInvocation.invoke(rpcRequest.getParameter());
    }
}
