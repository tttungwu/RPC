package com.Downshifting.Register.event;

import com.Downshifting.common.utils.ClientCache;
import com.Downshifting.common.utils.Endpoint;
import com.Downshifting.common.utils.EndpointService;
import com.Downshifting.common.utils.Service;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AddRpcLister implements RpcLister<AddRpcEventData>{

    private final Lock lock = new ReentrantLock();

    @Override
    public void exec(AddRpcEventData addRpcEventData) {
        final EndpointService endpointService = (EndpointService) addRpcEventData.getData();
        final Service service = endpointService.getService();
        final Endpoint endpoint = endpointService.getEndpoint();

        lock.lock();  // 获取锁
        try {
            // 使用 computeIfAbsent 简化初始化逻辑
            ClientCache.SERVICE_ENDPOINTS_MAP.computeIfAbsent(service, k -> new ArrayList<>()).add(endpoint);

            // 同步连接，确保连接成功后再放入 map 中
            if (!ClientCache.ENDPOINT_CHANNEL_MAP.containsKey(endpoint)) {
                ChannelFuture channelFuture = ClientCache.BOOT_STRAP.connect(endpoint.getIp(), endpoint.getPort()).sync();
                ClientCache.ENDPOINT_CHANNEL_MAP.put(endpoint, channelFuture);
            }
        } catch (InterruptedException e) {
            // 移除已经添加的 endpoint
            ClientCache.SERVICE_ENDPOINTS_MAP.get(service).remove(endpoint);
            Thread.currentThread().interrupt(); // 恢复中断状态
            throw new RuntimeException("Failed to connect to " + endpoint, e);
        } finally {
            lock.unlock();  // 释放锁
        }
    }
}
