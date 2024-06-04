package cn.edu.xmu.invoke;

import cn.edu.xmu.common.RPC.RpcRequest;

import java.lang.reflect.InvocationTargetException;


public interface Invoker {
    Object invoke(RpcRequest rpcRequest) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException;
}
