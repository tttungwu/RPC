package com.Downshifting.Register.event;

import com.Downshifting.common.utils.ClientCache;
import com.Downshifting.common.utils.Endpoint;
import com.Downshifting.common.utils.EndpointService;
import com.Downshifting.common.utils.Service;
import io.netty.channel.ChannelFuture;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RemoveRpcLister implements RpcLister<RemoveEventData>{

    private final Lock lock = new ReentrantLock();

    @Override
    public void exec(RemoveEventData removeEventData) {
        final EndpointService endpointService = (EndpointService) removeEventData.getData();
        final Service service = endpointService.getService();
        final Endpoint endpoint = endpointService.getEndpoint();

        lock.lock();  // 获取锁
        try {
            // 获取服务URL列表并移除特定URL
            List<Endpoint> endpoints = ClientCache.SERVICE_ENDPOINTS_MAP.get(service);
            endpoints.remove(endpoint);

            // 移除ChannelFuture并关闭连接
            ChannelFuture channelFuture = ClientCache.ENDPOINT_CHANNEL_MAP.remove(endpoint);
            if (channelFuture != null) {
                channelFuture.channel().close();
            }
        } finally {
            lock.unlock();  // 释放锁
        }
    }
}
