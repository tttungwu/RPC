package com.Downshifting.socket.serialization;

import com.Downshifting.common.constants.RpcSerializationType;

import java.util.HashMap;
import java.util.Map;


public class SerializationFactory {

    private static Map<RpcSerializationType, com.Downshifting.socket.serialization.RpcSerialization> serializationMap
            = new HashMap<RpcSerializationType, com.Downshifting.socket.serialization.RpcSerialization>();

    static {
        serializationMap.put(RpcSerializationType.JSON,new JsonSerialization());
    }

    public static com.Downshifting.socket.serialization.RpcSerialization get(RpcSerializationType serialization){
        return serializationMap.get(serialization);
    }
}
