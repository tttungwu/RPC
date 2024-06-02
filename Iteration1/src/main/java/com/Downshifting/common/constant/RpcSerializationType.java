package com.Downshifting.common.constant;

public enum RpcSerializationType {
    JSON;

    public static RpcSerializationType fromOrdinal(int ordinal) {
        for (RpcSerializationType type : RpcSerializationType.values()) {
            if (type.ordinal() == ordinal) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ordinal: " + ordinal);
    }

}
