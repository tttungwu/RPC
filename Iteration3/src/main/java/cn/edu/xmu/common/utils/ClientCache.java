package cn.edu.xmu.common.utils;

import cn.edu.xmu.filter.FilterChain;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientCache {
    // 订阅的服务服务->端点
    public static final ConcurrentMap<Service, List<Endpoint>> SERVICE_ENDPOINTS_MAP = new ConcurrentHashMap<>();
    // 端点->channel
    public static final ConcurrentMap<Endpoint, ChannelFuture> ENDPOINT_CHANNEL_MAP = new ConcurrentHashMap<>();
    // 所有订阅的服务
    public static List<Service> SUBSCRIBE_SERVICE_LIST = new CopyOnWriteArrayList<>();

    public static Bootstrap BOOT_STRAP;

    public static FilterChain beforeFilterChain = new FilterChain();

    public static FilterChain afterFilterChain = new FilterChain();


    // 私有构造函数，防止实例化
    private ClientCache() {
    }
}
