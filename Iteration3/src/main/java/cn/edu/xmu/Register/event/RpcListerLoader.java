package cn.edu.xmu.Register.event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcListerLoader {

    private static final ExecutorService eventThreadPool = Executors.newFixedThreadPool(3);
    private static final List<RpcLister> rpcListerList = new ArrayList<>();


    static {
        registerLister(new AddRpcLister());
        registerLister(new RemoveRpcLister());
    }

    // 注册监听器
    public static void registerLister(RpcLister rpcLister) {
        rpcListerList.add(rpcLister);
    }

    // 发送事件
    public static void sendEvent(RpcEventData eventData) {
        if (eventData == null) {
            return;
        }
        for (RpcLister rpcLister : rpcListerList) {
            // 获取接口上的泛型
            final Class<?> generics = getInterfaceGenerics(rpcLister);
            if (eventData.getClass().equals(generics)) {
                eventThreadPool.execute(() -> rpcLister.exec(eventData));
            }
        }
    }

    // 获取接口上的泛型
    public static Class<?> getInterfaceGenerics(Object o) {
        Type[] types = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) types[0];
        Type type = parameterizedType.getActualTypeArguments()[0];
        return (type instanceof Class<?>) ? (Class<?>) type : null;
    }

    // 关闭线程池
    public static void shutdown() {
        eventThreadPool.shutdown();
    }
}
