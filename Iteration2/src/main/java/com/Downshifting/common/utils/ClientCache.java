package com.Downshifting.common.utils;

import io.netty.channel.ChannelFuture;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientCache {
    public static final ConcurrentMap<Service, Endpoint> services = new ConcurrentHashMap<>();
    public static final ConcurrentMap<Endpoint, ChannelFuture> channelFutureMap = new ConcurrentHashMap<>();

    // 私有构造函数，防止实例化
    private ClientCache() {
    }
}
