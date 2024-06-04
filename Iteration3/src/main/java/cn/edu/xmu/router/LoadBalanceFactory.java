package cn.edu.xmu.router;

import cn.edu.xmu.common.constants.LoadBalanceType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LoadBalanceFactory {

    private static final ConcurrentMap<LoadBalanceType, LoadBalanceStrategy> loadBalanceStrategyMap = new ConcurrentHashMap<>();

    static {
        // 初始化时注册已知的负载均衡策略
        registerLoadBalanceStrategy(LoadBalanceType.Round, new RoundRobinLoadBalanceStrategy());
        registerLoadBalanceStrategy(LoadBalanceType.Random, new RandomLoadBalanceStrategy());
    }

    // 注册新的负载均衡策略
    private static void registerLoadBalanceStrategy(LoadBalanceType loadBalanceType, LoadBalanceStrategy loadBalanceStrategy) {
        if (loadBalanceType == null || loadBalanceStrategy == null) {
            throw new IllegalArgumentException("LoadBalanceType and LoadBalanceStrategy must not be null");
        }
        loadBalanceStrategyMap.put(loadBalanceType, loadBalanceStrategy);
    }

    // 根据负载均衡类型获取对应的负载均衡策略实例
    public static LoadBalanceStrategy get(LoadBalanceType loadBalanceType) {
        LoadBalanceStrategy loadBalanceStrategy = loadBalanceStrategyMap.get(loadBalanceType);
        if (loadBalanceStrategy == null) {
            throw new IllegalArgumentException("No load balance strategy registered for type: " + loadBalanceType);
        }
        return loadBalanceStrategy;
    }
}
