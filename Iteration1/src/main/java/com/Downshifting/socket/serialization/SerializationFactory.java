package com.Downshifting.socket.serialization;

import com.Downshifting.common.constants.RpcSerialization;

import java.util.HashMap;
import java.util.Map;


public class SerializationFactory {

    private static Map<RpcSerialization, com.Downshifting.socket.serialization.RpcSerialization> serializationMap
            = new HashMap<RpcSerialization, com.Downshifting.socket.serialization.RpcSerialization>();

    static {
        serializationMap.put(RpcSerialization.JSON,new JsonSerialization());
    }

    public static com.Downshifting.socket.serialization.RpcSerialization get(RpcSerialization serialization){
        return serializationMap.get(serialization);
    }
}
