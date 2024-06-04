package cn.edu.xmu.common.tolerant;

import cn.edu.xmu.common.RPC.RpcFuture;
import cn.edu.xmu.common.RPC.RpcRequestTracker;
import cn.edu.xmu.common.RPC.RpcResponse;
import cn.edu.xmu.common.utils.ClientCache;
import cn.edu.xmu.common.utils.EndpointService;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RetryFaultTolerantStrategy implements FaultTolerantStrategy{
    @Override
    public Object handler(FaultContext faultContext) throws Exception {
        final EndpointService curEndpointService = faultContext.getCurEndpointService();
        final List<EndpointService> endpointServices = faultContext.getEndpointServices();
        endpointServices.remove(curEndpointService);

        if (endpointServices.isEmpty()) {
            throw new Exception("All servers are down, triggering fault tolerance mechanism: Fallback, no available services.");
        }

        for (EndpointService endpointService : endpointServices) {
            try {
                final ChannelFuture channelFuture = ClientCache.ENDPOINT_CHANNEL_MAP.get(endpointService.getEndpoint());
                channelFuture.channel().writeAndFlush(faultContext.getRpcProtocol());

                RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), 3000L);
                RpcRequestTracker.REQUEST_MAP.put(faultContext.getRequestId(), future);
                RpcResponse rpcResponse = future.getPromise().sync().get(future.getTimeout(), TimeUnit.MILLISECONDS);

                if (rpcResponse.getException() == null) {
                    return rpcResponse.getData();
                } else {
                    faultContext.setCurEndpointService(endpointService);
                    return handler(faultContext);
                }
            } catch (Exception e) {
                throw e;
            }
        }
        throw new Exception("Failed to process the request for all available services.");
    }
}
