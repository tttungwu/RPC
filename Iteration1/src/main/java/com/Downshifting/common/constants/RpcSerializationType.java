package com.Downshifting.common.constants;


public enum RpcSerializationType {
    JSON("json"),
    JDK("jdk");

    public String name;
    RpcSerializationType(String type){
        this.name = type;
    }

    public static RpcSerializationType get(String type){
        for (RpcSerializationType value : values()) {
            if (value.name.equals(type)) {
                return value;
            }
        }
        return null;
    }
}
