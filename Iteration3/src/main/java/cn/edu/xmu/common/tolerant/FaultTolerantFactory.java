package cn.edu.xmu.common.tolerant;

import cn.edu.xmu.common.constants.FaultTolerantType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FaultTolerantFactory {

    private static final ConcurrentMap<FaultTolerantType, FaultTolerantStrategy> faultTolerantStrategyMap = new ConcurrentHashMap<>();

    static {
        // 初始化时注册已知的容错策略
        registerFaultTolerantStrategy(FaultTolerantType.NoOp, new NoOpFaultToleranceStrategy());
        registerFaultTolerantStrategy(FaultTolerantType.Retry, new RetryFaultTolerantStrategy());
    }

    // 注册新的容错策略
    private static void registerFaultTolerantStrategy(FaultTolerantType faultTolerantType, FaultTolerantStrategy faultTolerantStrategy) {
        if (faultTolerantType == null || faultTolerantStrategy == null) {
            throw new IllegalArgumentException("FaultTolerantType and FaultTolerantStrategy must not be null");
        }
        faultTolerantStrategyMap.put(faultTolerantType, faultTolerantStrategy);
    }

    // 根据容错类型获取对应的容错策略实例
    public static FaultTolerantStrategy get(FaultTolerantType faultTolerantType) {
        FaultTolerantStrategy faultTolerantStrategy = faultTolerantStrategyMap.get(faultTolerantType);
        if (faultTolerantStrategy == null) {
            throw new IllegalArgumentException("No fault-tolerant strategy registered for type: " + faultTolerantType);
        }
        return faultTolerantStrategy;
    }
}