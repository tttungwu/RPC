package com.Downshifting.proxy.cgclib;

import com.Downshifting.Register.RegistryFactory;
import com.Downshifting.common.RPC.*;
import com.Downshifting.common.constants.MsgType;
import com.Downshifting.common.constants.ProtocolConstants;
import com.Downshifting.common.constants.RegisterType;
import com.Downshifting.common.constants.RpcSerializationType;
import com.Downshifting.common.utils.ClientCache;
import com.Downshifting.common.utils.Endpoint;
import com.Downshifting.common.utils.EndpointService;
import com.Downshifting.common.utils.Service;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CgLibProxy implements MethodInterceptor {
    private final Object object;

    private final Random random = new Random();

    public CgLibProxy(Object o){
        this.object = o;
    }

    // 构建请求头
    private ProtoHeader buildProtoHeader() {
        ProtoHeader header = new ProtoHeader();
        long requestId = RpcRequestTracker.getRequestId();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);
        header.setSerializationType((byte) RpcSerializationType.JSON.ordinal());
        header.setMsgType((byte) MsgType.REQUEST.ordinal());
        header.setStatus((byte) 0x1);
        return header;
    }

    // 构建请求体
    private RpcRequest buildRpcRequest(Method method, Object[] objects) {
        RpcRequest rpcRequest = new RpcRequest();

        rpcRequest.setClassName(object.getClass().getName());
        rpcRequest.setMethodCode(method.hashCode());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setServiceVersion("1.0");

        if (objects != null && objects.length > 0) {
            Class<?>[] parameterTypes = new Class[objects.length];
            for (int i = 0; i < objects.length; i++) {
                parameterTypes[i] = objects[i].getClass();
            }
            rpcRequest.setParameterTypes(parameterTypes);
            rpcRequest.setParameters(objects);
        }

        return rpcRequest;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        final RpcProtocol rpcProtocol = new RpcProtocol();
        // 构建请求头
        final ProtoHeader header = buildProtoHeader();
        rpcProtocol.setHeader(header);
        // 构建请求体
        final RpcRequest rpcRequest = buildRpcRequest(method, objects);
        rpcProtocol.setBody(rpcRequest);

        final List<Endpoint> endpoints = RegistryFactory.get(RegisterType.ZOOKEEPER).discovery(new Service(o.getClass().getName(), "1.0"));
        if (endpoints.isEmpty()){
            throw new Exception("No service is available");
        }
        final Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));
        final ChannelFuture channelFuture = ClientCache.ENDPOINT_CHANNEL_MAP.get(endpoint);

        // 通过Netty的channel发送RPC协议对象
        channelFuture.channel().writeAndFlush(rpcProtocol);
        // 跟踪RPC请求的响应
        RpcFuture<RpcResponse> future = new RpcFuture(new DefaultPromise(new DefaultEventLoop()), 3000L);
        RpcRequestTracker.REQUEST_MAP.put(header.getRequestId(), future);
        RpcResponse rpcResponse = future.getPromise().sync().get(future.getTimeout(), TimeUnit.MILLISECONDS);

        if (rpcResponse.getException() != null){
            throw rpcResponse.getException();
        }
        return rpcResponse.getData();
    }
}
