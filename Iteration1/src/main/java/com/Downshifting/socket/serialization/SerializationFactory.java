package com.Downshifting.socket.serialization;

import com.Downshifting.common.constants.RpcSerializationType;

import java.util.HashMap;
import java.util.Map;


public class SerializationFactory {

    private static Map<RpcSerializationType, RpcSerialization> serializationMap
            = new HashMap<RpcSerializationType, RpcSerialization>();

    static {
        serializationMap.put(RpcSerializationType.JSON,new JsonSerialization());
    }

    public static RpcSerialization get(RpcSerializationType serialization){
        return serializationMap.get(serialization);
    }
}
