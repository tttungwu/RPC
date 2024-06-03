package com.Downshifting.Register;

import com.Downshifting.common.constants.RegisterType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RegistryFactory {

    private static final ConcurrentMap<RegisterType, RegistryService> registerServiceMap = new ConcurrentHashMap<>();

    static {
        // 注册默认的注册服务
        register(RegisterType.ZOOKEEPER, new CuratorZookeeperRegistry("127.0.0.1:2181"));
    }

    // 注册新的注册服务
    public static void register(RegisterType register, RegistryService registryService) {
        if (register == null || registryService == null) {
            throw new IllegalArgumentException("Register and RegistryService must not be null");
        }
        registerServiceMap.put(register, registryService);
    }

    // 获取指定类型的注册服务实例
    public static RegistryService get(RegisterType register) {
        RegistryService registryService = registerServiceMap.get(register);
        if (registryService == null) {
            throw new IllegalArgumentException("No registry service registered for type: " + register);
        }
        return registryService;
    }
}
