package cn.edu.xmu.tolerant;

import cn.edu.xmu.common.RPC.RpcFuture;
import cn.edu.xmu.common.RPC.RpcRequestTracker;
import cn.edu.xmu.common.RPC.RpcResponse;
import cn.edu.xmu.common.utils.ClientCache;
import cn.edu.xmu.common.utils.Endpoint;
import cn.edu.xmu.common.utils.EndpointService;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RetryFaultTolerantStrategy implements FaultTolerantStrategy{
    @Override
    public Object handler(FaultContext faultContext) throws Exception {
        final Endpoint curEndpoint = faultContext.getCurEndpoint();
        final List<Endpoint> endpoints = faultContext.getEndpoints();
        endpoints.remove(curEndpoint);

        if (endpoints.isEmpty()) {
            throw new Exception("All servers are down, triggering fault tolerance mechanism: Fallback, no available services.");
        }

        for (Endpoint endpoint : endpoints) {
            try {
                final ChannelFuture channelFuture = ClientCache.ENDPOINT_CHANNEL_MAP.get(endpoint);
                channelFuture.channel().writeAndFlush(faultContext.getRpcProtocol());

                RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), 3000L);
                RpcRequestTracker.REQUEST_MAP.put(faultContext.getRequestId(), future);
                RpcResponse rpcResponse = future.getPromise().sync().get(future.getTimeout(), TimeUnit.MILLISECONDS);

                if (rpcResponse.getException() == null) {
                    return rpcResponse.getData();
                } else {
                    faultContext.setCurEndpoint(endpoint);
                    return handler(faultContext);
                }
            } catch (Exception e) {
                throw e;
            }
        }
        throw new Exception("Failed to process the request for all available services.");
    }
}
