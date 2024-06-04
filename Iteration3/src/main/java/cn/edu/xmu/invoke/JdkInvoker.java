package cn.edu.xmu.invoke;

import cn.edu.xmu.common.RPC.RpcRequest;
import cn.edu.xmu.common.utils.ServerCache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JdkInvoker implements Invoker{

    private final ConcurrentMap<Integer, MethodInvocation> methodCache = new ConcurrentHashMap<>();

    @Override
    public Object invoke(RpcRequest rpcRequest) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
        final Integer methodCode = rpcRequest.getMethodCode();

        // 检查缓存中是否已存在该方法的调用信息
        methodCache.computeIfAbsent(methodCode, code -> {
            try {
                // 构建服务键
                final String key = String.join("$", rpcRequest.getClassName(), rpcRequest.getServiceVersion());
                // 从缓存中获取服务实例
                Object bean = ServerCache.SERVICE_MAP.get(key);
                final Class<?> aClass = bean.getClass();

                // 获取方法
                final Method method = aClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                return new MethodInvocation(bean, method);
            } catch (Exception e) {
                throw new RuntimeException("Failed to cache method invocation", e);
            }
        });

        // 从缓存中获取方法调用信息并执行
        final MethodInvocation methodInvocation = methodCache.get(methodCode);
        return methodInvocation.invoke(rpcRequest.getParameters());
    }
}