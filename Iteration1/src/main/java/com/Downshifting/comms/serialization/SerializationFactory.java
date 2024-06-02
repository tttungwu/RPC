package com.Downshifting.comms.serialization;

import com.Downshifting.common.constant.RpcSerializationType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SerializationFactory {

    private static final ConcurrentMap<RpcSerializationType, RpcSerialization> serializationMap = new ConcurrentHashMap<>();

    static {
        register(RpcSerializationType.JSON, new JsonSerialization());
    }

    // 注册序列化器
    public static void register(RpcSerializationType key, RpcSerialization serializer) {
        if (key == null || serializer == null) {
            throw new IllegalArgumentException("Key and Serializer must not be null");
        }
        serializationMap.put(key, serializer);
    }

    // 检查是否注册了特定的序列化器
    public static boolean isRegistered(RpcSerializationType key) {
        return serializationMap.containsKey(key);
    }

    // 获取序列化器
    public static RpcSerialization get(RpcSerializationType key) {
        if (!isRegistered(key)) {
            throw new IllegalArgumentException("No serializer registered for key: " + key);
        }
        return serializationMap.get(key);
    }
}
