package cn.edu.xmu.common.constants;

public enum RpcProxyType {
    CG_LIB;

    public static RpcProxyType fromOrdinal(int ordinal) {
        for (RpcProxyType type : RpcProxyType.values()) {
            if (type.ordinal() == ordinal) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ordinal: " + ordinal);
    }
}
