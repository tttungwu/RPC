package cn.edu.xmu.common.tolerant;

import cn.edu.xmu.common.constants.FaultTolerantType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FaultTolerantFactory {

    private static final ConcurrentMap<FaultTolerantType, FaultTolerantStrategy> faultTolerantStrategyMap = new ConcurrentHashMap<>();


}
