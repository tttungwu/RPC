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

        // 使用computeIfAbsent来简化懒加载逻辑
        MethodInvocation methodInvocation = methodCache.computeIfAbsent(methodCode, code -> {
            try {
                final Class<?> objClass = Class.forName(rpcRequest.getClassName());
                final Method method = objClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                return new MethodInvocation(objClass.getDeclaredConstructor().newInstance(), method);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return methodInvocation.invoke(rpcRequest.getParameter());
    }
}