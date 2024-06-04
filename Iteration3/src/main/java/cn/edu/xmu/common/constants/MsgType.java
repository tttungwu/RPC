package cn.edu.xmu.common.constants;


public enum  MsgType {
    REQUEST,
    RESPONSE,
    HEARTBEAT;

    public static MsgType fromOrdinal(int ordinal) {
        for (MsgType type : MsgType.values()) {
            if (type.ordinal() == ordinal) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ordinal: " + ordinal);
    }
}
