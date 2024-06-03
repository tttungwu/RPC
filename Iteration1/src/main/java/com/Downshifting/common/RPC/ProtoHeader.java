package com.Downshifting.common.RPC;


import java.io.Serializable;


public class ProtoHeader implements Serializable {
    // 魔数
    private short magic;
    // 协议版本号
    private byte version;
    // 请求类型
    private byte msgType;
    // 请求状态
    private byte status;
    // 请求ID
    private long requestId;
    // 序列化类型
    private byte serializationType;
    // 请求体长度
    private int msgLen;

    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getMsgType() {
        return msgType;
    }

    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public byte getSerializationType() {
        return serializationType;
    }

    public void setSerializationType(byte serializationType) {
        this.serializationType = serializationType;
    }

    public int getMsgLen() {
        return msgLen;
    }

    public void setMsgLen(int msgLen) {
        this.msgLen = msgLen;
    }
}
