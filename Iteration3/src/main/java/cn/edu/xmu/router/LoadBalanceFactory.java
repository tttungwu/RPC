package cn.edu.xmu.router;

import cn.edu.xmu.Register.RegistryService;
import cn.edu.xmu.common.constants.LoadBalanceType;
import cn.edu.xmu.common.constants.RegisterType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LoadBalanceFactory {

    private static final ConcurrentMap<LoadBalanceType, LoadBalanceStrategy> loadBalanceStrategyMap = new ConcurrentHashMap<>();



}
